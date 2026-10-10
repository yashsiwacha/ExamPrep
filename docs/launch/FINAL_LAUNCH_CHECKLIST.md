# ExamPrepOS — Final Pre-Launch Verification Checklist

**Document Version:** 1.0.0  
**Date:** October 10, 2026  
**Auditor / Role:** Google Play Release Specialist & Lead QA Engineer  
**Target Track:** Google Play Console Closed Testing (Alpha Track)  

---

## 1. Technical & Binary Gate

- [x] **SDK Targets:** `compileSdk = 35`, `targetSdk = 35`, `minSdk = 26` (Verified in `app/build.gradle.kts`).
- [x] **Version Configuration:** `versionCode = 1`, `versionName = "1.0.0"`.
- [x] **Build Verification:** `./gradlew assembleRelease bundleRelease` completed successfully with zero errors.
- [x] **R8 Obfuscation & Shrinking:** `isMinifyEnabled = true`, `isShrinkResources = true` verified. Release APK is ~13MB.
- [x] **Signing Verification:** Production keystore signed (`keystore/examprep-release.jks`, key alias: `examprep-release`).
- [x] **Unit & Domain Tests:** 55/55 automated tests passing (100% success rate across `:domain` and `:app`).
- [x] **Permissions Guard:** Only 4 non-invasive permissions requested (`INTERNET`, `ACCESS_NETWORK_STATE`, `POST_NOTIFICATIONS`, `RECEIVE_BOOT_COMPLETED`). Zero dangerous permissions.
- [x] **Database Schema Stability:** Room Database version 4 verified across 20 entities with clean table mapping.
- [x] **Security Configuration:** Cleartext traffic disabled (`cleartextTrafficPermitted="false"`), only exported component is `MainActivity`.

---

## 2. Google Play Store Listing & Asset Gate

- [x] **App Title:** `ExamPrepOS: Study Plan & Mocks` (29 / 30 chars).
- [x] **Short Description:** `Personalized study plans, syllabus tracker, focus timer & mocks for JEE & NEET.` (79 / 80 chars).
- [x] **Full Description:** Structured 4,000-character copy with feature breakdowns and mandatory NTA/UPSC non-affiliation disclaimer.
- [x] **High-Res App Icon:** 512 x 512 px PNG (32-bit, alpha disabled) designed and specified.
- [x] **Feature Graphic:** 1024 x 500 px PNG designed with safe zone guidelines.
- [x] **Phone Screenshots:** 8 verified high-resolution screenshots (1080 x 2400 px) specified from authentic app captures.
- [x] **Category & Content Rating:** Set to `Education`, IARC rating Everyone / 3+ (Ages 13+ declared).

---

## 3. Privacy, Policy & Legal Gate

- [x] **In-App Legal Hub:** Interactive dialogs for Privacy Policy, Terms of Service, Open Source Licenses, and Disclaimers.
- [x] **Data Safety Form Declarations:** Formulated in `PRIVACY_AND_POLICY_AUDIT.md` (Diagnostic logs + AdMob Ad ID declared; data encrypted; deletion supported).
- [x] **Data Deletion:** 1-tap in-app data deletion implemented and verified in SQLite.
- [x] **Mandatory Disclaimer:** Prominent non-affiliation disclaimer with government testing agencies included in store listing and in-app legal screen.
- [ ] **Founder Action:** Host `PRIVACY_POLICY.md` on public HTTPS domain (`https://examprepos.app/privacy`).
- [ ] **Founder Action:** Replace AdMob public test App ID with publisher production App ID.

---

## 4. Google Play Closed Testing Track Execution Gate

- [ ] **Upload Artifact:** Upload `app-release.aab` to Play Console Closed Testing track.
- [ ] **Tester Opt-In:** Enroll 20 opted-in testers for personal developer account verification.
- [ ] **14-Day Testing Window:** Maintain active test sessions across 14 consecutive calendar days.
- [ ] **Production Rollout Application:** Apply for production access following completion of the 14-day testing period.
