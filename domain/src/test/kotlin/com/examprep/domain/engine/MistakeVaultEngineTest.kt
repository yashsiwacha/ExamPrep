package com.examprep.domain.engine

import com.examprep.domain.model.MistakeItem
import com.examprep.domain.model.MistakeMasteryStatus
import com.examprep.domain.model.MistakeReason
import org.junit.Assert.*
import org.junit.Test

class MistakeVaultEngineTest {

    @Test
    fun `mistake item transitions to MASTERED status after 2 consecutive correct answers`() {
        val initial = MistakeItem(
            id = "m1",
            questionId = "q1",
            questionText = "Sample physics question",
            options = listOf("A", "B", "C", "D"),
            correctAnswer = "B",
            studentAnswer = "A",
            explanation = "Sample solution",
            subject = "Physics",
            reason = MistakeReason.CONCEPTUAL,
            masteryStatus = MistakeMasteryStatus.ACTIVE,
            attemptCount = 1,
            consecutiveCorrect = 0
        )

        // First correct review -> REVIEWING
        val step1 = initial.copy(
            consecutiveCorrect = initial.consecutiveCorrect + 1,
            masteryStatus = if (initial.consecutiveCorrect + 1 >= 2) MistakeMasteryStatus.MASTERED else MistakeMasteryStatus.REVIEWING
        )
        assertEquals(1, step1.consecutiveCorrect)
        assertEquals(MistakeMasteryStatus.REVIEWING, step1.masteryStatus)

        // Second correct review -> MASTERED
        val step2 = step1.copy(
            consecutiveCorrect = step1.consecutiveCorrect + 1,
            masteryStatus = if (step1.consecutiveCorrect + 1 >= 2) MistakeMasteryStatus.MASTERED else MistakeMasteryStatus.REVIEWING
        )
        assertEquals(2, step2.consecutiveCorrect)
        assertEquals(MistakeMasteryStatus.MASTERED, step2.masteryStatus)
    }

    @Test
    fun `incorrect attempt resets consecutive correct counter and sets ACTIVE status`() {
        val reviewingItem = MistakeItem(
            id = "m2",
            questionId = "q2",
            questionText = "Sample chemistry question",
            options = listOf("A", "B", "C", "D"),
            correctAnswer = "C",
            studentAnswer = "A",
            explanation = "Sample solution",
            subject = "Chemistry",
            reason = MistakeReason.CALCULATION,
            masteryStatus = MistakeMasteryStatus.REVIEWING,
            attemptCount = 2,
            consecutiveCorrect = 1
        )

        val failedRetry = reviewingItem.copy(
            attemptCount = reviewingItem.attemptCount + 1,
            consecutiveCorrect = 0,
            masteryStatus = MistakeMasteryStatus.ACTIVE
        )

        assertEquals(0, failedRetry.consecutiveCorrect)
        assertEquals(3, failedRetry.attemptCount)
        assertEquals(MistakeMasteryStatus.ACTIVE, failedRetry.masteryStatus)
    }
}
