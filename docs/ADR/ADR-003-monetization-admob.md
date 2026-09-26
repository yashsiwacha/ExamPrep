# ADR-003: Freemium + AdMob Monetization (No Feature Gating)

**Status:** Accepted  
**Date:** September 2025  
**Deciders:** Product + Engineering

---

## Context

The app needs to generate revenue from day one without a server, payment processing infrastructure, or costly content acquisition. We need the simplest viable monetization model that doesn't degrade the core student experience.

## Decision

**All core features are free.** Monetization is via:
1. **AdMob ads** on the free tier (banner + occasional interstitial between sessions)
2. **Ad-Free subscription** (₹99/month or ₹799/year) removes all ads
3. **7-day free trial** for Ad-Free to drive conversion

We explicitly reject feature gating as the primary monetization mechanism.

**Reasoning:** Students in competitive exam prep are price-sensitive. Gating features (e.g., "only 3 quizzes/day free") creates resentment and drives users away. Ads are less punishing — students who study for 4+ hours can tolerate occasional banner ads.

## Entitlement Architecture

```
Entitlement (domain model — no payment SDK reference)
  ↑ implemented by
EntitlementRepositoryImpl
  ↑ powered by (in Phase 5)
Google Play Billing Library
```

The domain model knows only `EntitlementTier.FREE | AD_FREE_TRIAL | AD_FREE`. Payment logic is entirely in the data layer.

## Ad Placement Rules

| Placement | Trigger | Free | Ad-Free |
|-----------|---------|------|---------|
| `HOME_BANNER` | Dashboard load | ✓ | ✗ |
| `RESULT_BANNER` | Quiz result | ✓ | ✗ |
| `BETWEEN_SESSIONS` | Focus session end | ✓ (1 in 3) | ✗ |
| `SETTINGS_BANNER` | Settings open | ✓ | ✗ |

## Consequences

**Positive:**
- Zero barrier to full feature access
- Simple implementation (no server-side entitlement)
- AdMob is mature and well-documented

**Negative:**
- Lower ARPU than feature-gated apps
- Ad revenue per user is low (~₹5–15/month)
- Requires high DAU to generate meaningful ad revenue

## Alternatives Considered

1. **Feature gating (quiz limits, plan locked)** — Rejected. Creates bad UX for exam prep students.
2. **One-time purchase** — Considered for Phase 5 alongside subscription.
3. **Content packs (paid question banks)** — Planned for Phase 5 as additional revenue stream.
