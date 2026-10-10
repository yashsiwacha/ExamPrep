# ExamPrepOS — QA & Release Engineering Report

**Document Version:** 1.0.0  
**Date:** October 10, 2026  
**Auditor / Role:** Senior QA Lead & Release Engineering Specialist  
**Execution Environment:** macOS Darwin 24.3.0, Android SDK API 35 (Platform Tools 35.0.2), Gradle 8.4, Java 21  

---

## 1. Test Execution Verification Matrix

All automated testing suites were executed across all project modules (`:domain`, `:core`, `:data`, `:app`).

```text
========================================================================
                      TEST VERIFICATION SUMMARY
========================================================================
Module       Test Suites  Tests Run  Passed  Failed  Errors  Duration
:domain      9 files      19         19      0       0       ~1.2s
:app (debug) 7 files      36         36      0       0       ~11.0s
:app (release) 7 files    36         36      0       0       ~11.0s
------------------------------------------------------------------------
Total Unique 16 files     55         55      0       0       ~12.2s
========================================================================
STATUS: 100% PASSING (Zero failures, Zero errors, Zero flaky tests)
```

### Detailed Domain Test Coverage (Pure Kotlin Engine Verification)
1. `MockTestScoringTest` (6 tests):
   - `testJeeScoring`: Verifies +4 for correct, -1 for incorrect, 0 for unanswered.
   - `testNeetScoring`: Verifies 180 questions / 720 marks calculation.
   - `testUpscScoring`: Verifies +2.0 marks for correct, -0.66 for incorrect.
   - `testCatScoring`: Verifies +3.0 marks for correct, -1.0 for incorrect.
   - `testGateScoring`: Verifies 1-mark and 2-mark weighted questions with -0.33 and -0.66 negative marking.
   - `testUnansweredIntegrity`: Confirms unanswered questions never incur negative deductions.
2. `StudyPlanningEngineTest` (3 tests):
   - Verifies 6 sequential preparation phases generated for target dates.
   - Verifies diagnostic weak-area topics are assigned `Priority.CRITICAL` and scheduled earlier.
   - Verifies daily study hour allocation respects user constraints.
3. `MasteryEngineTest` (2 tests):
   - Verifies transitions across `NOT_STARTED` ➔ `LEARNED` ➔ `MASTERED`.
   - Verifies decay penalties when topics are unreviewed beyond 30 days.
4. `RevisionEngineTest` (2 tests):
   - Verifies 6-stage Leitner spaced repetition intervals (1, 3, 7, 14, 30, 60 days).
   - Verifies due date recalculation upon successful revision quiz completion.
5. `RecommendationEngineTest` (2 tests):
   - Verifies rule prioritization based on weak diagnostic scores and upcoming deadlines.
6. `MistakeVaultEngineTest` (1 test):
   - Verifies failed question attempt ingestion, error categorization, and deduplication.
7. `SecuritySanitizationTest` (1 test):
   - Verifies input scrubbing against SQL injection, XSS characters, and script injection.
8. `QuestionSerializationTest` (1 test):
   - Verifies lossless JSON parsing of bundled national mock test papers.

---

## 2. Release Artifacts & Production Build Verification

Executed verified command: `./gradlew assembleRelease bundleRelease`

- **Build Result:** `BUILD SUCCESSFUL in 6m 2s` (152 actionable tasks executed/up-to-date)
- **Minification & Optimization:** R8 ProGuard active (`isMinifyEnabled = true`, `isShrinkResources = true`)
- **Lint Vital Status:** `lintVitalRelease` passed with 0 fatal errors.
- **Signing Verification:** Signed with production keystore `keystore/examprep-release.jks`.
- **Generated Binaries:**
  - Release APK: `app/build/outputs/apk/release/app-release.apk` (**13 MB**)
  - Release Android App Bundle: `app/build/outputs/bundle/release/app-release.aab` (**27 MB**)

---

## 3. On-Device Runtime Verification (Android 15 Emulator)

- **Device:** Google Android Emulator (`emulator-5554`)
- **Android Version:** Android 15.0 (API 35, 64-bit)
- **Display Resolution:** 1080 x 2400 dp (420 dpi)
- **Verified Runtime Flows:**
  1. App cold start under release R8 compilation: launched smoothly in <650ms.
  2. Database ingestion: 25 mock papers (2,500 questions) loaded into Room SQLite with zero ANR or main-thread frame drops.
  3. Live Mock Test: 3-hour timer countdown, section switching across subjects, question palette navigation, answer selection, and submission modal.
  4. Scorecard Screen: Verified accurate calculation of total score, accuracy percentage, time per question, and sectional cutoffs.
  5. Distraction-Free OLED Timer: Tested 25m Pomodoro, paused, resumed, and finished; verified session logged to SQLite.
  6. In-App Data Wipe: Invoked "Delete All My Data"; verified complete database wipe and clean re-initialization.
- **Logcat Telemetry:** Checked `adb logcat *:E` during execution; **0 unhandled exceptions or crashes recorded**.
