package com.examprep.data.di

import com.examprep.domain.engine.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EngineModule {

    @Provides
    @Singleton
    fun provideStudyPlanningEngine(): StudyPlanningEngine = StudyPlanningEngineImpl()

    @Provides
    @Singleton
    fun provideMasteryEngine(): MasteryEngine = MasteryEngineImpl()

    @Provides
    @Singleton
    fun provideQuizScoringEngine(): QuizScoringEngine = QuizScoringEngineImpl()

    @Provides
    @Singleton
    fun provideRevisionEngine(): RevisionEngine = RevisionEngineImpl()

    @Provides
    @Singleton
    fun provideRecommendationEngine(): RecommendationEngine = RecommendationEngineImpl()
}
