package com.examprep.domain.engine

import com.examprep.domain.model.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RevisionEngineTest {

    private lateinit var engine: RevisionEngine

    @Before
    fun setUp() {
        engine = RevisionEngineImpl(RevisionConfig(intervalDays = listOf(1, 3, 7, 14, 30, 60)))
    }

    @Test
    fun `scheduleRevision initializes first interval to day 1`() = runTest {
        val mastery = MasteryRecord(
            id = "m1",
            topicId = "top_1",
            userId = "u1",
            masteryState = MasteryState.MASTERED,
            quizAccuracy = 90f,
            totalAttempts = 5,
            correctAttempts = 5
        )

        val record = engine.scheduleRevision("top_1", "u1", mastery)

        assertEquals(1, record.revisionNumber)
        assertFalse(record.isCompleted)
        assertTrue(record.scheduledDateEpoch > System.currentTimeMillis())
    }

    @Test
    fun `computeNextRevision advances interval if performance meets threshold`() {
        val initialRecord = RevisionRecord(
            id = "rev_1",
            topicId = "top_1",
            userId = "u1",
            revisionNumber = 1,
            scheduledDateEpoch = System.currentTimeMillis() - 1000L,
            completedDateEpoch = null,
            isCompleted = false,
            performanceScore = null,
            nextRevisionDateEpoch = null,
            createdAt = System.currentTimeMillis()
        )

        val updated = engine.computeNextRevision(initialRecord, performanceScore = 85f)

        assertTrue(updated.isCompleted)
        assertEquals(85f, updated.performanceScore)
        assertNotNull(updated.nextRevisionDateEpoch)
    }
}
