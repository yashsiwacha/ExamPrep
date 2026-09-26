package com.examprep.domain.engine

import com.examprep.domain.model.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.UUID

class RecommendationEngineTest {

    private lateinit var engine: RecommendationEngine

    @Before
    fun setUp() {
        engine = RecommendationEngineImpl()
    }

    @Test
    fun `recommends catch-up when plan is behind`() = runTest {
        val plan = StudyPlan(
            id = "plan_1",
            userId = "user_1",
            examId = "exam_jee",
            examDate = System.currentTimeMillis() + 100000000L,
            generatedAt = System.currentTimeMillis() - 50000000L,
            status = PlanStatus.BEHIND,
            daysAheadOrBehind = -5
        )

        val recs = engine.generateRecommendations("user_1", emptyList(), plan, emptyList())

        assertTrue(recs.any { it.type == RecommendationType.CATCH_UP })
        assertEquals(Priority.CRITICAL, recs.first { it.type == RecommendationType.CATCH_UP }.priority)
    }

    @Test
    fun `recommends revision when topic accuracy is below threshold`() = runTest {
        val mastery = MasteryRecord(
            id = "m1",
            topicId = "top_thermo",
            userId = "user_1",
            masteryState = MasteryState.IN_PROGRESS,
            quizAccuracy = 40f,
            totalAttempts = 6,
            correctAttempts = 2
        )

        val recs = engine.generateRecommendations("user_1", listOf(mastery), null, emptyList())

        assertTrue(recs.any { it.type == RecommendationType.REVISE_TOPIC })
    }
}
