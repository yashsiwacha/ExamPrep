# ExamPrepOS — Legal & Founder Actionable Blockers

**Document Version:** 1.0.0  
**Date:** October 2026  
**Auditor / Role:** Senior Technical Lead & Release Governance Specialist  
**Status:** Pre-Release Clearance Gate  

---

## 1. Governance Policy & Founder Decision Boundaries

In accordance with our core operating principles:
> *"The agent is forbidden from publishing a release, spending money, creating paid subscriptions, changing account ownership, or accepting legal terms without explicit founder authorization."*

This document isolates the **exact, finite list of founder decisions and administrative inputs** required before ExamPrepOS can be published to the Google Play Store. Everything within engineering and code capability has been prepared autonomously.

---

## 2. Immediate Founder Decisions & Actions Required

### Category A: Developer Account & Google Play Console Setup

1. **Google Play Developer Account Enrollment:**
   - *Action Required:* Ensure an active Google Play Developer Account is registered ($25 USD one-time Google registration fee).
   - *Requirement:* For personal accounts registered after November 2023, Google mandates **14 days of Closed Testing with at least 20 opted-in testers** before the production track can be unlocked.
   - *Decision:* Designate whether this will be published under a Personal Account (requires 20 testers) or an Organization Account (DUNS-verified, exempt from 20-tester rule).

2. **Upload Release Artifact (AAB):**
   - *Action Required:* Upload `app/build/outputs/bundle/release/app-release.aab` to Google Play Console Closed Testing track.
   - *Keystore Credentials:* Production keystore is configured at `keystore/examprep-release.jks`. Keystore password and alias must be securely archived in the founder's password manager (e.g. 1Password / Bitwarden).

### Category B: AdMob Production Credentials

3. **Replace Test AdMob App ID:**
   - *Current State:* Configured with Google's official public test App ID (`ca-app-pub-3940256099942544~3347511713`) in `AndroidManifest.xml` and test banner ID in `AdUnitIds.kt`.
   - *Action Required:* Create an app in [Google AdMob Console](https://apps.admob.com), generate a production Android App ID and Banner Ad Unit ID, and paste into `AndroidManifest.xml` and `AdUnitIds.kt`.

### Category C: Public Web Hosting for Legal Policies

4. **Host Privacy Policy & Terms of Service on HTTPS Domain:**
   - *Current State:* Full markdown texts completed at `docs/legal/PRIVACY_POLICY.md` and `docs/legal/TERMS_OF_SERVICE.md`.
   - *Action Required:* Host these pages on a public HTTPS web endpoint (e.g., using GitHub Pages, Vercel, or Cloudflare Pages for free at `https://examprepos.app/privacy` or `https://<founder-username>.github.io/examprepos/privacy`).
   - *Link in Play Console:* Google Play Console strictly rejects apps if the Privacy Policy URL is missing or inaccessible.

5. **Fill In Publisher Legal Placeholders:**
   - *Current Placeholders in Legal Docs:*
     - Legal Entity / Developer Name: `[Insert Legal Entity / Founder Name]`
     - Support Email: `[support@examprepos.app]`
     - Physical Postal Address (Required for India DPDP Grievance Officer): `[Insert City, State, PIN]`
     - Designated Grievance Officer: `[Insert Grievance Officer Name]`

---

## 3. Financial & Monetization Approvals

6. **Subscription Pricing Approval:**
   - *Proposed Pricing:*
     - Pro Monthly: **₹149 / month**
     - Pro Season Pass (Recommended): **₹499 / 6 months**
     - Pro Annual: **₹799 / year**
   - *Decision:* Confirm whether these initial launch price points are approved or should be adjusted.

---

## 4. What Remains Fully Green (No Blockers)

- ✅ Codebase compiles cleanly with zero blocking errors.
- ✅ R8 ProGuard minification & resource shrinking verified.
- ✅ Unit tests 100% passing (55/55 tests across `:app` and `:domain`).
- ✅ Database version 4 and Room migrations verified.
- ✅ 100% offline-first functionality verified on live emulator.
- ✅ Zero invasive permissions.
