# Google Play Release & Legal Compliance Implementation Plan: ExamPrepOS Android

## 1. Executive Summary & Objective
This plan establishes the technical, operational, and legal readiness of the **ExamPrepOS** Android application for public release on Google Play. 

The mandate requires active compliance engineering:
1. Inspecting actual application data handling, storage, and networking.
2. Eliminating policy blockers (e.g., unjustified exact alarm permissions).
3. Implementing working in-app Privacy & Legal UI, functional Data Deletion workflows, and ad personalization controls.
4. Producing comprehensive data inventories, Data Safety specifications, licensing audits, and official policies tailored specifically to ExamPrepOS.

---

## 2. Applicable Regulatory & Platform Standards

### 2.1. Google Play Developer Program Policies (2024–2026 Standards)
- **User Data Policy**: Complete transparency regarding collection, use, and sharing of personal data.
- **Data Safety Section**: Precise field-by-field declaration of collected data (account credentials, diagnostics, progress).
- **Account Deletion Policy**: Mandatory in-app and web-based account and data deletion mechanisms.
- **Exact Alarms Policy**: Removal of `SCHEDULE_EXACT_ALARM` unless core functionality is an alarm clock/timer.
- **Advertising & Families Policies**: Transparency, appropriate ad labeling, non-personalized advertising options, age declarations.
- **Impersonation & Government Misleading Claims**: Clear disclaimers stating non-affiliation with government exam bodies (NTA, UPSC, IIT, IIM).
- **Target SDK**: Play requirement targetSdk >= 34 (App is currently targetSdk 35).

### 2.2. Indian Regulatory Framework
- **Digital Personal Data Protection Act, 2023 (DPDPA 2023)**:
  - Lawful processing based on specified purpose.
  - Notice with itemized personal data items and purpose.
  - Right to access, correction, and erasure (data deletion).
  - Grievance redressal contact details.
  - Children's data protections (prohibiting behavioral tracking of minors without verifiable parental consent).
- **Information Technology Act, 2000 & SPDI Rules (2011)**:
  - Security practices for sensitive personal data (passwords, credentials).
- **Consumer Protection Act, 2019 & E-Commerce Rules, 2020**:
  - Non-deceptive educational claims, transparent pricing, refund/cancellation disclosures.

---

## 3. Phased Implementation Roadmap

### Phase 1: Technical Permissions & Security Hygiene
- **Manifest Audit**: Remove unused `SCHEDULE_EXACT_ALARM` from `AndroidManifest.xml` to avoid immediate Play rejection.
- **Security Check**: Audit logging statements to ensure no passwords, emails, or personal tokens are printed in production release logs.

### Phase 2: Technical Privacy Controls & In-App UI Implementation
- **Data Deletion Engine**:
  - Add `deleteCurrentUserAccount()` in `AuthStore` and `AuthViewModel`.
  - Add local data erasure in `ExamPrepDatabase` to purge personal study history, attempts, and profile records.
  - Add Firebase Auth user deletion in `FirebaseAuthRepositoryImpl`.
- **Privacy & Legal Center**:
  - Enhance `SettingsScreen.kt` with a working **Privacy & Legal** section.
  - Implement full-screen dialogs / viewers for:
    - Privacy Policy viewer
    - Terms of Service viewer
    - Open-Source Licenses viewer
    - Educational & Non-Affiliation Disclaimer
    - Advertising & Personalization Preferences (personalized vs non-personalized ads toggle)
    - Permanent Account & Data Deletion Confirmation Workflow
    - Contact Privacy / Grievance Officer details

### Phase 3: Legal & Regulatory Documentation
- Generate `docs/compliance/DATA_INVENTORY.md`
- Generate `docs/compliance/GOOGLE_PLAY_DATA_SAFETY.md`
- Generate `docs/compliance/CONTENT_RIGHTS_AUDIT.md`
- Generate `docs/compliance/ANALYTICS_PRIVACY.md`
- Generate `docs/legal/PRIVACY_POLICY.md`
- Generate `docs/legal/TERMS_OF_SERVICE.md`
- Generate `docs/legal/OPEN_SOURCE_LICENSES.md`

### Phase 4: Compliance Checklist & Final Audit
- Generate `docs/compliance/GOOGLE_PLAY_RELEASE_CHECKLIST.md`
- Generate `docs/compliance/FINAL_COMPLIANCE_AUDIT.md`

### Phase 5: Verification & Quality Gates
- Execute `./gradlew testDebugUnitTest` to guarantee zero test regressions.
- Execute `./gradlew assembleDebug` to confirm clean packaging and compilation.
