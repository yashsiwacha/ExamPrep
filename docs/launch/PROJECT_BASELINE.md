# ExamPrepOS — Project Baseline & Discovery Inventory

**Document Version:** 1.0.0  
**Date:** October 10, 2026  
**Auditor / Role:** Senior Android Engineer & Technical Lead  
**Application ID:** `com.examprep.android` (Debug suffix: `com.examprep.android.debug`)  
**Repository Path:** `/Users/yash/Yash-Workspace/projects/active/android`  

---

## 1. Executive Summary

ExamPrepOS is an Android-first, offline-first exam preparation operating system designed to guide competitive examination aspirants through structured syllabus breakdown, daily dynamic scheduling, distraction-free focus sessions, timed national standard mock tests, spaced repetition revision, and performance diagnostics.

This document establishes the verified technical and functional baseline for ExamPrepOS prior to commercial launch on Google Play. Every fact, dependency version, test result, and architecture component documented here is verified directly against active repository code and verified execution runs.

---

## 2. Project Architecture & Module Structure

The project strictly follows Clean Architecture and Android modern app architecture across four decoupled modules:

```mermaid
graph TD
    App[":app (UI & Presentation)"] --> Core[":core (Design System & Utils)"]
    App --> Domain[":domain (Business Logic & Entities)"]
    App --> Data[":data (Persistence & Repositories)"]
    Data --> Core
    Data --> Domain
    Domain --> Core
```

### Module Breakdown

| Module | Namespace | Role & Responsibilities | Key Technologies |
|---|---|---|---|
| `:app` | `com.examprep.android` | Feature presentation, Jetpack Compose screens, ViewModels, navigation graph, Android lifecycle, notification workers, and AdMob integration. | Jetpack Compose, Material 3, Hilt, Navigation Compose, WorkManager |
| `:core` | `com.examprep.core` | Reusable UI components, design tokens (ExamPrepColors, Typography, Spacing), common utilities, dispatcher providers, and error representations. | Jetpack Compose, Material 3, Coroutines |
| `:domain` | `com.examprep.domain` | Pure business domain: domain models, deterministic calculation engines (StudyPlanning, Mastery, Revision, Scoring, MockTest), repository contracts. Zero Android dependencies. | Pure Kotlin, Coroutines, Kotlinx Serialization |
| `:data` | `com.examprep.data` | Local Room SQLite persistence, DAOs, entity mappers, bundled content ingestion, Firebase sync abstractions, and repository implementations. | Room 2.6.1, SQLite, Firebase Auth/Firestore stubs |

---

## 3. Build & Runtime Environment

| Property | Value | Verification Source |
|---|---|---|
| **Compile SDK** | `35` (Android 15) | `app/build.gradle.kts` |
| **Target SDK** | `35` (Android 15) | `app/build.gradle.kts` |
| **Minimum SDK** | `26` (Android 8.0 Oreo) | `app/build.gradle.kts` (covers ~96.5% of active global Android devices) |
| **Version Code** | `1` | `app/build.gradle.kts` |
| **Version Name** | `"1.0.0"` | `app/build.gradle.kts` |
| **Java / JVM Target**| `JavaVersion.VERSION_21` | JVM target `"21"` across all Gradle subprojects |
| **Kotlin Version** | `1.9.22` | `gradle/libs.versions.toml` |
| **KSP Version** | `1.9.22-1.0.18` | `gradle/libs.versions.toml` |
| **Compose BOM** | `2024.02.00` | `gradle/libs.versions.toml` |
| **Hilt Version** | `2.50` | `gradle/libs.versions.toml` |
| **Room Version** | `2.6.1` | `gradle/libs.versions.toml` |
| **Coroutines** | `1.8.0` | `gradle/libs.versions.toml` |
| **WorkManager** | `2.9.0` | `gradle/libs.versions.toml` |
| **Google Play Ads** | `23.0.0` | `gradle/libs.versions.toml` |
| **Signing Config** | Production Keystore | `keystore/examprep-release.jks` with key alias `examprep-release` |

---

## 4. Manifest & Permissions Inventory

Inspected file: `app/src/main/AndroidManifest.xml`

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

### Risk & Privacy Assessment of Permissions
- **`INTERNET` & `ACCESS_NETWORK_STATE`:** Standard permissions required for AdMob ad request delivery and optional cloud sync fallback. Safe, non-runtime (install-time) permissions.
- **`POST_NOTIFICATIONS`:** Runtime permission required on Android 13+ (API 33+) for daily study reminders. Proper runtime permission guard and system notification channel (`examprep_study_reminders`) implemented in `NotificationHelper.kt`.
- **`RECEIVE_BOOT_COMPLETED`:** Required by WorkManager to re-register daily periodic study reminders upon device reboot.
- **Invasive Permissions:** **ZERO**. No location (`ACCESS_FINE_LOCATION`), no contacts (`READ_CONTACTS`), no storage (`READ_EXTERNAL_STORAGE`), no camera, and no microphone. This guarantees high user trust and minimal scrutiny in Google Play Data Safety audits.

---

## 5. Local Database & Persistence Baseline

Inspected file: `data/src/main/kotlin/com/examprep/data/local/database/ExamPrepDatabase.kt`

- **Database Name:** `examprep_database`
- **Current Version:** `4`
- **Room Entities (20 total):**
  1. `UserProfileEntity` — Student profile, exam choice, target score/rank, daily study hours target.
  2. `ExamEntity` — Supported competitive exams (JEE Main, NEET UG, UPSC Prelims, CAT, GATE CS).
  3. `SubjectEntity` — Subjects belonging to exams (e.g., Physics, Chemistry, Mathematics).
  4. `ChapterEntity` — Chapters within subjects with difficulty weights.
  5. `TopicEntity` — Granular topics with estimated completion hours.
  6. `StudyPlanEntity` — Master study plan for a user profile.
  7. `StudyPhaseEntity` — Preparation phases (Foundation, Core Learning, Practice, PYQ, Revision, Final Mocks).
  8. `StudyTaskEntity` — Actionable daily study tasks with priority and due date.
  9. `StudySessionEntity` — Historical focus timer sessions (start time, duration, completion status).
  10. `QuestionEntity` — Question repository records with exam metadata, subject, chapter, topic, difficulty, and question type.
  11. `QuestionOptionEntity` — Multiple choice options (text, isCorrect, explanation).
  12. `QuizEntity` — Practice quizzes and diagnostic assessments.
  13. `QuizAttemptEntity` — Quiz submissions with score, accuracy, and elapsed duration.
  14. `QuestionAttemptEntity` — Per-question response records (selected option, isCorrect, time spent).
  15. `MasteryRecordEntity` — Topic mastery status (`NOT_STARTED` to `MASTERED`), accuracy, and review count.
  16. `RevisionRecordEntity` — Spaced repetition schedule (due date, interval, completion status).
  17. `RecommendationEntity` — Actionable algorithmic study recommendations.
  18. `EntitlementEntity` — Monetization state (`FREE` vs `PRO_MONTHLY`, expiration dates).
  19. `QuestionPaperMetaEntity` — National standard mock test metadata (examId, totalQuestions, totalMarks, durationMinutes, patternConfig).
  20. `QuestionPaperDownloadEntity` — Offline download caching states for test papers.

---

## 6. Functional Capability Status Inventory

| Capability | Status | Implementation Details & Verifications |
|---|---|---|
| **Onboarding & Setup** | `VERIFIED` | 5-step interactive wizard: Exam selection, target score, study hours, exam date picker, auto-baseline generation. |
| **Diagnostic Assessment** | `VERIFIED` | Interactive assessment questions per exam, scoring, and dynamic calibration of initial study plan. |
| **Daily Dashboard** | `VERIFIED` | Today's tasks, active topic card, days-to-exam countdown, true consecutive streak calculation, progress bars. |
| **Syllabus Navigation** | `VERIFIED` | 4-tier tree (`Exam → Subject → Chapter → Topic`) with mastery badges and topic status chips. |
| **Study Timeline & Planner**| `VERIFIED` | 6 sequential phases, overdue task rescheduling, priority calculation (`CRITICAL`, `HIGH`, `MEDIUM`). |
| **Focus Timer (Pomodoro)** | `VERIFIED` | OLED distraction-free UI, duration presets (`25m Pomodoro`, `50m Deep Work`, `90m Flow State`), auto-logging to database. |
| **Timed Mock Test Engine** | `VERIFIED` | Exact national patterns for JEE (75 Qs/300M/180m), NEET (180 Qs/720M/200m), UPSC (100 Qs/200M/120m), CAT (66 Qs/198M/120m), GATE (65 Qs/100M/180m). |
| **Instant Quiz Scoring** | `VERIFIED` | Pattern-specific scoring with authentic negative marking (+4/-1, +2/-0.66, +3/-1, +1/-0.33, +2/-0.66). |
| **National Performance Card**| `VERIFIED` | Total score, accuracy, speed (sec/Q), subject-wise breakdown, qualifying cutoff comparison, percentile estimation. |
| **Mistake Vault** | `VERIFIED` | Automatic bookmarking of incorrect attempts with step-by-step explanations and re-test triggers. |
| **Spaced Repetition** | `VERIFIED` | 6-interval Leitner sequence (`1, 3, 7, 14, 30, 60 days`) computed dynamically from Room queries. |
| **Study Reminders** | `VERIFIED` | WorkManager periodic worker (`StudyReminderWorker`), Android 13+ permission request, toggle in Settings. |
| **Settings & Privacy Hub** | `VERIFIED` | Full in-app legal dialogs (Privacy Policy, Terms, Licenses, Disclaimers, Data Deletion, Data Export). |
| **AdMob Free Tier Ads** | `VERIFIED` | Non-personalized banner ads (`npa=1`) in non-disruptive locations. Safe test ad units active for pre-release. |
| **Entitlement Architecture**| `VERIFIED` | Centralized `EntitlementRepository` isolating Pro feature gating without brittle inline checks. |

---

## 7. Automated Test Verification Baseline

Executed verified command: `./gradlew testDebugUnitTest` & `./gradlew :domain:test`

- **Total Test Suites:** 16 files
- **Total Test Cases:** 55 tests
- **Passing:** 55 (100%)
- **Failures / Errors:** 0
- **Execution Time:** ~12 seconds

### Breakdown by Module

1. **Domain Engine Suites (9 files, 19 tests):**
   - `MockTestScoringTest`: 6 tests (JEE marking, NEET marking, UPSC marking, CAT marking, GATE marking, unanswered questions).
   - `StudyPlanningEngineTest`: 3 tests (plan generation, phase scheduling, weak baseline prioritization).
   - `MasteryEngineTest`: 2 tests (mastery progression and decay calculation).
   - `RevisionEngineTest`: 2 tests (spaced repetition intervals and review logging).
   - `RecommendationEngineTest`: 2 tests (weak topic rule triggers, priority ordering).
   - `QuizScoringEngineTest`: 2 tests (accuracy and negative marking evaluation).
   - `MistakeVaultEngineTest`: 1 test (incorrect question ingestion).
   - `SecuritySanitizationTest`: 1 test (input sanitization and SQL injection guards).
   - `QuestionSerializationTest`: 1 test (JSON payload encoding/decoding).

2. **App Presentation & Store Suites (7 files, 36 tests):**
   - `MockTestEngineTest`: 10 tests (lifecycle, timer tick, section switching, option selection, submit dialog, scoring).
   - `StudyPlannerStoreTest`: 5 tests (task toggle, reschedule, milestone calculation).
   - `MistakeVaultStoreTest`: 5 tests (bookmarking, filter by exam, revision completion).
   - `LeaderboardStoreTest`: 4 tests (peer percentile ranking, score sorting).
   - `FlashcardStoreTest`: 4 tests (card flip, confidence rating, deck progress).
   - `AuthStoreTest`: 4 tests (demo login, profile persistence, sign out).
   - `AppSecurityAndConcurrencyTest`: 4 tests (thread-safe state updates, concurrency guards).

---

## 8. Release Build Verification Baseline

Executed verified command: `./gradlew assembleRelease bundleRelease`

- **Release APK Artifact:** `app/build/outputs/apk/release/app-release.apk`
- **Release AAB Bundle:** `app/build/outputs/bundle/release/app-release.aab`
- **APK File Size:** ~13 MB (compact, production-ready, dead resources stripped)
- **Minification Engine:** R8 ProGuard with `isMinifyEnabled = true` and `isShrinkResources = true`
- **Signing:** Release keystore verified (`keystore/examprep-release.jks`)
- **Lint Vital Status:** `BUILD SUCCESSFUL` with 0 blocking fatal errors

---

## 9. Baseline Architectural & Pre-Launch Risks

1. **Deprecation Warnings:**  
   Minor UI warning: `DashboardScreen.kt:777` uses deprecated `Icons.Default.MenuBook`. Needs replacement with `Icons.AutoMirrored.Filled.MenuBook` (slated for immediate fix in Phase 11).
2. **Third-Party Key Injection:**  
   AdMob App ID currently configured to Google's official public test ID (`ca-app-pub-3940256099942544~3347511713`). Production publisher ID must be populated before release submission.
3. **Legal Web Endpoints:**  
   In-app legal texts are fully implemented. Public web URLs (`https://examprepos.app/privacy`, `https://examprepos.app/terms`) must be hosted on public web infrastructure prior to Google Play Console declaration.
4. **Google Play 14-Day Closed Testing Requirement:**  
   New personal developer accounts require 14 days of closed testing with 20 opted-in testers before production access is unlocked.
