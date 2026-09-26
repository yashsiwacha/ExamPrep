package com.examprep.domain.engine

import com.examprep.domain.model.*
import java.util.UUID

/**
 * Spaced Repetition Revision Scheduling Engine.
 *
 * Implements interval-based spaced repetition with performance-gated progression.
 * Standard interval ladder: Day 1, 3, 7, 14, 30, 60.
 */
class RevisionEngineImpl(
    private val config: RevisionConfig = RevisionConfig()
) : RevisionEngine {

    override suspend fun scheduleRevision(
        topicId: String,
        userId: String,
        masteryRecord: MasteryRecord
    ): RevisionRecord {
        val now = System.currentTimeMillis()
        val intervalDays = getRevisionIntervalDays(1, config)
        val scheduledDate = now + (intervalDays * 86_400_000L)

        return RevisionRecord(
            id = UUID.randomUUID().toString(),
            topicId = topicId,
            userId = userId,
            revisionNumber = 1,
            scheduledDateEpoch = scheduledDate,
            completedDateEpoch = null,
            isCompleted = false,
            performanceScore = null,
            nextRevisionDateEpoch = null,
            createdAt = now
        )
    }

    override fun computeNextRevision(
        record: RevisionRecord,
        performanceScore: Float
    ): RevisionRecord {
        val now = System.currentTimeMillis()
        val nextRevNum = if (performanceScore >= config.minPerformanceToAdvance) {
            record.revisionNumber + 1
        } else {
            record.revisionNumber // Repeat interval if performance was low
        }

        val nextIntervalDays = if (nextRevNum <= config.maxRevisions) {
            getRevisionIntervalDays(nextRevNum, config)
        } else {
            90 // Extended interval once max revisions reached
        }

        val nextScheduledDate = now + (nextIntervalDays * 86_400_000L)

        return record.copy(
            isCompleted = true,
            completedDateEpoch = now,
            performanceScore = performanceScore,
            nextRevisionDateEpoch = nextScheduledDate
        )
    }

    override suspend fun getDueRevisions(userId: String): List<RevisionRecord> {
        return emptyList() // Resolved via RevisionRepository
    }

    override fun getRevisionIntervalDays(revisionNumber: Int, config: RevisionConfig): Int {
        val index = (revisionNumber - 1).coerceIn(0, config.intervalDays.size - 1)
        return config.intervalDays[index]
    }
}
