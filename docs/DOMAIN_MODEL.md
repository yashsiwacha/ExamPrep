# ExamPrep OS — Domain Model Reference

**Version:** 1.0  
**Last Updated:** September 2025

---

## Entity Relationships

```
Exam
  └── Subject (1:N)
        └── Chapter (1:N)
              └── Topic (1:N)
                    ├── MasteryRecord (1:1 per user)
                    ├── RevisionRecord (1:N)
                    └── Question (1:N via topicId)

UserProfile (1 per install)
  └── StudyPlan (1:1)
        └── StudyPhase (1:N)
              └── StudyTask (1:N)
                    └── StudySession (1:N)

Quiz
  └── QuizAttempt (1:N)
        └── QuestionAttempt (1:N per question)

Recommendation (1:N per user)
Entitlement (1:1 per user)
```

---

## Core Enumerations

### MasteryState

The most important domain concept. A topic progresses through these states:

| State | Meaning | Triggers |
|-------|---------|---------|
| `NOT_STARTED` | Topic hasn't been opened | Default |
| `IN_PROGRESS` | Currently being studied | Session started on topic |
| `LEARNED` | Studied at least once | Session completed |
| `PRACTICING` | Active quiz practice happening | Quiz attempts > 0 |
| `MASTERED` | High accuracy + sufficient attempts | Accuracy ≥ 85%, attempts ≥ 5 |
| `REVISION_DUE` | Mastered but needs review | Scheduled revision date exceeded |

### StudyPhaseType (in order)

1. `FOUNDATION` — Building base knowledge
2. `CORE_LEARNING` — Deep study of all topics
3. `PRACTICE` — Active question solving
4. `REVISION` — Spaced repetition pass
5. `PYQ` — Previous Year Questions
6. `MOCK_TESTS` — Full exam simulation
7. `FINAL_REVISION` — Last-pass revision

### Difficulty

| Level | Weight | Description |
|-------|--------|-------------|
| `EASY` | 1.0× | Foundational concepts |
| `MEDIUM` | 1.5× | Application-level |
| `HARD` | 2.0× | Analytical reasoning |
| `VERY_HARD` | 2.5× | Multi-concept synthesis |

### ContentSource (IP compliance)

| Value | Description | Usage |
|-------|-------------|-------|
| `ORIGINAL` | Created by ExamPrep team | Preferred |
| `OFFICIAL` | Released by exam authority | With attribution |
| `LICENSED` | Rights explicitly obtained | Requires agreement |
| `USER_GENERATED` | User-submitted (future) | Needs moderation |
| `THIRD_PARTY_REFERENCE` | Reference only | MUST NOT be shown in-app |

---

## Key Domain Models

### UserProfile

The central personalization model. Updates as student uses the app.

```kotlin
data class UserProfile(
    val id: String,                                    // UUID
    val name: String?,                                 // Optional — not required
    val selectedExamId: String?,                       // Links to Exam
    val examDate: Long?,                               // Epoch millis — the North Star
    val targetScore: Float?,                           // Optional target score
    val targetRank: Int?,                              // Optional target rank
    val dailyStudyHours: Float = 4f,                   // Input for plan generation
    val studyDaysPerWeek: Int = 6,                     // Input for plan generation
    val preferredSessionMinutes: Int = 50,             // Focus session length
    val subjectProficiency: Map<String, ProficiencyLevel>, // Onboarding assessment
    val onboardingCompleted: Boolean,
    val diagnosticCompleted: Boolean
)
```

### StudyPlan

The master preparation roadmap:

```kotlin
data class StudyPlan(
    val id: String,
    val userId: String,
    val examId: String,
    val examDate: Long,                    // Must match UserProfile.examDate
    val status: PlanStatus,               // ON_TRACK | BEHIND | AHEAD | AT_RISK
    val completionPercentage: Float,       // 0–100
    val daysAheadOrBehind: Int,           // Positive = ahead, negative = behind
    val phases: List<StudyPhase>
)
```

### MasteryRecord

Per-topic learning state:

```kotlin
data class MasteryRecord(
    val topicId: String,
    val userId: String,
    val masteryState: MasteryState,
    val quizAccuracy: Float,              // 0–100%
    val pyqAccuracy: Float,               // 0–100%
    val mockAccuracy: Float,              // 0–100%
    val totalAttempts: Int,
    val correctAttempts: Int,
    val averageTimeSecs: Float,
    val lastPracticedAt: Long?
)
```

### Question (Content Rights Critical)

```kotlin
data class Question(
    val id: String,
    val source: ContentSource,            // MUST be set. Never default to OFFICIAL.
    val sourceReference: String?,         // "JEE Main 2019 Paper 1 Q12" or "Original - ExamPrep v1"
    val isVerified: Boolean,              // Must be manually verified before activation
    val isActive: Boolean                 // Only active questions shown to users
    // ... other fields
)
```

---

## Marking Scheme

ExamPrep OS supports configurable marking schemes per exam:

| Exam | Correct | Incorrect | Unattempted |
|------|---------|-----------|-------------|
| JEE Main (MCQ) | +4 | -1 | 0 |
| JEE Main (Numerical) | +4 | 0 | 0 |
| NEET | +4 | -1 | 0 |
| UPSC Prelims | +2 | -0.66 | 0 |
| CAT | +3 | -1 | 0 |

---

## Planning Algorithm (Conceptual)

The `StudyPlanningEngine` uses this logic to generate a plan:

```
Input:
  - examDate
  - availableHoursPerDay
  - studyDaysPerWeek
  - subjectProficiency (per subject)
  - syllabusTopics (with estimatedHours and importanceWeight)
  - currentMasteryRecords

Algorithm:
1. Calculate total available study hours = (examDate - today) * hoursPerDay * (daysPerWeek/7)
2. Calculate total required hours = Σ(topic.estimatedHours * (1 - masteryMultiplier))
   where masteryMultiplier = 1.0 for NOT_STARTED, 0.5 for IN_PROGRESS, 0.0 for MASTERED
3. If total required > available → flag as AT_RISK, prioritize by importanceWeight
4. Assign phase durations based on exam proximity:
   - > 6 months: heavy Foundation + Core
   - 3–6 months: Core + Practice
   - 1–3 months: PYQ + Revision + Mock
   - < 1 month: Revision only + Mock
5. Generate daily tasks within each phase
6. Assign priority scores to each task
```

---

## Revision Intervals (Spaced Repetition)

Default schedule (number of days after first learning):

| Revision # | Days After Previous | Cumulative |
|-----------|---------------------|-----------|
| 1st | 1 day | Day 1 |
| 2nd | 3 days | Day 4 |
| 3rd | 7 days | Day 11 |
| 4th | 14 days | Day 25 |
| 5th | 30 days | Day 55 |
| 6th | 60 days | Day 115 |

If performance on a revision quiz < 70%, the interval does not advance (repeat same interval).
After 6th successful revision, topic is considered **permanently mastered**.

---

## Recommendation Rules

The `RecommendationEngine` applies these rules in priority order:

| Rule | Condition | Action | Priority |
|------|-----------|--------|---------|
| R1: Revise | Topic accuracy < 50% AND attempts ≥ 5 | `REVISE_TOPIC` | HIGH |
| R2: Practice | Topic accuracy 50–70% | `PRACTICE_MORE` | MEDIUM |
| R3: Quiz | No quiz in past 3 days | `TAKE_QUIZ` | MEDIUM |
| R4: Catch-up | Plan behind by > 3 days | `CATCH_UP` | CRITICAL |
| R5: Mock | In MOCK_TESTS phase AND no mock in 7 days | `TAKE_MOCK` | HIGH |
| R6: Review | Wrong answers in last quiz > 30% | `REVIEW_MISTAKES` | MEDIUM |
