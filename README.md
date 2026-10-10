# ExamPrepOS — Autonomous Android Preparation Operating System

[![Platform](https://img.shields.io/badge/Platform-Android_8.0+_(API_26+)-brightgreen.svg)](https://developer.android.com)
[![Language](https://img.shields.io/badge/Language-Kotlin_1.9.25-blue.svg)](https://kotlinlang.org)
[![UI](https://img.shields.io/badge/UI-Jetpack_Compose_Material_3-purple.svg)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-Clean_Architecture_+_MVVM-orange.svg)](docs/SYSTEM_ARCHITECTURE.md)
[![Build](https://img.shields.io/badge/Build-Gradle_8.7_KTS-blue.svg)](https://gradle.org)

ExamPrepOS is a production-grade, offline-first exam preparation operating system engineered for competitive exam aspirants (JEE, NEET, UPSC, GATE, CAT, SSC, and Banking). It continuously maintains an intelligent mastery model of the student's preparation, generating adaptive study plans, spaced revision intervals, diagnostic assessments, and OLED-optimized distraction-free study sessions.

---

## 🏛️ System Architecture

ExamPrepOS follows strict **Clean Architecture** and **Multi-Module Separation of Concerns**:

```text
ExamPrepOS/
├── app/                           # Android Application Module
│   ├── src/main/kotlin/com/examprep/android/
│   │   ├── feature/               # Feature UI & ViewModels (MVI/MVVM pattern)
│   │   │   ├── auth/              # Email/Password + Google Sign-In & Verification
│   │   │   ├── onboarding/        # Diagnostic Assessment, Stream, Target Date Setup
│   │   │   ├── dashboard/         # Preparation Health, Streak, Today's Focus Plan
│   │   │   ├── study/             # OLED Focus Session & Pomodoro Timer
│   │   │   ├── mocktest/          # Full-length timed mock tests with negative marking
│   │   │   ├── quiz/              # Topic-level micro-quizzes & instant feedback
│   │   │   ├── mistakes/          # Mistake Vault error tracking & bookmarking
│   │   │   ├── revision/          # Spaced repetition queue & mastery decay
│   │   │   ├── syllabus/          # Hierarchical syllabus tree & progress indicators
│   │   │   ├── performance/       # Weak-area analytics, speed-accuracy curves
│   │   │   ├── subscription/      # Google Play In-App Billing (Pro Tier)
│   │   │   └── settings/          # Data export, theme toggles, notification configs
│   │   ├── navigation/            # Type-safe Navigation Compose routes & NavHost
│   │   ├── notification/          # Daily study reminder notification manager
│   │   └── ads/                   # Google AdMob banner & rewarded video wrappers
│   └── src/main/assets/content/   # Bundled 2,500 question curriculum JSONs
│
├── core/                          # Shared Android Library Module
│   ├── designsystem/              # ExamPrepTheme (Dark OLED / Deep Space), Typography, Shapes
│   ├── component/                 # Buttons, cards, badges, progress rings, dialogs
│   └── security/                  # Cryptographic hashing & secure payload verification
│
├── domain/                        # Pure Kotlin Library (Zero Android dependencies)
│   ├── model/                     # Domain entities, value objects, and enum classes
│   ├── repository/                # Abstract repository contracts
│   └── engine/                    # Core business logic engines
│       ├── StudyPlanningEngine    # Dynamic syllabus pacing & time allocation
│       ├── MasteryEngine          # Multi-signal mastery state calculation
│       ├── QuizScoringEngine      # Negative marking & percentile estimation
│       ├── RevisionEngine         # SuperMemo/SM-2 spaced repetition decay
│       └── RecommendationEngine   # Deterministic, explainable next-action recommendations
│
├── data/                          # Data & Persistence Layer Module
│   ├── local/
│   │   ├── database/              # Room SQLite database (18 entities, TypeConverters)
│   │   ├── dao/                   # 11 DAO interfaces with reactive Kotlin Flows
│   │   └── entity/                # Room table definitions & relational indices
│   ├── repository/                # Clean Architecture repository implementations
│   ├── firebase/                  # Firebase Auth & Cloud Firestore sync
│   └── di/                        # Dagger Hilt dependency injection modules
│
├── marketing/                     # Autonomous Short-Form Video & Content Studio
│   ├── templates/                 # Neural TTS (Edge-TTS) & FFmpeg / MoviePy render engines
│   ├── assets/                    # Production screenshots, app icons, and sound design
│   ├── posts/                     # Platform packages (YouTube, IG, TikTok, X, LinkedIn, FB)
│   ├── calendar/                  # 30-day content calendar & publishing queue
│   └── videos/final/              # Mastered 1080x1920 MP4 marketing videos & thumbnails
│
├── scripts/                       # Reusable Engineering & Asset Generation Utilities
│   └── generate_question_bank.py  # 2,500+ Question Bank JSON curriculum generator
│
└── docs/                          # Comprehensive Architecture, Product & Maintenance Docs
    ├── architecture/              # System architecture, engine contracts & DB schemas
    ├── product/                   # Product vision, PRDs & feature roadmaps
    ├── ADR/                       # Architectural Decision Records (ADR-001 to ADR-009)
    ├── launch/                    # Pre-launch checklists, ASO strategies, pricing models
    ├── legal/                     # Privacy Policy, Terms of Service, IP guidelines
    └── maintenance/               # Repository baselines and cleanup reports
```

---

## 🚀 Getting Started

### Prerequisites

| Component | Required Version | Verification Command |
|-----------|------------------|----------------------|
| **JDK** | OpenJDK 21 (LTS) | `javac -version` |
| **Android SDK** | API 35 (compileSdk), API 26+ (minSdk) | `sdkmanager --list` |
| **Gradle** | 8.7 (bundled via wrapper) | `./gradlew -v` |
| **Python** | 3.10+ (for marketing & scripts) | `python3 --version` |
| **FFmpeg** | 5.0+ (for video rendering) | `ffmpeg -version` |

### Environment Configuration

Ensure your `local.properties` specifies your local Android SDK directory:

```properties
sdk.dir=/path/to/your/android-sdk
```

---

## 🔨 Building & Testing

### 1. Verification & Unit Tests
Run all unit tests across all modules (Domain, Data, Core, App):

```bash
# Run all unit test suites
./gradlew test

# Run app-specific unit tests
./gradlew :app:testDebugUnitTest

# Run pure domain engine tests
./gradlew :domain:test
```

### 2. Compiling the Application
```bash
# Build Debug APK
./gradlew :app:assembleDebug

# Build Release APK (unsigned)
./gradlew :app:assembleRelease

# Install directly on a connected device/emulator
./gradlew :app:installDebug
```

---

## 📚 Question Bank Pipeline

ExamPrepOS includes a standalone, deterministic curriculum generator that populates full-length papers and micro-quizzes across Physics, Chemistry, Mathematics, Biology, History, Economy, and Quantitative Aptitude.

```bash
# Regenerate curriculum directly into Android assets:
python3 scripts/generate_question_bank.py
```

Generated assets are placed directly in `data/src/main/assets/content/` and `app/src/main/assets/content/`.

---

## 🎬 Marketing Video Studio Pipeline

To build and render high-converting vertical marketing videos with natural neural voiceovers:

```bash
# Run voice benchmarking:
python3 marketing/templates/generate_voice_samples.py

# Render master marketing videos:
python3 marketing/templates/build_initial_assets.py
```

Master deliverables will be exported to `marketing/videos/final/`. For complete details, see [marketing/README.md](marketing/README.md).

---

## 📖 Key Documentation Index

- **Product Strategy**: [PRODUCT_VISION.md](docs/PRODUCT_VISION.md) | [FEATURE_ROADMAP.md](docs/FEATURE_ROADMAP.md)
- **Architecture**: [SYSTEM_ARCHITECTURE.md](docs/SYSTEM_ARCHITECTURE.md) | [ENGINE_CONTRACTS.md](docs/ENGINE_CONTRACTS.md) | [DATABASE_SCHEMA.md](docs/DATABASE_SCHEMA.md)
- **Architectural Decisions**: [ADR Index](docs/ADR/) (ADR-001 through ADR-009)
- **Launch & Growth**: [ORGANIC_GROWTH_PLAN.md](docs/launch/ORGANIC_GROWTH_PLAN.md) | [MONETIZATION_STRATEGY.md](docs/launch/MONETIZATION_STRATEGY.md) | [FINAL_PRE_LAUNCH_REPORT.md](docs/launch/FINAL_PRE_LAUNCH_REPORT.md)
- **Legal & Compliance**: [PRIVACY_POLICY.md](docs/legal/PRIVACY_POLICY.md) | [TERMS_OF_SERVICE.md](docs/legal/TERMS_OF_SERVICE.md)
- **Repository Maintenance**: [REPOSITORY_BASELINE.md](docs/maintenance/REPOSITORY_BASELINE.md) | [REPOSITORY_CLEANUP_REPORT.md](docs/maintenance/REPOSITORY_CLEANUP_REPORT.md)

---

## ⚖️ License & Content Rights

Copyright © 2025–2026 ExamPrepOS. All rights reserved.  
All curriculum questions are original works created for ExamPrepOS or derived under fair-use open education guidelines (ADR-007).
