package com.examprep.android.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examprep.domain.model.EntitlementTier
import com.examprep.domain.model.UserProfile
import com.examprep.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val userProfile: UserProfile? = null,
    val entitlementTier: EntitlementTier = EntitlementTier.AD_FREE_TRIAL,
    val studyReminderEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val dailyHours: Float = 4f
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository
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
    }

    fun toggleSound(enabled: Boolean) {
        _uiState.update { it.copy(soundEnabled = enabled) }
    }

    fun toggleVibration(enabled: Boolean) {
        _uiState.update { it.copy(vibrationEnabled = enabled) }
    }

    fun updateDailyHours(hours: Float) {
        viewModelScope.launch {
            val profile = userProfileRepository.getUserProfileOnce()
            if (profile != null) {
                userProfileRepository.updateUserProfile(profile.copy(dailyStudyHours = hours))
            }
        }
    }
}
