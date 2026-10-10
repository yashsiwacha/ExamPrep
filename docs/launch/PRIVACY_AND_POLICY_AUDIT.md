# ExamPrepOS — Privacy, Policy & Data Safety Audit

**Document Version:** 1.0.0  
**Date:** October 2026  
**Auditor / Role:** Chief Information Security Officer & Privacy Legal Counsel  
**Jurisdiction & Standards:** Google Play Developer Program Policies (2026), India Digital Personal Data Protection (DPDP) Act 2023, GDPR, CCPA/CPRA  

---

## 1. Regulatory Context & Legal Posture

ExamPrepOS is designed from the ground up as a **Privacy-First Educational Utility**. Unlike ad-tech trackers, ExamPrepOS requires no personal registration to deliver its core value.

### Key Statutory Compliances:
- **India DPDP Act 2023:** Data minimization observed. No personal identifier is mandated. Local data processing requires zero cross-border data transfer.
- **GDPR (Articles 15, 17, 20):** Right to Access (in-app data inspection), Right to Erasure (1-tap complete data deletion), and Data Portability (JSON export) fully implemented.
- **Google Play Families Policy:** ExamPrepOS is rated for Everyone / Ages 13+. It does not knowingly collect personal data from children under 13.

---

## 2. In-App Security Configuration Audit

Inspected files:
- `app/src/main/AndroidManifest.xml`
- `app/src/main/res/xml/network_security_config.xml`
- `app/src/main/res/xml/data_extraction_rules.xml`
- `app/src/main/res/xml/backup_rules.xml`

### Findings:
1. **Component Export Security:**
   - Only `MainActivity` is declared with `<intent-filter>` and marked `android:exported="true"`.
   - Zero unprotected exported broadcast receivers, services, or content providers exist. This eliminates unauthorized inter-process communication (IPC) attack vectors.
2. **Network Security Config:**
   - Cleartext traffic is completely blocked (`cleartextTrafficPermitted="false"`).
   - Only HTTPS traffic is permitted to Google Play / AdMob endpoints.
3. **Data Extraction & Backup Rules:**
   - `data_extraction_rules.xml` and `backup_rules.xml` configure Android Auto Backup safely, excluding ephemeral local session caches while backing up the user's primary syllabus progression across device switches.
4. **Zero Hardcoded Secrets in Source:**
   - Static analysis confirms zero private API keys, cloud master secrets, or proprietary tokens hardcoded in the codebase.

---

## 3. Google Play Data Safety Form Declarations

This section outlines the exact responses required in the Google Play Console Data Safety section:

### Question 1: Does your app collect or share any user data?
- **Declaration:** **Yes** (solely for essential diagnostic crash logs and non-personalized advertising).

### Data Types Disclosed:
1. **App Activity:**
   - *Data Type:* App interactions (e.g., sessions started, quizzes completed).
   - *Collected:* Yes (Aggregated/Anonymous).
   - *Shared:* No.
   - *Purpose:* App functionality, analytics.
   - *Ephemeral / Linked to Identity:* Not linked to personal user identity.
2. **App Info and Performance:**
   - *Data Type:* Crash logs and diagnostics.
   - *Collected:* Yes.
   - *Shared:* No.
   - *Purpose:* Analytics, bug fixing, stability.
3. **Device or Other Identifiers:**
   - *Data Type:* Advertising ID (Google Play Services).
   - *Collected:* Yes.
   - *Shared:* Shared with AdMob for ad delivery and fraud prevention.
   - *Note:* Configured in non-personalized mode (`npa=1`).

### Question 2: Is data encrypted in transit?
- **Declaration:** **Yes**, all network communication uses standard TLS/HTTPS.

### Question 3: Can users request that their data be deleted?
- **Declaration:** **Yes**. The app provides an in-app 1-tap deletion mechanism under `Settings -> Privacy Hub -> Delete All My Data`. A web deletion request URL is also provided at `https://examprepos.app/delete-account`.

---

## 4. Third-Party SDK Policy Assessment

| SDK / Dependency | Version | Purpose | Policy Risk Level | Required Disclosures |
|---|---|---|---|---|
| **AndroidX Core / Compose** | Jetpack 2024.02 | UI framework | **None (Zero Risk)** | None |
| **Room SQLite** | 2.6.1 | Local on-device storage | **None (Zero Risk)** | Data remains 100% on device |
| **WorkManager** | 2.9.0 | Local study reminders | **None (Zero Risk)** | Uses local device alarm/job scheduler |
| **Google Play Ads (AdMob)**| 23.0.0 | Ad-supported free tier | **Low / Managed** | Ad ID disclosed in Data Safety; Non-personalized mode active |

---

## 5. Mandatory Action Items for Publisher

1. **Host Public Privacy Policy:**  
   Host `docs/legal/PRIVACY_POLICY.md` on an accessible HTTPS URL (`https://examprepos.app/privacy`) before submitting the app for review.
2. **Insert Legal Entity Name:**  
   Replace `[Insert Publisher/Entity Legal Name]` and `[support@examprepos.app]` placeholders with the publisher's registered business or developer identity.
