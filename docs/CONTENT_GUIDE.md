# ExamPrep OS — Content & Question Bank Guide

**Version:** 1.0  
**Last Updated:** September 2025

> ⚠️ **READ BEFORE ADDING ANY QUESTIONS**  
> All questions must comply with the content rights policy in [ADR-007](ADR/ADR-007-content-rights.md).

---

## Content Pipeline Overview

```
Create/Source Question
       ↓
Content Review (accuracy check)
       ↓
Rights Verification (legal sign-off for non-ORIGINAL)
       ↓
isVerified = true
       ↓
QA Testing (answer validation)
       ↓
isActive = true  ← Only at this point is the question shown to users
```

---

## Question Format

Every question requires these fields:

```kotlin
Question(
    id = UUID.randomUUID().toString(),  // Generated
    examId = "exam_jee_main",
    subjectId = "subj_jee_physics",
    chapterId = "chap_mechanics",
    topicId = "topic_newtons_laws",
    
    text = "...",                        // Question text (LaTeX supported in future)
    questionType = QuestionType.MCQ,     // MCQ | NUMERICAL | ASSERTION_REASON | MATCH
    difficulty = Difficulty.MEDIUM,
    estimatedTimeSecs = 90,             // Expected time to solve
    marks = 4,                          // Full marks
    
    source = ContentSource.ORIGINAL,    // MANDATORY — never skip
    sourceReference = "Original - ExamPrep Physics Team v1.0", // or "JEE Main 2022 Session 1 Q14"
    isVerified = false,                 // Set to true after expert review
    isActive = false,                   // Set to true after QA
    
    explanation = "...",                // Solution explanation (REQUIRED)
    hint = "...",                       // Optional first-attempt hint
    tags = listOf("newton", "force", "acceleration"),
    
    importanceWeight = 0.8f             // 0.0–1.0; based on frequency in actual exams
)
```

---

## Exam Configuration

Exams are pre-seeded in the database. Each exam has:

### JEE Main
```kotlin
Exam(
    id = "exam_jee_main",
    name = "JEE Main",
    fullName = "Joint Entrance Examination (Main)",
    category = ExamCategory.ENGINEERING,
    markingScheme = MarkingScheme(
        correctMarks = 4f,
        incorrectMarks = -1f,     // Negative marking
        unattemptedMarks = 0f
    ),
    totalMarks = 300,
    durationMinutes = 180,
    subjects = listOf("Physics", "Chemistry", "Mathematics")
)
```

### NEET UG
```kotlin
Exam(
    id = "exam_neet",
    markingScheme = MarkingScheme(correctMarks = 4f, incorrectMarks = -1f),
    totalMarks = 720,
    durationMinutes = 200,
    subjects = listOf("Physics", "Chemistry", "Biology (Botany)", "Biology (Zoology)")
)
```

---

## Content Quality Standards

### For ORIGINAL questions:
- ✅ Single, unambiguous correct answer
- ✅ All 3 distractors (wrong options) are plausible and commonly confused with correct answer
- ✅ Explanation covers both why correct is right AND why distractors are wrong
- ✅ Reviewed by subject expert before `isVerified = true`
- ❌ No spelling errors in question or options
- ❌ No formatting inconsistency in symbols/units

### Difficulty calibration:
| Level | Expected accuracy | Expected time |
|-------|------------------|--------------|
| EASY | ≥ 75% of students | < 60 seconds |
| MEDIUM | 50–75% | 60–120 seconds |
| HARD | 25–50% | 90–180 seconds |
| VERY_HARD | < 25% | > 2 minutes |

---

## Phase-wise Question Bank Target

| Phase | JEE (per subject) | NEET (per subject) | Total target |
|-------|------------------|-------------------|-------------|
| Phase 0 (Scaffold) | 0 | 0 | 0 |
| Phase 1 (MVP) | 50 | 30 | 240 |
| Phase 3 (Practice) | 200 | 150 | 1,050 |
| Phase 5 (Full) | 500 | 400 | 2,700+ |

---

## LaTeX and Mathematical Notation

In MVP, question text is plain text. Mathematical expressions use Unicode where possible:
- `v² = u² + 2as` (superscript via Unicode)
- `F = ma`
- `ΔE = hν`

**Phase 3+:** Integrate a math rendering library (e.g., `io.github.nickelc:math-for-compose`) to support full LaTeX.

---

## Exam Seed Data (Pre-load)

The following exams are pre-seeded in `DatabaseSeedData.kt` (to be implemented in Phase 1):

1. JEE Main — Engineering entrance
2. JEE Advanced — IIT entrance
3. NEET UG — Medical entrance
4. UPSC CSE Prelims — Civil Services
5. CAT — MBA entrance
6. GATE (CS) — Graduate engineering
7. GATE (EE) — Graduate engineering (Electrical)
