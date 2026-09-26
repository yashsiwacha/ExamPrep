package com.examprep.android.feature.roadmap

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class StudyPlannerStoreTest {

    private lateinit var store: StudyPlannerStore

    @Before
    fun setUp() {
        store = StudyPlannerStore()
    }

    @Test
    fun testDefaultPlanInitialized() {
        val plan = store.plan.value
        assertEquals("JEE Main 2026 Session 1", plan.targetExamName)
        assertTrue(plan.daysRemaining > 0)
        assertEquals(4, plan.dailyTasks.size)
        assertTrue(plan.highPriorityTopics.isNotEmpty())
    }

    @Test
    fun testToggleTaskRecalculatesHoursStudied() {
        val targetTask = "task_3" // 25 mins, originally false
        val initialHours = store.plan.value.hoursStudiedToday
        
        // Toggle on
        store.toggleTask(targetTask)
        var updated = store.plan.value
        val task = updated.dailyTasks.first { it.id == targetTask }
        assertTrue(task.isCompleted)
        assertTrue(updated.hoursStudiedToday > initialHours)

        // Toggle off
        store.toggleTask(targetTask)
        updated = store.plan.value
        assertFalse(updated.dailyTasks.first { it.id == targetTask }.isCompleted)
        assertEquals(initialHours, updated.hoursStudiedToday, 0.01f)
    }

    @Test
    fun testAllTasksCompletedCalculatesTotalAllocatedTime() {
        val initialPlan = store.plan.value
        initialPlan.dailyTasks.forEach { task ->
            if (!task.isCompleted) {
                store.toggleTask(task.id)
            }
        }
        val totalMins = initialPlan.dailyTasks.sumOf { it.estimatedMinutes }
        val expectedHours = totalMins / 60f
        assertEquals(expectedHours, store.plan.value.hoursStudiedToday, 0.01f)
    }
}
