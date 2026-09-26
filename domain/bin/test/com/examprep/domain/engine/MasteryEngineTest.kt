package com.examprep.domain.engine

import com.examprep.domain.model.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.UUID

class MasteryEngineTest {

    private lateinit var engine: MasteryEngine

    @Before
    fun setUp() {
        engine = MasteryEngineImpl(revisionDueThresholdDays = 14)
    }

    private fun createEmptyRecord(topicId: String = "top_1"): MasteryRecord {
        return MasteryRecord(
            id = UUID.randomUUID().toString(),
            topicId = topicId,
            userId = "user_1",
            masteryState = MasteryState.NOT_STARTED,
            quizAccuracy = 0f,
            totalAttempts = 0,
            correctAttempts = 0,
            averageTimeSecs = 0f
        )
    }

    @Test
    fun `few attempts transition to IN_PROGRESS`() {
        val initial = createEmptyRecord()
        val attempts = listOf(
            QuestionAttempt(
                id = "1",
                quizAttemptId = "q_1",
                questionId = "ques_1",
                selectedOptionId = "opt_1",
                isCorrect = true,
                isSkipped = false,
                timeTakenMillis = 45000L,
                marksEarned = 4f
            )
        )

        val updated = engine.calculateMastery(initial, attempts)

        assertEquals(MasteryState.IN_PROGRESS, updated.masteryState)
        assertEquals(1, updated.totalAttempts)
        assertEquals(100f, updated.quizAccuracy, 0.01f)
    }

    @Test
    fun `five attempts with high accuracy transition to MASTERED`() {
        val initial = createEmptyRecord()
        val attempts = (1..5).map { i ->
            QuestionAttempt(
                id = "$i",
                quizAttemptId = "q_1",
                questionId = "ques_$i",
                selectedOptionId = "opt_1",
                isCorrect = true,
                isSkipped = false,
                timeTakenMillis = 50000L,
                marksEarned = 4f
            )
        }

        val updated = engine.calculateMastery(initial, attempts)

        assertEquals(MasteryState.MASTERED, updated.masteryState)
        assertEquals(5, updated.totalAttempts)
        assertEquals(100f, updated.quizAccuracy, 0.01f)
    }

    @Test
    fun `five attempts with low accuracy transition to PRACTICING`() {
        val initial = createEmptyRecord()
        val attempts = (1..5).map { i ->
            QuestionAttempt(
                id = "$i",
                quizAttemptId = "q_1",
                questionId = "ques_$i",
                selectedOptionId = "opt_1",
                isCorrect = i <= 2, // 2/5 = 40%
                isSkipped = false,
                timeTakenMillis = 50000L,
                marksEarned = if (i <= 2) 4f else -1f
            )
        }

        val updated = engine.calculateMastery(initial, attempts)

        assertEquals(MasteryState.PRACTICING, updated.masteryState)
        assertEquals(40f, updated.quizAccuracy, 0.01f)
    }

    @Test
    fun `isRevisionDue returns true after threshold days`() {
        val now = System.currentTimeMillis()
        val oldPracticeEpoch = now - (15L * 24 * 60 * 60 * 1000) // 15 days ago

        val record = createEmptyRecord().copy(
            masteryState = MasteryState.MASTERED,
            lastPracticedAt = oldPracticeEpoch
        )

        assertTrue(engine.isRevisionDue(record, now))
    }
}
