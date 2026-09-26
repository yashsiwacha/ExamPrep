# ExamPrep OS — Database Schema Reference

**Version:** 1.0  
**Last Updated:** September 2025  
**Room DB Version:** 1

---

## Tables Overview

| Table | Rows (Est.) | FK Parent | Purpose |
|-------|------------|-----------|---------|
| `user_profile` | 1 | — | Student profile and preferences |
| `exam` | 10–20 | — | Exam configuration registry |
| `subject` | 2–10 per exam | `exam` | Subject definitions |
| `chapter` | 5–30 per subject | `subject` | Chapter definitions |
| `topic` | 5–20 per chapter | `chapter` | Topic definitions + mastery state |
| `study_plan` | 1 per user | `exam` | Active preparation plan |
| `study_phase` | 5–7 per plan | `study_plan` | Plan phase segments |
| `study_task` | 200–500 | `study_phase` | Individual daily tasks |
| `study_session` | 1–5 per task | `study_task` | Timed study session records |
| `question` | 500–5000 | — | Question bank |
| `question_option` | 4 per question | `question` | MCQ option records |
| `quiz` | 1 per session | — | Quiz configuration |
| `quiz_attempt` | 1 per quiz | `quiz` | Quiz attempt record |
| `question_attempt` | N per attempt | `quiz_attempt` | Per-question answer record |
| `mastery_record` | 1 per (user, topic) | `topic` | Topic mastery state |
| `revision_record` | 1–6 per topic | `topic` | Spaced repetition schedule |
| `recommendation` | 0–10 active | — | Active study recommendations |
| `entitlement` | 1 per user | — | Subscription/tier state |

---

## Key Schema Decisions

### 1. Cascade Deletes

All foreign keys use `onDelete = ForeignKey.CASCADE`:
- Deleting an `exam` removes all `subjects`, `chapters`, `topics`
- Deleting a `study_plan` removes all `phases` and `tasks`
- Deleting a `quiz_attempt` removes all `question_attempts`

This keeps the database clean without manual cleanup code.

### 2. Indexes

All foreign key columns are indexed. Additional indexes:

| Table | Indexed Columns | Reason |
|-------|----------------|--------|
| `study_task` | `phaseId`, `topicId`, `subjectId` | Timeline and today's tasks queries |
| `quiz_attempt` | `userId`, `quizId` | User history queries |
| `mastery_record` | `userId`, `topicId` | Per-user, per-topic lookup |
| `question` | `examId`, `topicId`, `subjectId` | Random question selection |
| `recommendation` | `userId`, `topicId` | Active recommendation lookup |

### 3. TypeConverters

Enums are stored as `name()` strings (e.g., `"MASTERED"`, `"NOT_STARTED"`).

**Why strings over ordinals?**  
- Ordinals break if enum entries are reordered (a common refactoring)
- Strings are human-readable in SQLite browser tools
- Name-based `valueOf()` is stable across versions

### 4. Timestamps

All timestamps are stored as `Long` (epoch milliseconds). No `Date` or `LocalDate` objects in entities. Time zone handling is the responsibility of the UI layer.

---

## Critical Queries

### Today's Tasks (Dashboard)
```sql
SELECT st.* FROM study_task st
INNER JOIN study_phase sp ON st.phaseId = sp.id
INNER JOIN study_plan spl ON sp.planId = spl.id
WHERE spl.userId = :userId
  AND st.plannedDateEpoch >= :startOfDay
  AND st.plannedDateEpoch < :endOfDay
ORDER BY st.priority DESC, st.plannedDateEpoch ASC
```

### Random Questions for Quiz
```sql
SELECT * FROM question
WHERE examId = :examId
  AND (:topicId IS NULL OR topicId = :topicId)
  AND (:difficulty IS NULL OR difficulty = :difficulty)
  AND isActive = 1
ORDER BY RANDOM()
LIMIT :count
```

### Weak Topics (for Recommendations)
```sql
SELECT * FROM mastery_record
WHERE userId = :userId
  AND quizAccuracy < :threshold
```

### Revision Due Today
```sql
SELECT * FROM revision_record
WHERE userId = :userId
  AND isCompleted = 0
  AND scheduledDateEpoch <= :now
ORDER BY scheduledDateEpoch ASC
```

---

## Migration Strategy

- **Schema export** is enabled (`exportSchema = true` in `@Database`)
- All exports are committed to `schemas/` directory (to be configured in `data/build.gradle.kts`)
- Every schema change REQUIRES a `Migration` object
- `fallbackToDestructiveMigration()` is NEVER used in production builds
- Automated migration via `@AutoMigration` is preferred for simple column additions

### Migration Template
```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE topic ADD COLUMN newColumn TEXT")
    }
}
```
