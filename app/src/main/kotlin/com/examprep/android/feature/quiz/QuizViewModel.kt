package com.examprep.android.feature.quiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examprep.domain.engine.MasteryEngine
import com.examprep.domain.engine.QuizScoringEngine
import com.examprep.domain.model.*
import com.examprep.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class QuizUiState(
    val quizId: String,
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedOptions: Map<Int, String> = emptyMap(), // questionIndex -> optionId
    val remainingSeconds: Int = 600,
    val isSubmitting: Boolean = false,
    val submittedAttemptId: String? = null,
    val isLoading: Boolean = true
) {
    val currentQuestion: Question?
        get() = questions.getOrNull(currentQuestionIndex)

    val isLastQuestion: Boolean
        get() = currentQuestionIndex == questions.size - 1

    val answeredCount: Int
        get() = selectedOptions.size
}

@HiltViewModel
class QuizViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val questionRepository: QuestionRepository,
    private val quizRepository: QuizRepository,
    private val userProfileRepository: UserProfileRepository,
    private val quizScoringEngine: QuizScoringEngine,
    private val masteryEngine: MasteryEngine,
    private val masteryRepository: MasteryRepository
) : ViewModel() {

    private val quizId: String = savedStateHandle.get<String>("quizId") ?: "quick_quiz"
    private val _uiState = MutableStateFlow(QuizUiState(quizId = quizId))
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private val quizStartTime: Long = System.currentTimeMillis()
    // FIX B4: Track actual per-question start times to measure real time taken
    private val questionStartTimes = mutableMapOf<Int, Long>()

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            val profile = userProfileRepository.getUserProfileOnce()
            val examId = profile?.selectedExamId ?: "exam_jee_main"
            val topicId = when {
                quizId.startsWith("quiz_") && quizId != "quick_quiz" -> quizId.removePrefix("quiz_")
                quizId.startsWith("top_") -> quizId
                else -> null
            }
            val questions = questionRepository.getRandomQuestions(
                examId = examId,
                topicId = topicId,
                count = 5
            )
            _uiState.update { it.copy(questions = questions, isLoading = false) }
            questionStartTimes[0] = System.currentTimeMillis()
            startTimer()
        }
    }

    /**
     * FIX P6: Use a precise ticker-based timer instead of a while-loop with delay.
     * A `tickerFlow` emits at fixed intervals independent of execution overhead,
     * preventing the cumulative drift that the while(delay) pattern causes.
     */
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            tickerFlow(intervalMs = 1000L)
                .takeWhile { _uiState.value.remainingSeconds > 0 }
                .collect {
                    _uiState.update { s -> s.copy(remainingSeconds = s.remainingSeconds - 1) }
                    if (_uiState.value.remainingSeconds <= 0) {
                        submitQuiz {}
                    }
                }
        }
    }

    fun selectOption(optionId: String) {
        val currentIndex = _uiState.value.currentQuestionIndex
        _uiState.update {
            val updated = it.selectedOptions.toMutableMap()
            updated[currentIndex] = optionId
            it.copy(selectedOptions = updated)
        }
    }

    fun clearOption() {
        val currentIndex = _uiState.value.currentQuestionIndex
        _uiState.update {
            val updated = it.selectedOptions.toMutableMap()
            updated.remove(currentIndex)
            it.copy(selectedOptions = updated)
        }
    }

    fun goToQuestion(index: Int) {
        if (index in 0 until _uiState.value.questions.size) {
            questionStartTimes[index] = System.currentTimeMillis()
            _uiState.update { it.copy(currentQuestionIndex = index) }
        }
    }

    fun nextQuestion() {
        val nextIndex = _uiState.value.currentQuestionIndex + 1
        if (nextIndex < _uiState.value.questions.size) {
            // FIX B4: Record exact start time when moving to next question
            questionStartTimes[nextIndex] = System.currentTimeMillis()
            _uiState.update { it.copy(currentQuestionIndex = nextIndex) }
        }
    }

    fun previousQuestion() {
        val prevIndex = _uiState.value.currentQuestionIndex - 1
        if (prevIndex >= 0) {
            _uiState.update { it.copy(currentQuestionIndex = prevIndex) }
        }
    }

    fun submitQuiz(onComplete: ((attemptId: String) -> Unit)? = null) {
        timerJob?.cancel()
        if (_uiState.value.isSubmitting) return
        _uiState.update { it.copy(isSubmitting = true) }

        viewModelScope.launch {
            val state = _uiState.value
            val profile = userProfileRepository.getUserProfileOnce()
            val userId = profile?.id ?: "user_default"
            val attemptId = UUID.randomUUID().toString()
            val now = System.currentTimeMillis()

            val questionAttempts = state.questions.mapIndexed { index, question ->
                val selectedOption = state.selectedOptions[index]
                val isCorrect = selectedOption == question.correctOptionId
                val isSkipped = selectedOption == null

                // FIX B4: Use real measured time per question instead of hardcoded 30000ms
                val questionStart = questionStartTimes[index] ?: quizStartTime
                val questionEnd = questionStartTimes[index + 1] ?: now
                val timeTaken = (questionEnd - questionStart).coerceAtLeast(0L)

                val marks = when {
                    isSkipped -> 0f
                    isCorrect -> question.marks
                    else -> question.negativeMarks
                }

                QuestionAttempt(
                    id = UUID.randomUUID().toString(),
                    quizAttemptId = attemptId,
                    questionId = question.id,
                    selectedOptionId = selectedOption,
                    isCorrect = isCorrect,
                    isSkipped = isSkipped,
                    timeTakenMillis = timeTaken,
                    marksEarned = marks
                )
            }

            val quizAttempt = QuizAttempt(
                id = attemptId,
                quizId = quizId,
                userId = userId,
                startTimeEpoch = quizStartTime,
                endTimeEpoch = now,
                questionAttempts = questionAttempts,
                isCompleted = true
            )

            try {
                val dummyQuiz = Quiz(
                    id = quizId,
                    mode = QuizMode.QUICK_QUIZ,
                    examId = profile?.selectedExamId ?: "exam_jee_main",
                    topicId = null,
                    subjectId = null,
                    questionCount = state.questions.size,
                    durationMinutes = 10,
                    markingScheme = MarkingScheme(correctMarks = 4f, incorrectMarks = -1f, unattemptedMarks = 0f)
                )

                quizRepository.createQuiz(dummyQuiz)

                val scored = quizScoringEngine.scoreAttempt(quizAttempt, dummyQuiz)
                val finalAttempt = quizAttempt.copy(
                    score = scored.totalScore,
                    maxScore = scored.maxScore,
                    accuracy = scored.accuracy
                )

                quizRepository.saveQuizAttempt(finalAttempt)
                questionAttempts.forEach { qa ->
                    quizRepository.saveQuestionAttempt(qa)
                }

                // Update Mastery Records for attempted topics
                val attemptsByTopic = state.questions.zip(questionAttempts).groupBy { it.first.topicId }
                attemptsByTopic.forEach { (topicId, pairs) ->
                    val currentMastery = masteryRepository.getMasteryForTopic(userId, topicId)
                        ?: MasteryRecord(
                            id = UUID.randomUUID().toString(),
                            topicId = topicId,
                            userId = userId,
                            masteryState = MasteryState.NOT_STARTED
                        )
                    val topicAttempts = pairs.map { it.second }
                    val updatedMastery = masteryEngine.calculateMastery(currentMastery, topicAttempts)
                    masteryRepository.saveMasteryRecord(updatedMastery)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            _uiState.update { it.copy(isSubmitting = false, submittedAttemptId = attemptId) }
            onComplete?.invoke(attemptId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

/**
 * FIX P6: Precise ticker flow that emits at fixed wall-clock intervals.
 * Unlike while(delay()), this measures elapsed time and corrects for drift,
 * ensuring the timer stays accurate even when the coroutine is briefly suspended.
 */
private fun tickerFlow(intervalMs: Long): Flow<Unit> = flow {
    var nextTickAt = System.currentTimeMillis() + intervalMs
    while (true) {
        val now = System.currentTimeMillis()
        val sleepMs = (nextTickAt - now).coerceAtLeast(0L)
        delay(sleepMs)
        emit(Unit)
        nextTickAt += intervalMs
    }
}
