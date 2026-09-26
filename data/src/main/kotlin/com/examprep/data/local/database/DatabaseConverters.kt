package com.examprep.data.local.database

import androidx.room.TypeConverter
import com.examprep.domain.model.*

/**
 * Room type converters for enums, collections, and other non-primitive types.
 *
 * Strategy:
 * - Enums: stored as name() string (stable across versions)
 * - Lists: stored as JSON-encoded strings (simple, human-readable in SQLite)
 * - Timestamps: stored as Long (epoch millis) — Room handles them natively
 */
class DatabaseConverters {

    // ── Enum Converters ──────────────────────────────────────────────────────

    @TypeConverter
    fun masteryStateToString(value: MasteryState): String = value.name

    @TypeConverter
    fun stringToMasteryState(value: String): MasteryState = MasteryState.valueOf(value)

    @TypeConverter
    fun taskTypeToString(value: StudyTaskType): String = value.name

    @TypeConverter
    fun stringToTaskType(value: String): StudyTaskType = StudyTaskType.valueOf(value)

    @TypeConverter
    fun taskStatusToString(value: TaskStatus): String = value.name

    @TypeConverter
    fun stringToTaskStatus(value: String): TaskStatus = TaskStatus.valueOf(value)

    @TypeConverter
    fun sessionOutcomeToString(value: SessionOutcome?): String? = value?.name

    @TypeConverter
    fun stringToSessionOutcome(value: String?): SessionOutcome? =
        value?.let { SessionOutcome.valueOf(it) }

    @TypeConverter
    fun quizModeToString(value: QuizMode): String = value.name

    @TypeConverter
    fun stringToQuizMode(value: String): QuizMode = QuizMode.valueOf(value)

    @TypeConverter
    fun questionTypeToString(value: QuestionType): String = value.name

    @TypeConverter
    fun stringToQuestionType(value: String): QuestionType = QuestionType.valueOf(value)

    @TypeConverter
    fun difficultyToString(value: Difficulty): String = value.name

    @TypeConverter
    fun stringToDifficulty(value: String): Difficulty = Difficulty.valueOf(value)

    @TypeConverter
    fun contentSourceToString(value: ContentSource): String = value.name

    @TypeConverter
    fun stringToContentSource(value: String): ContentSource = ContentSource.valueOf(value)

    @TypeConverter
    fun planStatusToString(value: PlanStatus): String = value.name

    @TypeConverter
    fun stringToPlanStatus(value: String): PlanStatus = PlanStatus.valueOf(value)

    @TypeConverter
    fun studyPhaseTypeToString(value: StudyPhaseType): String = value.name

    @TypeConverter
    fun stringToStudyPhaseType(value: String): StudyPhaseType = StudyPhaseType.valueOf(value)

    @TypeConverter
    fun priorityToString(value: Priority): String = value.name

    @TypeConverter
    fun stringToPriority(value: String): Priority = Priority.valueOf(value)

    @TypeConverter
    fun recommendationTypeToString(value: RecommendationType): String = value.name

    @TypeConverter
    fun stringToRecommendationType(value: String): RecommendationType =
        RecommendationType.valueOf(value)

    @TypeConverter
    fun entitlementTierToString(value: EntitlementTier): String = value.name

    @TypeConverter
    fun stringToEntitlementTier(value: String): EntitlementTier = EntitlementTier.valueOf(value)
}
