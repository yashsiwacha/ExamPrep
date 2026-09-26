# ExamPrep OS — Android Application

**Platform:** Android (API 26+)  
**Language:** Kotlin (exclusive)  
**Architecture:** Clean Architecture + MVVM  
**Status:** Phase 0 — Scaffold Complete ✅

---

## Overview

ExamPrep OS is a personalized exam preparation manager for competitive exam students (JEE, NEET, UPSC, CAT, GATE, and more). It continuously maintains a model of the student's preparation and helps them understand where they are, what comes next, and whether they are on track.

> **Core principle:** This app does not simply tell students what to study. It acts as their preparation operating system — tracking mastery, generating dynamic study plans, scheduling revision, and providing transparent progress intelligence.

---

## Quick Start (Developer)

### Prerequisites

| Tool | Version | Install |
|------|---------|---------|
| JDK | 21 | `brew install openjdk@21` |
| Android SDK | API 35 | Via `sdkmanager` or Android Studio |
| Android Studio | Hedgehog+ | Optional but recommended |
| Kotlin | 1.9.25 | Bundled via Gradle |

### Build

```bash
# Clone / open the project
cd projects/active/android/

# Build domain module (fastest verification)
./gradlew :domain:compileKotlin

# Build all modules
./gradlew build

# Run unit tests
./gradlew test

# Install on connected device
./gradlew :app:installDebug
```

### SDK Setup

The project uses the Android SDK at the path configured in `local.properties`:

```properties
sdk.dir=/opt/homebrew/share/android-commandlinetools
```

Update this to match your local SDK path if different.

---

## Project Structure

```
android/
├── app/                    # Android application module
│   ├── navigation/         # Type-safe navigation routes + NavHost
│   └── feature/            # One package per screen
│       ├── onboarding/     # Splash, Exam Selection, Setup, Diagnostic
│       ├── dashboard/      # Home screen (today's task, countdown, stats)
│       ├── timeline/       # Study plan phases + task list
│       ├── study/          # Focus session (Pomodoro timer)
│       ├── quiz/           # Quiz engine + result screen
│       ├── syllabus/       # Syllabus tree + practice hub
│       ├── performance/    # Analytics and progress
│       ├── revision/       # Spaced repetition queue
│       └── settings/       # User preferences
│
├── core/                   # Shared Android library
│   └── designsystem/
│       └── theme/          # ExamPrepTheme (Indigo/Teal palette), Typography, Shapes
│
├── domain/                 # Pure Kotlin JVM — NO Android dependencies
│   ├── model/              # All domain models + enums
│   ├── repository/         # Repository interfaces
│   └── engine/             # Engine interfaces (Planning, Mastery, Quiz, Revision)
│
├── data/                   # Android library — Room + DataStore + DI
│   ├── local/
│   │   ├── database/       # ExamPrepDatabase, TypeConverters
│   │   ├── entity/         # 18 Room @Entity classes
│   │   └── dao/            # 11 DAO interfaces
│   ├── mapper/             # Entity ↔ Domain mappings
│   ├── repository/         # Repository implementations
│   └── di/                 # Hilt modules
│
└── docs/                   # Architecture + product documentation
    ├── PRODUCT_VISION.md
    ├── SYSTEM_ARCHITECTURE.md
    ├── DOMAIN_MODEL.md
    ├── FEATURE_ROADMAP.md
    ├── DATABASE_SCHEMA.md
    ├── ENGINE_CONTRACTS.md
    └── ADR/                # Architecture Decision Records (8 ADRs)
```

---

## Technology Stack

| Concern | Technology |
|---------|-----------|
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture |
| Navigation | Navigation Compose (single-activity) |
| DI | Hilt |
| Database | Room (SQLite) |
| Async | Kotlin Coroutines + Flow |
| Background Jobs | WorkManager |
| Preferences | DataStore (Preferences) |
| Serialization | kotlinx.serialization |
| Logging | Timber (debug only) |
| Testing | JUnit4 + MockK + Turbine |

---

## Architecture

```
:app  →  :core  →  :domain
:app  →           :domain
:data →           :domain
```

**Clean Architecture layers:**
1. **Domain** (`:domain`) — Pure Kotlin. Models, repository interfaces, engine interfaces.
2. **Data** (`:data`) — Android. Room entities, DAOs, repository implementations, Hilt DI.
3. **Core** (`:core`) — Android library. Design system, shared utilities.
4. **App** (`:app`) — Android application. Screens, ViewModels, navigation.

The `:domain` module has **zero Android dependencies** — all business logic is portable and unit-testable without an emulator.

---

## Domain Concepts

### MasteryState
A topic progresses through: `NOT_STARTED → IN_PROGRESS → LEARNED → PRACTICING → MASTERED`

Mastery is determined by quiz accuracy, attempt count, time efficiency, and recency — not just "completion."

### StudyPlan
Generated from: exam date + available hours + current mastery. Organized into phases (Foundation → Core → Practice → PYQ → Revision → Mock).

### Recommendation Engine
Deterministic, rule-based. Every recommendation includes a `reason` string shown to the user. No black-box AI in MVP.

### Content Rights
All questions have a `ContentSource` classification. Only `ORIGINAL`, `OFFICIAL`, and `LICENSED` questions may be shown to users. **Never use copyrighted coaching institute questions.**

---

## Feature Roadmap

| Phase | Focus | Status |
|-------|-------|--------|
| 0 | Scaffold + Architecture | ✅ Complete |
| 1 | Core User Journey (onboarding, plan, session) | 🔜 Next |
| 2 | Mastery Layer (syllabus tracking, mastery engine) | 📅 Planned |
| 3 | Practice Engine (quiz, scoring, performance) | 📅 Planned |
| 4 | Revision + Intelligence (spaced rep, recommendations) | 📅 Planned |
| 5 | Monetization + AI (AdMob, billing, Gemini) | 📅 Planned |

See [FEATURE_ROADMAP.md](docs/FEATURE_ROADMAP.md) for detailed user stories.

---

## Documentation Index

| Document | Purpose |
|----------|---------|
| [PRODUCT_VISION.md](docs/PRODUCT_VISION.md) | Why this app exists, target users, success metrics |
| [SYSTEM_ARCHITECTURE.md](docs/SYSTEM_ARCHITECTURE.md) | Module structure, data flow, technology choices |
| [DOMAIN_MODEL.md](docs/DOMAIN_MODEL.md) | Entity relationships, enums, key models |
| [DATABASE_SCHEMA.md](docs/DATABASE_SCHEMA.md) | Table definitions, indexes, migration strategy |
| [ENGINE_CONTRACTS.md](docs/ENGINE_CONTRACTS.md) | Precise engine interface specifications |
| [FEATURE_ROADMAP.md](docs/FEATURE_ROADMAP.md) | Phase-by-phase development plan |
| [ADR/ADR-001](docs/ADR/ADR-001-offline-first.md) | Offline-first architecture |
| [ADR/ADR-002](docs/ADR/ADR-002-pure-kotlin-domain.md) | Pure Kotlin domain module |
| [ADR/ADR-003](docs/ADR/ADR-003-monetization-admob.md) | Freemium + AdMob monetization |
| [ADR/ADR-004](docs/ADR/ADR-004-recommendation-engine.md) | Rule-based recommendation engine |
| [ADR/ADR-005](docs/ADR/ADR-005-uuid-primary-keys.md) | UUID string primary keys |
| [ADR/ADR-006](docs/ADR/ADR-006-single-activity-navigation.md) | Single activity + Navigation Compose |
| [ADR/ADR-007](docs/ADR/ADR-007-content-rights.md) | Content rights & IP policy |
| [ADR/ADR-008](docs/ADR/ADR-008-mastery-model.md) | Multi-signal mastery model |

---

## Contributing

### Branching Strategy

```
main              ← stable, release-ready
  └── develop     ← integration branch
        └── feature/EP-NNN-short-description
        └── fix/EP-NNN-short-description
```

### Code Standards

- **Kotlin only** — no Java
- **ktlint** formatting (configure in Phase 1)
- **No business logic in Composables** — all logic in ViewModels or domain
- **No Android imports in `:domain`** — enforced by module structure
- **All repository access via coroutines** — no blocking calls on main thread

### Testing Requirements

- Domain models and engines: **100% unit test coverage** (target)
- Repository implementations: integration tests with in-memory Room
- UI screens: snapshot tests (Phase 2)

---

## License

Copyright © 2025 ExamPrep. All rights reserved.  
Proprietary and confidential. Not for distribution.
