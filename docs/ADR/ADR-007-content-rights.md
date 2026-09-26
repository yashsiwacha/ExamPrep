# ADR-007: Content Rights & Question Bank IP Policy

**Status:** Accepted  
**Date:** September 2025  
**Deciders:** Product + Legal Review

---

## Context

ExamPrep OS requires a question bank for quizzes, PYQ practice, and diagnostic assessments. Competitive exam questions exist widely online but carry significant IP risk. Using questions from coaching institutes (Allen, Aakash, T.I.M.E., Resonance) or textbooks without permission is a legal liability that could result in app takedown.

## Decision

**All questions in ExamPrep OS must have an explicit, auditable content rights classification.**

Every `Question` entity has:
```kotlin
val source: ContentSource      // ORIGINAL | OFFICIAL | LICENSED | ...
val sourceReference: String?   // "Original - ExamPrep v1.0" or "JEE Main 2022 Paper 1 Q14"
val isVerified: Boolean        // Must be manually set to true before question is activated
val isActive: Boolean          // Only active questions shown to users
```

### Permitted Sources

| Source | Condition | Example |
|--------|-----------|---------|
| `ORIGINAL` | Created by ExamPrep team | In-house questions, reviewed by subject experts |
| `OFFICIAL` | Released by exam authority in public domain | NTA-released JEE sample papers |
| `LICENSED` | Written license agreement in place | Partnership with a content provider |

### Prohibited Sources

| Source | Reason |
|--------|--------|
| Coaching institute papers | Copyright owned by institute |
| State board textbooks | Copyright owned by state/publisher |
| Scraped web content | Unknown origin, unverifiable rights |
| `THIRD_PARTY_REFERENCE` | Internal use only; never shown to users |

### Content Pipeline

1. Question created/imported → `isVerified = false`, `isActive = false`
2. Subject expert reviews for accuracy and rights → `isVerified = true`
3. Legal sign-off for non-ORIGINAL sources → `isActive = true`
4. Only `isActive = true` questions appear in the app

## Enforcement in Code

The `QuestionRepository.getRandomQuestions()` and all DAO queries filter `WHERE isActive = 1`. There is no way to serve unverified questions to users without bypassing the repository interface.

## Consequences

**Positive:**
- Clear legal defensibility
- Audit trail for every question
- Reduces IP liability risk significantly

**Negative:**
- Slower question bank growth (manual verification required)
- Bootstrapping problem (thin question bank at launch)

**Mitigation:** Launch with 50–100 original questions per subject for JEE, growing to 500+ by Phase 3.
