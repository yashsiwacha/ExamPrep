package com.examprep.android.feature.mistakes

import com.examprep.domain.model.MistakeMasteryStatus
import com.examprep.domain.model.MistakeReason
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MistakeVaultStoreTest {

    private lateinit var store: MistakeVaultStore

    @Before
    fun setUp() {
        store = MistakeVaultStore()
    }

    @Test
    fun testDefaultMistakesLoaded() {
        val mistakes = store.mistakes.value
        assertTrue("Store should have default seeded mistakes", mistakes.isNotEmpty())
        assertEquals(4, mistakes.size)
    }

    @Test
    fun testRecordNewMistakeInsertsAtTop() {
        val initialCount = store.mistakes.value.size
        val testQuestionId = "q_test_sec_101"

        store.recordMistake(
            questionId = testQuestionId,
            questionText = "What is the magnetic field inside a long solenoid?",
            options = listOf("μ₀ n I", "μ₀ I / 2r", "0", "μ₀ n I / 2"),
            correctAnswer = "μ₀ n I",
            studentAnswer = "0",
            explanation = "Ampere's law gives B = μ₀ n I.",
            subject = "Physics",
            chapter = "Magnetism",
            reason = MistakeReason.CONCEPTUAL
        )

        val updated = store.mistakes.value
        assertEquals(initialCount + 1, updated.size)
        val firstItem = updated.first()
        assertEquals(testQuestionId, firstItem.questionId)
        assertEquals(MistakeMasteryStatus.ACTIVE, firstItem.masteryStatus)
        assertEquals(0, firstItem.consecutiveCorrect)
    }

    @Test
    fun testRecordExistingMistakeUpdatesAttemptCountAndResetsMastery() {
        val existingId = "q_phy_rot_1"
        
        store.recordMistake(
            questionId = existingId,
            questionText = "Modified text",
            options = listOf("A", "B"),
            correctAnswer = "A",
            studentAnswer = "B",
            explanation = "Expl",
            subject = "Physics",
            reason = MistakeReason.CALCULATION
        )

        val item = store.mistakes.value.first { it.questionId == existingId }
        assertEquals(3, item.attemptCount)
        assertEquals(0, item.consecutiveCorrect)
        assertEquals(MistakeMasteryStatus.ACTIVE, item.masteryStatus)
        assertEquals(MistakeReason.CALCULATION, item.reason)
    }

    @Test
    fun testMarkPracticedProgressesThroughMasteryStateMachine() {
        val testId = "m_math_1"

        // 1st correct practice -> REVIEWING
        store.markPracticed(testId, isCorrect = true)
        var item = store.mistakes.value.first { it.id == testId }
        assertEquals(1, item.consecutiveCorrect)
        assertEquals(MistakeMasteryStatus.REVIEWING, item.masteryStatus)

        // 2nd consecutive correct practice -> MASTERED
        store.markPracticed(testId, isCorrect = true)
        item = store.mistakes.value.first { it.id == testId }
        assertEquals(2, item.consecutiveCorrect)
        assertEquals(MistakeMasteryStatus.MASTERED, item.masteryStatus)

        // Wrong practice afterwards -> resets back to ACTIVE
        store.markPracticed(testId, isCorrect = false)
        item = store.mistakes.value.first { it.id == testId }
        assertEquals(0, item.consecutiveCorrect)
        assertEquals(MistakeMasteryStatus.ACTIVE, item.masteryStatus)
    }

    @Test
    fun testRemoveMistake() {
        val initialCount = store.mistakes.value.size
        store.removeMistake("m_phy_1")
        assertEquals(initialCount - 1, store.mistakes.value.size)
        assertTrue(store.mistakes.value.none { it.id == "m_phy_1" })
    }

    @Test
    fun testUpdateReasonTag() {
        store.updateReasonTag("m_phy_1", MistakeReason.TIME_PRESSURE)
        val item = store.mistakes.value.first { it.id == "m_phy_1" }
        assertEquals(MistakeReason.TIME_PRESSURE, item.reason)
    }
}
