# ExamPrepOS — Repository Baseline Assessment

**Document ID:** `DOC_REPO_BASELINE_01`  
**Date:** October 10, 2026  
**Auditor / Roles:** Senior Android Architect, Repository Maintenance Engineer, Build-System Specialist  
**Repository Root:** `/Users/yash/Yash-Workspace/projects/active/android`  
**Target Application:** `ExamPrepOS` (`com.examprep.android`)  

---

## 1. Executive Summary

This baseline establishes the pre-cleanup technical and structural state of the ExamPrepOS multi-module Android project and its associated Marketing Studio subsystem. Prior to performing any deletion, restructuring, or deduplication, all source sets, assets, build artifacts, git statuses, and test results were cataloged.

The repository compiles cleanly with Gradle 8.9 / AGP 8.8.0 / Kotlin 2.0.21 / Jetpack Compose / Room / Hilt, and passes 100% of unit tests across all four modules (`app`, `core`, `domain`, `data`).

---

## 2. Multi-Module Project Architecture

```
ExamPrepOS/
├── app/                  # Main Android Application (Jetpack Compose UI, Navigation, ViewModels, DI entry)
├── core/                 # Shared Foundations (Theme, Design System, Common Utilities, Security)
├── domain/               # Pure Kotlin Business Logic (Engines, Models, Repository Interfaces)
├── data/                 # Data Layer (Room SQLite, DAOs, Entities, Repository Implementations, Assets)
├── gradle/               # Gradle Version Catalogs & Wrapper
├── docs/                 # Product, Architecture, Compliance, Legal, Release, and QA Documentation
├── marketing/            # Marketing Studio (Scripts, Storyboards, Assets, Videos, Posts, Templates)
└── scripts/              # Developer & Generation Utilities (generate_question_bank.py)
```

### Module Responsibilities & Entry Points:
- **`app` (`com.examprep.android`):**
  - Application class: [`ExamPrepApplication.kt`](file:///Users/yash/Yash-Workspace/projects/active/android/app/src/main/kotlin/com/examprep/android/ExamPrepApplication.kt)
  - Main Activity: [`MainActivity.kt`](file:///Users/yash/Yash-Workspace/projects/active/android/app/src/main/kotlin/com/examprep/android/MainActivity.kt)
  - Navigation Host: [`AppNavigation.kt`](file:///Users/yash/Yash-Workspace/projects/active/android/app/src/main/kotlin/com/examprep/android/navigation/AppNavigation.kt)
  - Features: `auth`, `dashboard`, `flashcards`, `leaderboard`, `mistakes`, `mocktest`, `onboarding`, `performance`, `questionlibrary`, `quiz`, `rankaccelerator`, `revision`, `roadmap`, `settings`, `study`, `subscription`, `syllabus`, `timeline`.
- **`core` (`com.examprep.core`):**
  - Design tokens, color palettes, typography, components, string encryption, and security primitives.
- **`domain` (`com.examprep.domain`):**
  - Pure Kotlin domain models (`Exam`, `Subject`, `Chapter`, `Topic`, `Question`, `StudyPlan`, `StudySession`, `MistakeEntry`).
  - Core engines: `StudyPlanningEngineImpl`, `RevisionEngineImpl`, `MockTestEngineImpl`.
- **`data` (`com.examprep.data`):**
  - Room DB: `ExamPrepDatabase`, `QuestionDao`, `SessionHistoryDao`, `MistakeDao`, `StudyPlanDao`.
  - Repository Implementations: `ExamPrepRepositoryImpl`, `QuestionBankRepository`.
  - Bundled question papers: 25 full JSON mock tests in `data/src/main/assets/content/`.

---

## 3. Build & Test Baseline Verification

- **Gradle Execution:** `./gradlew test --rerun-tasks`
- **Result:** `BUILD SUCCESSFUL in 1m 35s` (169 actionable tasks executed, 0 failures).
- **Compilation Targets:**
  - `compileDebugKotlin` / `compileReleaseKotlin` for `app`, `core`, `domain`, `data`: **PASSED**
  - `kspDebugKotlin` / `kspReleaseKotlin`: **PASSED**
  - `transformClassesWithAsm` (Hilt / ProGuard): **PASSED**
  - Unit test suites (`app:test`, `domain:test`, `data:test`, `core:test`): **100% GREEN**

---

## 4. Git Working-Tree & Clutter Audit

### 4.1 Identified Redundant & Duplicate Files:
1. **Root Screenshot Clutter:**
   - `screen_dashboard.png`, `screen_dashboard2.png`, `screen_mocktest_active.png`, `screen_mocktest_home.png` at root are exact byte-for-byte MD5 duplicates of files in `marketing/assets/screenshots/`.
2. **Root `content/` Directory:**
   - `content/` at the root (4.1 MB) is an exact 100% duplicate of `data/src/main/assets/content/` (and `app/src/main/assets/content/`).
3. **`domain/bin/` Directory:**
   - Eclipse / VSCode / IDE artifact directory mirroring Kotlin source files in `domain/src/`.
4. **Temporary SQLite Files:**
   - `examprep_test.db-shm` and `examprep_test.db-wal` left behind at root from unit/local test execution.
5. **Python Cache:**
   - `marketing/templates/__pycache__/` contains compiled `.pyc` bytecode.
6. **Intermediate Media Artifacts:**
   - `marketing/videos/reviewed/vid01/` and `marketing/videos/reviewed/vid02/` (legacy v1 frame extractions, superseded by `vid01_v2/` and `vid02_v2/`).
   - `marketing/assets/screenshots/current_screen.png` and `current_emulator_screen.png` (disposable debug screencaps).

---

## 5. Storage Profile & Large Files

| Directory / Artifact | Size | Category | Action Required |
| :--- | :--- | :--- | :--- |
| `app/build/` | 1.4 GB | Gradle Build Intermediates | Preserve in local build / keep git-ignored |
| `data/src/main/assets/content/` | 4.1 MB | Question Paper JSONs (2,500 questions) | **Canonical source asset — PROTECT** |
| `content/` (root) | 4.1 MB | Question Paper JSONs Duplicate | **Safe to remove (identical to data assets)** |
| `marketing/videos/final/` | 10.4 MB | Master MP4 Videos & Thumbnails | **Canonical deliverables — PROTECT** |
| `marketing/videos/drafts/voice_tests/` | 12.3 MB | Voiceover Comparison WAVs | Preserve as test benchmark evidence |
| `marketing/videos/reviewed/` | ~3.8 MB | Keyframe Review PNGs | Retain v2 keyframes, clean legacy v1 |
| Root `screen_*.png` (4 files) | 760 KB | Loose Root Screenshots | **Remove (Identical copies in marketing/assets)** |
| Root `examprep_test.db*` | 32 KB | Local SQLite WAL/SHM artifacts | **Safe to remove** |
| `domain/bin/` | ~40 KB | Stale IDE build folder | **Safe to remove & add to .gitignore** |

---

## 6. Safety & Preservation Rules

- **Source Code Protection:** Never move or delete Kotlin/Java source files in `app/`, `core/`, `domain/`, `data/`.
- **Database & Asset Integrity:** Ensure all 25 mock exam papers remain in `data/src/main/assets/content/` and `app/src/main/assets/content/`.
- **Marketing Reproducibility:** Preserve all generation templates (`video_studio_engine.py`, `voice_engine.py`, `build_initial_assets.py`) and source brand logos (`logo_512.png`).
- **Legal & Compliance:** Preserve all privacy policies, terms of service, and compliance audits in `docs/legal/` and `docs/compliance/`.
