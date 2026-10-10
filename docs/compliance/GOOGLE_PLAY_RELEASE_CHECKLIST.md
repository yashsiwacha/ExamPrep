# Google Play Release Readiness Compliance Checklist

> **Target Application**: ExamPrepOS (`com.examprep.android`)  
> **Release Target**: Production Distribution / Closed Testing on Google Play Console  
> **Target SDK**: Android 14 (API 34) / Android 15 (API 35 compatibility)  
> **Date**: October 2026

---

## 1. Compliance Verification Matrix

| Requirement / Policy Area | Status | Technical Evidence in Codebase | Action / Recommendation |
|---|---|---|---|
| **Privacy Policy (URL & In-App)** | **PASS** | `docs/legal/PRIVACY_POLICY.md` authored; in-app Privacy Policy dialog implemented in `SettingsScreen.kt` and `AuthScreens.kt`. | Host markdown on public domain URL (e.g. `https://[DOMAIN]/privacy`) and link in Google Play Console. |
| **Data Safety Declaration** | **READY** | `docs/compliance/GOOGLE_PLAY_DATA_SAFETY.md` maps every field: Name, Email, GAID, and encrypted in transit. | Submit field-by-field answers in Play Console under App Content → Data Safety. |
| **Account & Data Deletion** | **PASS** | `SettingsViewModel.deleteAllUserData()` purges Firebase Auth credentials, clears Room tables, re-seeds static exam catalog, and resets `AuthStore`. | Publisher must host external web form `https://[DOMAIN]/delete-account` as mandated by Play Console policy. |
| **Permissions Audit** | **PASS** | Removed unnecessary `SCHEDULE_EXACT_ALARM`. Only `INTERNET`, `ACCESS_NETWORK_STATE`, and `POST_NOTIFICATIONS` remain in `AndroidManifest.xml`. | None. App requires zero invasive runtime permissions (no Location, Camera, Contacts, Storage, Audio). |
| **Target SDK Compliance** | **PASS** | `compileSdk = 34`, `targetSdk = 34`, `minSdk = 26` configured in `app/build.gradle.kts`. Complies with current Google Play minimum API 34+ requirement. | Ensure new Play updates maintain current target SDK thresholds annually. |
| **Advertising Policy (AdMob)** | **PASS** | `AdComponents.kt` updated with `isPersonalized` parameter; attaches `npa=1` when disabled; in-app Settings switch provided; no deceptive clicks; banner and interstitials respect focus sessions. | Declare "My app contains ads: YES" in Play Console. |
| **Non-Affiliation & Impersonation** | **PASS** | App prominently declares independence from NTA, UPSC, IIT, IIM in `SettingsScreen.kt`, `AuthScreens.kt`, and `docs/legal/TERMS_OF_SERVICE.md`. No government crests or official logos used. | Maintain strict neutral branding in Play Store screenshots and promotional graphics. |
| **Content Rights & Copyright** | **PASS** | All mock papers in `assets/content/` are marked `source: ORIGINAL`, `year: 2026` with synthetic questions. No pirated textbooks or coaching scans. | Publisher should preserve records of content authorship. |
| **Educational Claims & Disclaimers** | **PASS** | No claims of "guaranteed selection" or "100% rank prediction". Educational disclaimer modal active in Settings. | Ensure Play Store short and long descriptions do not claim guaranteed exam rank or marks. |
| **Open-Source License Attribution** | **PASS** | `docs/legal/OPEN_SOURCE_LICENSES.md` created; in-app Open-Source Licenses dialog accessible in Settings. Zero GPL/AGPL dependencies. | Complete. |
| **In-App Billing / Subscriptions** | **N/A (READY)** | Pro tier UI marked as "Early Access Beta / Coming Soon". No unapproved third-party payment gateways (Razorpay, Stripe) bundled in code. | When monetization launches, integrate official Google Play Billing Library (`com.android.billingclient`). |
| **Children & Families Policy** | **PASS** | App target audience is declared as 16+ (Secondary, College, Adult aspirants). No features directed specifically at young children under 13. | Select target age group "18 and over" or "16-17" in Play Console Target Audience questionnaire. |
| **Security & Secrets Scanning** | **PASS** | No hardcoded API secrets, private keys, or passwords in repository. Debug logs stripped via Proguard in release. | Ensure release keystore is stored securely outside version control in CI/CD secrets. |
| **App Access Instructions** | **REQUIRES PUBLISHER ACTION** | App has demo student 1-tap sign-in and guest access, but Play review requires test credentials. | Provide test credentials (e.g. `demo@examprep.io` / `Password123!`) in Play Console under App Access. |
| **Developer Verification (D-U-N-S)** | **REQUIRES PUBLISHER ACTION** | Google Play requires organization identity verification with D-U-N-S number, verified address, and contact email. | Publisher must complete developer verification on Play Console. |
| **Final Legal Counsel Review** | **REQUIRES HUMAN REVIEW** | Complete technical and product compliance preparation completed. | Publisher's legal counsel must review `PRIVACY_POLICY.md` and `TERMS_OF_SERVICE.md` prior to public launch. |
