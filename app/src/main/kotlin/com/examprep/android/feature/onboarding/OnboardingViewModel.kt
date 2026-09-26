package com.examprep.android.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examprep.domain.engine.StudyPlanningEngine
import com.examprep.domain.model.*
import com.examprep.domain.repository.ExamRepository
import com.examprep.domain.repository.StudyPlanRepository
import com.examprep.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed interface OnboardingUiState {
    data object Loading : OnboardingUiState
    data class Success(
        val availableExams: List<Exam> = emptyList(),
        val selectedExam: Exam? = null,
        val targetScore: Float = 250f,
        val targetRank: Int = 1000,
        val dailyHours: Float = 4f,
        val examDateEpoch: Long = System.currentTimeMillis() + (120L * 24 * 60 * 60 * 1000),
        val isOnboardingComplete: Boolean = false
    ) : OnboardingUiState
    data class Error(val message: String) : OnboardingUiState
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val examRepository: ExamRepository,
    private val userProfileRepository: UserProfileRepository,
    private val studyPlanRepository: StudyPlanRepository,
    private val studyPlanningEngine: StudyPlanningEngine
) : ViewModel() {

    private val _selectedExamId = MutableStateFlow<String?>(null)
    private val _targetScore = MutableStateFlow(250f)
    private val _dailyHours = MutableStateFlow(4f)
    private val _examDateEpoch = MutableStateFlow(System.currentTimeMillis() + (120L * 24 * 60 * 60 * 1000))

    val uiState: StateFlow<OnboardingUiState> = combine(
        examRepository.getAllExams(),
        _selectedExamId,
        _targetScore,
        _dailyHours,
        _examDateEpoch
    ) { exams, selectedId, score, hours, date ->
        val selected = exams.find { it.id == selectedId } ?: exams.firstOrNull()
        OnboardingUiState.Success(
            availableExams = exams,
            selectedExam = selected,
            targetScore = score,
            dailyHours = hours,
            examDateEpoch = date
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = OnboardingUiState.Loading
    )

    fun selectExam(examId: String) {
        _selectedExamId.value = examId
    }

    fun updateTargetScore(score: Float) {
        _targetScore.value = score
    }

    fun updateDailyHours(hours: Float) {
        _dailyHours.value = hours
    }

    fun updateExamDate(dateEpoch: Long) {
        _examDateEpoch.value = dateEpoch
    }

    fun completeOnboarding(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val currentState = uiState.value
            if (currentState is OnboardingUiState.Success) {
                val exam = currentState.selectedExam ?: return@launch

                // FIX B6: Reuse existing userId if a profile already exists.
                // Generating a new UUID every call caused duplicate profiles on back-navigation re-entry.
                val existingProfile = userProfileRepository.getUserProfileOnce()
                val userId = existingProfile?.id ?: java.util.UUID.randomUUID().toString()

                val profile = UserProfile(
                    id = userId,
                    name = existingProfile?.name ?: "Aspirant",
                    selectedExamId = exam.id,
                    examDate = currentState.examDateEpoch,
                    targetScore = currentState.targetScore,
                    dailyStudyHours = currentState.dailyHours,
                    onboardingCompleted = true,
                    createdAt = existingProfile?.createdAt ?: System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                userProfileRepository.saveUserProfile(profile)

                // Generate and save initial study plan only if none exists yet
                val existingPlan = studyPlanRepository.getStudyPlan(userId).firstOrNull()
                if (existingPlan == null) {
                    val initialPlan = studyPlanningEngine.generatePlan(profile, exam, emptyList())
                    studyPlanRepository.saveStudyPlan(initialPlan)
                }

                onSuccess()
            }
        }
    }
}
