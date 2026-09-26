# ADR-002: Pure Kotlin Domain Module (No Android Dependencies)

**Status:** Accepted  
**Date:** September 2025  
**Deciders:** Principal Architect

---

## Context

The business logic of ExamPrep OS (study planning, mastery calculation, quiz scoring, spaced repetition) is complex and must be extensively unit-tested. Android unit tests run on the JVM via Robolectric or require an emulator, which is slow. We need fast, reliable tests for the core engine logic.

## Decision

The `:domain` module is a **pure Kotlin JVM module** (`kotlin.jvm` plugin). It has zero Android dependencies. It contains:
- All domain models (data classes, enums)
- All repository interfaces
- All engine interfaces
- No Room, no Context, no Android SDK imports

## Consequences

**Positive:**
- Domain unit tests run in < 1 second on the JVM (no emulator needed)
- The domain layer is portable — it could be shared with a future iOS app via KMP
- Clear separation of concerns — no framework code bleeds into business logic
- Engine implementations can be swapped (e.g., different revision algorithms) without affecting the UI

**Negative:**
- Slightly more verbose — cannot use Android-specific serialization shortcuts
- Mapper layer (`data/mapper`) is required to bridge entities and domain models

## Alternatives Considered

1. **Android library for domain** — Rejected. Slower tests, unnecessary Android coupling.
2. **Kotlin Multiplatform** — Premature for MVP. Added as an explicit future path.
3. **Single module** — Rejected. Violates Clean Architecture. Creates a tightly coupled monolith.
