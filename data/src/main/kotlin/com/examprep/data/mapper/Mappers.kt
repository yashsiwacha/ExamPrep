package com.examprep.data.mapper

import com.examprep.data.local.entity.*
import com.examprep.domain.model.*

// ═══════════════════════════════════════════════════════════════════════════════
// MAPPERS: Entity ↔ Domain
// 
// Convention:
//   Entity.toDomain()  → converts Room entity to domain model
//   Domain.toEntity()  → converts domain model to Room entity
//
// These functions keep the mapping logic isolated and testable.
// ═══════════════════════════════════════════════════════════════════════════════

// ── Exam ─────────────────────────────────────────────────────────────────────

fun ExamEntity.toDomain(): Exam = Exam(
    id = id,
    name = name,
    shortName = shortName,
    category = category,
    authority = authority,
    syllabusVersion = syllabusVersion,
    markingScheme = MarkingScheme(
        correctMarks = correctMarks,
        incorrectMarks = incorrectMarks,
        unattemptedMarks = unattemptedMarks,
        hasNegativeMarking = hasNegativeMarking
    ),
    isActive = isActive
)

fun Exam.toEntity(): ExamEntity = ExamEntity(
    id = id,
    name = name,
    shortName = shortName,
    category = category,
    authority = authority,
    syllabusVersion = syllabusVersion,
    correctMarks = markingScheme.correctMarks,
    incorrectMarks = markingScheme.incorrectMarks,
    unattemptedMarks = markingScheme.unattemptedMarks,
    hasNegativeMarking = markingScheme.hasNegativeMarking,
    isActive = isActive,
    createdAt = System.currentTimeMillis(),
    updatedAt = System.currentTimeMillis()
)

// ── Subject ──────────────────────────────────────────────────────────────────

fun SubjectEntity.toDomain(): Subject = Subject(
    id = id,
    examId = examId,
    name = name,
    shortName = shortName,
    orderIndex = orderIndex,
    weightagePercent = weightagePercent,
    color = colorArgb
)

fun Subject.toEntity(): SubjectEntity = SubjectEntity(
    id = id,
    examId = examId,
    name = name,
    shortName = shortName,
    orderIndex = orderIndex,
    weightagePercent = weightagePercent,
    colorArgb = color,
    createdAt = System.currentTimeMillis(),
    updatedAt = System.currentTimeMillis()
)

// ── Chapter ──────────────────────────────────────────────────────────────────

fun ChapterEntity.toDomain(): Chapter = Chapter(
    id = id,
    subjectId = subjectId,
    name = name,
    orderIndex = orderIndex,
    estimatedHours = estimatedHours
)

fun Chapter.toEntity(): ChapterEntity = ChapterEntity(
    id = id,
    subjectId = subjectId,
    name = name,
    orderIndex = orderIndex,
    estimatedHours = estimatedHours,
    createdAt = System.currentTimeMillis(),
    updatedAt = System.currentTimeMillis()
)

// ── Topic ────────────────────────────────────────────────────────────────────

fun TopicEntity.toDomain(): Topic = Topic(
    id = id,
    chapterId = chapterId,
    name = name,
    orderIndex = orderIndex,
    importanceWeight = importanceWeight,
    estimatedHours = estimatedHours,
    masteryState = masteryState
    // Note: prerequisites stored as JSON — deserialize in Phase 1 if needed
)

fun Topic.toEntity(): TopicEntity = TopicEntity(
    id = id,
    chapterId = chapterId,
    name = name,
    orderIndex = orderIndex,
    importanceWeight = importanceWeight,
    estimatedHours = estimatedHours,
    masteryState = masteryState,
    prerequisiteTopicIds = "[]",  // Phase 1: serialize prerequisites list
    createdAt = System.currentTimeMillis(),
    updatedAt = System.currentTimeMillis()
)

// ── UserProfile ───────────────────────────────────────────────────────────────

fun UserProfileEntity.toDomain(): UserProfile = UserProfile(
    id = id,
    name = name,
    selectedExamId = selectedExamId,
    examDate = examDate,
    targetScore = targetScore,
    targetRank = targetRank,
    dailyStudyHours = dailyStudyHours,
    studyDaysPerWeek = studyDaysPerWeek,
    preferredSessionMinutes = preferredSessionMinutes,
    preferredStudyTime = preferredStudyTime,
    onboardingCompleted = onboardingCompleted,
    diagnosticCompleted = diagnosticCompleted,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun UserProfile.toEntity(): UserProfileEntity = UserProfileEntity(
    id = id,
    name = name,
    selectedExamId = selectedExamId,
    examDate = examDate,
    targetScore = targetScore,
    targetRank = targetRank,
    dailyStudyHours = dailyStudyHours,
    studyDaysPerWeek = studyDaysPerWeek,
    preferredSessionMinutes = preferredSessionMinutes,
    preferredStudyTime = preferredStudyTime,
    subjectProficiencyJson = "{}",  // Phase 1: serialize map
    onboardingCompleted = onboardingCompleted,
    diagnosticCompleted = diagnosticCompleted,
    createdAt = createdAt,
    updatedAt = updatedAt
)

// ── StudyPlan ─────────────────────────────────────────────────────────────────

fun StudyPlanEntity.toDomain(): StudyPlan = StudyPlan(
    id = id,
    userId = userId,
    examId = examId,
    examDate = examDate,
    generatedAt = generatedAt,
    status = status,
    completionPercentage = completionPercentage,
    daysAheadOrBehind = daysAheadOrBehind
)

fun StudyPlan.toEntity(): StudyPlanEntity = StudyPlanEntity(
    id = id,
    userId = userId,
    examId = examId,
    examDate = examDate,
    status = status,
    completionPercentage = completionPercentage,
    daysAheadOrBehind = daysAheadOrBehind,
    generatedAt = generatedAt,
    updatedAt = System.currentTimeMillis()
)

// ── StudyPhase ────────────────────────────────────────────────────────────────

fun StudyPhaseEntity.toDomain(): StudyPhase = StudyPhase(
    id = id,
    planId = planId,
    phaseType = phaseType,
    startDateEpoch = startDateEpoch,
    endDateEpoch = endDateEpoch,
    orderIndex = orderIndex,
    completionPercentage = completionPercentage
)

fun StudyPhase.toEntity(): StudyPhaseEntity = StudyPhaseEntity(
    id = id,
    planId = planId,
    phaseType = phaseType,
    startDateEpoch = startDateEpoch,
    endDateEpoch = endDateEpoch,
    orderIndex = orderIndex,
    completionPercentage = completionPercentage,
    createdAt = System.currentTimeMillis(),
    updatedAt = System.currentTimeMillis()
)

// ── StudyTask ─────────────────────────────────────────────────────────────────

fun StudyTaskEntity.toDomain(): StudyTask = StudyTask(
    id = id,
    phaseId = phaseId,
    topicId = topicId,
    subjectId = subjectId,
    taskType = taskType,
    priority = priority,
    status = status,
    plannedDateEpoch = plannedDateEpoch,
    estimatedMinutes = estimatedMinutes,
    actualMinutes = actualMinutes,
    completionPercentage = completionPercentage,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun StudyTask.toEntity(): StudyTaskEntity = StudyTaskEntity(
    id = id,
    phaseId = phaseId,
    topicId = topicId,
    subjectId = subjectId,
    taskType = taskType,
    priority = priority,
    status = status,
    plannedDateEpoch = plannedDateEpoch,
    estimatedMinutes = estimatedMinutes,
    actualMinutes = actualMinutes,
    completionPercentage = completionPercentage,
    dependsOnTaskIds = "[]",
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt
)

// ── StudySession ──────────────────────────────────────────────────────────────

fun StudySessionEntity.toDomain(): StudySession = StudySession(
    id = id,
    taskId = taskId,
    startTimeEpoch = startTimeEpoch,
    endTimeEpoch = endTimeEpoch,
    durationMinutes = durationMinutes,
    targetDurationMinutes = targetDurationMinutes,
    outcome = outcome,
    notes = notes,
    interruptions = interruptions,
    createdAt = createdAt
)

fun StudySession.toEntity(): StudySessionEntity = StudySessionEntity(
    id = id,
    taskId = taskId,
    startTimeEpoch = startTimeEpoch,
    endTimeEpoch = endTimeEpoch,
    durationMinutes = durationMinutes,
    targetDurationMinutes = targetDurationMinutes,
    outcome = outcome,
    notes = notes,
    interruptions = interruptions,
    createdAt = createdAt
)

// ── Question ──────────────────────────────────────────────────────────────────

fun QuestionEntity.toDomain(options: List<QuestionOptionEntity> = emptyList()): Question = Question(
    id = id,
    examId = examId,
    subjectId = subjectId,
    chapterId = chapterId,
    topicId = topicId,
    questionText = questionText,
    options = options.map { it.toDomain() },
    correctOptionId = correctOptionId,
    explanation = explanation,
    type = type,
    difficulty = difficulty,
    source = source,
    sourceReference = sourceReference,
    year = year,
    estimatedSeconds = estimatedSeconds,
    marks = marks,
    negativeMarks = negativeMarks,
    isVerified = isVerified,
    isActive = isActive
)

fun Question.toEntity(): QuestionEntity = QuestionEntity(
    id = id,
    examId = examId,
    subjectId = subjectId,
    chapterId = chapterId,
    topicId = topicId,
    questionText = questionText,
    correctOptionId = correctOptionId,
    explanation = explanation,
    type = type,
    difficulty = difficulty,
    source = source,
    sourceReference = sourceReference,
    year = year,
    estimatedSeconds = estimatedSeconds,
    marks = marks,
    negativeMarks = negativeMarks,
    tagsJson = "[]",
    isVerified = isVerified,
    isActive = isActive,
    createdAt = System.currentTimeMillis(),
    updatedAt = System.currentTimeMillis()
)

fun QuestionOptionEntity.toDomain(): QuestionOption = QuestionOption(
    id = id,
    questionId = questionId,
    text = text,
    isCorrect = isCorrect,
    orderIndex = orderIndex
)

fun QuestionOption.toEntity(): QuestionOptionEntity = QuestionOptionEntity(
    id = id,
    questionId = questionId,
    text = text,
    isCorrect = isCorrect,
    orderIndex = orderIndex
)

// ── Quiz ──────────────────────────────────────────────────────────────────────

fun QuizEntity.toDomain(): Quiz = Quiz(
    id = id,
    mode = mode,
    examId = examId,
    topicId = topicId,
    subjectId = subjectId,
    questionCount = questionCount,
    durationMinutes = durationMinutes,
    markingScheme = MarkingScheme(
        correctMarks = correctMarks,
        incorrectMarks = incorrectMarks,
        unattemptedMarks = unattemptedMarks
    ),
    createdAt = createdAt
)

fun Quiz.toEntity(): QuizEntity = QuizEntity(
    id = id,
    mode = mode,
    examId = examId,
    topicId = topicId,
    subjectId = subjectId,
    questionCount = questionCount,
    durationMinutes = durationMinutes,
    correctMarks = markingScheme.correctMarks,
    incorrectMarks = markingScheme.incorrectMarks,
    unattemptedMarks = markingScheme.unattemptedMarks,
    createdAt = createdAt
)

fun QuizAttemptEntity.toDomain(): QuizAttempt = QuizAttempt(
    id = id,
    quizId = quizId,
    userId = userId,
    startTimeEpoch = startTimeEpoch,
    endTimeEpoch = endTimeEpoch,
    score = score,
    maxScore = maxScore,
    accuracy = accuracy,
    isCompleted = isCompleted
)

fun QuizAttempt.toEntity(): QuizAttemptEntity = QuizAttemptEntity(
    id = id,
    quizId = quizId,
    userId = userId,
    startTimeEpoch = startTimeEpoch,
    endTimeEpoch = endTimeEpoch,
    score = score,
    maxScore = maxScore,
    accuracy = accuracy,
    isCompleted = isCompleted,
    createdAt = System.currentTimeMillis()
)

fun QuestionAttemptEntity.toDomain(): QuestionAttempt = QuestionAttempt(
    id = id,
    quizAttemptId = quizAttemptId,
    questionId = questionId,
    selectedOptionId = selectedOptionId,
    isCorrect = isCorrect,
    isSkipped = isSkipped,
    timeTakenMillis = timeTakenMillis,
    marksEarned = marksEarned
)

fun QuestionAttempt.toEntity(): QuestionAttemptEntity = QuestionAttemptEntity(
    id = id,
    quizAttemptId = quizAttemptId,
    questionId = questionId,
    selectedOptionId = selectedOptionId,
    isCorrect = isCorrect,
    isSkipped = isSkipped,
    timeTakenMillis = timeTakenMillis,
    marksEarned = marksEarned
)

// ── MasteryRecord ─────────────────────────────────────────────────────────────

fun MasteryRecordEntity.toDomain(): MasteryRecord = MasteryRecord(
    id = id,
    topicId = topicId,
    userId = userId,
    masteryState = masteryState,
    quizAccuracy = quizAccuracy,
    pyqAccuracy = pyqAccuracy,
    mockAccuracy = mockAccuracy,
    totalAttempts = totalAttempts,
    correctAttempts = correctAttempts,
    averageTimeSecs = averageTimeSecs,
    lastPracticedAt = lastPracticedAt,
    lastReviewedAt = lastReviewedAt,
    updatedAt = updatedAt
)

fun MasteryRecord.toEntity(): MasteryRecordEntity = MasteryRecordEntity(
    id = id,
    topicId = topicId,
    userId = userId,
    masteryState = masteryState,
    quizAccuracy = quizAccuracy,
    pyqAccuracy = pyqAccuracy,
    mockAccuracy = mockAccuracy,
    totalAttempts = totalAttempts,
    correctAttempts = correctAttempts,
    averageTimeSecs = averageTimeSecs,
    lastPracticedAt = lastPracticedAt,
    lastReviewedAt = lastReviewedAt,
    updatedAt = updatedAt
)

// ── RevisionRecord ────────────────────────────────────────────────────────────

fun RevisionRecordEntity.toDomain(): RevisionRecord = RevisionRecord(
    id = id,
    topicId = topicId,
    userId = userId,
    revisionNumber = revisionNumber,
    scheduledDateEpoch = scheduledDateEpoch,
    completedDateEpoch = completedDateEpoch,
    isCompleted = isCompleted,
    performanceScore = performanceScore,
    nextRevisionDateEpoch = nextRevisionDateEpoch,
    createdAt = createdAt
)

fun RevisionRecord.toEntity(): RevisionRecordEntity = RevisionRecordEntity(
    id = id,
    topicId = topicId,
    userId = userId,
    revisionNumber = revisionNumber,
    scheduledDateEpoch = scheduledDateEpoch,
    completedDateEpoch = completedDateEpoch,
    isCompleted = isCompleted,
    performanceScore = performanceScore,
    nextRevisionDateEpoch = nextRevisionDateEpoch,
    createdAt = createdAt
)

// ── Recommendation ────────────────────────────────────────────────────────────

fun RecommendationEntity.toDomain(): Recommendation = Recommendation(
    id = id,
    userId = userId,
    topicId = topicId,
    subjectId = subjectId,
    type = type,
    priority = priority,
    message = message,
    actionRoute = actionRoute,
    reason = reason,
    isActioned = isActioned,
    isDismissed = isDismissed,
    expiresAt = expiresAt,
    createdAt = createdAt
)

fun Recommendation.toEntity(): RecommendationEntity = RecommendationEntity(
    id = id,
    userId = userId,
    topicId = topicId,
    subjectId = subjectId,
    type = type,
    priority = priority,
    message = message,
    actionRoute = actionRoute,
    reason = reason,
    isActioned = isActioned,
    isDismissed = isDismissed,
    expiresAt = expiresAt,
    createdAt = createdAt
)

// ── Entitlement ───────────────────────────────────────────────────────────────

fun EntitlementEntity.toDomain(): Entitlement = Entitlement(
    userId = userId,
    tier = tier,
    trialStartedAt = trialStartedAt,
    trialEndsAt = trialEndsAt,
    subscriptionStartedAt = subscriptionStartedAt,
    subscriptionExpiresAt = subscriptionExpiresAt,
    autoRenewalEnabled = autoRenewalEnabled
)

fun Entitlement.toEntity(): EntitlementEntity = EntitlementEntity(
    userId = userId,
    tier = tier,
    trialStartedAt = trialStartedAt,
    trialEndsAt = trialEndsAt,
    subscriptionStartedAt = subscriptionStartedAt,
    subscriptionExpiresAt = subscriptionExpiresAt,
    autoRenewalEnabled = autoRenewalEnabled,
    updatedAt = System.currentTimeMillis()
)

// ── ExamContentBundle ─────────────────────────────────────────────────────────

// ContentBundle uses domain models directly (no entity mapping needed for import)
