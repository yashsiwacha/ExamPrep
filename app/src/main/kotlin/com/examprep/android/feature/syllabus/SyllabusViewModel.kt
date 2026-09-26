package com.examprep.android.feature.syllabus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examprep.domain.model.*
import com.examprep.domain.repository.ExamRepository
import com.examprep.domain.repository.MasteryRepository
import com.examprep.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SyllabusUiState {
    data object Loading : SyllabusUiState
    data class Success(
        val exam: Exam,
        val masteryMap: Map<String, MasteryRecord> = emptyMap(), // topicId -> MasteryRecord
        val overallMasteryPercentage: Float = 0f
    ) : SyllabusUiState
    data class Error(val message: String) : SyllabusUiState
}

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class SyllabusViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val examRepository: ExamRepository,
    private val masteryRepository: MasteryRepository
) : ViewModel() {

    val uiState: StateFlow<SyllabusUiState> = userProfileRepository.getUserProfile()
        .flatMapLatest { profile ->
            val examId = profile?.selectedExamId ?: "exam_jee_main"
            val userId = profile?.id ?: "user_default"

            combine(
                examRepository.getAllExams(),
                masteryRepository.getMasteryRecords(userId)
            ) { exams, masteryRecords ->
                val seedExams = com.examprep.data.local.seed.DatabaseSeedData.getInitialExams()
                val exam = exams.find { it.id == examId && it.subjects.isNotEmpty() }
                    ?: seedExams.find { it.id == examId }
                    ?: exams.find { it.subjects.isNotEmpty() }
                    ?: seedExams.first()
                val masteryMap = masteryRecords.associateBy { it.topicId }
                    val allTopics = exam.subjects.flatMap { s -> s.chapters.flatMap { c -> c.topics } }
                    val masteredCount = allTopics.count { topic ->
                        masteryMap[topic.id]?.masteryState == MasteryState.MASTERED
                    }
                    val percent = if (allTopics.isNotEmpty()) (masteredCount.toFloat() / allTopics.size) * 100f else 0f

                    SyllabusUiState.Success(
                        exam = exam,
                        masteryMap = masteryMap,
                        overallMasteryPercentage = percent
                    )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SyllabusUiState.Loading
        )

    fun markTopicLearned(topicId: String) {
        viewModelScope.launch {
            val profile = userProfileRepository.getUserProfileOnce()
            val userId = profile?.id ?: return@launch
            masteryRepository.updateMasteryState(userId, topicId, MasteryState.LEARNED)
        }
    }
}
