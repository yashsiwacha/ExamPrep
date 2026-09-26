package com.examprep.data.di

import android.content.Context
import androidx.room.Room
import com.examprep.data.local.dao.*
import com.examprep.data.local.database.ExamPrepDatabase
import com.examprep.data.mapper.*
import com.examprep.data.repository.*
import com.examprep.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

/**
 * Hilt module for the data layer.
 *
 * Provides:
 * - Room database instance (singleton)
 * - All DAO instances (from database)
 * - Repository implementations bound to domain interfaces
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): ExamPrepDatabase {
        lateinit var database: ExamPrepDatabase
        database = Room.databaseBuilder(
            context,
            ExamPrepDatabase::class.java,
            ExamPrepDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .addCallback(object : androidx.room.RoomDatabase.Callback() {
                private fun seedDatabase() {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val exams = com.examprep.data.local.seed.DatabaseSeedData.getInitialExams()
                            val examDao = database.examDao()
                            val questionDao = database.questionDao()

                            exams.forEach { exam ->
                                examDao.insertOrReplace(exam.toEntity())
                                exam.subjects.forEach { subject ->
                                    examDao.insertSubject(subject.toEntity())
                                    subject.chapters.forEach { chapter ->
                                        examDao.insertChapter(chapter.toEntity())
                                        chapter.topics.forEach { topic ->
                                            examDao.insertTopic(topic.toEntity())
                                        }
                                    }
                                }
                            }

                            val questions = com.examprep.data.local.seed.DatabaseSeedData.getInitialQuestions()
                            questions.forEach { question ->
                                questionDao.insert(question.toEntity())
                                questionDao.insertOptions(question.options.map { it.toEntity() })
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                    super.onCreate(db)
                    seedDatabase()
                }

                override fun onDestructiveMigration(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                    super.onDestructiveMigration(db)
                    seedDatabase()
                }
            })
            .build()
        return database
    }

    @Provides fun provideUserProfileDao(db: ExamPrepDatabase): UserProfileDao = db.userProfileDao()
    @Provides fun provideExamDao(db: ExamPrepDatabase): ExamDao = db.examDao()
    @Provides fun provideStudyPlanDao(db: ExamPrepDatabase): StudyPlanDao = db.studyPlanDao()
    @Provides fun provideStudyTaskDao(db: ExamPrepDatabase): StudyTaskDao = db.studyTaskDao()
    @Provides fun provideStudySessionDao(db: ExamPrepDatabase): StudySessionDao = db.studySessionDao()
    @Provides fun provideQuestionDao(db: ExamPrepDatabase): QuestionDao = db.questionDao()
    @Provides fun provideQuizDao(db: ExamPrepDatabase): QuizDao = db.quizDao()
    @Provides fun provideMasteryDao(db: ExamPrepDatabase): MasteryDao = db.masteryDao()
    @Provides fun provideRevisionDao(db: ExamPrepDatabase): RevisionDao = db.revisionDao()
    @Provides fun provideRecommendationDao(db: ExamPrepDatabase): RecommendationDao = db.recommendationDao()
    @Provides fun provideEntitlementDao(db: ExamPrepDatabase): EntitlementDao = db.entitlementDao()
}

/**
 * Binds repository implementations to domain interfaces.
 * This module uses @Binds for zero-overhead binding (no object created at call site).
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindExamRepository(impl: ExamRepositoryImpl): ExamRepository

    @Binds @Singleton
    abstract fun bindUserProfileRepository(impl: UserProfileRepositoryImpl): UserProfileRepository

    @Binds @Singleton
    abstract fun bindStudyPlanRepository(impl: StudyPlanRepositoryImpl): StudyPlanRepository

    @Binds @Singleton
    abstract fun bindStudySessionRepository(impl: StudySessionRepositoryImpl): StudySessionRepository

    @Binds @Singleton
    abstract fun bindQuestionRepository(impl: QuestionRepositoryImpl): QuestionRepository

    @Binds @Singleton
    abstract fun bindQuizRepository(impl: QuizRepositoryImpl): QuizRepository

    @Binds @Singleton
    abstract fun bindMasteryRepository(impl: MasteryRepositoryImpl): MasteryRepository

    @Binds @Singleton
    abstract fun bindRevisionRepository(impl: RevisionRepositoryImpl): RevisionRepository

    @Binds @Singleton
    abstract fun bindRecommendationRepository(impl: RecommendationRepositoryImpl): RecommendationRepository

    @Binds @Singleton
    abstract fun bindContentRepository(impl: ContentRepositoryImpl): ContentRepository
}
