package com.examprep.android.feature.mocktest

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.examprep.domain.model.MockTestResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class MockTestResultUiState(
    val isLoading: Boolean = true,
    val result: MockTestResult? = null,
    val selectedFilter: SolutionFilter = SolutionFilter.ALL
)

enum class SolutionFilter {
    ALL,
    CORRECT,
    INCORRECT,
    UNATTEMPTED
}

@HiltViewModel
class MockTestResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mockTestStore: MockTestStore
) : ViewModel() {

    private val attemptId: String = savedStateHandle["mockTestAttemptId"] ?: ""

    private val _uiState = MutableStateFlow(MockTestResultUiState())
    val uiState: StateFlow<MockTestResultUiState> = _uiState.asStateFlow()

    init {
        val found = mockTestStore.getAttemptResult(attemptId)
        _uiState.value = MockTestResultUiState(
            isLoading = false,
            result = found
        )
    }

    fun setFilter(filter: SolutionFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
    }
}
