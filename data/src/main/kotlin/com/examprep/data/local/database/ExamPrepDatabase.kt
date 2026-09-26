package com.examprep.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.examprep.data.local.dao.*
import com.examprep.data.local.entity.*

/**
 * ExamPrep local database.
 *
 * Database design decisions:
 * - Version 1: Initial scaffold schema
 * - All IDs are String (UUID) for future sync compatibility
 * - Type converters handle enums and collections
 * - Migrations required for every schema change (no destructive migration in production)
 *
 * Future migration strategy:
 *   - Use Room's AutoMigration where possible
 *   - Write manual Migration objects for complex changes
 *   - Never use fallbackToDestructiveMigration in release builds
 */
@Database(
    entities = [
        UserProfileEntity::class,
        ExamEntity::class,
        SubjectEntity::class,
        ChapterEntity::class,
        TopicEntity::class,
        StudyPlanEntity::class,
        StudyPhaseEntity::class,
        StudyTaskEntity::class,
        StudySessionEntity::class,
        QuestionEntity::class,
        QuestionOptionEntity::class,
        QuizEntity::class,
        QuizAttemptEntity::class,
        QuestionAttemptEntity::class,
        MasteryRecordEntity::class,
        RevisionRecordEntity::class,
        RecommendationEntity::class,
        EntitlementEntity::class,
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(DatabaseConverters::class)
abstract class ExamPrepDatabase : RoomDatabase() {

    abstract fun userProfileDao(): UserProfileDao
    abstract fun examDao(): ExamDao
    abstract fun studyPlanDao(): StudyPlanDao
    abstract fun studyTaskDao(): StudyTaskDao
    abstract fun studySessionDao(): StudySessionDao
    abstract fun questionDao(): QuestionDao
    abstract fun quizDao(): QuizDao
    abstract fun masteryDao(): MasteryDao
    abstract fun revisionDao(): RevisionDao
    abstract fun recommendationDao(): RecommendationDao
    abstract fun entitlementDao(): EntitlementDao

    companion object {
        const val DATABASE_NAME = "examprep_database"
        const val DATABASE_VERSION = 3
    }
}
