package com.examprep.android.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examprep.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SplashDestination {
    data object Deciding : SplashDestination
    data object GoToOnboarding : SplashDestination
    data object GoToDashboard : SplashDestination
}

/**
 * SplashViewModel — determines the initial routing destination.
 *
 * Decision logic:
 * - If user profile exists AND onboardingCompleted = true → Dashboard (returning user)
 * - Otherwise → Onboarding flow (new user)
 *
 * This is the ONLY place where we make the onboarding-skip decision, keeping
 * the routing concern isolated from the UI layer.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination>(SplashDestination.Deciding)
    val destination: StateFlow<SplashDestination> = _destination.asStateFlow()

    init {
        determineDestination()
    }

    private fun determineDestination() {
        viewModelScope.launch {
            try {
                // Single-shot read — we only need the current profile, not a stream
                val profile = userProfileRepository.getUserProfileOnce()
                _destination.value = if (profile != null && profile.onboardingCompleted) {
                    SplashDestination.GoToDashboard
                } else {
                    SplashDestination.GoToOnboarding
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _destination.value = SplashDestination.GoToOnboarding
            }
        }
    }
}
