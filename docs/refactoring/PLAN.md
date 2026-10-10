# Codebase Minimization & Refactoring Plan: ExamPrepOS Android

## 1. Executive Summary & Architectural Scope
This plan details a behavior-preserving refactoring to minimize the codebase of ExamPrepOS without altering any product features, navigation routes, business logic, scoring algorithms, or visual presentation.
The target is: **Less code, less duplication, more reuse, clearer abstractions, identical behavior.**

## 2. Baseline Metrics
- **Kotlin Files**: 92
- **Total Kotlin LOC**: 22,780 lines (19,665 code lines)
- **Functions**: 755
- **Unit Tests**: 14 test classes, 46 `@Test` methods (all currently passing)

## 3. Phased Implementation Roadmap

### Phase A: Architecture & Core Utilities Foundation
- Create `core/src/main/kotlin/com/examprep/core/util/FormatUtils.kt`:
  - `fun formatTimeSeconds(seconds: Int): String` (consolidates `QuizScreens`, `FocusSessionScreen`, `MockTestScreen`)
  - `fun formatDurationHms(seconds: Int): String`
  - `fun String.capitalizeWords(): String` (consolidates `MistakeVaultScreen` & `PerformanceScreen`)
  - `fun calculatePercentage(part: Number, total: Number): Float`
- Create `core/src/main/kotlin/com/examprep/core/designsystem/component/CommonUiComponents.kt`:
  - `MetricStatBox`: Consolidates `ProgressStat` (Dashboard), `ResultStat` (Quiz), `ResultMetricItem` (MockTestResult), `StatBox` (Performance)
  - `StatusChip`: Consolidates `StatusBadge` (Timeline), `BreakdownChip` (Quiz), `SolutionFilterChip` (MockTestResult)
  - `ExamPrepLoadingState` & `ExamPrepErrorState`: Consolidates 8+ duplicate loading/error Box implementations
  - `ActionBannerCard`: Consolidates `MockExamBannerCard` and `QuestionLibraryBannerCard` (Dashboard)
  - `ScreenHeader`: Consolidates repeated screen title/subtitle header rows

### Phase B: Screen-Level Deduplication & Component Consolidation
- **TimelineScreen**:
  - Consolidate `RealPhaseCard` and `FallbackPhaseCard` into a unified `PhaseCard`.
  - Use `ExamPrepLoadingState` and `ExamPrepErrorState`.
- **RevisionScreen**:
  - Consolidate `RealRevisionCard` and `SampleRevisionCard` into a unified `RevisionCard`.
  - Use `ExamPrepLoadingState` and `ExamPrepErrorState`.
- **DashboardScreen**:
  - Replace `ProgressStat` with `MetricStatBox`.
  - Consolidate `MockExamBannerCard` and `QuestionLibraryBannerCard` with `ActionBannerCard`.
- **QuizScreens & MockTestScreen**:
  - Extract and reuse `QuestionOptionCard` between `OptionCard` and `MockOptionCard`.
  - Standardize timer displays with `formatTimeSeconds`.
- **PerformanceScreen & MockTestResultScreen**:
  - Replace `StatBox` and `ResultMetricItem` with `MetricStatBox`.
- **AppNavigation**:
  - Remove redundant 15-line forwarding wrapper `SplashScreen` and import the real composable directly.

### Phase C: Dead Code & Redundant Boilerplate Removal
- Clean up unused imports, dead parameters, and duplicate local extension functions across all refactored files.
- Verify zero regression against tests.

### Phase D: Verification & Audit
- Run `./gradlew testDebugUnitTest` after each major refactoring phase.
- Build debug variant: `./gradlew assembleDebug`.
- Generate `docs/refactoring/CODE_DUPLICATION_ANALYSIS.md` and `docs/refactoring/CODE_MINIMIZATION_REPORT.md`.
- Produce final Before vs After metrics and Routing Report.
