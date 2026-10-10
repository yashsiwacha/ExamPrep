package com.examprep.android.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examprep.android.feature.auth.AuthStore
import com.examprep.domain.model.EntitlementTier
import com.examprep.domain.model.UserProfile
import com.examprep.domain.repository.FirebaseAuthRepository
import com.examprep.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

data class SettingsUiState(
    val userProfile: UserProfile? = null,
    val entitlementTier: EntitlementTier = EntitlementTier.AD_FREE_TRIAL,
    val studyReminderEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val dailyHours: Float = 4f,
    val adPersonalizationEnabled: Boolean = false,
    val isDeletingData: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context,
    private val userProfileRepository: UserProfileRepository,
    private val authStore: AuthStore,
    private val firebaseAuthRepository: FirebaseAuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = combine(
        userProfileRepository.getUserProfile(),
        _uiState
    ) { profile, current ->
        current.copy(
            userProfile = profile,
            dailyHours = profile?.dailyStudyHours ?: current.dailyHours
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun toggleStudyReminder(enabled: Boolean) {
        _uiState.update { it.copy(studyReminderEnabled = enabled) }
        if (enabled) {
            com.examprep.android.notification.StudyReminderWorker.scheduleDailyReminder(context)
        } else {
            com.examprep.android.notification.StudyReminderWorker.cancelDailyReminder(context)
        }
    }

    fun toggleSound(enabled: Boolean) {
        _uiState.update { it.copy(soundEnabled = enabled) }
    }

    fun toggleVibration(enabled: Boolean) {
        _uiState.update { it.copy(vibrationEnabled = enabled) }
    }

    fun toggleAdPersonalization(enabled: Boolean) {
        _uiState.update { it.copy(adPersonalizationEnabled = enabled) }
    }

    fun updateDailyHours(hours: Float) {
        viewModelScope.launch {
            val profile = userProfileRepository.getUserProfileOnce()
            if (profile != null) {
                userProfileRepository.updateUserProfile(profile.copy(dailyStudyHours = hours))
            }
        }
    }

    /**
     * Permanent user data & account deletion workflow.
     * Complies with Google Play's User Data & Account Deletion policy and Indian DPDPA 2023.
     *
     * 1. Deletes Firebase Auth user if authenticated.
     * 2. Clears all user study records, quiz history, and progress in Room via UserProfileRepository.
     * 3. Clears in-memory AuthStore session and local user database.
     */
    fun deleteAllUserData(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingData = true) }
            withContext(Dispatchers.IO) {
                try {
                    firebaseAuthRepository.deleteAccount()
                } catch (e: Exception) {
                    Timber.w(e, "Firebase account deletion error during full purge: ${e.message}")
                }
                try {
                    userProfileRepository.clearAllUserData()
                } catch (e: Exception) {
                    Timber.e(e, "Error clearing local database during full purge: ${e.message}")
                }
            }
            authStore.deleteCurrentUserAccount()
            _uiState.update { it.copy(isDeletingData = false) }
            onComplete()
        }
    }
}
