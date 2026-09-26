package com.examprep.domain.engine

import com.examprep.domain.model.*
import java.util.UUID

/**
 * Deterministic Rule-Based Recommendation Engine.
 *
 * Scans student progress, topic mastery, and plan adherence to emit
 * prioritized, actionable study recommendations.
 */
class RecommendationEngineImpl(
    private val config: RecommendationConfig = RecommendationConfig()
) : RecommendationEngine {

    override suspend fun generateRecommendations(
        userId: String,
        mastery: List<MasteryRecord>,
        plan: StudyPlan?,
        recentAttempts: List<QuizAttempt>
    ): List<Recommendation> {
        val recommendations = mutableListOf<Recommendation>()

        // Rule 1: Catch-up if plan is behind
        if (plan != null && (plan.status == PlanStatus.BEHIND || plan.status == PlanStatus.AT_RISK)) {
            recommendCatchUp(plan)?.let { recommendations.add(it) }
        }

        // Rule 2: Revision for due or weak topics
        mastery.forEach { record ->
            if (record.masteryState == MasteryState.REVISION_DUE || (record.totalAttempts >= config.minAttemptsForRecommendation && record.quizAccuracy < config.weakAccuracyThreshold)) {
                recommendRevision(record, config.weakAccuracyThreshold)?.let { recommendations.add(it) }
            } else if (record.totalAttempts >= config.minAttemptsForRecommendation && record.quizAccuracy in config.weakAccuracyThreshold..config.practiceAccuracyThreshold) {
                recommendPractice(record, config.weakAccuracyThreshold, config.practiceAccuracyThreshold)?.let { recommendations.add(it) }
            }
        }

        // Rule 3: General Mock suggestion if core topics are mostly learned/mastered
        val masteredRatio = if (mastery.isNotEmpty()) {
            mastery.count { it.masteryState == MasteryState.MASTERED || it.masteryState == MasteryState.LEARNED }.toFloat() / mastery.size
        } else 0f

        if (masteredRatio > 0.6f && (recentAttempts.isEmpty() || recentAttempts.none { it.maxScore > 100 })) {
            recommendations.add(
                Recommendation(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    topicId = null,
                    subjectId = null,
                    type = RecommendationType.TAKE_MOCK,
                    priority = Priority.HIGH,
                    message = "Take a Full-Length Mock Test to benchmark your exam readiness.",
                    actionRoute = "quiz/mock_full",
                    reason = "You have covered over 60% of the syllabus with good accuracy.",
                    isActioned = false,
                    isDismissed = false,
                    expiresAt = System.currentTimeMillis() + 3 * 86_400_000L,
                    createdAt = System.currentTimeMillis()
                )
            )
        }

        return recommendations
            .sortedByDescending { it.priority }
            .take(config.maxRecommendationsPerDay)
    }

    override fun recommendRevision(
        mastery: MasteryRecord,
        threshold: Float
    ): Recommendation? {
        val isWeak = mastery.quizAccuracy < threshold

        val priority = if (isWeak) Priority.CRITICAL else Priority.HIGH
        val reason = if (isWeak) {
            "Accuracy is currently ${mastery.quizAccuracy.toInt()}%, which is below the target ${threshold.toInt()}% threshold."
        } else {
            "Spaced repetition schedule indicates this topic is due for review."
        }

        return Recommendation(
            id = UUID.randomUUID().toString(),
            userId = mastery.userId,
            topicId = mastery.topicId,
            subjectId = null,
            type = RecommendationType.REVISE_TOPIC,
            priority = priority,
            message = "Review and strengthen concepts in Topic ${mastery.topicId}",
            actionRoute = "focus_session/${mastery.topicId}",
            reason = reason,
            isActioned = false,
            isDismissed = false,
            expiresAt = System.currentTimeMillis() + 2 * 86_400_000L,
            createdAt = System.currentTimeMillis()
        )
    }

    override fun recommendPractice(
        mastery: MasteryRecord,
        lowThreshold: Float,
        highThreshold: Float
    ): Recommendation? {
        return Recommendation(
            id = UUID.randomUUID().toString(),
            userId = mastery.userId,
            topicId = mastery.topicId,
            subjectId = null,
            type = RecommendationType.PRACTICE_MORE,
            priority = Priority.MEDIUM,
            message = "Solve 10 practice questions for Topic ${mastery.topicId}",
            actionRoute = "quiz/practice_${mastery.topicId}",
            reason = "Accuracy is at ${mastery.quizAccuracy.toInt()}%. Additional problem-solving will push you into Mastery.",
            isActioned = false,
            isDismissed = false,
            expiresAt = System.currentTimeMillis() + 2 * 86_400_000L,
            createdAt = System.currentTimeMillis()
        )
    }

    override fun recommendCatchUp(plan: StudyPlan): Recommendation? {
        val daysBehind = -plan.daysAheadOrBehind
        return Recommendation(
            id = UUID.randomUUID().toString(),
            userId = plan.userId,
            topicId = null,
            subjectId = null,
            type = RecommendationType.CATCH_UP,
            priority = Priority.CRITICAL,
            message = "Schedule a 60-minute catch-up session this weekend.",
            actionRoute = "timeline",
            reason = "Your plan is approximately $daysBehind day(s) behind schedule.",
            isActioned = false,
            isDismissed = false,
            expiresAt = System.currentTimeMillis() + 3 * 86_400_000L,
            createdAt = System.currentTimeMillis()
        )
    }
}
