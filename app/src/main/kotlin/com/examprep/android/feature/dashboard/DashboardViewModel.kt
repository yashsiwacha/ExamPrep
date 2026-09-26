package com.examprep.android.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examprep.domain.model.*
import com.examprep.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Success(
        val studentName: String,
        val examName: String,
        val daysRemaining: Int,
        val todayMinutesStudied: Int,
        val targetDailyMinutes: Int,
        val currentTask: StudyTask?,
        val todaysTasks: List<StudyTask>,
        val activeRecommendations: List<Recommendation>,
        val syllabusProgressPercentage: Float,
        // FIX B2: These now come from real DB data, not hardcoded values
        val quizAccuracy: Float,
        val studyStreakDays: Int,
        val masteredTopicsCount: Int
    ) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val examRepository: ExamRepository,
    private val studyPlanRepository: StudyPlanRepository,
    private val studySessionRepository: StudySessionRepository,
    private val recommendationRepository: RecommendationRepository,
    private val masteryRepository: MasteryRepository,
    private val quizRepository: QuizRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = userProfileRepository.getUserProfile()
        .flatMapLatest { profile ->
            if (profile == null) {
                flowOf(
                    DashboardUiState.Success(
                        studentName = "Aspirant",
                        examName = "Competitive Exam",
                        daysRemaining = 120,
                        todayMinutesStudied = 0,
                        targetDailyMinutes = 240,
                        currentTask = null,
                        todaysTasks = emptyList(),
                        activeRecommendations = emptyList(),
                        syllabusProgressPercentage = 0f,
                        quizAccuracy = 0f,
                        studyStreakDays = 0,
                        masteredTopicsCount = 0
                    )
                )
            } else {
                // FIX B1: Replace non-reactive flow{} with a proper reactive stream.
                // studySessionRepository now exposes getRecentSessions() as a Flow,
                // which re-emits whenever a new session is completed.
                combine(
                    studyPlanRepository.getTodaysTasks(profile.id),
                    recommendationRepository.getActiveRecommendations(profile.id),
                    masteryRepository.getMasteryRecords(profile.id),
                    studySessionRepository.getRecentSessions(profile.id, limit = 50), // reactive stream
                    quizRepository.getAttemptsForUser(profile.id) // reactive stream
                ) { tasks, recs, mastery, recentSessions, quizAttempts ->
                    val exam = profile.selectedExamId?.let { examRepository.getExamById(it) }
                    val examDate = profile.examDate
                        ?: (System.currentTimeMillis() + 120L * 86_400_000L)
                    val daysRemaining = ((examDate - System.currentTimeMillis()) / 86_400_000L)
                        .toInt().coerceAtLeast(0)

                    // FIX B1: Compute today's minutes reactively from the sessions stream
                    val startOfDay = run {
                        val now = System.currentTimeMillis()
                        now - (now % 86_400_000L)
                    }
                    val todayMinutes = recentSessions
                        .filter { it.startTimeEpoch >= startOfDay && it.outcome == SessionOutcome.COMPLETED }
                        .sumOf { it.durationMinutes }

                    // FIX B2: Compute quiz accuracy from real data
                    val allAttempts = quizAttempts.flatMap { it.questionAttempts }
                    val totalAttempted = allAttempts.size
                    val correctCount = allAttempts.count { it.isCorrect }
                    val accuracy = if (totalAttempted > 0) (correctCount.toFloat() / totalAttempted) * 100f else 0f

                    val masteredCount = mastery.count { it.masteryState == MasteryState.MASTERED }
                    val progressPercent = if (mastery.isNotEmpty()) {
                        (masteredCount.toFloat() / mastery.size) * 100f
                    } else 0f

                    // FIX B2: Compute streak from session history
                    val streakDays = computeStreak(recentSessions)

                    DashboardUiState.Success(
                        studentName = profile.name ?: "Aspirant",
                        examName = exam?.name ?: "Competitive Exam",
                        daysRemaining = daysRemaining,
                        todayMinutesStudied = todayMinutes,
                        targetDailyMinutes = (profile.dailyStudyHours * 60).toInt(),
                        currentTask = tasks.firstOrNull {
                            it.status == TaskStatus.PENDING || it.status == TaskStatus.IN_PROGRESS
                        },
                        todaysTasks = tasks,
                        activeRecommendations = recs,
                        syllabusProgressPercentage = progressPercent,
                        quizAccuracy = accuracy,
                        studyStreakDays = streakDays,
                        masteredTopicsCount = masteredCount
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardUiState.Loading
        )

    fun markTaskComplete(taskId: String) {
        viewModelScope.launch {
            studyPlanRepository.updateTaskStatus(taskId, TaskStatus.COMPLETED, actualMinutes = 50)
        }
    }

    fun dismissRecommendation(recommendationId: String) {
        viewModelScope.launch {
            recommendationRepository.dismissRecommendation(recommendationId)
        }
    }

    /**
     * Computes consecutive daily study streak from session history.
     * A day "counts" if at least one COMPLETED session exists for that day.
     */
    private fun computeStreak(sessions: List<StudySession>): Int {
        if (sessions.isEmpty()) return 0
        val msPerDay = 86_400_000L
        val today = System.currentTimeMillis() / msPerDay
        val daysWithSessions = sessions
            .filter { it.outcome == SessionOutcome.COMPLETED }
            .map { it.startTimeEpoch / msPerDay }
            .toSortedSet()

        var streak = 0
        var checkDay = today
        while (daysWithSessions.contains(checkDay)) {
            streak++
            checkDay--
        }
        return streak
    }
}
