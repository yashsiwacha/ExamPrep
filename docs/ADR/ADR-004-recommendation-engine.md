# ADR-004: Deterministic Rule-Based Recommendation Engine (No AI in MVP)

**Status:** Accepted  
**Date:** September 2025  
**Deciders:** Product + Engineering

---

## Context

Personalized study recommendations are a core value proposition of ExamPrep OS. Two implementation approaches were considered: (1) rule-based deterministic engine, (2) LLM/AI-powered engine. The product principle states: "Every recommendation must explain its reasoning."

## Decision

**The MVP RecommendationEngine is deterministic and rule-based.** It uses explicit thresholds applied to mastery records, quiz performance, and plan status. Every recommendation includes a `reason` field that is surfaced directly to the user.

Example:
> "We recommend revising **Newton's Laws of Motion** because your last 4 quiz attempts showed 38% accuracy."

The `AiService` interface is defined in the domain layer to allow a future swap to LLM-powered recommendations without changing the domain model.

## Recommendation Rules (MVP)

```
IF accuracy < 50% AND attempts >= 5 → REVISE_TOPIC (HIGH priority)
IF accuracy 50–70% AND attempts >= 3 → PRACTICE_MORE (MEDIUM)
IF no quiz in past 3 days → TAKE_QUIZ (MEDIUM)
IF plan behind by > 3 days → CATCH_UP (CRITICAL)
IF in MOCK phase AND no mock in 7 days → TAKE_MOCK (HIGH)
IF wrong answers > 30% in last quiz → REVIEW_MISTAKES (MEDIUM)
```

## Consequences

**Positive:**
- Fully deterministic — easy to test, debug, and explain
- No LLM API costs or latency
- Works offline
- Transparent to users (rule-driven reasons)

**Negative:**
- Cannot learn from patterns the rules don't capture
- Rules require manual tuning as product matures

## AI Integration Path (Phase 5)

```kotlin
// Current
class RecommendationRepositoryImpl @Inject constructor(
    private val engine: RecommendationEngine  // RuleBasedRecommendationEngine
)

// Future — swap with zero domain changes
class RecommendationRepositoryImpl @Inject constructor(
    private val engine: RecommendationEngine  // AiRecommendationEngine (implements same interface)
)
```

## Alternatives Considered

1. **Gemini API for recommendations** — Rejected for MVP. API cost, latency, offline constraint.
2. **On-device ML (TFLite)** — Too complex for MVP. Requires training data that doesn't exist yet.
3. **Hybrid (rules + AI explanation)** — Planned for Phase 5.
