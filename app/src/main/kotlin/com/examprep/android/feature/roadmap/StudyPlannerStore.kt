package com.examprep.android.feature.roadmap

import com.examprep.domain.model.DailyStudyTask
import com.examprep.domain.model.StudyRoadmapPlan
import com.examprep.domain.model.StudyTaskType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudyPlannerStore @Inject constructor() {

    private val _plan = MutableStateFlow(defaultPlan())
    val plan: StateFlow<StudyRoadmapPlan> = _plan.asStateFlow()

    fun toggleTask(taskId: String) {
        val current = _plan.value
        val updatedTasks = current.dailyTasks.map { task ->
            if (task.id == taskId) task.copy(isCompleted = !task.isCompleted) else task
        }
        val completedMinutes = updatedTasks.filter { it.isCompleted }.sumOf { it.estimatedMinutes }
        val hoursStudied = completedMinutes / 60f

        _plan.value = current.copy(
            dailyTasks = updatedTasks,
            hoursStudiedToday = hoursStudied
        )
    }

    companion object {
        private var instance: StudyPlannerStore? = null
        fun get(): StudyPlannerStore {
            return instance ?: StudyPlannerStore().also { instance = it }
        }

        private fun defaultPlan(): StudyRoadmapPlan {
            val tasks = listOf(
                DailyStudyTask(
                    id = "task_1",
                    title = "Solve 15 Rotational Dynamics PYQ Questions",
                    subject = "Physics",
                    estimatedMinutes = 45,
                    taskType = StudyTaskType.QUIZ,
                    isCompleted = true,
                    priorityLabel = "High Weightage (8% Exam)",
                    deepLinkRoute = "practice_home"
                ),
                DailyStudyTask(
                    id = "task_2",
                    title = "Review 10 Mistake Vault Items in Thermodynamics",
                    subject = "Chemistry",
                    estimatedMinutes = 20,
                    taskType = StudyTaskType.REVIEW_MISTAKES,
                    isCompleted = true,
                    priorityLabel = "Weak Spot Drill",
                    deepLinkRoute = "mistake_vault"
                ),
                DailyStudyTask(
                    id = "task_3",
                    title = "Revise Calculus & Integral Formulas Deck",
                    subject = "Mathematics",
                    estimatedMinutes = 25,
                    taskType = StudyTaskType.REVISE,
                    isCompleted = false,
                    priorityLabel = "Formula Retention",
                    deepLinkRoute = "flashcards"
                ),
                DailyStudyTask(
                    id = "task_4",
                    title = "Attempt Full-Length Timed Mock Exam #3",
                    subject = "Full Syllabus",
                    estimatedMinutes = 180,
                    taskType = StudyTaskType.MOCK_TEST,
                    isCompleted = false,
                    priorityLabel = "Weekly Benchmark",
                    deepLinkRoute = "mock_test/mock_jee_2026_01"
                )
            )
            val initialCompletedMins = tasks.filter { it.isCompleted }.sumOf { it.estimatedMinutes }
            return StudyRoadmapPlan(
                targetExamName = "JEE Main 2026 Session 1",
                daysRemaining = 114,
                syllabusCompletionPct = 68.5f,
                dailyTargetHours = 4.5f,
                hoursStudiedToday = initialCompletedMins / 60f,
                dailyTasks = tasks,
                weeklyPacingVerdict = "On Track • Pacing is 12% faster than last week",
                highPriorityTopics = listOf(
                    "Rotational Mechanics",
                    "Electrochemistry",
                    "Indefinite Integrals",
                    "Ray & Wave Optics"
                )
            )
        }
    }
}
