package com.examprep.domain.repository

import com.examprep.domain.model.*
import kotlinx.coroutines.flow.Flow

// ═══════════════════════════════════════════════════════════════════════════════
// EXAM REPOSITORY
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Repository interface for exam configuration data.
 *
 * Implementation lives in the :data module.
 * Domain code depends only on this interface — never on Room directly.
 */
interface ExamRepository {
    fun getAllExams(): Flow<List<Exam>>
    suspend fun getExamById(examId: String): Exam?
    suspend fun insertExam(exam: Exam)
    suspend fun updateExam(exam: Exam)
    suspend fun deleteExam(examId: String)
    fun getSubjectsForExam(examId: String): Flow<List<Subject>>
    suspend fun getSubjectById(subjectId: String): Subject?
    fun getChaptersForSubject(subjectId: String): Flow<List<Chapter>>
    fun getTopicsForChapter(chapterId: String): Flow<List<Topic>>
    suspend fun getTopicById(topicId: String): Topic?
    suspend fun updateTopicMastery(topicId: String, masteryState: MasteryState)
}

// ═══════════════════════════════════════════════════════════════════════════════
// USER PROFILE REPOSITORY
// ═══════════════════════════════════════════════════════════════════════════════

interface UserProfileRepository {
    fun getUserProfile(): Flow<UserProfile?>
    suspend fun getUserProfileOnce(): UserProfile?
    suspend fun saveUserProfile(profile: UserProfile)
    suspend fun updateUserProfile(profile: UserProfile)
    suspend fun markOnboardingComplete(userId: String)
    suspend fun markDiagnosticComplete(userId: String)
    suspend fun getEntitlement(): Entitlement?
    suspend fun saveEntitlement(entitlement: Entitlement)
}

// ═══════════════════════════════════════════════════════════════════════════════
// STUDY PLAN REPOSITORY
// ═══════════════════════════════════════════════════════════════════════════════

interface StudyPlanRepository {
    fun getStudyPlan(userId: String): Flow<StudyPlan?>
    suspend fun saveStudyPlan(plan: StudyPlan)
    suspend fun updatePlanStatus(planId: String, status: PlanStatus)
    fun getTodaysTasks(userId: String): Flow<List<StudyTask>>
    fun getTasksForPhase(phaseId: String): Flow<List<StudyTask>>
    suspend fun getTaskById(taskId: String): StudyTask?
    suspend fun saveTask(task: StudyTask)
    suspend fun updateTaskStatus(taskId: String, status: TaskStatus, actualMinutes: Int = 0)
    suspend fun updateTaskCompletion(taskId: String, completionPercent: Float)
    fun getUpcomingTasks(userId: String, daysAhead: Int = 7): Flow<List<StudyTask>>
}

// ═══════════════════════════════════════════════════════════════════════════════
// STUDY SESSION REPOSITORY
// ═══════════════════════════════════════════════════════════════════════════════

interface StudySessionRepository {
    suspend fun startSession(session: StudySession): String
    suspend fun updateSession(session: StudySession)
    suspend fun completeSession(sessionId: String, outcome: SessionOutcome, actualMinutes: Int)
    fun getSessionsForTask(taskId: String): Flow<List<StudySession>>
    fun getRecentSessions(userId: String, limit: Int = 20): Flow<List<StudySession>>
    suspend fun getTotalStudyMinutes(userId: String): Int
    suspend fun getStudyMinutesToday(userId: String): Int
    suspend fun getStudyStreakDays(userId: String): Int
}

// ═══════════════════════════════════════════════════════════════════════════════
// QUESTION / QUIZ REPOSITORY
// ═══════════════════════════════════════════════════════════════════════════════

interface QuestionRepository {
    fun getQuestionsForTopic(topicId: String): Flow<List<Question>>
    fun getQuestionsForSubject(subjectId: String): Flow<List<Question>>
    suspend fun getQuestionById(questionId: String): Question?
    suspend fun getRandomQuestions(
        examId: String,
        topicId: String? = null,
        subjectId: String? = null,
        count: Int,
        difficulty: Difficulty? = null
    ): List<Question>
    suspend fun insertQuestion(question: Question)
    suspend fun insertQuestions(questions: List<Question>)
    suspend fun getQuestionCountForTopic(topicId: String): Int
}

interface QuizRepository {
    suspend fun createQuiz(quiz: Quiz): String
    suspend fun getQuizById(quizId: String): Quiz?
    suspend fun saveQuizAttempt(attempt: QuizAttempt): String
    suspend fun updateQuizAttempt(attempt: QuizAttempt)
    suspend fun getQuizAttemptById(attemptId: String): QuizAttempt?
    fun getAttemptsForUser(userId: String): Flow<List<QuizAttempt>>
    suspend fun saveQuestionAttempt(attempt: QuestionAttempt)
    fun getQuestionAttemptsForQuizAttempt(quizAttemptId: String): Flow<List<QuestionAttempt>>
}

// ═══════════════════════════════════════════════════════════════════════════════
// MASTERY REPOSITORY
// ═══════════════════════════════════════════════════════════════════════════════

interface MasteryRepository {
    fun getMasteryRecords(userId: String): Flow<List<MasteryRecord>>
    suspend fun getMasteryForTopic(userId: String, topicId: String): MasteryRecord?
    suspend fun saveMasteryRecord(record: MasteryRecord)
    suspend fun updateMasteryState(userId: String, topicId: String, state: MasteryState)
    fun getWeakTopics(userId: String, accuracyThreshold: Float = 50f): Flow<List<MasteryRecord>>
    fun getStrongTopics(userId: String, accuracyThreshold: Float = 85f): Flow<List<MasteryRecord>>
}

// ═══════════════════════════════════════════════════════════════════════════════
// REVISION REPOSITORY
// ═══════════════════════════════════════════════════════════════════════════════

interface RevisionRepository {
    fun getDueRevisions(userId: String): Flow<List<RevisionRecord>>
    fun getUpcomingRevisions(userId: String, daysAhead: Int = 7): Flow<List<RevisionRecord>>
    suspend fun saveRevisionRecord(record: RevisionRecord)
    suspend fun markRevisionComplete(recordId: String, performanceScore: Float)
    suspend fun scheduleNextRevision(topicId: String, userId: String, revisionNumber: Int): RevisionRecord
    suspend fun getRevisionHistory(userId: String, topicId: String): List<RevisionRecord>
}

// ═══════════════════════════════════════════════════════════════════════════════
// RECOMMENDATION REPOSITORY
// ═══════════════════════════════════════════════════════════════════════════════

interface RecommendationRepository {
    fun getActiveRecommendations(userId: String): Flow<List<Recommendation>>
    suspend fun saveRecommendation(recommendation: Recommendation)
    suspend fun saveRecommendations(recommendations: List<Recommendation>)
    suspend fun markRecommendationActioned(recommendationId: String)
    suspend fun dismissRecommendation(recommendationId: String)
    suspend fun clearExpiredRecommendations(userId: String)
}

// ═══════════════════════════════════════════════════════════════════════════════
// CONTENT REPOSITORY (for structured import)
// ═══════════════════════════════════════════════════════════════════════════════

interface ContentRepository {
    suspend fun importExamBundle(bundle: ExamContentBundle)
    suspend fun importQuestionsFromJson(jsonContent: String): Int  // Returns count imported
    suspend fun getContentVersion(examId: String): String?
    suspend fun updateContentVersion(examId: String, version: String)
}
