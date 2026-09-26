package com.examprep.android.feature.mistakes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examprep.domain.model.MistakeItem
import com.examprep.domain.model.MistakeMasteryStatus
import com.examprep.domain.model.MistakeReason
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class MistakeVaultUiState(
    val mistakes: List<MistakeItem> = emptyList(),
    val filteredMistakes: List<MistakeItem> = emptyList(),
    val selectedSubject: String = "All",
    val selectedStatus: String = "All",
    val searchQuery: String = "",
    val totalCount: Int = 0,
    val activeCount: Int = 0,
    val masteredCount: Int = 0,
    val masteryPercentage: Float = 0f,
    val activeSprintQuestion: MistakeItem? = null,
    val sprintIndex: Int = 0,
    val sprintTotal: Int = 0,
    val sprintSelectedOption: String? = null,
    val isSprintAnswerChecked: Boolean = false,
    val isSprintFinished: Boolean = false
)

@HiltViewModel
class MistakeVaultViewModel @Inject constructor(
    private val store: MistakeVaultStore
) : ViewModel() {

    private val _selectedSubject = MutableStateFlow("All")
    private val _selectedStatus = MutableStateFlow("All")
    private val _searchQuery = MutableStateFlow("")

    private val _sprintQuestions = MutableStateFlow<List<MistakeItem>>(emptyList())
    private val _sprintIndex = MutableStateFlow(0)
    private val _sprintSelectedOption = MutableStateFlow<String?>(null)
    private val _isSprintAnswerChecked = MutableStateFlow(false)
    private val _isSprintFinished = MutableStateFlow(false)

    val uiState: StateFlow<MistakeVaultUiState> = combine(
        store.mistakes,
        _selectedSubject,
        _selectedStatus,
        _searchQuery,
        _sprintQuestions,
        _sprintIndex,
        _sprintSelectedOption,
        _isSprintAnswerChecked,
        _isSprintFinished
    ) { params ->
        @Suppress("UNCHECKED_CAST")
        val allMistakes = params[0] as List<MistakeItem>
        val subject = params[1] as String
        val status = params[2] as String
        val query = params[3] as String
        @Suppress("UNCHECKED_CAST")
        val sprintList = params[4] as List<MistakeItem>
        val sIndex = params[5] as Int
        val sOption = params[6] as String?
        val isChecked = params[7] as Boolean
        val isFinished = params[8] as Boolean

        val filtered = allMistakes.filter { item ->
            val matchSubject = subject == "All" || item.subject.equals(subject, ignoreCase = true)
            val matchStatus = when (status) {
                "Active" -> item.masteryStatus == MistakeMasteryStatus.ACTIVE
                "Reviewing" -> item.masteryStatus == MistakeMasteryStatus.REVIEWING
                "Mastered" -> item.masteryStatus == MistakeMasteryStatus.MASTERED
                else -> true
            }
            val matchQuery = query.isBlank() ||
                item.questionText.contains(query, ignoreCase = true) ||
                item.chapterName.contains(query, ignoreCase = true) ||
                item.subject.contains(query, ignoreCase = true)

            matchSubject && matchStatus && matchQuery
        }

        val total = allMistakes.size
        val mastered = allMistakes.count { it.masteryStatus == MistakeMasteryStatus.MASTERED }
        val active = total - mastered
        val masteryPct = if (total > 0) (mastered.toFloat() / total) * 100f else 0f

        val sprintQ = if (sprintList.isNotEmpty() && sIndex in sprintList.indices) sprintList[sIndex] else null

        MistakeVaultUiState(
            mistakes = allMistakes,
            filteredMistakes = filtered,
            selectedSubject = subject,
            selectedStatus = status,
            searchQuery = query,
            totalCount = total,
            activeCount = active,
            masteredCount = mastered,
            masteryPercentage = masteryPct,
            activeSprintQuestion = sprintQ,
            sprintIndex = sIndex,
            sprintTotal = sprintList.size,
            sprintSelectedOption = sOption,
            isSprintAnswerChecked = isChecked,
            isSprintFinished = isFinished
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MistakeVaultUiState()
    )

    fun setSubject(subject: String) {
        _selectedSubject.value = subject
    }

    fun setStatus(status: String) {
        _selectedStatus.value = status
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun markPracticed(mistakeId: String, isCorrect: Boolean) {
        store.markPracticed(mistakeId, isCorrect)
    }

    fun updateReason(mistakeId: String, reason: MistakeReason) {
        store.updateReasonTag(mistakeId, reason)
    }

    fun startSprint() {
        val activeList = store.mistakes.value.filter { it.masteryStatus != MistakeMasteryStatus.MASTERED }
        _sprintQuestions.value = if (activeList.isNotEmpty()) activeList.shuffled() else store.mistakes.value.shuffled()
        _sprintIndex.value = 0
        _sprintSelectedOption.value = null
        _isSprintAnswerChecked.value = false
        _isSprintFinished.value = false
    }

    fun selectSprintOption(option: String) {
        if (!_isSprintAnswerChecked.value) {
            _sprintSelectedOption.value = option
        }
    }

    fun checkSprintAnswer() {
        val currentQ = uiState.value.activeSprintQuestion ?: return
        val selected = _sprintSelectedOption.value ?: return
        _isSprintAnswerChecked.value = true
        val isCorrect = selected == currentQ.correctAnswer
        store.markPracticed(currentQ.id, isCorrect)
    }

    fun nextSprintQuestion() {
        val nextIdx = _sprintIndex.value + 1
        if (nextIdx < _sprintQuestions.value.size) {
            _sprintIndex.value = nextIdx
            _sprintSelectedOption.value = null
            _isSprintAnswerChecked.value = false
        } else {
            _isSprintFinished.value = true
        }
    }

    fun closeSprint() {
        _sprintQuestions.value = emptyList()
        _sprintIndex.value = 0
        _sprintSelectedOption.value = null
        _isSprintAnswerChecked.value = false
        _isSprintFinished.value = false
    }
}
