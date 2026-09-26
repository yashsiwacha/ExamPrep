# ADR-001: Offline-First Architecture

**Status:** Accepted  
**Date:** September 2025  
**Deciders:** Principal Architect

---

## Context

ExamPrep OS serves students in Tier 2/3 Indian cities who often have unreliable internet connectivity. The product must be functional in all network conditions. Additionally, all core user value (plan, session, quiz, mastery) must not be blocked by network calls.

## Decision

**The app will be 100% offline-first.** All data is stored locally in a Room database. No core feature requires a network call. Network is treated as an optional enhancement layer.

Network calls are only used for:
- AdMob ad loading (gracefully degrades — no ads shown if offline)
- Future AI service calls (gracefully degrade — AI suggestions hidden)
- Future server-side entitlement verification (local fallback in MVP)

## Consequences

**Positive:**
- Works in poor connectivity zones
- No backend infrastructure cost in MVP
- No API design, versioning, or server maintenance
- Student data never leaves the device (privacy by design)

**Negative:**
- No sync across devices (addressed in Phase 5+ with optional cloud sync)
- No cross-device analytics for the team (mitigated by opt-in anonymous analytics)
- Content updates (question bank) require app update or in-app download mechanism

## Alternatives Considered

1. **Server-side plan generation** — Rejected. Would break in offline conditions and add infrastructure complexity.
2. **Firebase Firestore + offline persistence** — Rejected. Overkill for MVP, billing concern, vendor lock-in.
3. **Hybrid (core offline, analytics online)** — Considered for Phase 2+.
