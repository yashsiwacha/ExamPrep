package com.examprep.android.feature.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examprep.domain.engine.StudyPlanningEngine
import com.examprep.domain.model.*
import com.examprep.domain.repository.StudyPlanRepository
import com.examprep.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TimelineUiState {
    data object Loading : TimelineUiState
    data class Success(
        val plan: StudyPlan?,
        val upcomingTasks: List<StudyTask>,
        val currentPhase: StudyPhaseType = StudyPhaseType.FOUNDATION,
        val totalPhasesCount: Int = 6,
        val currentPhaseIndex: Int = 1
    ) : TimelineUiState
    data class Error(val message: String) : TimelineUiState
}

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class TimelineViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val studyPlanRepository: StudyPlanRepository,
    private val studyPlanningEngine: StudyPlanningEngine
) : ViewModel() {

    val uiState: StateFlow<TimelineUiState> = userProfileRepository.getUserProfile()
        .flatMapLatest { profile ->
            if (profile == null) {
                flowOf(TimelineUiState.Success(plan = null, upcomingTasks = emptyList()))
            } else {
                combine(
                    studyPlanRepository.getStudyPlan(profile.id),
                    studyPlanRepository.getUpcomingTasks(profile.id, daysAhead = 14)
                ) { plan, upcomingTasks ->
                    TimelineUiState.Success(
                        plan = plan,
                        upcomingTasks = upcomingTasks,
                        currentPhase = plan?.phases?.firstOrNull()?.phaseType ?: StudyPhaseType.FOUNDATION
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TimelineUiState.Loading
        )

    fun rebalancePlan(missedDays: Int) {
        viewModelScope.launch {
            val currentState = uiState.value
            if (currentState is TimelineUiState.Success && currentState.plan != null) {
                val updatedPlan = studyPlanningEngine.rebalancePlan(currentState.plan, missedDays)
                studyPlanRepository.saveStudyPlan(updatedPlan)
            }
        }
    }
}
