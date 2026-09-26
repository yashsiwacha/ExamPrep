# ExamPrep OS — API / Engine Contracts

**Version:** 1.0  
**Last Updated:** September 2025

---

This document defines the contracts for all domain engine interfaces. These are the core business logic boundaries — implementations may change but these contracts are stable.

---

## StudyPlanningEngine

**Location:** `domain/engine/Engines.kt`  
**Purpose:** Generates and manages personalized study plans

### `generatePlan(profile, exam, masteryRecords): StudyPlan`

**Inputs:**
- `profile.examDate` — Must be in the future. Throws `IllegalArgumentException` if not.
- `profile.dailyStudyHours` — Clamped to range [1.0, 16.0]
- `profile.studyDaysPerWeek` — Clamped to range [1, 7]
- `masteryRecords` — May be empty (new student) → treats all topics as NOT_STARTED

**Output:** A `StudyPlan` with:
- `phases` non-empty (minimum 1 phase)
- All tasks have `plannedDateEpoch` between today and `examDate`
- Total estimated minutes across all tasks ≤ available study time

**Error Cases:**
- Exam date in the past → return `StudyPlan` with `status = AT_RISK`, single "Intensive" phase
- Available time insufficient for full syllabus → prioritize by `topic.importanceWeight`

### `rebalancePlan(plan, missedDays): StudyPlan`

Called when the app detects the student has missed `N` consecutive days.

**Rules:**
- If `missedDays ≤ 3` → compress remaining tasks (increase daily load slightly)
- If `missedDays > 3 && ≤ 7` → skip lowest-priority tasks in current phase
- If `missedDays > 7` → restructure plan, flag as `AT_RISK`

### `calculatePlanStatus(plan): PlanStatus`

| Condition | Status |
|-----------|--------|
| Behind by > 7 days | `AT_RISK` |
| Behind by 1–7 days | `BEHIND` |
| Within 1 day | `ON_TRACK` |
| Ahead by > 3 days | `AHEAD` |
| No tasks started | `NOT_STARTED` |

---

## MasteryEngine

**Location:** `domain/engine/Engines.kt`  
**Purpose:** Calculates topic mastery state from performance data

### `calculateMastery(current, attempts): MasteryRecord`

**Mastery Score Formula:**
```
masteryScore = (accuracy * 0.4) + (attemptBonus * 0.2) + (timeBonus * 0.15) + (recencyBonus * 0.15) + (pyqAccuracy * 0.1)

where:
  accuracy        = correctAttempts / totalAttempts * 100
  attemptBonus    = min(totalAttempts / 10, 1.0) * 100  (saturates at 10 attempts)
  timeBonus       = max(0, 100 - (avgTimeSecs - estimatedSecs) / estimatedSecs * 50)
  recencyBonus    = decays linearly from 100 (today) to 0 (30 days ago)
  pyqAccuracy     = pyqCorrect / pyqTotal * 100 (0 if no PYQ attempts)
```

**State Determination from Score:**
| Score | State |
|-------|-------|
| 0 | NOT_STARTED |
| 1–49 | IN_PROGRESS or PRACTICING |
| 50–84 | PRACTICING |
| 85–100 | MASTERED |

**Minimum Attempts:** A topic cannot reach MASTERED with fewer than 5 total attempts, regardless of accuracy.

---

## QuizScoringEngine

**Location:** `domain/engine/Engines.kt`  
**Purpose:** Scores quiz attempts applying exam-specific marking schemes

### `scoreAttempt(attempt, quiz): ScoredAttempt`

```
For each QuestionAttempt:
  if isSkipped:
    marksEarned = quiz.markingScheme.unattemptedMarks
  elif isCorrect:
    marksEarned = quiz.markingScheme.correctMarks * question.marks
  else:
    marksEarned = quiz.markingScheme.incorrectMarks * question.marks (negative for NM exams)

totalScore = Σ marksEarned
accuracy = correctCount / (totalAttempts - skippedCount) * 100
```

**Edge Cases:**
- All questions skipped → accuracy = 0, score = 0
- Negative total score is valid (heavy negative marking, all wrong)
- `maxScore` = questionCount × correctMarks

---

## RevisionEngine

**Location:** `domain/engine/Engines.kt`  
**Purpose:** Manages spaced repetition revision scheduling

### `getRevisionIntervalDays(revisionNumber, config): Int`

Default intervals: `[1, 3, 7, 14, 30, 60]`

If `performanceScore < config.minPerformanceToAdvance` (default 70):
- Do NOT advance to next interval
- Repeat current interval (same number of days)

### `computeNextRevision(record, performanceScore): RevisionRecord`

```
if revisionNumber >= config.maxRevisions (default 6):
  → No further revision scheduled (topic permanently mastered)
else if performanceScore >= minPerformanceToAdvance:
  nextRevisionNumber = record.revisionNumber + 1
  nextDate = today + intervalDays[nextRevisionNumber - 1]
else:
  nextRevisionNumber = record.revisionNumber  // stay at same level
  nextDate = today + intervalDays[nextRevisionNumber - 1]
```

---

## RecommendationEngine

**Location:** `domain/engine/Engines.kt`  
**Purpose:** Generates prioritized, rule-based study recommendations

### Rule Priority Order

1. `CATCH_UP` (CRITICAL) — checked first, overrides all others if plan is severely behind
2. `REVISE_TOPIC` (HIGH) — any topic below weak threshold with sufficient attempts
3. `TAKE_MOCK` (HIGH) — in mock phase with no recent mock
4. `PRACTICE_MORE` (MEDIUM) — topics between weak and strong thresholds
5. `TAKE_QUIZ` (MEDIUM) — no quiz activity in past N days
6. `REVIEW_MISTAKES` (MEDIUM) — high wrong rate in last quiz

### Output Contract

- Maximum `config.maxRecommendationsPerDay` recommendations returned
- Sorted by `Priority` (CRITICAL → HIGH → MEDIUM → LOW)
- Each recommendation has a non-null `reason` string (human-readable)
- Recommendations with `expiresAt` in the past are never returned
