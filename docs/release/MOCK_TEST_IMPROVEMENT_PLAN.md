# Mock Test Improvement & Bug Fix Plan

## Objective
Address student pain points in the ExamPrepOS Full Mock Test feature:
1. **Explain and fix why there are only 8 questions in the mock test** (expand to realistic, balanced 30+ question simulation across all exams).
2. **Fix missing submission button** (ensure Submit button is prominent, unclipped, and accessible in the Top Bar, Bottom Bar, and Question Palette sheet).
3. **Implement auto-submission when timer expires** (automatically evaluate answers, persist attempt results, and navigate to results when time reaches 0).

---

## Root Causes Identified

### 1. Only 8 Questions in Mock Test
- **Root Cause A:** Initial database seed data (`DatabaseSeedData.kt`) only contained 16 minimal placeholder questions across 5 exams, with exactly 8 questions authored for JEE Main (4 Physics, 2 Chemistry, 2 Maths) and 2 for each other exam.
- **Root Cause B:** The rich 100-question papers (5 per exam = 2,500 questions total in `assets/content/`) were only configured for manual download via `QuestionBankRepository` from the Question Library screen.
- **Root Cause C:** `MockTestStore.generateStandardMockTest` hardcoded `"subj_jee_phy"` (typo with `subj_` instead of `sub_`) and rigid Physics/Chem/Math section buckets, preventing dynamic subject distribution and non-JEE exam support.

### 2. No Submission Button
- **Root Cause A:** In `MockTestTopBar`, the title `Row` lacked `Modifier.weight(1f)` and `TextOverflow.Ellipsis`. On standard mobile viewport widths, long titles pushed the right-hand action row (Timer, Palette, Submit button) off-screen or clipped it.
- **Root Cause B:** `MockTestBottomBar` completely lacked a Submit button. Standard CBT testing UX (NTA, TCS-iON) expects a primary submit action in the bottom controls or on the final question.
- **Root Cause C:** The Question Palette sheet (`showPaletteSheet`) showed the grid and legend but had no "Submit Test" button at the bottom.

### 3. Missing Auto-Submission
- **Root Cause A:** In `MockTestViewModel.kt`, `startTimer()` simply terminated its loop when `remainingSeconds <= 0` without invoking `submitTest()`.
- **Root Cause B:** There was no reactive event/state stream (`submittedAttemptId`) triggering navigation when the timer naturally timed out.

---

## Action Plan

### Step 1: Preload Question Bank & Dynamic Question Population
- In `QuestionBankRepository.kt`: Add a helper `ensureDefaultPaperLoaded(examId: String)` that automatically parses and inserts the bundled 100-question Paper 1 for the selected exam if not already downloaded.
- In `MockTestViewModel.kt`: Inject `QuestionBankRepository` and ensure Paper 1 is loaded if the exam question count in Room is insufficient.
- In `MockTestStore.kt`: Update `generateStandardMockTest` to construct dynamic, balanced sections with 30-45 questions (10-15 per section for JEE, NEET, UPSC, CAT, GATE) with correct subject IDs (`sub_jee_phy`, `sub_jee_chem`, `sub_jee_math`, etc.).

### Step 2: Submission UX Improvements
- In `MockTestScreen.kt`:
  - `MockTestTopBar`: Constrain title with `Modifier.weight(1f)` and `overflow = TextOverflow.Ellipsis` to ensure the top-right "Submit" button is always clearly visible.
  - `MockTestBottomBar`: Add a permanent "Submit" action and transform "Save & Next" into "Submit Test" on the final question.
  - Question Palette sheet: Add a full-width "Submit Mock Test" button displaying answered count.

### Step 3: Auto-Submission on Timer Expiry
- In `MockTestViewModel.kt`:
  - Add `isTimeOver: Boolean` and `submittedAttemptId: String?` to `MockTestUiState`.
  - In `startTimer()`, when `remainingSeconds <= 0`, invoke `autoSubmitTest()`, evaluate answers, save attempt result to `MockTestStore`, and populate `submittedAttemptId`.
- In `MockTestScreen.kt`:
  - Add `LaunchedEffect(uiState.submittedAttemptId)` to navigate to `onTestSubmitted(attemptId)` immediately upon submission (manual or auto).

### Step 4: Verification & Testing
- Unit test in `MockTestViewModelTest` or `MockTestScoringTest` verifying auto-submit and question counts.
- Device testing on `emulator-5554` verifying:
  - 30 questions displayed across Physics, Chemistry, and Mathematics (10 each).
  - Submit button clearly visible and operable in top bar, bottom bar, and palette.
  - Timer expiration triggers automatic submission and directs to results.
- Production build compilation (`./gradlew assembleRelease`).
