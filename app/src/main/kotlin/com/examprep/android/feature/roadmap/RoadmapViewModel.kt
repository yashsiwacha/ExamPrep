package com.examprep.android.feature.roadmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examprep.domain.model.StudyRoadmapPlan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RoadmapViewModel @Inject constructor(
    private val store: StudyPlannerStore
) : ViewModel() {

    val planState: StateFlow<StudyRoadmapPlan> = store.plan.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = store.plan.value
    )

    fun toggleTask(taskId: String) {
        store.toggleTask(taskId)
    }
}
