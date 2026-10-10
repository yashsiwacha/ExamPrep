package com.examprep.android.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examprep.android.feature.auth.AuthStore
import com.examprep.data.repository.QuestionBankRepository
import com.examprep.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SplashDestination {
    data object Deciding : SplashDestination
    data object GoToAuth : SplashDestination
    data object GoToOnboarding : SplashDestination
    data object GoToDashboard : SplashDestination
}

/**
 * SplashViewModel — determines the initial routing destination.
 *
 * Decision logic:
 * - If user is not authenticated -> AuthScreen (Login / Sign Up)
 * - If authenticated AND onboarding completed -> Dashboard (returning user)
 * - If authenticated BUT onboarding incomplete -> Onboarding flow (new user)
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authStore: AuthStore,
    private val userProfileRepository: UserProfileRepository,
    private val questionBankRepository: QuestionBankRepository
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination>(SplashDestination.Deciding)
    val destination: StateFlow<SplashDestination> = _destination.asStateFlow()

    init {
        determineDestination()
    }

    private fun determineDestination() {
        viewModelScope.launch {
            try {
                val currentUser = authStore.currentUser.value
                val profile = userProfileRepository.getUserProfileOnce()
                val selectedExamId = profile?.selectedExamId ?: "exam_jee_main"

                // Preload question bank in background to keep cold start fast
                launch {
                    try {
                        questionBankRepository.ensureDefaultPaperLoaded(selectedExamId)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                if (currentUser == null) {
                    _destination.value = SplashDestination.GoToAuth
                } else {
                    val isOnboarded = (profile != null && profile.onboardingCompleted) || currentUser.hasCompletedOnboarding
                    _destination.value = if (isOnboarded) {
                        SplashDestination.GoToDashboard
                    } else {
                        SplashDestination.GoToOnboarding
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _destination.value = SplashDestination.GoToAuth
            }
        }
    }
}
