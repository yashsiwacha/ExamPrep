package com.examprep.domain.model

import kotlinx.serialization.Serializable

// ═══════════════════════════════════════════════════════════════════════════════
// CORE ENUMERATIONS
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Topic mastery states — progressing from unknown to mastered.
 *
 * IMPORTANT: Do not equate LEARNED with MASTERED.
 * Mastery requires demonstrated performance, not just completion.
 */
enum class MasteryState {
    NOT_STARTED,
    IN_PROGRESS,
    LEARNED,
    PRACTICING,
    MASTERED,
    REVISION_DUE;

    val isActionable: Boolean
        get() = this != MASTERED

    val requiresRevision: Boolean
        get() = this == REVISION_DUE
}

/** Types of study tasks a student can perform. */
enum class StudyTaskType {
    LEARN,
    PRACTICE,
    SOLVE_PYQ,
    REVISE,
    QUIZ,
    MOCK_TEST,
    REVIEW_MISTAKES;

    val isAssessment: Boolean
        get() = this in setOf(QUIZ, MOCK_TEST, SOLVE_PYQ)
}

/** Lifecycle status of a study task. */
enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    SKIPPED,
    CANCELLED
}

/** How a focus session ended. */
enum class SessionOutcome {
    COMPLETED,
    PARTIALLY_COMPLETED,
    NEED_MORE_TIME,
    INTERRUPTED
}

/** Quiz/test mode — determines question count, duration, and marking scheme. */
enum class QuizMode {
    QUICK_QUIZ,     // ~10 questions, ~10 minutes
    TOPIC_TEST,     // ~25 questions, ~30 minutes
    SUBJECT_TEST,   // ~50 questions, ~60 minutes
    FULL_MOCK;      // Exam-specific configuration

    companion object {
        val default = QUICK_QUIZ
    }
}

/** Question format. */
enum class QuestionType {
    MCQ,            // Single-correct multiple choice
    MULTI_SELECT,   // Multiple correct answers
    TRUE_FALSE,
    SHORT_ANSWER    // Non-interactive for PYQ display
}

/** Question difficulty tiers. */
enum class Difficulty {
    EASY,
    MEDIUM,
    HARD,
    VERY_HARD;

    val weight: Float get() = when (this) {
        EASY -> 1.0f
        MEDIUM -> 1.5f
        HARD -> 2.0f
        VERY_HARD -> 2.5f
    }
}

/**
 * Content rights metadata — critical for IP/legal compliance.
 *
 * IMPORTANT: Never assume publicly visible exam questions can be
 * commercially republished without verification.
 */
enum class ContentSource {
    OFFICIAL,               // Officially released by exam authority
    LICENSED,               // Explicitly licensed from a rights holder
    ORIGINAL,               // Created in-house by ExamPrep team
    USER_GENERATED,         // Submitted by a user (future)
    THIRD_PARTY_REFERENCE   // Reference only — must NOT be shown in-app
}

/** Study plan health status. */
enum class PlanStatus {
    ON_TRACK,
    AHEAD,
    BEHIND,
    AT_RISK,
    NOT_STARTED
}

/** Phases within a preparation plan. */
enum class StudyPhaseType(val displayName: String, val orderIndex: Int) {
    FOUNDATION("Foundation", 1),
    CORE_LEARNING("Core Learning", 2),
    PRACTICE("Practice", 3),
    REVISION("Revision", 4),
    PYQ("PYQ Practice", 5),
    MOCK_TESTS("Mock Tests", 6),
    FINAL_REVISION("Final Revision", 7);
}

/** App entitlement / subscription tiers. */
enum class EntitlementTier {
    FREE,
    AD_FREE_TRIAL,
    AD_FREE;

    val hasAds: Boolean get() = this == FREE
    val isAdFree: Boolean get() = this != FREE
}

/** Student self-assessed proficiency during onboarding. */
enum class ProficiencyLevel(val displayName: String, val score: Int) {
    STRUGGLING("Struggling", 1),
    BEGINNER("Beginner", 2),
    AVERAGE("Average", 3),
    GOOD("Good", 4),
    STRONG("Strong", 5)
}

/** Types of recommendations the engine can produce. */
enum class RecommendationType {
    REVISE_TOPIC,
    PRACTICE_MORE,
    TAKE_QUIZ,
    TAKE_MOCK,
    MOVE_TO_NEXT_TOPIC,
    REVIEW_MISTAKES,
    TAKE_BREAK,
    CATCH_UP
}

/** Priority level for recommendations and tasks. */
enum class Priority {
    CRITICAL,
    HIGH,
    MEDIUM,
    LOW
}

// ═══════════════════════════════════════════════════════════════════════════════
// EXAM CONFIGURATION — Core domain model
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Root exam configuration model.
 *
 * Architectural principle: All exam-specific behavior is expressed through
 * configuration rather than conditional code in the UI or domain layers.
 */
@Serializable
data class Exam(
    val id: String,
    val name: String,
    val shortName: String,
    val category: String,       // e.g., "Engineering", "Civil Services", "Medical"
    val authority: String,      // e.g., "NTA", "UPSC", "SSC"
    val syllabusVersion: String,
    val subjects: List<Subject> = emptyList(),
    val markingScheme: MarkingScheme,
    val phases: List<ExamPhase> = emptyList(),
    val isActive: Boolean = true
)

@Serializable
data class Subject(
    val id: String,
    val examId: String,
    val name: String,
    val shortName: String,
    val orderIndex: Int,
    val weightagePercent: Float = 0f,
    val chapters: List<Chapter> = emptyList(),
    val color: Long = 0xFF6200EE  // ARGB for UI theming
)

@Serializable
data class Chapter(
    val id: String,
    val subjectId: String,
    val name: String,
    val orderIndex: Int,
    val topics: List<Topic> = emptyList(),
    val estimatedHours: Float = 0f
)

@Serializable
data class Topic(
    val id: String,
    val chapterId: String,
    val name: String,
    val orderIndex: Int,
    val importanceWeight: Float = 1.0f,     // 1.0 = normal, 2.0 = high importance
    val estimatedHours: Float = 1.0f,
    val masteryState: MasteryState = MasteryState.NOT_STARTED,
    val prerequisites: List<String> = emptyList()  // Topic IDs
)

@Serializable
data class MarkingScheme(
    val correctMarks: Float,
    val incorrectMarks: Float,   // Negative value for negative marking, 0 for none
    val unattemptedMarks: Float = 0f,
    val hasNegativeMarking: Boolean = incorrectMarks < 0f
)

@Serializable
data class ExamPhase(
    val id: String,
    val examId: String,
    val name: String,
    val description: String,
    val orderIndex: Int
)

// ═══════════════════════════════════════════════════════════════════════════════
// USER / STUDENT MODEL
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Central user profile — the foundation of personalization.
 *
 * This model evolves over time as the student uses the app.
 * It is NOT a "settings" object — it represents preparation state.
 */
data class UserProfile(
    val id: String,
    val name: String? = null,
    val selectedExamId: String? = null,
    val examDate: Long? = null,             // Epoch millis
    val targetScore: Float? = null,
    val targetRank: Int? = null,
    val dailyStudyHours: Float = 4f,
    val studyDaysPerWeek: Int = 6,
    val preferredSessionMinutes: Int = 50,
    val preferredStudyTime: String? = null,  // e.g., "morning", "evening"
    val subjectProficiency: Map<String, ProficiencyLevel> = emptyMap(),
    val onboardingCompleted: Boolean = false,
    val diagnosticCompleted: Boolean = false,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)

data class StudyPreferences(
    val userId: String,
    val preferredSessionDurationMinutes: Int = 50,
    val breakDurationMinutes: Int = 10,
    val studyReminderEnabled: Boolean = true,
    val reminderTimeHour: Int = 8,
    val reminderTimeMinute: Int = 0,
    val revisionReminderEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
)

// ═══════════════════════════════════════════════════════════════════════════════
// STUDY PLAN
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * The personalized preparation roadmap.
 *
 * A study plan is generated from:
 *   exam date + available time + syllabus + current mastery
 *
 * It is a living document — updated as the student progresses.
 */
data class StudyPlan(
    val id: String,
    val userId: String,
    val examId: String,
    val examDate: Long,
    val generatedAt: Long,
    val status: PlanStatus = PlanStatus.NOT_STARTED,
    val phases: List<StudyPhase> = emptyList(),
    val completionPercentage: Float = 0f,
    val daysAheadOrBehind: Int = 0    // Positive = ahead, negative = behind
)

data class StudyPhase(
    val id: String,
    val planId: String,
    val phaseType: StudyPhaseType,
    val startDateEpoch: Long,
    val endDateEpoch: Long,
    val orderIndex: Int,
    val tasks: List<StudyTask> = emptyList(),
    val completionPercentage: Float = 0f
)

/**
 * An atomic study task — the primary unit of daily work.
 *
 * Tasks are what the student sees on their "Today" screen.
 * They link to specific topics, subjects, or assessments.
 */
data class StudyTask(
    val id: String,
    val phaseId: String,
    val topicId: String?,
    val subjectId: String?,
    val taskType: StudyTaskType,
    val priority: Priority = Priority.MEDIUM,
    val status: TaskStatus = TaskStatus.PENDING,
    val plannedDateEpoch: Long,
    val estimatedMinutes: Int,
    val actualMinutes: Int = 0,
    val completionPercentage: Float = 0f,
    val dependsOnTaskIds: List<String> = emptyList(),
    val notes: String? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)

// ═══════════════════════════════════════════════════════════════════════════════
// FOCUS SESSION
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * A timed study session linked to a specific task.
 *
 * Sessions persist locally for progress tracking and analytics.
 * They are the raw "time on task" data that feeds mastery calculations.
 */
data class StudySession(
    val id: String,
    val taskId: String,
    val startTimeEpoch: Long,
    val endTimeEpoch: Long?,
    val durationMinutes: Int,
    val targetDurationMinutes: Int,
    val outcome: SessionOutcome?,
    val notes: String? = null,
    val interruptions: Int = 0,
    val createdAt: Long = 0L
)

// ═══════════════════════════════════════════════════════════════════════════════
// QUESTIONS & QUIZ ENGINE
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * A question in the question bank.
 *
 * CONTENT RIGHTS: Every question MUST have a source and content rights
 * classification. Do not add questions without verifying licensing.
 */
@Serializable
data class Question(
    val id: String,
    val examId: String,
    val subjectId: String,
    val chapterId: String,
    val topicId: String,
    val questionText: String,
    val options: List<QuestionOption>,
    val correctOptionId: String,
    val explanation: String?,
    val type: QuestionType,
    val difficulty: Difficulty,
    val source: ContentSource,
    val sourceReference: String?,       // "Exam Year XXXX", "Original - ExamPrep", etc.
    val year: Int?,                     // Exam year for PYQs
    val estimatedSeconds: Int = 60,
    val marks: Float = 1f,
    val negativeMarks: Float = 0f,
    val tags: List<String> = emptyList(),
    val isVerified: Boolean = false,
    val isActive: Boolean = true
)

@Serializable
data class QuestionOption(
    val id: String,
    val questionId: String,
    val text: String,
    val isCorrect: Boolean,
    val orderIndex: Int
)

/** Configuration for a quiz session. */
data class Quiz(
    val id: String,
    val mode: QuizMode,
    val examId: String,
    val topicId: String?,
    val subjectId: String?,
    val questionCount: Int,
    val durationMinutes: Int,
    val markingScheme: MarkingScheme,
    val questions: List<Question> = emptyList(),
    val createdAt: Long = 0L
)

/** One attempt at a quiz — the primary performance record. */
data class QuizAttempt(
    val id: String,
    val quizId: String,
    val userId: String,
    val startTimeEpoch: Long,
    val endTimeEpoch: Long?,
    val questionAttempts: List<QuestionAttempt> = emptyList(),
    val score: Float = 0f,
    val maxScore: Float = 0f,
    val accuracy: Float = 0f,          // 0–100%
    val isCompleted: Boolean = false
)

/** Per-question result within a quiz attempt. */
data class QuestionAttempt(
    val id: String,
    val quizAttemptId: String,
    val questionId: String,
    val selectedOptionId: String?,
    val isCorrect: Boolean,
    val isSkipped: Boolean,
    val timeTakenMillis: Long,
    val marksEarned: Float
)

// ═══════════════════════════════════════════════════════════════════════════════
// MASTERY & REVISION
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Topic mastery record — tracks the current mastery state and history.
 *
 * Mastery is determined by multiple signals, not just completion:
 *   - Quiz accuracy
 *   - Time efficiency
 *   - Number of attempts
 *   - Recency of practice
 *   - Difficulty of questions attempted
 */
data class MasteryRecord(
    val id: String,
    val topicId: String,
    val userId: String,
    val masteryState: MasteryState,
    val quizAccuracy: Float = 0f,      // 0–100%
    val pyqAccuracy: Float = 0f,
    val mockAccuracy: Float = 0f,
    val totalAttempts: Int = 0,
    val correctAttempts: Int = 0,
    val averageTimeSecs: Float = 0f,
    val lastPracticedAt: Long? = null,
    val lastReviewedAt: Long? = null,
    val updatedAt: Long = 0L
)

/**
 * Revision scheduling record.
 *
 * Designed to support spaced repetition algorithms.
 * The algorithm is abstracted — see RevisionEngine interface.
 */
data class RevisionRecord(
    val id: String,
    val topicId: String,
    val userId: String,
    val revisionNumber: Int,           // 1 = first revision, 2 = second, etc.
    val scheduledDateEpoch: Long,
    val completedDateEpoch: Long?,
    val isCompleted: Boolean = false,
    val performanceScore: Float? = null,  // Score from revision quiz
    val nextRevisionDateEpoch: Long? = null,
    val createdAt: Long = 0L
)

// ═══════════════════════════════════════════════════════════════════════════════
// RECOMMENDATION
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * An engine-generated study recommendation.
 *
 * ARCHITECTURE: Recommendations are produced by the deterministic
 * RecommendationEngine. They are NOT AI-generated in MVP.
 * Future AI recommendations will implement the same interface.
 */
data class Recommendation(
    val id: String,
    val userId: String,
    val topicId: String?,
    val subjectId: String?,
    val type: RecommendationType,
    val priority: Priority,
    val message: String,
    val actionRoute: String?,           // Navigation route to handle the recommendation
    val reason: String,                 // Why this was recommended (transparent to user)
    val isActioned: Boolean = false,
    val isDismissed: Boolean = false,
    val expiresAt: Long? = null,
    val createdAt: Long = 0L
)

// ═══════════════════════════════════════════════════════════════════════════════
// PERFORMANCE ANALYTICS
// ═══════════════════════════════════════════════════════════════════════════════

/** Aggregated performance at subject/chapter/topic level. */
data class PerformanceSummary(
    val userId: String,
    val examId: String,
    val subjectPerformance: List<SubjectPerformance>,
    val overallAccuracy: Float,
    val totalQuestionsAttempted: Int,
    val totalCorrect: Int,
    val totalIncorrect: Int,
    val totalSkipped: Int,
    val averageTimeSecs: Float,
    val weakTopics: List<TopicPerformance>,
    val strongTopics: List<TopicPerformance>,
    val calculatedAt: Long
)

data class SubjectPerformance(
    val subjectId: String,
    val subjectName: String,
    val accuracy: Float,
    val questionsAttempted: Int,
    val correctCount: Int,
    val chapterPerformance: List<ChapterPerformance>
)

data class ChapterPerformance(
    val chapterId: String,
    val chapterName: String,
    val accuracy: Float,
    val questionsAttempted: Int,
    val topicPerformance: List<TopicPerformance>
)

data class TopicPerformance(
    val topicId: String,
    val topicName: String,
    val accuracy: Float,
    val questionsAttempted: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val averageTimeSecs: Float,
    val masteryState: MasteryState,
    val isWeak: Boolean = accuracy < 50f,
    val isStrong: Boolean = accuracy >= 85f
)

// ═══════════════════════════════════════════════════════════════════════════════
// ENTITLEMENT
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Entitlement model — completely decoupled from payment provider.
 *
 * ARCHITECTURE: The domain model must not reference Google Play Billing,
 * RevenueCat, Razorpay, or any payment SDK. Payment verification
 * happens at the data layer behind an interface.
 */
data class Entitlement(
    val userId: String,
    val tier: EntitlementTier,
    val trialStartedAt: Long? = null,
    val trialEndsAt: Long? = null,
    val subscriptionStartedAt: Long? = null,
    val subscriptionExpiresAt: Long? = null,
    val autoRenewalEnabled: Boolean = false,
    val isActive: Boolean = tier != EntitlementTier.FREE
)

// ═══════════════════════════════════════════════════════════════════════════════
// CONTENT IMPORT
// ═══════════════════════════════════════════════════════════════════════════════

/** Structured content bundle for importing exam content from JSON files. */
@Serializable
data class ExamContentBundle(
    val schemaVersion: String,
    val exam: Exam,
    val questions: List<Question> = emptyList(),
    val importedAt: Long = 0L,
    val importedBy: String = "system"
)

// ═══════════════════════════════════════════════════════════════════════════════
// MOCK TEST SIMULATOR MODELS
// ═══════════════════════════════════════════════════════════════════════════════

enum class MockQuestionStatus {
    NOT_VISITED,
    NOT_ANSWERED,
    ANSWERED,
    MARKED_FOR_REVIEW,
    ANSWERED_AND_MARKED_FOR_REVIEW
}

@Serializable
data class MockTestSection(
    val id: String,
    val name: String,
    val subjectId: String,
    val questions: List<Question>
)

@Serializable
data class MockTest(
    val id: String,
    val examId: String,
    val title: String,
    val description: String,
    val durationMinutes: Int = 180,
    val totalMarks: Int = 300,
    val positiveMarks: Int = 4,
    val negativeMarks: Int = 1,
    val sections: List<MockTestSection>
)

@Serializable
data class MockSectionResult(
    val sectionName: String,
    val totalQuestions: Int,
    val attemptedCount: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val unattemptedCount: Int,
    val marksObtained: Int,
    val totalPossibleMarks: Int,
    val accuracy: Float
)

@Serializable
data class MockTestResult(
    val attemptId: String,
    val mockTestId: String,
    val title: String,
    val examName: String,
    val totalQuestions: Int,
    val attemptedQuestions: Int,
    val correctAnswers: Int,
    val incorrectAnswers: Int,
    val unattemptedQuestions: Int,
    val totalScore: Int,
    val maximumMarks: Int,
    val overallAccuracy: Float,
    val estimatedPercentile: Float,
    val projectedRankRange: String,
    val timeSpentSeconds: Long,
    val totalDurationSeconds: Long,
    val sectionResults: List<MockSectionResult>,
    val userAnswers: Map<String, String>,
    val questions: List<Question>
)

// ═══════════════════════════════════════════════════════════════════════════════
// MISTAKE VAULT (SMART ERROR BOOK)
// ═══════════════════════════════════════════════════════════════════════════════

enum class MistakeReason {
    CONCEPTUAL,
    CALCULATION,
    FORMULA_CONFUSION,
    TIME_PRESSURE,
    MISREAD_QUESTION
}

enum class MistakeMasteryStatus {
    ACTIVE,
    REVIEWING,
    MASTERED
}

@Serializable
data class MistakeItem(
    val id: String,
    val questionId: String,
    val questionText: String,
    val options: List<String>,
    val correctAnswer: String,
    val studentAnswer: String?,
    val explanation: String?,
    val subject: String,
    val chapterName: String = "High Yield Core",
    val reason: MistakeReason = MistakeReason.CONCEPTUAL,
    val masteryStatus: MistakeMasteryStatus = MistakeMasteryStatus.ACTIVE,
    val attemptCount: Int = 1,
    val consecutiveCorrect: Int = 0,
    val recordedAt: Long = System.currentTimeMillis(),
    val notes: String = ""
)

// ═══════════════════════════════════════════════════════════════════════════════
// INTERACTIVE FLASHCARDS & FORMULA DECK
// ═══════════════════════════════════════════════════════════════════════════════

@Serializable
data class Flashcard(
    val id: String,
    val subject: String,
    val topic: String,
    val title: String,
    val frontPrompt: String,
    val backAnswer: String,
    val keyFormula: String? = null,
    val mnemonicTip: String? = null,
    val importanceRating: Int = 5,
    val isBookmarked: Boolean = false,
    val isMastered: Boolean = false,
    val reviewCount: Int = 0
)

// ═══════════════════════════════════════════════════════════════════════════════
// DYNAMIC AI STUDY ROADMAP & DAILY GOALS
// ═══════════════════════════════════════════════════════════════════════════════

@Serializable
data class DailyStudyTask(
    val id: String,
    val title: String,
    val subject: String,
    val estimatedMinutes: Int,
    val taskType: StudyTaskType,
    val isCompleted: Boolean = false,
    val priorityLabel: String = "High Yield",
    val deepLinkRoute: String? = null
)

@Serializable
data class StudyRoadmapPlan(
    val targetExamName: String,
    val daysRemaining: Int,
    val syllabusCompletionPct: Float,
    val dailyTargetHours: Float,
    val hoursStudiedToday: Float,
    val dailyTasks: List<DailyStudyTask>,
    val weeklyPacingVerdict: String,
    val highPriorityTopics: List<String>
)

// ═══════════════════════════════════════════════════════════════════════════════
// ALL-INDIA LEADERBOARD & PEER BENCHMARK
// ═══════════════════════════════════════════════════════════════════════════════

@Serializable
data class LeaderboardStudent(
    val rank: Int,
    val name: String,
    val badge: String,
    val score: Int,
    val accuracy: Float,
    val streakDays: Int,
    val isCurrentUser: Boolean = false,
    val stateLocation: String = "Delhi, IN"
)

@Serializable
data class PeerBenchmarkReport(
    val examTitle: String,
    val totalParticipants: Int,
    val userRank: Int,
    val userPercentile: Float,
    val userScore: Int,
    val topperScore: Int,
    val averageScore: Int,
    val accuracyComparison: Float,
    val leaderboard: List<LeaderboardStudent>
)

