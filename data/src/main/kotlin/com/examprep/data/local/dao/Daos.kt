package com.examprep.data.local.dao

import androidx.room.*
import com.examprep.data.local.entity.*
import com.examprep.domain.model.*
import kotlinx.coroutines.flow.Flow

// ═══════════════════════════════════════════════════════════════════════════════
// USER PROFILE DAO
// ═══════════════════════════════════════════════════════════════════════════════

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile LIMIT 1")
    fun observeUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(profile: UserProfileEntity)

    @Update
    suspend fun update(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET onboardingCompleted = 1, updatedAt = :timestamp WHERE id = :userId")
    suspend fun markOnboardingComplete(userId: String, timestamp: Long)

    @Query("UPDATE user_profile SET diagnosticCompleted = 1, updatedAt = :timestamp WHERE id = :userId")
    suspend fun markDiagnosticComplete(userId: String, timestamp: Long)
}

// ═══════════════════════════════════════════════════════════════════════════════
// EXAM DAO
// ═══════════════════════════════════════════════════════════════════════════════

@Dao
interface ExamDao {
    @Query("SELECT * FROM exam WHERE isActive = 1 ORDER BY name")
    fun observeAllExams(): Flow<List<ExamEntity>>

    @Query("SELECT * FROM exam WHERE id = :examId")
    suspend fun getExamById(examId: String): ExamEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(exam: ExamEntity)

    @Update
    suspend fun update(exam: ExamEntity)

    @Query("DELETE FROM exam WHERE id = :examId")
    suspend fun deleteExam(examId: String)

    @Query("SELECT * FROM subject WHERE examId = :examId ORDER BY orderIndex")
    fun observeSubjectsForExam(examId: String): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subject WHERE id = :subjectId")
    suspend fun getSubjectById(subjectId: String): SubjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<SubjectEntity>)

    @Query("SELECT * FROM chapter WHERE subjectId = :subjectId ORDER BY orderIndex")
    fun observeChaptersForSubject(subjectId: String): Flow<List<ChapterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: ChapterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<ChapterEntity>)

    @Query("SELECT * FROM topic WHERE chapterId = :chapterId ORDER BY orderIndex")
    fun observeTopicsForChapter(chapterId: String): Flow<List<TopicEntity>>

    @Query("SELECT * FROM topic WHERE id = :topicId")
    suspend fun getTopicById(topicId: String): TopicEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopic(topic: TopicEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopics(topics: List<TopicEntity>)

    @Query("UPDATE topic SET masteryState = :masteryState, updatedAt = :timestamp WHERE id = :topicId")
    suspend fun updateTopicMastery(topicId: String, masteryState: MasteryState, timestamp: Long)
}

// ═══════════════════════════════════════════════════════════════════════════════
// STUDY PLAN DAO
// ═══════════════════════════════════════════════════════════════════════════════

@Dao
interface StudyPlanDao {
    @Query("SELECT * FROM study_plan WHERE userId = :userId LIMIT 1")
    fun observeStudyPlan(userId: String): Flow<StudyPlanEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(plan: StudyPlanEntity)

    @Query("UPDATE study_plan SET status = :status, updatedAt = :timestamp WHERE id = :planId")
    suspend fun updateStatus(planId: String, status: PlanStatus, timestamp: Long)

    @Query("SELECT * FROM study_phase WHERE planId = :planId ORDER BY orderIndex")
    suspend fun getPhasesForPlan(planId: String): List<StudyPhaseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhase(phase: StudyPhaseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhases(phases: List<StudyPhaseEntity>)
}

// ═══════════════════════════════════════════════════════════════════════════════
// STUDY TASK DAO
// ═══════════════════════════════════════════════════════════════════════════════

@Dao
interface StudyTaskDao {
    @Query("""
        SELECT st.* FROM study_task st
        INNER JOIN study_phase sp ON st.phaseId = sp.id
        INNER JOIN study_plan spl ON sp.planId = spl.id
        WHERE spl.userId = :userId
        AND st.plannedDateEpoch >= :startOfDayEpoch
        AND st.plannedDateEpoch < :endOfDayEpoch
        ORDER BY st.priority DESC, st.plannedDateEpoch ASC
    """)
    fun observeTodaysTasks(userId: String, startOfDayEpoch: Long, endOfDayEpoch: Long): Flow<List<StudyTaskEntity>>

    @Query("SELECT * FROM study_task WHERE phaseId = :phaseId ORDER BY plannedDateEpoch")
    fun observeTasksForPhase(phaseId: String): Flow<List<StudyTaskEntity>>

    @Query("SELECT * FROM study_task WHERE id = :taskId")
    suspend fun getTaskById(taskId: String): StudyTaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(task: StudyTaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<StudyTaskEntity>)

    @Query("""
        UPDATE study_task 
        SET status = :status, actualMinutes = :actualMinutes, updatedAt = :timestamp 
        WHERE id = :taskId
    """)
    suspend fun updateStatus(taskId: String, status: TaskStatus, actualMinutes: Int, timestamp: Long)

    @Query("UPDATE study_task SET completionPercentage = :percent, updatedAt = :timestamp WHERE id = :taskId")
    suspend fun updateCompletion(taskId: String, percent: Float, timestamp: Long)

    @Query("""
        SELECT st.* FROM study_task st
        INNER JOIN study_phase sp ON st.phaseId = sp.id
        INNER JOIN study_plan spl ON sp.planId = spl.id
        WHERE spl.userId = :userId
        AND st.plannedDateEpoch >= :fromEpoch
        AND st.plannedDateEpoch <= :toEpoch
        ORDER BY st.plannedDateEpoch ASC
    """)
    fun observeUpcomingTasks(userId: String, fromEpoch: Long, toEpoch: Long): Flow<List<StudyTaskEntity>>
}

// ═══════════════════════════════════════════════════════════════════════════════
// STUDY SESSION DAO
// ═══════════════════════════════════════════════════════════════════════════════

@Dao
interface StudySessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: StudySessionEntity)

    @Update
    suspend fun update(session: StudySessionEntity)

    @Query("""
        UPDATE study_session 
        SET outcome = :outcome, durationMinutes = :durationMinutes, endTimeEpoch = :endTime
        WHERE id = :sessionId
    """)
    suspend fun completeSession(sessionId: String, outcome: SessionOutcome, durationMinutes: Int, endTime: Long)

    @Query("SELECT * FROM study_session WHERE taskId = :taskId ORDER BY startTimeEpoch DESC")
    fun observeSessionsForTask(taskId: String): Flow<List<StudySessionEntity>>

    @Query("SELECT * FROM study_session WHERE (:userId = :userId) ORDER BY startTimeEpoch DESC LIMIT :limit")
    fun observeRecentSessions(userId: String, limit: Int): Flow<List<StudySessionEntity>>

    @Query("SELECT COALESCE(SUM(durationMinutes), 0) FROM study_session WHERE (:userId = :userId) AND (outcome = 'COMPLETED' OR outcome = 'PARTIALLY_COMPLETED')")
    suspend fun getTotalStudyMinutes(userId: String): Int

    @Query("SELECT COALESCE(SUM(durationMinutes), 0) FROM study_session WHERE (:userId = :userId) AND startTimeEpoch >= :startOfDayEpoch")
    suspend fun getStudyMinutesToday(userId: String, startOfDayEpoch: Long): Int
}

// ═══════════════════════════════════════════════════════════════════════════════
// QUESTION DAO
// ═══════════════════════════════════════════════════════════════════════════════

@Dao
interface QuestionDao {
    @Query("SELECT * FROM question WHERE topicId = :topicId AND isActive = 1")
    fun observeQuestionsForTopic(topicId: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM question WHERE subjectId = :subjectId AND isActive = 1")
    fun observeQuestionsForSubject(subjectId: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM question WHERE id = :questionId")
    suspend fun getQuestionById(questionId: String): QuestionEntity?

    @Query("""
        SELECT * FROM question 
        WHERE examId = :examId 
        AND (:topicId IS NULL OR topicId = :topicId)
        AND (:subjectId IS NULL OR subjectId = :subjectId)
        AND (:difficulty IS NULL OR difficulty = :difficulty)
        AND isActive = 1
        ORDER BY RANDOM()
        LIMIT :count
    """)
    suspend fun getRandomQuestions(
        examId: String,
        topicId: String?,
        subjectId: String?,
        difficulty: Difficulty?,
        count: Int
    ): List<QuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(question: QuestionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<QuestionEntity>)

    @Query("SELECT * FROM question_option WHERE questionId = :questionId ORDER BY orderIndex")
    suspend fun getOptionsForQuestion(questionId: String): List<QuestionOptionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOptions(options: List<QuestionOptionEntity>)

    @Query("SELECT COUNT(*) FROM question WHERE topicId = :topicId AND isActive = 1")
    suspend fun getQuestionCountForTopic(topicId: String): Int
}

// ═══════════════════════════════════════════════════════════════════════════════
// QUIZ DAO
// ═══════════════════════════════════════════════════════════════════════════════

@Dao
interface QuizDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuiz(quiz: QuizEntity)

    @Query("SELECT * FROM quiz WHERE id = :quizId")
    suspend fun getQuizById(quizId: String): QuizEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: QuizAttemptEntity)

    @Update
    suspend fun updateAttempt(attempt: QuizAttemptEntity)

    @Query("SELECT * FROM quiz_attempt WHERE id = :attemptId")
    suspend fun getAttemptById(attemptId: String): QuizAttemptEntity?

    @Query("SELECT * FROM quiz_attempt WHERE userId = :userId ORDER BY startTimeEpoch DESC")
    fun observeAttemptsForUser(userId: String): Flow<List<QuizAttemptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestionAttempt(attempt: QuestionAttemptEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestionAttempts(attempts: List<QuestionAttemptEntity>)

    @Query("SELECT * FROM question_attempt WHERE quizAttemptId = :quizAttemptId")
    fun observeQuestionAttempts(quizAttemptId: String): Flow<List<QuestionAttemptEntity>>
}

// ═══════════════════════════════════════════════════════════════════════════════
// MASTERY DAO
// ═══════════════════════════════════════════════════════════════════════════════

@Dao
interface MasteryDao {
    @Query("SELECT * FROM mastery_record WHERE userId = :userId ORDER BY updatedAt DESC")
    fun observeMasteryRecords(userId: String): Flow<List<MasteryRecordEntity>>

    @Query("SELECT * FROM mastery_record WHERE userId = :userId AND topicId = :topicId")
    suspend fun getMasteryForTopic(userId: String, topicId: String): MasteryRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(record: MasteryRecordEntity)

    @Query("UPDATE mastery_record SET masteryState = :state, updatedAt = :timestamp WHERE userId = :userId AND topicId = :topicId")
    suspend fun updateMasteryState(userId: String, topicId: String, state: MasteryState, timestamp: Long)

    @Query("SELECT * FROM mastery_record WHERE userId = :userId AND quizAccuracy < :threshold")
    fun observeWeakTopics(userId: String, threshold: Float): Flow<List<MasteryRecordEntity>>

    @Query("SELECT * FROM mastery_record WHERE userId = :userId AND quizAccuracy >= :threshold")
    fun observeStrongTopics(userId: String, threshold: Float): Flow<List<MasteryRecordEntity>>
}

// ═══════════════════════════════════════════════════════════════════════════════
// REVISION DAO
// ═══════════════════════════════════════════════════════════════════════════════

@Dao
interface RevisionDao {
    @Query("""
        SELECT * FROM revision_record 
        WHERE userId = :userId AND isCompleted = 0 AND scheduledDateEpoch <= :now 
        ORDER BY scheduledDateEpoch ASC
    """)
    fun observeDueRevisions(userId: String, now: Long): Flow<List<RevisionRecordEntity>>

    @Query("""
        SELECT * FROM revision_record 
        WHERE userId = :userId AND isCompleted = 0 
        AND scheduledDateEpoch BETWEEN :from AND :to
        ORDER BY scheduledDateEpoch ASC
    """)
    fun observeUpcomingRevisions(userId: String, from: Long, to: Long): Flow<List<RevisionRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(record: RevisionRecordEntity)

    @Query("""
        UPDATE revision_record 
        SET isCompleted = 1, completedDateEpoch = :completedAt, performanceScore = :score
        WHERE id = :recordId
    """)
    suspend fun markComplete(recordId: String, completedAt: Long, score: Float)

    @Query("SELECT * FROM revision_record WHERE userId = :userId AND topicId = :topicId ORDER BY revisionNumber ASC")
    suspend fun getRevisionHistory(userId: String, topicId: String): List<RevisionRecordEntity>
}

// ═══════════════════════════════════════════════════════════════════════════════
// RECOMMENDATION DAO
// ═══════════════════════════════════════════════════════════════════════════════

@Dao
interface RecommendationDao {
    @Query("""
        SELECT * FROM recommendation 
        WHERE userId = :userId AND isActioned = 0 AND isDismissed = 0
        AND (expiresAt IS NULL OR expiresAt > :now)
        ORDER BY priority DESC, createdAt DESC
    """)
    fun observeActiveRecommendations(userId: String, now: Long): Flow<List<RecommendationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(recommendation: RecommendationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(recommendations: List<RecommendationEntity>)

    @Query("UPDATE recommendation SET isActioned = 1 WHERE id = :recommendationId")
    suspend fun markActioned(recommendationId: String)

    @Query("UPDATE recommendation SET isDismissed = 1 WHERE id = :recommendationId")
    suspend fun markDismissed(recommendationId: String)

    @Query("DELETE FROM recommendation WHERE userId = :userId AND (expiresAt IS NOT NULL AND expiresAt <= :now)")
    suspend fun deleteExpired(userId: String, now: Long)
}

// ═══════════════════════════════════════════════════════════════════════════════
// ENTITLEMENT DAO
// ═══════════════════════════════════════════════════════════════════════════════

@Dao
interface EntitlementDao {
    @Query("SELECT * FROM entitlement WHERE userId = :userId")
    suspend fun getEntitlement(userId: String): EntitlementEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(entitlement: EntitlementEntity)
}
