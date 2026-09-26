package com.examprep.android.feature.revision

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examprep.domain.engine.RevisionEngine
import com.examprep.domain.model.RevisionRecord
import com.examprep.domain.repository.RevisionRepository
import com.examprep.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface RevisionUiState {
    data object Loading : RevisionUiState
    data class Success(
        val dueRevisions: List<RevisionRecord>,
        val upcomingRevisions: List<RevisionRecord>,
        val totalCompletedRevisions: Int
    ) : RevisionUiState
    data class Error(val message: String) : RevisionUiState
}

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class RevisionViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val revisionRepository: RevisionRepository,
    private val revisionEngine: RevisionEngine
) : ViewModel() {

    val uiState: StateFlow<RevisionUiState> = userProfileRepository.getUserProfile()
        .flatMapLatest { profile ->
            val userId = profile?.id ?: "user_default"
            combine(
                revisionRepository.getDueRevisions(userId),
                revisionRepository.getUpcomingRevisions(userId, daysAhead = 14)
            ) { due, upcoming ->
                RevisionUiState.Success(
                    dueRevisions = due,
                    upcomingRevisions = upcoming,
                    totalCompletedRevisions = 12
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RevisionUiState.Loading
        )

    fun completeRevision(record: RevisionRecord, score: Float = 85f) {
        viewModelScope.launch {
            revisionRepository.markRevisionComplete(record.id, score)
            val updated = revisionEngine.computeNextRevision(record, score)
            if (updated.nextRevisionDateEpoch != null) {
                revisionRepository.saveRevisionRecord(updated)
            }
        }
    }
}
