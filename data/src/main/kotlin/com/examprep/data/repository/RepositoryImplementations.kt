package com.examprep.data.repository

import com.examprep.data.local.dao.*
import com.examprep.data.local.entity.*
import com.examprep.data.mapper.*
import com.examprep.domain.model.*
import com.examprep.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// ═══════════════════════════════════════════════════════════════════════════════
// EXAM REPOSITORY IMPLEMENTATION
// ═══════════════════════════════════════════════════════════════════════════════

@Singleton
class ExamRepositoryImpl @Inject constructor(
    private val examDao: ExamDao
) : ExamRepository {

    override fun getAllExams(): Flow<List<Exam>> =
        examDao.observeAllExams().map { entities ->
            val seedExams = com.examprep.data.local.seed.DatabaseSeedData.getInitialExams()
            if (entities.isEmpty()) {
                seedExams
            } else {
                entities.map { entity ->
                    val seed = seedExams.find { it.id == entity.id }
                    if (seed != null && seed.subjects.isNotEmpty()) {
                        seed
                    } else {
                        entity.toDomain()
                    }
                }
            }
        }

    override suspend fun getExamById(examId: String): Exam? {
        val found = examDao.getExamById(examId)?.toDomain()
        if (found != null) return found
        val seedExam = com.examprep.data.local.seed.DatabaseSeedData.getInitialExams().find { it.id == examId }
        if (seedExam != null) {
            examDao.insertOrReplace(seedExam.toEntity())
            seedExam.subjects.forEach { subject ->
                examDao.insertSubject(subject.toEntity())
                subject.chapters.forEach { chapter ->
                    examDao.insertChapter(chapter.toEntity())
                    chapter.topics.forEach { topic ->
                        examDao.insertTopic(topic.toEntity())
                    }
                }
            }
            return seedExam
        }
        return null
    }

    override suspend fun insertExam(exam: Exam) =
        examDao.insertOrReplace(exam.toEntity())

    override suspend fun updateExam(exam: Exam) =
        examDao.update(exam.toEntity())

    override suspend fun deleteExam(examId: String) =
        examDao.deleteExam(examId)

    override fun getSubjectsForExam(examId: String): Flow<List<Subject>> =
        examDao.observeSubjectsForExam(examId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getSubjectById(subjectId: String): Subject? =
        examDao.getSubjectById(subjectId)?.toDomain()

    override fun getChaptersForSubject(subjectId: String): Flow<List<Chapter>> =
        examDao.observeChaptersForSubject(subjectId).map { entities -> entities.map { it.toDomain() } }

    override fun getTopicsForChapter(chapterId: String): Flow<List<Topic>> =
        examDao.observeTopicsForChapter(chapterId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getTopicById(topicId: String): Topic? =
        examDao.getTopicById(topicId)?.toDomain()

    override suspend fun updateTopicMastery(topicId: String, masteryState: MasteryState) =
        examDao.updateTopicMastery(topicId, masteryState, System.currentTimeMillis())
}

// ═══════════════════════════════════════════════════════════════════════════════
// USER PROFILE REPOSITORY IMPLEMENTATION
// ═══════════════════════════════════════════════════════════════════════════════

@Singleton
class UserProfileRepositoryImpl @Inject constructor(
    private val userProfileDao: UserProfileDao,
    private val entitlementDao: EntitlementDao
) : UserProfileRepository {

    override fun getUserProfile(): Flow<UserProfile?> =
        userProfileDao.observeUserProfile().map { it?.toDomain() }

    override suspend fun getUserProfileOnce(): UserProfile? =
        userProfileDao.getUserProfileOnce()?.toDomain()

    override suspend fun saveUserProfile(profile: UserProfile) =
        userProfileDao.insertOrReplace(profile.toEntity())

    override suspend fun updateUserProfile(profile: UserProfile) =
        userProfileDao.update(profile.toEntity())

    override suspend fun markOnboardingComplete(userId: String) =
        userProfileDao.markOnboardingComplete(userId, System.currentTimeMillis())

    override suspend fun markDiagnosticComplete(userId: String) =
        userProfileDao.markDiagnosticComplete(userId, System.currentTimeMillis())

    override suspend fun getEntitlement(): Entitlement? =
        userProfileDao.getUserProfileOnce()?.let { profile ->
            entitlementDao.getEntitlement(profile.id)?.toDomain()
        }

    override suspend fun saveEntitlement(entitlement: Entitlement) =
        entitlementDao.insertOrReplace(entitlement.toEntity())
}

// ═══════════════════════════════════════════════════════════════════════════════
// STUDY PLAN REPOSITORY IMPLEMENTATION
// ═══════════════════════════════════════════════════════════════════════════════

@Singleton
class StudyPlanRepositoryImpl @Inject constructor(
    private val studyPlanDao: StudyPlanDao,
    private val studyTaskDao: StudyTaskDao
) : StudyPlanRepository {

    override fun getStudyPlan(userId: String): Flow<StudyPlan?> =
        studyPlanDao.observeStudyPlan(userId).map { it?.toDomain() }

    override suspend fun saveStudyPlan(plan: StudyPlan) =
        studyPlanDao.insertOrReplace(plan.toEntity())

    override suspend fun updatePlanStatus(planId: String, status: PlanStatus) =
        studyPlanDao.updateStatus(planId, status, System.currentTimeMillis())

    override fun getTodaysTasks(userId: String): Flow<List<StudyTask>> {
        val now = System.currentTimeMillis()
        val startOfDay = now - (now % 86_400_000)  // Simplified; use Calendar for proper TZ
        val endOfDay = startOfDay + 86_400_000
        return studyTaskDao.observeTodaysTasks(userId, startOfDay, endOfDay)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getTasksForPhase(phaseId: String): Flow<List<StudyTask>> =
        studyTaskDao.observeTasksForPhase(phaseId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getTaskById(taskId: String): StudyTask? =
        studyTaskDao.getTaskById(taskId)?.toDomain()

    override suspend fun saveTask(task: StudyTask) =
        studyTaskDao.insertOrReplace(task.toEntity())

    override suspend fun updateTaskStatus(taskId: String, status: TaskStatus, actualMinutes: Int) =
        studyTaskDao.updateStatus(taskId, status, actualMinutes, System.currentTimeMillis())

    override suspend fun updateTaskCompletion(taskId: String, completionPercent: Float) =
        studyTaskDao.updateCompletion(taskId, completionPercent, System.currentTimeMillis())

    override fun getUpcomingTasks(userId: String, daysAhead: Int): Flow<List<StudyTask>> {
        val now = System.currentTimeMillis()
        val future = now + (daysAhead * 86_400_000L)
        return studyTaskDao.observeUpcomingTasks(userId, now, future)
            .map { entities -> entities.map { it.toDomain() } }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// STUDY SESSION REPOSITORY IMPLEMENTATION
// ═══════════════════════════════════════════════════════════════════════════════

@Singleton
class StudySessionRepositoryImpl @Inject constructor(
    private val studySessionDao: StudySessionDao
) : StudySessionRepository {

    override suspend fun startSession(session: StudySession): String {
        try {
            studySessionDao.insert(session.toEntity())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return session.id
    }

    override suspend fun updateSession(session: StudySession) {
        try {
            studySessionDao.update(session.toEntity())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun completeSession(sessionId: String, outcome: SessionOutcome, actualMinutes: Int) {
        try {
            studySessionDao.completeSession(sessionId, outcome, actualMinutes, System.currentTimeMillis())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getSessionsForTask(taskId: String): Flow<List<StudySession>> =
        studySessionDao.observeSessionsForTask(taskId).map { entities -> entities.map { it.toDomain() } }

    override fun getRecentSessions(userId: String, limit: Int): Flow<List<StudySession>> =
        studySessionDao.observeRecentSessions(userId, limit).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getTotalStudyMinutes(userId: String): Int =
        studySessionDao.getTotalStudyMinutes(userId)

    override suspend fun getStudyMinutesToday(userId: String): Int {
        val now = System.currentTimeMillis()
        val startOfDay = now - (now % 86_400_000)
        return studySessionDao.getStudyMinutesToday(userId, startOfDay)
    }

    override suspend fun getStudyStreakDays(userId: String): Int {
        return 0
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// QUESTION REPOSITORY IMPLEMENTATION
// ═══════════════════════════════════════════════════════════════════════════════

@Singleton
class QuestionRepositoryImpl @Inject constructor(
    private val questionDao: QuestionDao
) : QuestionRepository {

    override fun getQuestionsForTopic(topicId: String): Flow<List<Question>> =
        questionDao.observeQuestionsForTopic(topicId).map { entities ->
            if (entities.isEmpty()) {
                com.examprep.data.local.seed.DatabaseSeedData.getInitialQuestions()
                    .filter { it.topicId == topicId }
            } else {
                entities.map { entity ->
                    val options = questionDao.getOptionsForQuestion(entity.id)
                    entity.toDomain(options)
                }
            }
        }

    override fun getQuestionsForSubject(subjectId: String): Flow<List<Question>> =
        questionDao.observeQuestionsForSubject(subjectId).map { entities ->
            if (entities.isEmpty()) {
                com.examprep.data.local.seed.DatabaseSeedData.getInitialQuestions()
                    .filter { it.subjectId == subjectId }
            } else {
                entities.map { entity ->
                    val options = questionDao.getOptionsForQuestion(entity.id)
                    entity.toDomain(options)
                }
            }
        }

    override suspend fun getQuestionById(questionId: String): Question? {
        try {
            val fromDb = questionDao.getQuestionById(questionId)?.let { entity ->
                val options = questionDao.getOptionsForQuestion(questionId)
                entity.toDomain(options)
            }
            if (fromDb != null) return fromDb
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return com.examprep.data.local.seed.DatabaseSeedData.getInitialQuestions().find { it.id == questionId }
    }

    override suspend fun getRandomQuestions(
        examId: String,
        topicId: String?,
        subjectId: String?,
        count: Int,
        difficulty: Difficulty?
    ): List<Question> {
        try {
            val fromDb = questionDao.getRandomQuestions(examId, topicId, subjectId, difficulty, count)
                .map { entity ->
                    val options = questionDao.getOptionsForQuestion(entity.id)
                    entity.toDomain(options)
                }
            if (fromDb.isNotEmpty() && fromDb.all { it.options.isNotEmpty() }) return fromDb
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Fallback to seed questions matching exam/topic/subject
        val allSeed = com.examprep.data.local.seed.DatabaseSeedData.getInitialQuestions()
        val matchingExam = allSeed.filter { it.examId == examId }
        val pool = when {
            topicId != null -> matchingExam.filter { it.topicId == topicId }.ifEmpty { matchingExam }.ifEmpty { allSeed }
            subjectId != null -> matchingExam.filter { it.subjectId == subjectId }.ifEmpty { matchingExam }.ifEmpty { allSeed }
            else -> matchingExam.ifEmpty { allSeed }
        }

        return pool.shuffled().take(count).ifEmpty { allSeed.take(count) }
    }

    override suspend fun insertQuestion(question: Question) {
        questionDao.insert(question.toEntity())
        questionDao.insertOptions(question.options.map { it.toEntity() })
    }

    override suspend fun insertQuestions(questions: List<Question>) {
        questionDao.insertAll(questions.map { it.toEntity() })
        val allOptions = questions.flatMap { q -> q.options.map { it.toEntity() } }
        questionDao.insertOptions(allOptions)
    }

    override suspend fun getQuestionCountForTopic(topicId: String): Int =
        questionDao.getQuestionCountForTopic(topicId)
}

// ═══════════════════════════════════════════════════════════════════════════════
// QUIZ REPOSITORY IMPLEMENTATION
// ═══════════════════════════════════════════════════════════════════════════════

@Singleton
class QuizRepositoryImpl @Inject constructor(
    private val quizDao: QuizDao
) : QuizRepository {

    override suspend fun createQuiz(quiz: Quiz): String {
        try {
            quizDao.insertQuiz(quiz.toEntity())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return quiz.id
    }

    override suspend fun getQuizById(quizId: String): Quiz? =
        quizDao.getQuizById(quizId)?.toDomain()

    override suspend fun saveQuizAttempt(attempt: QuizAttempt): String {
        try {
            quizDao.insertAttempt(attempt.toEntity())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return attempt.id
    }

    override suspend fun updateQuizAttempt(attempt: QuizAttempt) {
        try {
            quizDao.updateAttempt(attempt.toEntity())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun getQuizAttemptById(attemptId: String): QuizAttempt? =
        quizDao.getAttemptById(attemptId)?.toDomain()

    override fun getAttemptsForUser(userId: String): Flow<List<QuizAttempt>> =
        quizDao.observeAttemptsForUser(userId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun saveQuestionAttempt(attempt: QuestionAttempt) {
        try {
            quizDao.insertQuestionAttempt(attempt.toEntity())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getQuestionAttemptsForQuizAttempt(quizAttemptId: String): Flow<List<QuestionAttempt>> =
        quizDao.observeQuestionAttempts(quizAttemptId).map { entities -> entities.map { it.toDomain() } }
}

// ═══════════════════════════════════════════════════════════════════════════════
// MASTERY REPOSITORY IMPLEMENTATION
// ═══════════════════════════════════════════════════════════════════════════════

@Singleton
class MasteryRepositoryImpl @Inject constructor(
    private val masteryDao: MasteryDao
) : MasteryRepository {

    override fun getMasteryRecords(userId: String): Flow<List<MasteryRecord>> =
        masteryDao.observeMasteryRecords(userId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getMasteryForTopic(userId: String, topicId: String): MasteryRecord? =
        masteryDao.getMasteryForTopic(userId, topicId)?.toDomain()

    override suspend fun saveMasteryRecord(record: MasteryRecord) {
        try {
            masteryDao.insertOrReplace(record.toEntity())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun updateMasteryState(userId: String, topicId: String, state: MasteryState) {
        try {
            masteryDao.updateMasteryState(userId, topicId, state, System.currentTimeMillis())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getWeakTopics(userId: String, accuracyThreshold: Float): Flow<List<MasteryRecord>> =
        masteryDao.observeWeakTopics(userId, accuracyThreshold).map { entities -> entities.map { it.toDomain() } }

    override fun getStrongTopics(userId: String, accuracyThreshold: Float): Flow<List<MasteryRecord>> =
        masteryDao.observeStrongTopics(userId, accuracyThreshold).map { entities -> entities.map { it.toDomain() } }
}

// ═══════════════════════════════════════════════════════════════════════════════
// REVISION REPOSITORY IMPLEMENTATION
// ═══════════════════════════════════════════════════════════════════════════════

@Singleton
class RevisionRepositoryImpl @Inject constructor(
    private val revisionDao: RevisionDao
) : RevisionRepository {

    override fun getDueRevisions(userId: String): Flow<List<RevisionRecord>> =
        revisionDao.observeDueRevisions(userId, System.currentTimeMillis())
            .map { entities -> entities.map { it.toDomain() } }

    override fun getUpcomingRevisions(userId: String, daysAhead: Int): Flow<List<RevisionRecord>> {
        val now = System.currentTimeMillis()
        val future = now + (daysAhead * 86_400_000L)
        return revisionDao.observeUpcomingRevisions(userId, now, future)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun saveRevisionRecord(record: RevisionRecord) =
        revisionDao.insertOrReplace(record.toEntity())

    override suspend fun markRevisionComplete(recordId: String, performanceScore: Float) =
        revisionDao.markComplete(recordId, System.currentTimeMillis(), performanceScore)

    override suspend fun scheduleNextRevision(
        topicId: String,
        userId: String,
        revisionNumber: Int
    ): RevisionRecord {
        // Default intervals in days: [1, 3, 7, 14, 30, 60]
        val intervalDays = listOf(1, 3, 7, 14, 30, 60)
        val days = intervalDays.getOrElse(revisionNumber - 1) { 60 }
        val record = RevisionRecord(
            id = java.util.UUID.randomUUID().toString(),
            topicId = topicId,
            userId = userId,
            revisionNumber = revisionNumber,
            scheduledDateEpoch = System.currentTimeMillis() + (days * 86_400_000L),
            completedDateEpoch = null,
            createdAt = System.currentTimeMillis()
        )
        revisionDao.insertOrReplace(record.toEntity())
        return record
    }

    override suspend fun getRevisionHistory(userId: String, topicId: String): List<RevisionRecord> =
        revisionDao.getRevisionHistory(userId, topicId).map { it.toDomain() }
}

// ═══════════════════════════════════════════════════════════════════════════════
// RECOMMENDATION REPOSITORY IMPLEMENTATION
// ═══════════════════════════════════════════════════════════════════════════════

@Singleton
class RecommendationRepositoryImpl @Inject constructor(
    private val recommendationDao: RecommendationDao
) : RecommendationRepository {

    override fun getActiveRecommendations(userId: String): Flow<List<Recommendation>> =
        recommendationDao.observeActiveRecommendations(userId, System.currentTimeMillis())
            .map { entities -> entities.map { it.toDomain() } }

    override suspend fun saveRecommendation(recommendation: Recommendation) =
        recommendationDao.insertOrReplace(recommendation.toEntity())

    override suspend fun saveRecommendations(recommendations: List<Recommendation>) =
        recommendationDao.insertAll(recommendations.map { it.toEntity() })

    override suspend fun markRecommendationActioned(recommendationId: String) =
        recommendationDao.markActioned(recommendationId)

    override suspend fun dismissRecommendation(recommendationId: String) =
        recommendationDao.markDismissed(recommendationId)

    override suspend fun clearExpiredRecommendations(userId: String) =
        recommendationDao.deleteExpired(userId, System.currentTimeMillis())
}

// ═══════════════════════════════════════════════════════════════════════════════
// CONTENT REPOSITORY IMPLEMENTATION
// ═══════════════════════════════════════════════════════════════════════════════

@Singleton
class ContentRepositoryImpl @Inject constructor(
    private val examDao: ExamDao,
    private val questionDao: QuestionDao
) : ContentRepository {

    override suspend fun importExamBundle(bundle: ExamContentBundle) {
        // Insert exam
        examDao.insertOrReplace(bundle.exam.toEntity())

        // Insert subjects → chapters → topics in order
        bundle.exam.subjects.forEach { subject ->
            examDao.insertSubject(subject.toEntity())
            subject.chapters.forEach { chapter ->
                examDao.insertChapter(chapter.toEntity())
                chapter.topics.forEach { topic ->
                    examDao.insertTopic(topic.toEntity())
                }
            }
        }

        // Insert questions
        if (bundle.questions.isNotEmpty()) {
            questionDao.insertAll(bundle.questions.map { it.toEntity() })
            val allOptions = bundle.questions.flatMap { q -> q.options.map { it.toEntity() } }
            questionDao.insertOptions(allOptions)
        }
    }

    override suspend fun importQuestionsFromJson(jsonContent: String): Int {
        // TODO Phase 3: Parse JSON and insert questions
        // Use kotlinx.serialization to deserialize List<Question>
        return 0
    }

    override suspend fun getContentVersion(examId: String): String? {
        return examDao.getExamById(examId)?.syllabusVersion
    }

    override suspend fun updateContentVersion(examId: String, version: String) {
        // TODO Phase 1: Implement version update
    }
}
