package com.examprep.android.feature.mocktest

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examprep.domain.model.MockQuestionStatus
import com.examprep.domain.model.MockTest
import com.examprep.domain.model.Question
import com.examprep.domain.repository.QuestionRepository
import com.examprep.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MockTestUiState(
    val isLoading: Boolean = true,
    val mockTest: MockTest? = null,
    val currentSectionIndex: Int = 0,
    val currentQuestionIndex: Int = 0,
    val userAnswers: Map<String, String> = emptyMap(),
    val markedForReview: Set<String> = emptySet(),
    val visitedQuestions: Set<String> = emptySet(),
    val remainingSeconds: Long = 180 * 60L,
    val isSubmitted: Boolean = false,
    val showSubmitDialog: Boolean = false
) {
    val currentSection get() = mockTest?.sections?.getOrNull(currentSectionIndex)
    val currentQuestion: Question? get() = currentSection?.questions?.getOrNull(currentQuestionIndex)

    fun getQuestionStatus(questionId: String): MockQuestionStatus {
        val isAnswered = userAnswers.containsKey(questionId)
        val isMarked = markedForReview.contains(questionId)
        val isVisited = visitedQuestions.contains(questionId)

        return when {
            isAnswered && isMarked -> MockQuestionStatus.ANSWERED_AND_MARKED_FOR_REVIEW
            isAnswered -> MockQuestionStatus.ANSWERED
            isMarked -> MockQuestionStatus.MARKED_FOR_REVIEW
            isVisited -> MockQuestionStatus.NOT_ANSWERED
            else -> MockQuestionStatus.NOT_VISITED
        }
    }

    val totalQuestionsCount: Int get() = mockTest?.sections?.sumOf { it.questions.size } ?: 0
    val answeredCount: Int get() = userAnswers.size
    val markedCount: Int get() = markedForReview.size
    val unattemptedCount: Int get() = totalQuestionsCount - answeredCount
}

@HiltViewModel
class MockTestViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val questionRepository: QuestionRepository,
    private val userProfileRepository: UserProfileRepository,
    private val mockTestStore: MockTestStore
) : ViewModel() {

    private val mockTestId: String = savedStateHandle["mockTestId"] ?: "mock_jee_all_india_1"

    private val _uiState = MutableStateFlow(MockTestUiState())
    val uiState: StateFlow<MockTestUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var totalDurationSeconds: Long = 180 * 60L

    init {
        loadMockTest()
    }

    private fun loadMockTest() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val profile = userProfileRepository.getUserProfileOnce()
            val examId = profile?.selectedExamId ?: "exam_jee_main"

            val questions = questionRepository.getRandomQuestions(
                examId = examId,
                topicId = null,
                subjectId = null,
                count = 30,
                difficulty = null
            )

            val mockTest = mockTestStore.generateStandardMockTest(examId, questions)
            totalDurationSeconds = mockTest.durationMinutes * 60L

            val firstQId = mockTest.sections.firstOrNull()?.questions?.firstOrNull()?.id
            val initialVisited = if (firstQId != null) setOf(firstQId) else emptySet()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    mockTest = mockTest,
                    remainingSeconds = totalDurationSeconds,
                    visitedQuestions = initialVisited
                )
            }

            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingSeconds > 0 && !_uiState.value.isSubmitted) {
                delay(1000L)
                _uiState.update {
                    val nextSec = it.remainingSeconds - 1
                    it.copy(remainingSeconds = nextSec)
                }
            }
        }
    }

    fun selectOption(questionId: String, optionId: String) {
        _uiState.update { current ->
            val updated = current.userAnswers.toMutableMap()
            updated[questionId] = optionId
            current.copy(userAnswers = updated)
        }
    }

    fun clearResponse(questionId: String) {
        _uiState.update { current ->
            val updated = current.userAnswers.toMutableMap()
            updated.remove(questionId)
            current.copy(userAnswers = updated)
        }
    }

    fun toggleMarkForReview(questionId: String) {
        _uiState.update { current ->
            val updated = current.markedForReview.toMutableSet()
            if (updated.contains(questionId)) {
                updated.remove(questionId)
            } else {
                updated.add(questionId)
            }
            current.copy(markedForReview = updated)
        }
    }

    fun selectSection(sectionIndex: Int) {
        _uiState.update { current ->
            val section = current.mockTest?.sections?.getOrNull(sectionIndex)
            val firstQId = section?.questions?.firstOrNull()?.id
            val updatedVisited = current.visitedQuestions.toMutableSet()
            if (firstQId != null) updatedVisited.add(firstQId)

            current.copy(
                currentSectionIndex = sectionIndex,
                currentQuestionIndex = 0,
                visitedQuestions = updatedVisited
            )
        }
    }

    fun jumpToQuestion(sectionIndex: Int, questionIndex: Int) {
        _uiState.update { current ->
            val section = current.mockTest?.sections?.getOrNull(sectionIndex)
            val qId = section?.questions?.getOrNull(questionIndex)?.id
            val updatedVisited = current.visitedQuestions.toMutableSet()
            if (qId != null) updatedVisited.add(qId)

            current.copy(
                currentSectionIndex = sectionIndex,
                currentQuestionIndex = questionIndex,
                visitedQuestions = updatedVisited
            )
        }
    }

    fun nextQuestion() {
        val state = _uiState.value
        val currentSection = state.currentSection ?: return
        if (state.currentQuestionIndex < currentSection.questions.size - 1) {
            jumpToQuestion(state.currentSectionIndex, state.currentQuestionIndex + 1)
        } else if (state.currentSectionIndex < (state.mockTest?.sections?.size ?: 0) - 1) {
            jumpToQuestion(state.currentSectionIndex + 1, 0)
        }
    }

    fun previousQuestion() {
        val state = _uiState.value
        if (state.currentQuestionIndex > 0) {
            jumpToQuestion(state.currentSectionIndex, state.currentQuestionIndex - 1)
        } else if (state.currentSectionIndex > 0) {
            val prevSection = state.mockTest?.sections?.getOrNull(state.currentSectionIndex - 1)
            val lastIndex = (prevSection?.questions?.size ?: 1) - 1
            jumpToQuestion(state.currentSectionIndex - 1, lastIndex.coerceAtLeast(0))
        }
    }

    fun showSubmitConfirmation(show: Boolean) {
        _uiState.update { it.copy(showSubmitDialog = show) }
    }

    fun submitTest(onComplete: (attemptId: String) -> Unit) {
        timerJob?.cancel()
        val state = _uiState.value
        val mockTest = state.mockTest ?: return

        val timeSpentSeconds = (totalDurationSeconds - state.remainingSeconds).coerceAtLeast(1L)
        val result = mockTestStore.evaluateTest(mockTest, state.userAnswers, timeSpentSeconds)

        _uiState.update { it.copy(isSubmitted = true, showSubmitDialog = false) }
        onComplete(result.attemptId)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
