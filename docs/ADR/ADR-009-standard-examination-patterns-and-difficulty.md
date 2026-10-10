# ADR-009: Standard Examination Patterns & Calibrated Difficulty Profiles

**Status:** Accepted  
**Date:** October 2026  
**Deciders:** Lead Android Engineer, Product Lead, Head of Curriculum & Assessment

---

## Context

Prior implementations of the mock test engine utilized an arbitrary 30-question, 120-mark slice (10 questions per section) for all exams. In competitive entrance exams (JEE Main, NEET UG, UPSC CSE, CAT, GATE CS), students depend on high-fidelity simulation of:
1. Exact examination sections and question volume.
2. Official positive and negative marking rules.
3. Realistic time budgets (e.g. 180 min, 200 min, 120 min).
4. Authentic difficulty distributions (Easy, Medium, Hard, Very Hard) aligned with national testing agencies (NTA, UPSC, IIMs, IITs).

Arbitrary slices fail to cultivate students' pacing, stamina, sectional time management, and negative marking discipline.

---

## Decision

**All mock tests in ExamPrepOS must conform strictly to authentic national examination patterns and difficulty profiles.**

### Standard Pattern Matrix

| Exam ID | Exam Authority | Sections | Questions | Total Marks | Duration | Marking Scheme | Difficulty Profile |
|---------|---------------|----------|-----------|-------------|----------|----------------|-------------------|
| `exam_jee_main` | NTA | Physics (25), Chemistry (25), Math (25) | 75 | 300 | 180 min | +4 / -1 | Easy 28%, Med 52%, Hard 20% |
| `exam_neet_ug` | NTA | Physics (45), Chemistry (45), Biology (90) | 180 | 720 | 200 min | +4 / -1 | Easy 30%, Med 50%, Hard 20% |
| `exam_upsc_cse` | UPSC | General Studies Paper I (100) | 100 | 200 | 120 min | +2 / -0.66 | Med 50%, Hard 30%, Very Hard 20% |
| `exam_cat_mba` | IIMs | VARC (24), DILR (20), QA (22) | 66 | 198 | 120 min | +3 / -1 | Med 30%, Hard 50%, Very Hard 20% |
| `exam_gate_cs` | IITs / IISc | GA & Maths (15), CS Core (50) | 65 | 100 | 180 min | +2 / -0.66 | Easy 25%, Med 50%, Hard 25% |

### Multi-Paper & Offline Paper Catalog

1. **5 Papers per Exam Pre-Packaged**: All 25 curated papers (5 per exam × 100 questions = 2,500 total questions) bundled in offline assets are indexed in SQLite Room database.
2. **Dual-Mode Mock Tests**:
   - **All-India Standard Mock**: Dynamically synthesizes the full official pattern across all syllabus topics with calibrated difficulty quotas.
   - **Paper-Specific Practice Mocks**: Allows launching any of the 5 individual papers (100 curated questions) with full CBT timer and answer-key scoring.

---

## Implementation Details

1. **`MockTestStore.generateStandardMockTest`**: Enforces strict question quotas per section and executes difficulty bucket allocation (`selectWithDifficulty`).
2. **`QuestionBankRepository.ensureAllPapersLoadedForExam`**: Loads offline assets in batch transactions into Room so the question bank has sufficient breadth (500 questions) to fulfill 180-question NEET and 75-question JEE tests.
3. **`MockTestViewModel`**: Automatically detects selected exam, calibrates timer duration, questions, and difficulty quotas.
4. **`QuestionLibraryScreen`**: Provides immediate "Take Mock Test" capability on every paper.
5. **`DashboardScreen`**: Highlights the exact examination pattern (Questions, Marks, Time, Marking) tailored to the active exam profile.

---

## Consequences

**Positive:**
- Students experience realistic test fatigue, time pressure, and scoring expectations identical to actual exam day.
- Sectional performance breakdowns reflect official syllabus weightages.
- Eliminates inaccurate scoring artifacts.

**Negative:**
- Generating a 180-question test requires greater memory and Room query efficiency (mitigated by batch inserts and indexed querying).
