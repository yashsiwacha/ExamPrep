package com.examprep.domain.engine

import com.examprep.domain.model.*

// ═══════════════════════════════════════════════════════════════════════════════
// STUDY PLANNING ENGINE
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Generates and manages the personalized study plan.
 *
 * This engine is responsible for answering:
 *   - What should I study today?
 *   - What comes next?
 *   - Am I on track?
 *   - How much is left?
 *
 * The implementation must be deterministic and testable without Android deps.
 */
interface StudyPlanningEngine {

    /**
     * Generates a fresh study plan for the user.
     *
     * @param profile Student profile with exam date, available hours, etc.
     * @param exam    Exam configuration with full syllabus
     * @param masteryRecords Current mastery state for all topics
     * @return Generated plan with phases and tasks
     */
    suspend fun generatePlan(
        profile: UserProfile,
        exam: Exam,
        masteryRecords: List<MasteryRecord>
    ): StudyPlan

    /**
     * Recalculates plan status after missed days.
     * May redistribute tasks or adjust phase timelines.
     */
    suspend fun rebalancePlan(
        plan: StudyPlan,
        missedDays: Int
    ): StudyPlan

    /**
     * Computes what the student should work on RIGHT NOW.
     */
    suspend fun getCurrentTask(userId: String): StudyTask?

    /**
     * Computes the next task after current.
     */
    suspend fun getNextTask(userId: String): StudyTask?

    /**
     * Calculates overall preparation status.
     */
    suspend fun calculatePlanStatus(plan: StudyPlan): PlanStatus

    /**
     * Calculates completion percentage across the full syllabus.
     */
    suspend fun calculateCompletionPercentage(userId: String, examId: String): Float
}

// ═══════════════════════════════════════════════════════════════════════════════
// MASTERY ENGINE
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Calculates and maintains topic mastery records.
 *
 * Mastery is multi-dimensional — accuracy alone is insufficient.
 * This engine weighs accuracy, time efficiency, attempts, and recency.
 */
interface MasteryEngine {

    /**
     * Recalculates mastery state for a topic after a quiz attempt.
     *
     * @param current    Existing mastery record
     * @param attempts   All question attempts for this topic
     * @return Updated mastery record
     */
    fun calculateMastery(
        current: MasteryRecord,
        attempts: List<QuestionAttempt>
    ): MasteryRecord

    /**
     * Calculates a mastery score (0–100) for display purposes.
     */
    fun calculateMasteryScore(record: MasteryRecord): Float

    /**
     * Determines whether a topic should transition to REVISION_DUE state.
     */
    fun isRevisionDue(record: MasteryRecord, currentTimeEpoch: Long): Boolean

    /**
     * Bulk-recalculates mastery for all topics of a user.
     */
    suspend fun recalculateAllMastery(userId: String)
}

// ═══════════════════════════════════════════════════════════════════════════════
// QUIZ SCORING ENGINE
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Scores quiz attempts and produces performance results.
 *
 * Supports exam-specific marking schemes (negative marking, partial, etc.)
 */
interface QuizScoringEngine {

    /**
     * Scores a completed quiz attempt.
     * Applies the exam-specific marking scheme.
     */
    fun scoreAttempt(
        attempt: QuizAttempt,
        quiz: Quiz
    ): ScoredAttempt

    /**
     * Calculates accuracy percentage for a set of question attempts.
     */
    fun calculateAccuracy(attempts: List<QuestionAttempt>): Float

    /**
     * Calculates average time per question in seconds.
     */
    fun calculateAverageTime(attempts: List<QuestionAttempt>): Float

    /**
     * Builds topic-level performance breakdown from quiz results.
     */
    suspend fun buildTopicPerformance(
        quizAttemptId: String,
        questions: List<Question>
    ): List<TopicPerformance>
}

/** Result of scoring a quiz attempt. */
data class ScoredAttempt(
    val quizAttemptId: String,
    val totalScore: Float,
    val maxScore: Float,
    val accuracy: Float,
    val correctCount: Int,
    val incorrectCount: Int,
    val skippedCount: Int,
    val averageTimeSecs: Float,
    val percentile: Float? = null
)

// ═══════════════════════════════════════════════════════════════════════════════
// RECOMMENDATION ENGINE
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Generates deterministic, rule-based study recommendations.
 *
 * ARCHITECTURE: This engine must NOT depend on any LLM or external API.
 * It uses configurable thresholds and rules applied to mastery + performance data.
 *
 * Future AI recommendations will implement this same interface,
 * allowing seamless provider swap without domain changes.
 */
interface RecommendationEngine {

    /**
     * Generates fresh recommendations based on current student state.
     *
     * @param userId    Student identifier
     * @param mastery   Current mastery records for all topics
     * @param plan      Active study plan
     * @param recentAttempts Recent quiz/PYQ attempts
     * @return Prioritized list of recommendations
     */
    suspend fun generateRecommendations(
        userId: String,
        mastery: List<MasteryRecord>,
        plan: StudyPlan?,
        recentAttempts: List<QuizAttempt>
    ): List<Recommendation>

    /**
     * Rule: Topics below accuracy threshold → revision recommendation.
     * Default threshold: 50%
     */
    fun recommendRevision(
        mastery: MasteryRecord,
        threshold: Float = 50f
    ): Recommendation?

    /**
     * Rule: Topics between thresholds → more practice.
     */
    fun recommendPractice(
        mastery: MasteryRecord,
        lowThreshold: Float = 50f,
        highThreshold: Float = 85f
    ): Recommendation?

    /**
     * Rule: Plan is behind → catch-up recommendation.
     */
    fun recommendCatchUp(plan: StudyPlan): Recommendation?
}

/** Configurable thresholds for recommendation rules. */
data class RecommendationConfig(
    val weakAccuracyThreshold: Float = 50f,      // Below this → revise
    val practiceAccuracyThreshold: Float = 70f,  // Below this → more practice
    val strongAccuracyThreshold: Float = 85f,    // Above this → strong
    val minAttemptsForRecommendation: Int = 5,
    val maxRecommendationsPerDay: Int = 5
)

// ═══════════════════════════════════════════════════════════════════════════════
// REVISION ENGINE
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Manages spaced-repetition revision scheduling.
 *
 * The algorithm is intentionally abstracted — the default implementation
 * uses a simple interval-based approach, but can be replaced with
 * SM-2, FSRS, or any other algorithm without touching the domain model.
 */
interface RevisionEngine {

    /**
     * Schedules the next revision for a topic after it's first learned.
     */
    suspend fun scheduleRevision(
        topicId: String,
        userId: String,
        masteryRecord: MasteryRecord
    ): RevisionRecord

    /**
     * Computes the next revision date after a completed revision.
     *
     * @param record         The just-completed revision record
     * @param performanceScore 0–100 score from the revision quiz
     * @return Updated record with next revision date
     */
    fun computeNextRevision(
        record: RevisionRecord,
        performanceScore: Float
    ): RevisionRecord

    /**
     * Returns all topics due for revision today or overdue.
     */
    suspend fun getDueRevisions(userId: String): List<RevisionRecord>

    /**
     * Revision intervals (in days) for each revision number.
     * Configurable — default: [1, 3, 7, 14, 30, 60]
     */
    fun getRevisionIntervalDays(revisionNumber: Int, config: RevisionConfig): Int
}

/** Configurable revision schedule. */
data class RevisionConfig(
    val intervalDays: List<Int> = listOf(1, 3, 7, 14, 30, 60),
    val minPerformanceToAdvance: Float = 70f,  // Below this → repeat same interval
    val maxRevisions: Int = 6                   // After this → considered mastered
)

// ═══════════════════════════════════════════════════════════════════════════════
// AI GATEWAY (future integration point — interface only)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * AI service abstraction layer.
 *
 * ARCHITECTURE RULE: The domain layer interacts ONLY with this interface.
 * Never reference Ollama, OpenAI, Gemini, or any specific LLM SDK here.
 *
 * Future implementations:
 *   - OllamaAiService  (local, on-device)
 *   - GeminiAiService  (Google Cloud)
 *   - OpenAiService    (OpenAI)
 */
interface AiService {

    /** Explains a concept in the context of a specific topic. */
    suspend fun explainConcept(topicName: String, conceptDescription: String): AiResponse

    /** Summarizes a student's performance in plain English. */
    suspend fun summarizePerformance(summary: PerformanceSummary): AiResponse

    /** Generates a plain-language study plan adjustment recommendation. */
    suspend fun suggestPlanAdjustment(plan: StudyPlan, missedDays: Int): AiResponse

    /** Generates practice questions for a topic. */
    suspend fun generatePracticeQuestions(topicId: String, count: Int): AiResponse

    /** Checks if the AI service is currently available. */
    suspend fun isAvailable(): Boolean
}

/** Wrapper for AI responses to decouple from provider-specific types. */
data class AiResponse(
    val content: String,
    val isSuccess: Boolean,
    val errorMessage: String? = null,
    val provider: String,       // "ollama", "gemini", "openai", etc.
    val model: String           // Model name used
)

// ═══════════════════════════════════════════════════════════════════════════════
// ANALYTICS GATEWAY (abstracted from Firebase)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Analytics tracker abstraction.
 * UI/Domain code fires events through this interface.
 * Implementation decides whether to use Firebase, Amplitude, local-only, etc.
 */
interface AnalyticsTracker {
    fun trackEvent(event: AnalyticsEvent)
    fun setUserProperty(key: String, value: String)
}

data class AnalyticsEvent(
    val name: String,
    val parameters: Map<String, Any> = emptyMap()
)

object AnalyticsEvents {
    const val APP_OPEN = "app_open"
    const val ONBOARDING_STARTED = "onboarding_started"
    const val ONBOARDING_COMPLETED = "onboarding_completed"
    const val EXAM_SELECTED = "exam_selected"
    const val PLAN_GENERATED = "plan_generated"
    const val STUDY_SESSION_STARTED = "study_session_started"
    const val STUDY_SESSION_COMPLETED = "study_session_completed"
    const val QUIZ_STARTED = "quiz_started"
    const val QUIZ_COMPLETED = "quiz_completed"
    const val PYQ_ATTEMPTED = "pyq_attempted"
    const val MOCK_COMPLETED = "mock_completed"
    const val REVISION_COMPLETED = "revision_completed"
    const val TRIAL_STARTED = "trial_started"
    const val AD_FREE_PURCHASED = "ad_free_purchased"
}

// ═══════════════════════════════════════════════════════════════════════════════
// NOTIFICATION SCHEDULER (abstracted from WorkManager)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Notification scheduling abstraction.
 * Hides WorkManager and AlarmManager details from domain code.
 */
interface NotificationScheduler {
    fun scheduleStudyReminder(hour: Int, minute: Int)
    fun scheduleRevisionReminder(topicName: String, scheduledAtEpoch: Long)
    fun scheduleMissedTaskReminder(taskId: String, delayMinutes: Int)
    fun scheduleExamCountdown(examName: String, examDateEpoch: Long)
    fun cancelStudyReminder()
    fun cancelAllReminders()
}

// ═══════════════════════════════════════════════════════════════════════════════
// AD SERVICE (abstracted from AdMob)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Ad display abstraction.
 * The UI never directly references AdMob SDK.
 * Entitlement tier determines whether ads are shown.
 */
interface AdService {
    fun shouldShowAd(placement: AdPlacement): Boolean
    fun loadBannerAd(placement: AdPlacement)
    fun showInterstitialAd(placement: AdPlacement, onDismissed: () -> Unit)
}

enum class AdPlacement {
    HOME_BANNER,
    RESULT_BANNER,
    BETWEEN_SESSIONS,
    SETTINGS_BANNER
}
