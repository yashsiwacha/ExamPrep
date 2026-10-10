# Engineering Plan: Standard Examination Pattern & Difficulty Calibration for All Mock Tests

## 1. Problem Statement
The current mock test generation was previously constrained to a quick 30-question, 120-mark slice (10 questions per section) with arbitrary question selection that bypassed authentic exam patterns, duration limits, and difficulty distributions.

Students preparing for high-stakes national competitive examinations require:
1. **Accurate Examination Patterns**:
   - **JEE Main**: 75 questions (25 Physics, 25 Chemistry, 25 Mathematics), 300 marks, 180 minutes, (+4 / -1).
   - **NEET UG**: 180 questions (45 Physics, 45 Chemistry, 90 Biology), 720 marks, 200 minutes, (+4 / -1).
   - **UPSC CSE Prelims**: 100 questions (GS Paper I), 200 marks, 120 minutes, (+2 / -0.66).
   - **CAT (IIMs)**: 66 questions (24 VARC, 20 DILR, 22 QA), 198 marks, 120 minutes, (+3 / -1).
   - **GATE CS**: 65 questions (15 GA/Engineering Math, 50 CS Core), 100 marks, 180 minutes, (+2 / -0.66).
2. **Authentic Difficulty Balancing**:
   - Each exam must balance Easy, Medium, and Hard / Very Hard questions according to the actual national examination bell curve.
3. **Multi-Paper Mock Test Catalog**:
   - Support taking any of the 5 full mock tests per exam (Paper 1 through Paper 5).
   - Ensure the offline asset question banks (500 questions per exam = 2,500 questions total) are efficiently indexed and loaded into Room.

---

## 2. Technical Architecture & Design

### A. Preload & Question Bank Ingestion (`QuestionBankRepository.kt`)
- Provide `ensureExamPapersLoaded(examId: String, count: Int = 5)`:
  - Automatically loads bundled papers (1 to 5) for the selected exam into Room SQLite using batch insertion.
  - Ensures a complete question pool of 500 questions per exam is available.

### B. Standard Examination Pattern Configuration
Create standard exam pattern specs in `MockTestStore.kt`:
- **Pattern Constants**:
  - `totalQuestions`: Int
  - `durationMinutes`: Int
  - `positiveMarks`: Int
  - `negativeMarks`: Int
  - `totalMarks`: Int
  - Section question targets and difficulty quotas.

### C. Standard Mock Test Generation Engine (`MockTestStore.kt`)
- Upgrade `generateStandardMockTest` to support:
  1. Specific paper mock tests (`mockTestId` matching `paperId` or full syllabus mock test 1 to 5).
  2. Exact standard question count and section breakdown for every supported exam.
  3. Calibrated difficulty balancing: selecting Easy, Medium, and Hard questions in the prescribed ratios.
  4. Correct total marks and duration calculation matching official authorities (NTA, UPSC, IIM, IITs).

### D. Mock Test Selection & Navigation
- Enhance `MockTestViewModel` to respect `mockTestId` and load questions with calibrated difficulty.
- In `QuestionLibraryScreen.kt`, add a direct "Start Mock Test" button on downloaded/available papers so students can launch specific papers immediately.
- On `DashboardScreen.kt`, show the standard exam pattern details (75 Qs · 300 Marks · 180 Min for JEE Main, etc.).

---

## 3. Implementation Steps

1. **Step 1: Ingestion Expansion in `QuestionBankRepository`**:
   - Batch insert questions to avoid slow single-item transactions.
   - Add `ensureAllPapersLoadedForExam(examId)` to populate Room with up to 500 questions.
2. **Step 2: Difficulty & Pattern Calibration in `MockTestStore`**:
   - Implement `generateStandardMockTest(examId, mockTestId, allQuestions)` with strict standard examination pattern enforcement and difficulty distribution.
   - Adjust evaluation logic to compute percentiles based on actual total marks (e.g. out of 300 for JEE, 720 for NEET, 200 for UPSC, 198 for CAT, 100 for GATE).
3. **Step 3: ViewModel & Repository Query Alignment in `MockTestViewModel`**:
   - Fetch questions with section-based and difficulty-calibrated queries or full paper sets.
4. **Step 4: UI Enhancements for Multi-Mock Selection**:
   - Update `QuestionLibraryScreen.kt` to allow starting mock tests directly for any paper.
   - Update `DashboardScreen.kt` to show the standard exam pattern details.
5. **Step 5: Testing & Verification**:
   - Update and add comprehensive unit tests in `MockTestEngineTest.kt` verifying all 5 standard patterns.
   - Verify on Android emulator and run `./gradlew testDebugUnitTest assembleRelease`.
