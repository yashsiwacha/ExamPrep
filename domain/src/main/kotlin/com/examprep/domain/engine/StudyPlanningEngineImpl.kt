package com.examprep.domain.engine

import com.examprep.domain.model.*
import java.util.UUID

/**
 * Deterministic Study Planning Engine.
 *
 * Generates structured study plans tailored to the student's exam date,
 * daily available hours, syllabus size, and baseline mastery.
 */
class StudyPlanningEngineImpl(
    private val defaultSessionMinutes: Int = 50
) : StudyPlanningEngine {

    override suspend fun generatePlan(
        profile: UserProfile,
        exam: Exam,
        masteryRecords: List<MasteryRecord>
    ): StudyPlan {
        val planId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        val examDate = profile.examDate ?: (now + 180L * 24 * 60 * 60 * 1000) // Default 6 months

        val totalDays = ((examDate - now) / (24 * 60 * 60 * 1000)).coerceAtLeast(14).toInt()

        // Distribute phases based on total preparation duration
        val foundationDays = (totalDays * 0.15f).toInt().coerceAtLeast(2)
        val coreLearningDays = (totalDays * 0.40f).toInt().coerceAtLeast(5)
        val practiceDays = (totalDays * 0.20f).toInt().coerceAtLeast(3)
        val pyqDays = (totalDays * 0.10f).toInt().coerceAtLeast(2)
        val revisionDays = (totalDays * 0.10f).toInt().coerceAtLeast(2)

        val allTopics = exam.subjects.flatMap { subject ->
            subject.chapters.flatMap { it.topics }
        }

        var currentPhaseStart = now
        val phases = mutableListOf<StudyPhase>()

        // 1. Foundation Phase
        val foundationEnd = currentPhaseStart + (foundationDays * 86_400_000L)
        val foundationPhaseId = UUID.randomUUID().toString()
        val foundationTasks = generateTasksForTopics(
            phaseId = foundationPhaseId,
            topics = allTopics.filter { it.importanceWeight >= 1.5f || it.orderIndex <= 2 },
            taskType = StudyTaskType.LEARN,
            startDate = currentPhaseStart,
            durationDays = foundationDays,
            dailyHours = profile.dailyStudyHours
        )
        phases.add(
            StudyPhase(
                id = foundationPhaseId,
                planId = planId,
                phaseType = StudyPhaseType.FOUNDATION,
                startDateEpoch = currentPhaseStart,
                endDateEpoch = foundationEnd,
                orderIndex = 1,
                tasks = foundationTasks
            )
        )
        currentPhaseStart = foundationEnd

        // 2. Core Learning Phase
        val coreEnd = currentPhaseStart + (coreLearningDays * 86_400_000L)
        val corePhaseId = UUID.randomUUID().toString()
        val coreTasks = generateTasksForTopics(
            phaseId = corePhaseId,
            topics = allTopics,
            taskType = StudyTaskType.LEARN,
            startDate = currentPhaseStart,
            durationDays = coreLearningDays,
            dailyHours = profile.dailyStudyHours
        )
        phases.add(
            StudyPhase(
                id = corePhaseId,
                planId = planId,
                phaseType = StudyPhaseType.CORE_LEARNING,
                startDateEpoch = currentPhaseStart,
                endDateEpoch = coreEnd,
                orderIndex = 2,
                tasks = coreTasks
            )
        )
        currentPhaseStart = coreEnd

        // 3. Practice Phase
        val practiceEnd = currentPhaseStart + (practiceDays * 86_400_000L)
        val practicePhaseId = UUID.randomUUID().toString()
        val practiceTasks = generateTasksForTopics(
            phaseId = practicePhaseId,
            topics = allTopics,
            taskType = StudyTaskType.PRACTICE,
            startDate = currentPhaseStart,
            durationDays = practiceDays,
            dailyHours = profile.dailyStudyHours
        )
        phases.add(
            StudyPhase(
                id = practicePhaseId,
                planId = planId,
                phaseType = StudyPhaseType.PRACTICE,
                startDateEpoch = currentPhaseStart,
                endDateEpoch = practiceEnd,
                orderIndex = 3,
                tasks = practiceTasks
            )
        )
        currentPhaseStart = practiceEnd

        // 4. PYQ Phase
        val pyqEnd = currentPhaseStart + (pyqDays * 86_400_000L)
        val pyqPhaseId = UUID.randomUUID().toString()
        val pyqTasks = generateTasksForTopics(
            phaseId = pyqPhaseId,
            topics = allTopics.filter { it.importanceWeight >= 1.2f },
            taskType = StudyTaskType.SOLVE_PYQ,
            startDate = currentPhaseStart,
            durationDays = pyqDays,
            dailyHours = profile.dailyStudyHours
        )
        phases.add(
            StudyPhase(
                id = pyqPhaseId,
                planId = planId,
                phaseType = StudyPhaseType.PYQ,
                startDateEpoch = currentPhaseStart,
                endDateEpoch = pyqEnd,
                orderIndex = 4,
                tasks = pyqTasks
            )
        )
        currentPhaseStart = pyqEnd

        // 5. Revision Phase
        val revisionEnd = currentPhaseStart + (revisionDays * 86_400_000L)
        val revisionPhaseId = UUID.randomUUID().toString()
        val revisionTasks = generateTasksForTopics(
            phaseId = revisionPhaseId,
            topics = allTopics,
            taskType = StudyTaskType.REVISE,
            startDate = currentPhaseStart,
            durationDays = revisionDays,
            dailyHours = profile.dailyStudyHours
        )
        phases.add(
            StudyPhase(
                id = revisionPhaseId,
                planId = planId,
                phaseType = StudyPhaseType.REVISION,
                startDateEpoch = currentPhaseStart,
                endDateEpoch = revisionEnd,
                orderIndex = 5,
                tasks = revisionTasks
            )
        )
        currentPhaseStart = revisionEnd

        // 6. Final Mock / Revision Phase
        val finalEnd = examDate
        val finalPhaseId = UUID.randomUUID().toString()
        val finalTasks = generateMockTasks(
            phaseId = finalPhaseId,
            startDate = currentPhaseStart,
            endDate = finalEnd
        )
        phases.add(
            StudyPhase(
                id = finalPhaseId,
                planId = planId,
                phaseType = StudyPhaseType.FINAL_REVISION,
                startDateEpoch = currentPhaseStart,
                endDateEpoch = finalEnd,
                orderIndex = 6,
                tasks = finalTasks
            )
        )

        return StudyPlan(
            id = planId,
            userId = profile.id,
            examId = exam.id,
            examDate = examDate,
            generatedAt = now,
            status = PlanStatus.ON_TRACK,
            phases = phases,
            completionPercentage = 0f,
            daysAheadOrBehind = 0
        )
    }

    private fun generateTasksForTopics(
        phaseId: String,
        topics: List<Topic>,
        taskType: StudyTaskType,
        startDate: Long,
        durationDays: Int,
        dailyHours: Float
    ): List<StudyTask> {
        val tasks = mutableListOf<StudyTask>()
        if (topics.isEmpty() || durationDays <= 0) return tasks

        val topicsPerDay = (topics.size.toFloat() / durationDays).coerceAtLeast(1f)
        val estimatedMinutes = (dailyHours * 60 / topicsPerDay.coerceAtLeast(1f)).toInt().coerceIn(30, 90)

        topics.forEachIndexed { index, topic ->
            val dayOffset = (index / topicsPerDay).toInt().coerceAtMost(durationDays - 1)
            val plannedDate = startDate + (dayOffset * 86_400_000L)

            val priority = when {
                topic.importanceWeight >= 1.8f -> Priority.CRITICAL
                topic.importanceWeight >= 1.4f -> Priority.HIGH
                topic.importanceWeight >= 1.0f -> Priority.MEDIUM
                else -> Priority.LOW
            }

            tasks.add(
                StudyTask(
                    id = UUID.randomUUID().toString(),
                    phaseId = phaseId,
                    topicId = topic.id,
                    subjectId = null,
                    taskType = taskType,
                    priority = priority,
                    status = TaskStatus.PENDING,
                    plannedDateEpoch = plannedDate,
                    estimatedMinutes = estimatedMinutes,
                    actualMinutes = 0,
                    completionPercentage = 0f,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
        return tasks
    }

    private fun generateMockTasks(
        phaseId: String,
        startDate: Long,
        endDate: Long
    ): List<StudyTask> {
        val tasks = mutableListOf<StudyTask>()
        val days = ((endDate - startDate) / 86_400_000L).toInt().coerceAtLeast(1)
        for (i in 0 until days step 2) {
            val taskDate = startDate + (i * 86_400_000L)
            tasks.add(
                StudyTask(
                    id = UUID.randomUUID().toString(),
                    phaseId = phaseId,
                    topicId = null,
                    subjectId = null,
                    taskType = StudyTaskType.MOCK_TEST,
                    priority = Priority.CRITICAL,
                    status = TaskStatus.PENDING,
                    plannedDateEpoch = taskDate,
                    estimatedMinutes = 180,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
        return tasks
    }

    override suspend fun rebalancePlan(plan: StudyPlan, missedDays: Int): StudyPlan {
        if (missedDays <= 0) return plan

        val shiftMillis = missedDays * 86_400_000L
        val updatedPhases = plan.phases.map { phase ->
            val updatedTasks = phase.tasks.map { task ->
                if (task.status == TaskStatus.PENDING) {
                    task.copy(
                        plannedDateEpoch = (task.plannedDateEpoch + shiftMillis).coerceAtMost(plan.examDate),
                        updatedAt = System.currentTimeMillis()
                    )
                } else {
                    task
                }
            }
            phase.copy(tasks = updatedTasks)
        }

        val status = if (missedDays > 7) PlanStatus.BEHIND else PlanStatus.ON_TRACK

        return plan.copy(
            phases = updatedPhases,
            status = status,
            daysAheadOrBehind = -missedDays
        )
    }

    override suspend fun getCurrentTask(userId: String): StudyTask? {
        return null // Delegated to repository query in practice
    }

    override suspend fun getNextTask(userId: String): StudyTask? {
        return null
    }

    override suspend fun calculatePlanStatus(plan: StudyPlan): PlanStatus {
        val allTasks = plan.phases.flatMap { it.tasks }
        if (allTasks.isEmpty()) return PlanStatus.NOT_STARTED

        val completedCount = allTasks.count { it.status == TaskStatus.COMPLETED }
        val now = System.currentTimeMillis()
        val expectedCompletedCount = allTasks.count { it.plannedDateEpoch < now }

        return when {
            expectedCompletedCount == 0 -> PlanStatus.ON_TRACK
            completedCount >= expectedCompletedCount + 3 -> PlanStatus.AHEAD
            completedCount >= expectedCompletedCount - 2 -> PlanStatus.ON_TRACK
            completedCount >= expectedCompletedCount - 6 -> PlanStatus.BEHIND
            else -> PlanStatus.AT_RISK
        }
    }

    override suspend fun calculateCompletionPercentage(userId: String, examId: String): Float {
        return 0f
    }
}
