package com.examprep.data.local.entity

import androidx.room.*
import com.examprep.domain.model.*

// ═══════════════════════════════════════════════════════════════════════════════
// ROOM ENTITIES
// All entities use String UUIDs as primary keys for future sync compatibility.
// All entities include createdAt/updatedAt timestamps.
// ═══════════════════════════════════════════════════════════════════════════════

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String,
    val name: String?,
    val selectedExamId: String?,
    val examDate: Long?,
    val targetScore: Float?,
    val targetRank: Int?,
    val dailyStudyHours: Float,
    val studyDaysPerWeek: Int,
    val preferredSessionMinutes: Int,
    val preferredStudyTime: String?,
    val subjectProficiencyJson: String, // serialized Map<String, ProficiencyLevel>
    val onboardingCompleted: Boolean,
    val diagnosticCompleted: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(tableName = "exam")
data class ExamEntity(
    @PrimaryKey val id: String,
    val name: String,
    val shortName: String,
    val category: String,
    val authority: String,
    val syllabusVersion: String,
    val correctMarks: Float,
    val incorrectMarks: Float,
    val unattemptedMarks: Float,
    val hasNegativeMarking: Boolean,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(
    tableName = "subject",
    foreignKeys = [ForeignKey(
        entity = ExamEntity::class,
        parentColumns = ["id"],
        childColumns = ["examId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("examId")]
)
data class SubjectEntity(
    @PrimaryKey val id: String,
    val examId: String,
    val name: String,
    val shortName: String,
    val orderIndex: Int,
    val weightagePercent: Float,
    val colorArgb: Long,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(
    tableName = "chapter",
    foreignKeys = [ForeignKey(
        entity = SubjectEntity::class,
        parentColumns = ["id"],
        childColumns = ["subjectId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("subjectId")]
)
data class ChapterEntity(
    @PrimaryKey val id: String,
    val subjectId: String,
    val name: String,
    val orderIndex: Int,
    val estimatedHours: Float,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(
    tableName = "topic",
    foreignKeys = [ForeignKey(
        entity = ChapterEntity::class,
        parentColumns = ["id"],
        childColumns = ["chapterId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("chapterId")]
)
data class TopicEntity(
    @PrimaryKey val id: String,
    val chapterId: String,
    val name: String,
    val orderIndex: Int,
    val importanceWeight: Float,
    val estimatedHours: Float,
    val masteryState: MasteryState,
    val prerequisiteTopicIds: String, // JSON array of topic IDs
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(
    tableName = "study_plan",
    foreignKeys = [
        ForeignKey(
            entity = ExamEntity::class,
            parentColumns = ["id"],
            childColumns = ["examId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("examId")]
)
data class StudyPlanEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val examId: String,
    val examDate: Long,
    val status: PlanStatus,
    val completionPercentage: Float,
    val daysAheadOrBehind: Int,
    val generatedAt: Long,
    val updatedAt: Long
)

@Entity(
    tableName = "study_phase",
    foreignKeys = [ForeignKey(
        entity = StudyPlanEntity::class,
        parentColumns = ["id"],
        childColumns = ["planId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("planId")]
)
data class StudyPhaseEntity(
    @PrimaryKey val id: String,
    val planId: String,
    val phaseType: StudyPhaseType,
    val startDateEpoch: Long,
    val endDateEpoch: Long,
    val orderIndex: Int,
    val completionPercentage: Float,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(
    tableName = "study_task",
    foreignKeys = [ForeignKey(
        entity = StudyPhaseEntity::class,
        parentColumns = ["id"],
        childColumns = ["phaseId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("phaseId"), Index("topicId"), Index("subjectId")]
)
data class StudyTaskEntity(
    @PrimaryKey val id: String,
    val phaseId: String,
    val topicId: String?,
    val subjectId: String?,
    val taskType: StudyTaskType,
    val priority: Priority,
    val status: TaskStatus,
    val plannedDateEpoch: Long,
    val estimatedMinutes: Int,
    val actualMinutes: Int,
    val completionPercentage: Float,
    val dependsOnTaskIds: String, // JSON array
    val notes: String?,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(
    tableName = "study_session",
    indices = [Index("taskId")]
)
data class StudySessionEntity(
    @PrimaryKey val id: String,
    val taskId: String,
    val startTimeEpoch: Long,
    val endTimeEpoch: Long?,
    val durationMinutes: Int,
    val targetDurationMinutes: Int,
    val outcome: SessionOutcome?,
    val notes: String?,
    val interruptions: Int,
    val createdAt: Long
)

@Entity(
    tableName = "question",
    indices = [
        Index("examId"),
        Index("topicId"),
        Index("subjectId")
    ]
)
data class QuestionEntity(
    @PrimaryKey val id: String,
    val examId: String,
    val subjectId: String,
    val chapterId: String,
    val topicId: String,
    val questionText: String,
    val correctOptionId: String,
    val explanation: String?,
    val type: QuestionType,
    val difficulty: Difficulty,
    val source: ContentSource,
    val sourceReference: String?,
    val year: Int?,
    val estimatedSeconds: Int,
    val marks: Float,
    val negativeMarks: Float,
    val tagsJson: String, // JSON array of strings
    val isVerified: Boolean,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(
    tableName = "question_option",
    foreignKeys = [ForeignKey(
        entity = QuestionEntity::class,
        parentColumns = ["id"],
        childColumns = ["questionId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("questionId")]
)
data class QuestionOptionEntity(
    @PrimaryKey val id: String,
    val questionId: String,
    val text: String,
    val isCorrect: Boolean,
    val orderIndex: Int
)

@Entity(tableName = "quiz")
data class QuizEntity(
    @PrimaryKey val id: String,
    val mode: QuizMode,
    val examId: String,
    val topicId: String?,
    val subjectId: String?,
    val questionCount: Int,
    val durationMinutes: Int,
    val correctMarks: Float,
    val incorrectMarks: Float,
    val unattemptedMarks: Float,
    val createdAt: Long
)

@Entity(
    tableName = "quiz_attempt",
    indices = [Index("quizId"), Index("userId")]
)
data class QuizAttemptEntity(
    @PrimaryKey val id: String,
    val quizId: String,
    val userId: String,
    val startTimeEpoch: Long,
    val endTimeEpoch: Long?,
    val score: Float,
    val maxScore: Float,
    val accuracy: Float,
    val isCompleted: Boolean,
    val createdAt: Long
)

@Entity(
    tableName = "question_attempt",
    indices = [Index("quizAttemptId"), Index("questionId")]
)
data class QuestionAttemptEntity(
    @PrimaryKey val id: String,
    val quizAttemptId: String,
    val questionId: String,
    val selectedOptionId: String?,
    val isCorrect: Boolean,
    val isSkipped: Boolean,
    val timeTakenMillis: Long,
    val marksEarned: Float
)

@Entity(
    tableName = "mastery_record",
    indices = [Index("topicId"), Index("userId")]
)
data class MasteryRecordEntity(
    @PrimaryKey val id: String,
    val topicId: String,
    val userId: String,
    val masteryState: MasteryState,
    val quizAccuracy: Float,
    val pyqAccuracy: Float,
    val mockAccuracy: Float,
    val totalAttempts: Int,
    val correctAttempts: Int,
    val averageTimeSecs: Float,
    val lastPracticedAt: Long?,
    val lastReviewedAt: Long?,
    val updatedAt: Long
)

@Entity(
    tableName = "revision_record",
    indices = [Index("topicId"), Index("userId")]
)
data class RevisionRecordEntity(
    @PrimaryKey val id: String,
    val topicId: String,
    val userId: String,
    val revisionNumber: Int,
    val scheduledDateEpoch: Long,
    val completedDateEpoch: Long?,
    val isCompleted: Boolean,
    val performanceScore: Float?,
    val nextRevisionDateEpoch: Long?,
    val createdAt: Long
)

@Entity(
    tableName = "recommendation",
    indices = [Index("userId"), Index("topicId")]
)
data class RecommendationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val topicId: String?,
    val subjectId: String?,
    val type: RecommendationType,
    val priority: Priority,
    val message: String,
    val actionRoute: String?,
    val reason: String,
    val isActioned: Boolean,
    val isDismissed: Boolean,
    val expiresAt: Long?,
    val createdAt: Long
)

@Entity(
    tableName = "entitlement",
    indices = [Index("userId")]
)
data class EntitlementEntity(
    @PrimaryKey val userId: String,
    val tier: EntitlementTier,
    val trialStartedAt: Long?,
    val trialEndsAt: Long?,
    val subscriptionStartedAt: Long?,
    val subscriptionExpiresAt: Long?,
    val autoRenewalEnabled: Boolean,
    val updatedAt: Long
)
