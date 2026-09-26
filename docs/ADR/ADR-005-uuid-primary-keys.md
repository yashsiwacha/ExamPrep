# ADR-005: UUID String Primary Keys for All Entities

**Status:** Accepted  
**Date:** September 2025  
**Deciders:** Principal Architect

---

## Context

Room supports both auto-increment integer PKs and string PKs. The choice affects future sync capability, merge conflict resolution, and URL-safe ID generation.

## Decision

All Room entities use **UUID v4 String primary keys**, generated on the client before insertion.

```kotlin
// Pattern used everywhere:
val id = java.util.UUID.randomUUID().toString()
```

## Rationale

1. **Future sync readiness:** When cloud sync is added (Phase 5), UUID-keyed records can be merged without PK conflicts. Auto-increment integers would collide across devices.
2. **Stable IDs:** A plan/task/session ID remains the same after export, restore, or migration.
3. **No extra round-trip:** Generate ID before insert → can reference it immediately without a `lastInsertRowId()` call.
4. **Navigation-safe:** IDs can safely be passed as navigation arguments.

## Performance Consideration

UUID string comparisons are slower than integer comparisons for SQLite. This is acceptable because:
- Table sizes are small (< 10,000 rows in any table for a single user)
- All foreign-key columns are indexed
- Room query performance is not the bottleneck in this app

## Alternatives Considered

1. **Auto-increment Int** — Rejected. Breaks cross-device sync and conflicts with navigation argument types.
2. **Short random String (nanoid)** — Considered. UUID is more standard and tooling support is universal.
