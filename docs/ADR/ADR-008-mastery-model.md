# ADR-008: Mastery as a Multi-Signal State (Not Just Completion)

**Status:** Accepted  
**Date:** September 2025  
**Deciders:** Product + Engineering

---

## Context

The core product principle is "mastery over completion." Most study apps track whether a student has *covered* a topic. ExamPrep OS must track whether a student has *mastered* it. These are fundamentally different.

## Decision

**Topic mastery is determined by multiple signals, not just completion.** The `MasteryEngine` weighs the following signals:

| Signal | Weight | Description |
|--------|--------|-------------|
| Quiz accuracy | 40% | Primary signal — actual performance |
| Attempt count | 20% | Minimum threshold before mastery is credible |
| Time efficiency | 15% | Solving quickly without sacrificing accuracy |
| Recency | 15% | How recently the topic was practiced |
| PYQ accuracy | 10% | Performance on actual previous year questions |

### Mastery State Transitions

```
NOT_STARTED
  ↓ (session started on topic)
IN_PROGRESS
  ↓ (session completed)
LEARNED
  ↓ (first quiz attempt)
PRACTICING
  ↓ (accuracy ≥ 85%, attempts ≥ 5)
MASTERED
  ↓ (revision interval exceeded)
REVISION_DUE
  ↓ (revision completed, accuracy ≥ 70%)
MASTERED (renewed)
```

### What Does NOT Constitute Mastery

- Watching a video about the topic
- Clicking "Mark as done"
- Completing a task in the study plan
- Attempting fewer than 5 questions

### Anti-Pattern: Fake Completion

The UI must never show a topic as "Mastered" unless the MasteryEngine has confirmed it. The dashboard "completion percentage" is calculated from *mastery records*, not from task completion status.

## Implementation

`MasteryEngine.calculateMastery(record, attempts)` is pure — it takes a current state and quiz attempts, and returns an updated state. It is deterministic and fully unit-testable.

## Consequences

**Positive:**
- Honest representation of student readiness
- Prevents the common pattern of "covered 80% but remember 20%"
- Drives practice behavior (students must prove mastery through quizzes)

**Negative:**
- Students may initially resist — they want to feel "done"
- UX must explain mastery states clearly to avoid frustration
- Requires question bank to be populated per topic before mastery can be assessed

## UX Implication

Every topic card in the syllabus shows a mastery state dot (color-coded). The explanation of each state must be accessible via tooltip or info sheet.
