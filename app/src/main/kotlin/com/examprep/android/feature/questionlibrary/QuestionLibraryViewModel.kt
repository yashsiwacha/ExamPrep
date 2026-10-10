package com.examprep.android.feature.questionlibrary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examprep.data.local.entity.QuestionPaperDownloadEntity
import com.examprep.data.local.entity.QuestionPaperMetaEntity
import com.examprep.data.repository.DownloadResult
import com.examprep.data.repository.QuestionBankRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ─────────────────────────────────────────────────────────────────────────────
// STATE
// ─────────────────────────────────────────────────────────────────────────────

data class QuestionLibraryUiState(
    val papers: List<QuestionPaperMetaEntity> = emptyList(),
    val downloads: Map<String, QuestionPaperDownloadEntity> = emptyMap(),
    /** paperId → download progress 0–100 */
    val downloadProgress: Map<String, Int> = emptyMap(),
    /** paperId → error message */
    val downloadErrors: Map<String, String> = emptyMap(),
    val selectedExamFilter: String? = null,     // null = show all
    val isLoading: Boolean = true
)

// ─────────────────────────────────────────────────────────────────────────────
// VIEWMODEL
// ─────────────────────────────────────────────────────────────────────────────

@HiltViewModel
class QuestionLibraryViewModel @Inject constructor(
    private val questionBankRepository: QuestionBankRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuestionLibraryUiState())
    val uiState: StateFlow<QuestionLibraryUiState> = _uiState.asStateFlow()

    init {
        // Seed catalog then observe
        viewModelScope.launch {
            questionBankRepository.seedCatalogIfEmpty()
        }

        // Observe papers
        viewModelScope.launch {
            questionBankRepository.observeAllPapers().collect { papers ->
                _uiState.update { it.copy(papers = papers, isLoading = false) }
            }
        }

        // Observe downloads as a map for O(1) lookup in UI
        viewModelScope.launch {
            questionBankRepository.observeDownloadedPapers().collect { list ->
                _uiState.update { state ->
                    state.copy(downloads = list.associateBy { it.paperId })
                }
            }
        }
    }

    fun setExamFilter(examId: String?) {
        _uiState.update { it.copy(selectedExamFilter = examId) }
    }

    fun downloadPaper(paperId: String) {
        // Guard: already downloading
        if (_uiState.value.downloadProgress.containsKey(paperId)) return

        _uiState.update { state ->
            state.copy(
                downloadProgress = state.downloadProgress + (paperId to 0),
                downloadErrors = state.downloadErrors - paperId
            )
        }

        viewModelScope.launch {
            val result = questionBankRepository.downloadPaper(paperId) { progress ->
                _uiState.update { state ->
                    state.copy(downloadProgress = state.downloadProgress + (paperId to progress))
                }
            }

            _uiState.update { state ->
                when (result) {
                    is DownloadResult.Success,
                    is DownloadResult.AlreadyDownloaded -> state.copy(
                        downloadProgress = state.downloadProgress - paperId,
                        downloadErrors = state.downloadErrors - paperId
                    )
                    is DownloadResult.Error -> state.copy(
                        downloadProgress = state.downloadProgress - paperId,
                        downloadErrors = state.downloadErrors + (paperId to result.message)
                    )
                    else -> state
                }
            }
        }
    }

    fun deletePaper(paperId: String) {
        viewModelScope.launch {
            questionBankRepository.deletePaper(paperId)
        }
    }

    fun clearError(paperId: String) {
        _uiState.update { state ->
            state.copy(downloadErrors = state.downloadErrors - paperId)
        }
    }
}
