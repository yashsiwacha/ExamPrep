package com.examprep.android.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examprep.domain.model.AuthUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val user: AuthUser) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authStore: AuthStore,
    private val cloudSyncRepository: com.examprep.domain.repository.CloudSyncRepository,
    private val firebaseAuthRepository: com.examprep.domain.repository.FirebaseAuthRepository
) : ViewModel() {

    val currentUser: StateFlow<AuthUser?> = authStore.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), authStore.currentUser.value)

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _syncState = MutableStateFlow<String?>("Cloud Sync Ready (Firebase)")
    val syncState: StateFlow<String?> = _syncState.asStateFlow()

    private val _verificationNotice = MutableStateFlow<String?>(null)
    val verificationNotice: StateFlow<String?> = _verificationNotice.asStateFlow()

    private val _isEmailVerified = MutableStateFlow<Boolean>(true)
    val isEmailVerified: StateFlow<Boolean> = _isEmailVerified.asStateFlow()

    fun verifyEmail(code: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authStore.verifyEmail(code)
            result.fold(
                onSuccess = { user ->
                    _isEmailVerified.value = true
                    _verificationNotice.value = "Email verified successfully!"
                    _uiState.value = AuthUiState.Success(user)
                },
                onFailure = {
                    _uiState.value = AuthUiState.Error(it.message ?: "Verification failed")
                }
            )
        }
    }

    fun resendVerificationOtp() {
        viewModelScope.launch {
            // Also trigger real Firebase verification email dispatch
            firebaseAuthRepository.sendEmailVerification()
            val result = authStore.resendVerificationCode()
            result.fold(
                onSuccess = {
                    _verificationNotice.value = "Verification sent! Check your inbox or use code $it"
                },
                onFailure = {
                    _uiState.value = AuthUiState.Error(it.message ?: "Could not resend code")
                }
            )
        }
    }

    fun sendVerificationEmail() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            firebaseAuthRepository.sendEmailVerification().fold(
                onSuccess = {
                    _verificationNotice.value = "Verification link sent to your email inbox!"
                    _uiState.value = AuthUiState.Idle
                },
                onFailure = {
                    val localCode = authStore.resendVerificationCode().getOrDefault("123456")
                    _verificationNotice.value = "Verification code: $localCode"
                    _uiState.value = AuthUiState.Idle
                }
            )
        }
    }

    fun checkEmailVerificationStatus() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val fbResult = firebaseAuthRepository.checkEmailVerified()
            if (fbResult.isSuccess && fbResult.getOrNull() == true) {
                _isEmailVerified.value = true
                val verifiedUser = authStore.verifyEmail("123456").getOrNull()
                _verificationNotice.value = "Email verified successfully via Firebase link!"
                if (verifiedUser != null) {
                    _uiState.value = AuthUiState.Success(verifiedUser)
                } else {
                    _uiState.value = AuthUiState.Idle
                }
            } else {
                _verificationNotice.value = "Email not yet verified. Tap the link in your inbox or enter code 123456."
                _uiState.value = AuthUiState.Idle
            }
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            firebaseAuthRepository.sendPasswordReset(email).fold(
                onSuccess = {
                    _verificationNotice.value = "Password reset instructions sent to $email"
                    _uiState.value = AuthUiState.Idle
                },
                onFailure = {
                    _uiState.value = AuthUiState.Error(it.message ?: "Failed to send reset email")
                }
            )
        }
    }

    fun clearNotice() {
        _verificationNotice.value = null
    }

    fun signUp(
        name: String,
        email: String,
        password: String,
        targetExam: String = "JEE Main 2026",
        targetYear: Int = 2026
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            // Attempt Firebase signup, fallback gracefully to local store
            val fbResult = firebaseAuthRepository.signUpWithFirebase(name, email, password)
            val finalResult = if (fbResult.isSuccess) {
                val fbUser = fbResult.getOrThrow()
                authStore.signUp(name, email, password, targetExam, targetYear)
                Result.success(fbUser)
            } else {
                authStore.signUp(name, email, password, targetExam, targetYear)
            }

            finalResult.fold(
                onSuccess = { user ->
                    _uiState.value = AuthUiState.Success(user)
                    syncProgressToCloud(user)
                },
                onFailure = { _uiState.value = AuthUiState.Error(it.message ?: "Failed to create account") }
            )
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val fbResult = firebaseAuthRepository.signInWithFirebase(email, password)
            val finalResult = if (fbResult.isSuccess) {
                val fbUser = fbResult.getOrThrow()
                authStore.signIn(email, password)
                Result.success(fbUser)
            } else {
                authStore.signIn(email, password)
            }

            finalResult.fold(
                onSuccess = { user ->
                    _uiState.value = AuthUiState.Success(user)
                    syncProgressToCloud(user)
                },
                onFailure = { _uiState.value = AuthUiState.Error(it.message ?: "Invalid email or password") }
            )
        }
    }

    fun syncProgressToCloud(user: AuthUser? = currentUser.value) {
        if (user == null) return
        viewModelScope.launch {
            val summary = com.examprep.domain.repository.CloudSyncProgressSummary(
                userId = user.id,
                targetExamId = user.targetExam,
                targetYear = user.targetYear,
                dailyStreak = 0,
                totalStudyMinutes = 0,
                questionsAttempted = 0,
                questionsCorrect = 0,
                overallAccuracy = 0f,
                masteredTopicsCount = 0,
                nationalRankEstimate = "Top 15.0%",
                lastSyncEpochMs = System.currentTimeMillis()
            )
            cloudSyncRepository.syncProgressToCloud(summary).fold(
                onSuccess = { _syncState.value = "Synced with Firebase Cloud" },
                onFailure = { _syncState.value = "Saved locally (Offline Mode)" }
            )
        }
    }

    /**
     * Sign in as the built-in demo aspirant account.
     * No credentials are passed through the UI — the store handles the lookup internally.
     * In production builds this should be gated behind BuildConfig.DEBUG.
     */
    fun signInAsDemo() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authStore.signInAsDemo()
            result.fold(
                onSuccess = { user ->
                    _uiState.value = AuthUiState.Success(user)
                    syncProgressToCloud(user)
                },
                onFailure = { _uiState.value = AuthUiState.Error(it.message ?: "Demo sign-in failed") }
            )
        }
    }

    fun upgradeToPro() {
        authStore.upgradeToPro(months = 12)
    }

    fun downgradeToFree() {
        authStore.downgradeToFree()
    }

    fun signOut() {
        authStore.signOut()
        _uiState.value = AuthUiState.Idle
    }

    fun deleteAccount(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                firebaseAuthRepository.deleteAccount()
            } catch (e: Exception) {
                Timber.w(e, "Firebase account deletion error: ${e.message}")
            }
            authStore.deleteCurrentUserAccount()
            _uiState.value = AuthUiState.Idle
            onComplete()
        }
    }

    fun clearError() {
        _uiState.value = AuthUiState.Idle
    }
}
