# Code Duplication Analysis: ExamPrepOS Android

## 1. Duplication Overview
This analysis maps out redundant patterns, identical components, repeated calculations, and boilerplate across the `ExamPrepOS` Android application codebase prior to refactoring.

## 2. Duplication Matrix by Category

| Category | Location | Duplication | Proposed Consolidation | Risk | Value |
|---|---|---|---|---|---|
| UI | `DashboardScreen:725`, `QuizScreens:769`, `MockTestResultScreen:317`, `PerformanceScreen:428` | Identical vertical Metric stat (value + label + color styling) | Shared `MetricStatBox` composable in core design system | Low | HIGH |
| UI | `TimelineScreen:50`, `RevisionScreen:46`, `SyllabusScreen:50`, `PerformanceScreen:72`, `MockTestScreen:78`, `QuestionLibraryScreen:156` | Full-screen centered `CircularProgressIndicator()` in `Box` | Shared `ExamPrepLoadingState` composable | Low | HIGH |
| UI | `TimelineScreen:55`, `RevisionScreen:51`, `SyllabusScreen:56`, `PerformanceScreen:78`, `AuthScreens:195`, `QuestionLibraryScreen:164` | Full-screen or banner error text display | Shared `ExamPrepErrorState` composable | Low | HIGH |
| UI | `TimelineScreen:198`, `QuizScreens:786`, `MockTestResultScreen:356`, `LeaderboardScreen:165` | Surface badge/chip with rounded corners, padding, text, and alpha backgrounds | Shared `StatusChip` composable | Low | HIGH |
| UI | `QuizScreens:487`, `MockTestScreen:394` | Option selection cards with letter badge, selection border, and text | Shared `QuestionOptionCard` composable | Low | HIGH |
| UI | `DashboardScreen:828-905`, `DashboardScreen:908-988` | Promotional banner card structure (Surface badge + tag + title + subtitle + CTA Button) | Reusable `ActionBannerCard` composable | Low | MEDIUM |
| UI | `TimelineScreen:100-143`, `TimelineScreen:145-195` | `RealPhaseCard` and `FallbackPhaseCard` have duplicate Card layouts, metadata rows, and CTA buttons | Unified `PhaseCard` composable | Low | HIGH |
| UI | `RevisionScreen:90-128`, `RevisionScreen:130-163` | `RealRevisionCard` and `SampleRevisionCard` share 95% identical Card layout, notification icon, and Revise button | Unified `RevisionCard` composable | Low | HIGH |
| UI | `AuthScreens:51-100`, `SettingsScreen:37-75` | Logout confirmation `AlertDialog` with identical cancel/confirm buttons | Unified `LogoutConfirmationDialog` | Low | MEDIUM |
| Formatting | `QuizScreens:485`, `FocusSessionScreen:85`, `MockTestScreen:324` | Seconds-to-minutes/hours division and `%02d:%02d` string formatting | Centralized `formatTimeSeconds` and `formatDurationHms` in `FormatUtils` | Low | HIGH |
| Formatting | `MistakeVaultScreen:430`, `PerformanceScreen:445` | Duplicate `fun String.capitalize()` extension functions | Shared extension `String.capitalizeWords()` in `core.util` | Low | MEDIUM |
| Navigation | `AppNavigation:635-645` | Redundant 15-line forwarding wrapper for `SplashScreen` | Direct invocation of `feature.onboarding.SplashScreen` | Low | LOW |
| Database / Seed | `RepositoryImplementations:23-57` | Fallback lookups against `DatabaseSeedData.getInitialExams()` | Centralized fallback helper in repository | Low | LOW |

---

## 3. Prioritization & Strategy

### High Value (Repeated 3+ Times)
1. **`MetricStatBox`**: Consolidates 4 separate identical stat components (`ProgressStat`, `ResultStat`, `ResultMetricItem`, `StatBox`).
2. **`ExamPrepLoadingState` & `ExamPrepErrorState`**: Consolidates 6+ duplicate loading/error containers into unified, accessible components.
3. **`StatusChip`**: Consolidates chip/pill surfaces across 4 feature screens.
4. **`QuestionOptionCard`**: Eliminates duplicated card layout, state borders, and option letter styling between Quiz and MockTest.
5. **Time & Duration Formatting**: Eliminates duplicate `/ 60` and `% 60` math across Quiz, FocusSession, and MockTest.

### Medium Value (Repeated 2 Times, High Line Count)
1. **`ActionBannerCard`**: Eliminates ~80 lines of duplicate banner card layout in `DashboardScreen.kt`.
2. **`PhaseCard` & `RevisionCard` Consolidation**: Eliminates duplicate card composables in `TimelineScreen.kt` and `RevisionScreen.kt`.
3. **`String.capitalizeWords()`**: Replaces duplicate local extensions with a single standard utility.

### Low Value (Kept or Simple Inlining)
1. **`SplashScreen` Wrapper in `AppNavigation`**: Remove the extra wrapper function and import directly.
2. In-memory MockTest question filters: Keep readable local filter queries to avoid excessive layers of indirection.
