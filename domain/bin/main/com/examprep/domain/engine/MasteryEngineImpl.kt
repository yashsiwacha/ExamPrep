package com.examprep.domain.engine

import com.examprep.domain.model.*

/**
 * Multi-signal Topic Mastery Engine.
 *
 * Evaluates mastery using a weighted combination of:
 * - Accuracy (40%)
 * - Volume of attempts (20%)
 * - Time efficiency (15%)
 * - Recency of practice (15%)
 * - PYQ / Mock performance (10%)
 *
 * Strict constraint: A topic requires at least 5 quiz attempts AND >= 85% accuracy
 * to reach the MASTERED state.
 */
class MasteryEngineImpl(
    private val revisionDueThresholdDays: Int = 14
) : MasteryEngine {

    override fun calculateMastery(
        current: MasteryRecord,
        attempts: List<QuestionAttempt>
    ): MasteryRecord {
        if (attempts.isEmpty()) return current

        val newAttemptsCount = attempts.size
        val newCorrectCount = attempts.count { it.isCorrect }
        val totalAttempts = current.totalAttempts + newAttemptsCount
        val totalCorrect = current.correctAttempts + newCorrectCount

        val totalTimeMillis = attempts.sumOf { it.timeTakenMillis }
        val avgTimeSecs = if (totalAttempts > 0) {
            (current.averageTimeSecs * current.totalAttempts + (totalTimeMillis / 1000f)) / totalAttempts
        } else 0f

        val quizAccuracy = if (totalAttempts > 0) {
            (totalCorrect.toFloat() / totalAttempts) * 100f
        } else 0f

        val now = System.currentTimeMillis()

        val newState = when {
            totalAttempts == 0 -> MasteryState.NOT_STARTED
            totalAttempts in 1..2 -> MasteryState.IN_PROGRESS
            totalAttempts in 3..4 && quizAccuracy >= 60f -> MasteryState.LEARNED
            totalAttempts >= 5 && quizAccuracy >= 85f -> MasteryState.MASTERED
            totalAttempts >= 3 -> MasteryState.PRACTICING
            else -> MasteryState.IN_PROGRESS
        }

        return current.copy(
            masteryState = newState,
            quizAccuracy = quizAccuracy,
            totalAttempts = totalAttempts,
            correctAttempts = totalCorrect,
            averageTimeSecs = avgTimeSecs,
            lastPracticedAt = now,
            updatedAt = now
        )
    }

    override fun calculateMasteryScore(record: MasteryRecord): Float {
        val accuracyComponent = (record.quizAccuracy.coerceIn(0f, 100f)) * 0.40f

        val attemptVolumeRatio = (record.totalAttempts.toFloat() / 10f).coerceIn(0f, 1f)
        val attemptComponent = attemptVolumeRatio * 100f * 0.20f

        // Time efficiency: target ~60 seconds per question
        val timeScore = when {
            record.averageTimeSecs <= 0f -> 50f
            record.averageTimeSecs in 20f..60f -> 100f
            record.averageTimeSecs in 60f..120f -> (120f - record.averageTimeSecs) * (100f / 60f)
            else -> 20f
        }.coerceIn(0f, 100f)
        val timeComponent = timeScore * 0.15f

        // Recency score (decays over 30 days)
        val now = System.currentTimeMillis()
        val daysSincePractice = if (record.lastPracticedAt != null) {
            ((now - record.lastPracticedAt) / 86_400_000L).coerceAtLeast(0)
        } else 30L
        val recencyScore = (100f - (daysSincePractice.toFloat() * (100f / 30f))).coerceIn(0f, 100f)
        val recencyComponent = recencyScore * 0.15f

        val pyqComponent = (record.pyqAccuracy.coerceIn(0f, 100f)) * 0.10f

        return (accuracyComponent + attemptComponent + timeComponent + recencyComponent + pyqComponent)
            .coerceIn(0f, 100f)
    }

    override fun isRevisionDue(record: MasteryRecord, currentTimeEpoch: Long): Boolean {
        if (record.masteryState == MasteryState.NOT_STARTED || record.masteryState == MasteryState.IN_PROGRESS) {
            return false
        }
        val lastInteraction = record.lastReviewedAt ?: record.lastPracticedAt ?: return false
        val daysSinceInteraction = (currentTimeEpoch - lastInteraction) / 86_400_000L
        return daysSinceInteraction >= revisionDueThresholdDays
    }

    override suspend fun recalculateAllMastery(userId: String) {
        // Bulk recalculation stub for repository orchestration
    }
}
