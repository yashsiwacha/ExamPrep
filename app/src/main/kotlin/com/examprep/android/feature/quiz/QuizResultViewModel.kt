package com.examprep.android.feature.quiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examprep.domain.model.*
import com.examprep.domain.repository.QuestionRepository
import com.examprep.domain.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuestionResultItem(
    val question: Question,
    val attempt: QuestionAttempt
)

sealed interface QuizResultUiState {
    data object Loading : QuizResultUiState
    data class Success(
        val attempt: QuizAttempt,
        val questionResults: List<QuestionResultItem>,
        val correctCount: Int,
        val incorrectCount: Int,
        val skippedCount: Int,
        val totalTimeSecs: Long
    ) : QuizResultUiState
    data class Error(val message: String) : QuizResultUiState
}

@HiltViewModel
class QuizResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val quizRepository: QuizRepository,
    private val questionRepository: QuestionRepository
) : ViewModel() {

    private val attemptId: String = savedStateHandle.get<String>("attemptId") ?: ""
    private val _uiState = MutableStateFlow<QuizResultUiState>(QuizResultUiState.Loading)
    val uiState: StateFlow<QuizResultUiState> = _uiState.asStateFlow()

    init {
        loadResults()
    }

    private fun loadResults() {
        viewModelScope.launch {
            try {
                val attempt = quizRepository.getQuizAttemptById(attemptId)
                if (attempt == null) {
                    _uiState.value = QuizResultUiState.Error("Attempt results not found")
                    return@launch
                }

                quizRepository.getQuestionAttemptsForQuizAttempt(attemptId).collectLatest { dbAttempts ->
                    val attempts = if (dbAttempts.isNotEmpty()) dbAttempts else attempt.questionAttempts
                    val resultItems = attempts.mapNotNull { qa ->
                        val question = questionRepository.getQuestionById(qa.questionId)
                        if (question != null) QuestionResultItem(question, qa) else null
                    }

                    val correct = attempts.count { it.isCorrect }
                    val incorrect = attempts.count { !it.isCorrect && !it.isSkipped }
                    val skipped = attempts.count { it.isSkipped }
                    val endTime = attempt.endTimeEpoch
                    val totalTime = if (endTime != null) {
                        (endTime - attempt.startTimeEpoch) / 1000
                    } else 0L

                    _uiState.value = QuizResultUiState.Success(
                        attempt = attempt,
                        questionResults = resultItems,
                        correctCount = correct,
                        incorrectCount = incorrect,
                        skippedCount = skipped,
                        totalTimeSecs = totalTime
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = QuizResultUiState.Error("Failed to load result: ${e.message}")
            }
        }
    }
}
