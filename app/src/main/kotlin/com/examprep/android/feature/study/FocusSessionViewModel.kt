package com.examprep.android.feature.study

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examprep.domain.model.*
import com.examprep.domain.repository.StudyPlanRepository
import com.examprep.domain.repository.StudySessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

enum class TimerStatus {
    INITIAL,
    RUNNING,
    PAUSED,
    COMPLETED
}

data class FocusSessionUiState(
    val taskId: String,
    val taskTitle: String = "Kinematics & Dynamics Problem Solving",
    val subjectName: String = "Physics",
    val totalSeconds: Int = 25 * 60,
    val remainingSeconds: Int = 25 * 60,
    val timerStatus: TimerStatus = TimerStatus.INITIAL,
    val isBreak: Boolean = false,
    val completedSessionsCount: Int = 0
)

@HiltViewModel
class FocusSessionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val studyPlanRepository: StudyPlanRepository,
    private val studySessionRepository: StudySessionRepository
) : ViewModel() {

    private val taskId: String = savedStateHandle.get<String>("taskId") ?: "task_sample"
    private val _uiState = MutableStateFlow(FocusSessionUiState(taskId = taskId))
    val uiState: StateFlow<FocusSessionUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var sessionStartTime: Long = 0L

    init {
        loadTaskDetails()
    }

    private fun loadTaskDetails() {
        viewModelScope.launch {
            val task = studyPlanRepository.getTaskById(taskId)
            if (task != null) {
                val durationSecs = task.estimatedMinutes * 60
                _uiState.update {
                    it.copy(
                        taskTitle = task.notes ?: "Study Session for Task",
                        totalSeconds = durationSecs,
                        remainingSeconds = durationSecs
                    )
                }
            }
        }
    }

    fun startTimer() {
        if (_uiState.value.timerStatus == TimerStatus.RUNNING) return

        if (_uiState.value.timerStatus == TimerStatus.INITIAL) {
            sessionStartTime = System.currentTimeMillis()
        }

        _uiState.update { it.copy(timerStatus = TimerStatus.RUNNING) }

        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingSeconds > 0 && _uiState.value.timerStatus == TimerStatus.RUNNING) {
                delay(1000L)
                _uiState.update { it.copy(remainingSeconds = it.remainingSeconds - 1) }
            }

            if (_uiState.value.remainingSeconds <= 0) {
                finishSession(SessionOutcome.COMPLETED)
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(timerStatus = TimerStatus.PAUSED) }
    }

    fun resetTimer() {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                remainingSeconds = it.totalSeconds,
                timerStatus = TimerStatus.INITIAL
            )
        }
    }

    fun completeSessionEarly() {
        finishSession(SessionOutcome.PARTIALLY_COMPLETED)
    }

    private var hasFinishedSession = false

    private fun finishSession(outcome: SessionOutcome) {
        if (hasFinishedSession) return
        hasFinishedSession = true

        timerJob?.cancel()
        _uiState.update { it.copy(timerStatus = TimerStatus.COMPLETED) }

        val elapsedMinutes = ((_uiState.value.totalSeconds - _uiState.value.remainingSeconds) / 60)
            .coerceAtLeast(1)
        val sessionId = java.util.UUID.randomUUID().toString()

        viewModelScope.launch {
            try {
                val effectiveStart = if (sessionStartTime > 0L) {
                    sessionStartTime
                } else {
                    System.currentTimeMillis() - (elapsedMinutes * 60_000L)
                }
                val session = StudySession(
                    id = sessionId,
                    taskId = taskId,
                    startTimeEpoch = effectiveStart,
                    endTimeEpoch = System.currentTimeMillis(),
                    durationMinutes = elapsedMinutes,
                    targetDurationMinutes = _uiState.value.totalSeconds / 60,
                    outcome = outcome,
                    notes = "Completed focus session — ${_uiState.value.taskTitle}",
                    createdAt = System.currentTimeMillis()
                )
                studySessionRepository.startSession(session)
                studySessionRepository.completeSession(sessionId, outcome, elapsedMinutes)
                studyPlanRepository.updateTaskStatus(
                    taskId,
                    if (outcome == SessionOutcome.COMPLETED) TaskStatus.COMPLETED else TaskStatus.IN_PROGRESS,
                    actualMinutes = elapsedMinutes
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
