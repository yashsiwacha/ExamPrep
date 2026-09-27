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
import javax.inject.Inject

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val user: AuthUser) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authStore: AuthStore
) : ViewModel() {

    val currentUser: StateFlow<AuthUser?> = authStore.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AuthStore.get().currentUser.value)

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signUp(
        name: String,
        email: String,
        password: String,
        targetExam: String = "JEE Main 2026",
        targetYear: Int = 2026
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authStore.signUp(name, email, password, targetExam, targetYear)
            result.fold(
                onSuccess = { _uiState.value = AuthUiState.Success(it) },
                onFailure = { _uiState.value = AuthUiState.Error(it.message ?: "Failed to create account") }
            )
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authStore.signIn(email, password)
            result.fold(
                onSuccess = { _uiState.value = AuthUiState.Success(it) },
                onFailure = { _uiState.value = AuthUiState.Error(it.message ?: "Invalid email or password") }
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

    fun clearError() {
        _uiState.value = AuthUiState.Idle
    }
}
