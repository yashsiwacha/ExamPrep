package com.examprep.android.feature.performance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examprep.domain.model.*
import com.examprep.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class SubjectMetric(
    val subjectName: String,
    val accuracy: Float,
    val questionsAttempted: Int,
    val color: Long
)

sealed interface PerformanceUiState {
    data object Loading : PerformanceUiState
    data class Success(
        val overallAccuracy: Float,
        val totalQuestionsAttempted: Int,
        val totalStudyMinutes: Int,
        val studyStreakDays: Int,
        val subjectMetrics: List<SubjectMetric>,
        val weakTopics: List<MasteryRecord>,
        val strongTopics: List<MasteryRecord>
    ) : PerformanceUiState
    data class Error(val message: String) : PerformanceUiState
}

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class PerformanceViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val quizRepository: QuizRepository,
    private val masteryRepository: MasteryRepository,
    private val studySessionRepository: StudySessionRepository,
    private val examRepository: ExamRepository
) : ViewModel() {

    val uiState: StateFlow<PerformanceUiState> = userProfileRepository.getUserProfile()
        .flatMapLatest { profile ->
            val userId = profile?.id ?: "user_default"
            val examId = profile?.selectedExamId ?: "exam_jee_main"

            combine(
                quizRepository.getAttemptsForUser(userId),
                masteryRepository.getMasteryRecords(userId),
                examRepository.getSubjectsForExam(examId),
                studySessionRepository.getRecentSessions(userId, 50)
            ) { attempts, mastery, subjects, recentSessions ->
                val allQuestionAttempts = attempts.flatMap { it.questionAttempts }
                val totalAttempted = allQuestionAttempts.size
                val correct = allQuestionAttempts.count { it.isCorrect }
                val overallAcc = if (totalAttempted > 0) (correct.toFloat() / totalAttempted) * 100f else 0f

                val totalMinutes = recentSessions.filter { it.outcome == SessionOutcome.COMPLETED }.sumOf { it.durationMinutes }

                val subjectMetrics = subjects.map { subject ->
                    val subjectTopicIds = subject.chapters.flatMap { it.topics }.map { it.id }.toSet()
                    val topicMastery = mastery.filter { it.topicId in subjectTopicIds }
                    val avgAccuracy = if (topicMastery.isNotEmpty()) {
                        topicMastery.map { it.quizAccuracy }.average().toFloat()
                    } else {
                        75f
                    }
                    SubjectMetric(
                        subjectName = subject.name,
                        accuracy = avgAccuracy,
                        questionsAttempted = topicMastery.sumOf { it.totalAttempts },
                        color = subject.color
                    )
                }

                val weak = mastery.filter { it.quizAccuracy in 1f..49f || it.masteryState == MasteryState.REVISION_DUE }
                val strong = mastery.filter { it.quizAccuracy >= 80f || it.masteryState == MasteryState.MASTERED }

                PerformanceUiState.Success(
                    overallAccuracy = overallAcc,
                    totalQuestionsAttempted = totalAttempted,
                    totalStudyMinutes = totalMinutes,
                    studyStreakDays = if (recentSessions.isNotEmpty()) 1 else 0,
                    subjectMetrics = subjectMetrics,
                    weakTopics = weak,
                    strongTopics = strong
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PerformanceUiState.Loading
        )
}
