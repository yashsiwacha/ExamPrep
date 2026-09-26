# ADR-006: Single Activity + Navigation Compose

**Status:** Accepted  
**Date:** September 2025  
**Deciders:** Principal Architect + Android Engineer

---

## Context

Android navigation can be implemented with multiple Activities, Fragments + Navigation Component, or a single Activity with Navigation Compose. We need a pattern that is idiomatic for Jetpack Compose and supports deep linking and state restoration.

## Decision

**Single `MainActivity` + Navigation Compose** is the navigation foundation.

All screens are Composable functions. The `AppNavigation` composable defines the complete navigation graph. There is no Fragment dependency.

```
MainActivity
  └── setContent { AppNavigation(navController) }
        ├── NavHost (root graph)
        │     ├── Splash
        │     ├── OnboardingGraph (nested)
        │     └── MainGraph → MainAppScreen
        │           └── NavHost (bottom nav graph)
        │                 ├── Dashboard
        │                 ├── Timeline
        │                 ├── PracticeHome
        │                 ├── Performance
        │                 └── Settings
        └── Full-screen overlays (FocusSession, Quiz, QuizResult)
```

## Type-Safe Routes

Routes are defined as `sealed class Screen(val route: String)` with factory functions for parameterized routes:

```kotlin
data object ExamSetup : Screen("exam_setup/{examId}") {
    fun createRoute(examId: String) = "exam_setup/$examId"
}
```

**Note:** Navigation Compose 2.8+ supports type-safe navigation with Kotlin serialization. Migration planned for Phase 2.

## Back Stack Management

- Bottom nav tabs use `saveState = true` and `restoreState = true`
- `popUpTo` with `inclusive = true` ensures onboarding is fully removed from back stack
- Contextual screens (FocusSession, Quiz) are pushed above the bottom nav

## Consequences

**Positive:**
- No Fragment API surface — pure Compose
- Single source of truth for navigation state
- Predictable back stack behavior
- Easy to add deep links

**Negative:**
- Navigation Compose argument passing uses strings (serialization boilerplate)
- Complex state restoration requires SavedStateHandle in ViewModel

## Alternatives Considered

1. **Fragments + Navigation Component** — Rejected. Incompatible with full Compose commitment.
2. **Multiple Activities** — Rejected. Harder back-stack management, state sharing complexity.
3. **Decompose library** — Considered but adds non-Google dependency. Revisit if Navigation Compose proves limiting.
