package com.examprep.android.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examprep.domain.engine.StudyPlanningEngine
import com.examprep.domain.model.*
import com.examprep.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed interface DiagnosticUiState {
    data object Loading : DiagnosticUiState
    data class Intro(
        val exam: Exam,
        val questionCount: Int,
        val estimatedMinutes: Int
    ) : DiagnosticUiState
    data class QuestionActive(
        val exam: Exam,
        val currentIndex: Int,
        val totalQuestions: Int,
        val currentQuestion: Question,
        val selectedOptionId: String? = null
    ) : DiagnosticUiState
    data object Submitting : DiagnosticUiState
    data class Completed(
        val totalQuestions: Int,
        val correctCount: Int,
        val accuracy: Float,
        val weakTopics: List<String>,
        val strongTopics: List<String>
    ) : DiagnosticUiState
    data class Error(val message: String) : DiagnosticUiState
}

@HiltViewModel
class DiagnosticViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val examRepository: ExamRepository,
    private val questionRepository: QuestionRepository,
    private val masteryRepository: MasteryRepository,
    private val studyPlanRepository: StudyPlanRepository,
    private val studyPlanningEngine: StudyPlanningEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow<DiagnosticUiState>(DiagnosticUiState.Loading)
    val uiState: StateFlow<DiagnosticUiState> = _uiState.asStateFlow()

    private var currentExam: Exam? = null
    private var diagnosticQuestions: List<Question> = emptyList()
    private val userAnswers = mutableMapOf<String, String?>()
    private var activeQuestionIndex = 0

    init {
        loadDiagnostic()
    }

    private fun loadDiagnostic() {
        viewModelScope.launch {
            try {
                val profile = userProfileRepository.getUserProfileOnce()
                val examId = profile?.selectedExamId ?: "exam_jee_main"
                val exam = examRepository.getExamById(examId) ?: examRepository.getAllExams().firstOrNull()?.firstOrNull()
                if (exam == null) {
                    _uiState.value = DiagnosticUiState.Error("No exam syllabus found.")
                    return@launch
                }
                currentExam = exam

                val questions = questionRepository.getRandomQuestions(
                    examId = exam.id,
                    count = 5
                )

                diagnosticQuestions = questions
                if (questions.isNotEmpty()) {
                    _uiState.value = DiagnosticUiState.Intro(
                        exam = exam,
                        questionCount = questions.size,
                        estimatedMinutes = (questions.size * 1.5f).toInt().coerceAtLeast(3)
                    )
                } else {
                    _uiState.value = DiagnosticUiState.Completed(
                        totalQuestions = 0,
                        correctCount = 0,
                        accuracy = 0f,
                        weakTopics = emptyList(),
                        strongTopics = emptyList()
                    )
                }
            } catch (e: Exception) {
                _uiState.value = DiagnosticUiState.Error(e.message ?: "Failed to load diagnostic assessment.")
            }
        }
    }

    fun startAssessment() {
        val exam = currentExam ?: return
        if (diagnosticQuestions.isEmpty()) return
        activeQuestionIndex = 0
        userAnswers.clear()
        _uiState.value = DiagnosticUiState.QuestionActive(
            exam = exam,
            currentIndex = 0,
            totalQuestions = diagnosticQuestions.size,
            currentQuestion = diagnosticQuestions[0],
            selectedOptionId = null
        )
    }

    fun selectOption(optionId: String) {
        val state = _uiState.value
        if (state is DiagnosticUiState.QuestionActive) {
            userAnswers[state.currentQuestion.id] = optionId
            _uiState.value = state.copy(selectedOptionId = optionId)
        }
    }

    fun nextQuestionOrSubmit() {
        val state = _uiState.value
        if (state is DiagnosticUiState.QuestionActive) {
            if (activeQuestionIndex + 1 < diagnosticQuestions.size) {
                activeQuestionIndex++
                val nextQ = diagnosticQuestions[activeQuestionIndex]
                _uiState.value = DiagnosticUiState.QuestionActive(
                    exam = state.exam,
                    currentIndex = activeQuestionIndex,
                    totalQuestions = diagnosticQuestions.size,
                    currentQuestion = nextQ,
                    selectedOptionId = userAnswers[nextQ.id]
                )
            } else {
                finishAssessment()
            }
        }
    }

    private fun finishAssessment() {
        viewModelScope.launch {
            _uiState.value = DiagnosticUiState.Submitting

            val profile = userProfileRepository.getUserProfileOnce()
            val exam = currentExam
            if (profile == null || exam == null) {
                _uiState.value = DiagnosticUiState.Error("Profile or exam not found.")
                return@launch
            }

            var correctCount = 0
            val weakTopicNames = mutableListOf<String>()
            val strongTopicNames = mutableListOf<String>()
            val baselineMasteryList = mutableListOf<MasteryRecord>()

            val topicMap = exam.subjects.flatMap { it.chapters }.flatMap { it.topics }.associateBy { it.id }

            diagnosticQuestions.forEach { question ->
                val selected = userAnswers[question.id]
                val isCorrect = selected != null && selected == question.correctOptionId
                if (isCorrect) correctCount++

                val topicName = topicMap[question.topicId]?.name ?: "Topic"
                if (isCorrect) {
                    strongTopicNames.add(topicName)
                } else {
                    weakTopicNames.add(topicName)
                }

                val record = MasteryRecord(
                    id = UUID.randomUUID().toString(),
                    topicId = question.topicId,
                    userId = profile.id,
                    masteryState = if (isCorrect) MasteryState.LEARNED else MasteryState.IN_PROGRESS,
                    quizAccuracy = if (isCorrect) 100f else 0f,
                    totalAttempts = 1,
                    correctAttempts = if (isCorrect) 1 else 0,
                    lastPracticedAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                baselineMasteryList.add(record)
                masteryRepository.saveMasteryRecord(record)
            }

            // Generate personalized plan prioritized by baseline diagnostic results
            val plan = studyPlanningEngine.generatePlan(profile, exam, baselineMasteryList)
            studyPlanRepository.saveStudyPlan(plan)

            // Mark diagnostic completed in user profile
            val updatedProfile = profile.copy(
                diagnosticCompleted = true,
                onboardingCompleted = true,
                updatedAt = System.currentTimeMillis()
            )
            userProfileRepository.saveUserProfile(updatedProfile)

            val accuracy = if (diagnosticQuestions.isNotEmpty()) {
                (correctCount.toFloat() / diagnosticQuestions.size) * 100f
            } else 0f

            _uiState.value = DiagnosticUiState.Completed(
                totalQuestions = diagnosticQuestions.size,
                correctCount = correctCount,
                accuracy = accuracy,
                weakTopics = weakTopicNames.distinct(),
                strongTopics = strongTopicNames.distinct()
            )
        }
    }

    fun skipAssessment(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val profile = userProfileRepository.getUserProfileOnce()
            val exam = currentExam
            if (profile != null && exam != null) {
                val plan = studyPlanningEngine.generatePlan(profile, exam, emptyList())
                studyPlanRepository.saveStudyPlan(plan)
                val updatedProfile = profile.copy(
                    onboardingCompleted = true,
                    updatedAt = System.currentTimeMillis()
                )
                userProfileRepository.saveUserProfile(updatedProfile)
            }
            onSuccess()
        }
    }
}
