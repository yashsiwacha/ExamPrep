# ExamPrep OS — System Architecture

**Version:** 1.0  
**Last Updated:** September 2025  
**Status:** Approved

---

## Overview

ExamPrep OS is a single-device, offline-first Android application built with Clean Architecture and MVVM. It requires no server infrastructure for its core functions. All data lives locally in a Room database.

---

## Architecture Layers

```
┌─────────────────────────────────────────────────────────┐
│                      :app Module                        │
│  UI Layer: Jetpack Compose Screens + ViewModels         │
│  Navigation: Navigation Compose, AppNavigation          │
└─────────────────────┬───────────────────────────────────┘
                      │ depends on
┌─────────────────────▼───────────────────────────────────┐
│                    :core Module                         │
│  Design System: ExamPrepTheme, Colors, Typography       │
│  Shared Utilities: Extensions, Analytics Abstraction    │
│  Notifications: NotificationScheduler abstraction       │
└─────────────────────────────────────────────────────────┘
                      │ depends on
┌─────────────────────▼───────────────────────────────────┐
│                   :domain Module                        │
│  Models: Pure Kotlin data classes & enums               │
│  Repository Interfaces: Contract definitions            │
│  Engine Interfaces: Planning, Mastery, Quiz, Revision   │
│  NO Android dependencies                                │
└─────────────────────────────────────────────────────────┘
                      │ implements
┌─────────────────────▼───────────────────────────────────┐
│                    :data Module                         │
│  Room: Entities, DAOs, Database, TypeConverters         │
│  Mappers: Entity ↔ Domain conversions                   │
│  Repository Implementations: ExamRepositoryImpl, etc.   │
│  DI: Hilt modules for database + repository binding     │
└─────────────────────────────────────────────────────────┘
```

### Dependency Rule

> Inner layers NEVER depend on outer layers.  
> `:domain` has zero Android dependencies.  
> `:core` depends only on `:domain`.  
> `:data` depends only on `:domain`.  
> `:app` depends on `:core`, `:domain`, `:data`.

---

## Module Structure

```
android/
├── app/                    # Android application module
│   ├── navigation/         # AppNavigation, Screen routes
│   └── feature/            # One package per feature screen
│       ├── onboarding/     # Splash, Exam Selection, Setup, Diagnostic
│       ├── dashboard/      # Home screen
│       ├── timeline/       # Study plan timeline
│       ├── study/          # Focus session (Pomodoro timer)
│       ├── quiz/           # Quiz + Result screens
│       ├── syllabus/       # Syllabus + Practice hub
│       ├── performance/    # Analytics and progress
│       ├── revision/       # Revision queue
│       └── settings/       # User preferences
│
├── core/                   # Shared Android library
│   └── designsystem/
│       ├── theme/          # ExamPrepTheme, Colors, Typography, Shapes
│       └── component/      # Shared Compose components (future)
│
├── domain/                 # Pure Kotlin module (NO Android)
│   ├── model/              # DomainModels.kt — all entities and enums
│   ├── repository/         # Repositories.kt — interface definitions
│   └── engine/             # Engines.kt — engine interface definitions
│
└── data/                   # Android library (Room, DataStore)
    ├── local/
    │   ├── database/       # ExamPrepDatabase, DatabaseConverters
    │   ├── entity/         # Entities.kt — Room @Entity classes
    │   └── dao/            # Daos.kt — Room @Dao interfaces
    ├── mapper/             # Mappers.kt — Entity ↔ Domain
    ├── repository/         # RepositoryImplementations.kt
    └── di/                 # DataModules.kt — Hilt DI
```

---

## Technology Stack

| Layer | Technology | Rationale |
|-------|-----------|-----------|
| Language | Kotlin (exclusive) | Modern, null-safe, concise |
| UI Framework | Jetpack Compose | Declarative, type-safe, future-proof |
| UI Architecture | MVVM + UiState sealed class | Testable, one-directional data flow |
| Dependency Injection | Hilt | Official Android DI, compile-time safe |
| Local Database | Room | SQLite abstraction, Flow support |
| Async | Kotlin Coroutines + Flow | Structured concurrency |
| Navigation | Navigation Compose | Type-safe, lifecycle-aware |
| Background Work | WorkManager | Guaranteed execution, device-restart safe |
| Preferences | DataStore (Preferences) | Replaces SharedPreferences, Flow-based |
| Serialization | kotlinx.serialization | Multiplatform ready, KSP-based |
| Material Design | Material 3 (M3) | Latest design system |
| Logging | Timber | Debug-only, configurable |
| Testing | JUnit4 + MockK + Turbine | Unit, integration, Flow testing |

---

## Data Flow

### Read Path (UI → Repository → Room)

```
Screen (Composable)
  └─► ViewModel.uiState: StateFlow<UiState>
        └─► Repository.getXxx(): Flow<List<DomainModel>>
              └─► DAO.observeXxx(): Flow<List<Entity>>
                    └─► Room SQLite (Reactive query)
```

### Write Path (User Action → UseCase → Room)

```
User Action → ViewModel.onEvent(event)
  └─► Repository.saveXxx(domainModel)
        └─► DAO.insert(entity)
              └─► Room SQLite
```

### Engine Path (Calculation → Repository)

```
WorkManager / ViewModel
  └─► Engine.calculate(inputs) → output
        └─► Repository.save(result)
              └─► DAO.insert(entity)
```

---

## Navigation Architecture

Single-Activity, Navigation Compose graph:

```
Splash
  ├─► [first launch] OnboardingGraph
  │     ├── Onboarding (welcome)
  │     ├── ExamSelection
  │     ├── ExamSetup/{examId}
  │     └── DiagnosticAssessment
  │           └─► MainGraph
  └─► [returning] MainGraph
        └── MainAppScreen (Scaffold + BottomNav)
              ├── Dashboard
              ├── Timeline
              ├── PracticeHome
              ├── Performance
              └── Settings
              
Full-screen overlays (above bottom nav):
  ├── FocusSession/{taskId}
  ├── Quiz/{quizId}
  ├── QuizResult/{attemptId}
  └── TopicDetail/{topicId}
```

---

## Engine Architecture

Engines are interfaces in `:domain` with implementations in `:data` or `:domain` (pure Kotlin).

| Engine | Responsibility | Phase |
|--------|---------------|-------|
| `StudyPlanningEngine` | Generate/rebalance study plans | Phase 1 |
| `MasteryEngine` | Calculate topic mastery state from quiz data | Phase 2 |
| `QuizScoringEngine` | Score quiz attempts per marking scheme | Phase 3 |
| `RevisionEngine` | Spaced repetition scheduling | Phase 4 |
| `RecommendationEngine` | Deterministic rule-based suggestions | Phase 4 |
| `AiService` | Future: LLM integration gateway | Phase 5+ |

---

## Database Design

- **18 tables**, all using String UUIDs as primary keys (for future sync)
- **Cascade deletes** on all foreign key relationships
- **Indexed** on all foreign keys and common query columns
- **TypeConverters** for enums (stored as `name()` strings)
- **Schema export** enabled for migration history
- **No destructive migration** policy — all schema changes require Migration objects

See [DATABASE_SCHEMA.md](DATABASE_SCHEMA.md) for full table definitions.

---

## Background Processing

| Job | Mechanism | Trigger |
|-----|-----------|---------|
| Study reminder notification | WorkManager (periodic) | Daily alarm |
| Revision due notification | WorkManager (one-shot) | Scheduled date |
| Plan rebalancing | WorkManager (one-shot) | On app open after miss |
| Recommendation generation | WorkManager (periodic) | Daily |
| Session timeout | WorkManager (one-shot) | Session start |

---

## Security Design

| Concern | Approach |
|---------|---------|
| No server = no API key exposure | Offline-first design |
| No PII transmitted | All data stays on device |
| No cloud backup of study data | `android:allowBackup="false"` + backup rules |
| Logging | Timber with DebugTree only in DEBUG build |
| Entitlement validation | Local only (MVP); server-side in Phase 5 |

---

## Offline-First Design

The app is 100% functional without internet. Network is only required for:
- SDK download at install time
- Ad display (gracefully degrades to no ads)
- Future: AI service calls (gracefully degrade to no AI suggestions)

All core features — planning, study sessions, quiz, mastery tracking, revision — work offline.
