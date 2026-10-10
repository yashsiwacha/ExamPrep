# ExamPrepOS — Analytics, Telemetry & Event Privacy Audit

> **Audit Type**: Privacy & Tracking Audit  
> **Applicable Policies**: Google Play User Data & SDK Policies, Apple App Tracking Transparency (ATT) Equivalence Principles, Indian DPDPA 2023 Sec 6 (Purpose Limitation)  
> **Status**: Verified against Codebase (`app/build.gradle.kts`, `:core`, `:data`, `:domain`)  
> **Date**: October 2026

---

## 1. Executive Summary

ExamPrepOS follows a strict **privacy-preserving, telemetry-minimized architecture**. 

### Critical Audit Finding:
- **No Third-Party Analytics SDKs**: ExamPrepOS does NOT bundle `firebase-analytics`, Google Analytics for Firebase, Mixpanel, Amplitude, Adjust, AppsFlyer, Segment, or Facebook SDK.
- **No Behavioral Session Recording**: No heatmaps, clickstream aggregators, or keystroke loggers are present.
- **No Advertising Tracking Beyond AdMob Standard**: AdMob serves ads according to Google Play Services terms. When users disable "Personalized Ads (AdMob)" in Settings, an explicit `npa=1` bundle is injected into all ad requests.

---

## 2. Telemetry & Tracking Inventory

| System Component | Purpose | Data Transmitted | PII Included? | Storage / Destination | User Opt-Out Available? |
|---|---|---|---|---|---|
| **Google Mobile Ads SDK (`play-services-ads:23.6.0`)** | Display banner and interstitial ads | IP address (for geolocation/fraud detection), Google Advertising ID (GAID), ad performance metrics | No direct PII (name, email, or passwords are never sent) | Google AdMob Ad Network servers | **Yes**: Toggle "Personalized Ads" OFF in app Settings (`npa=1`) or reset GAID in Android OS settings. |
| **Timber Application Logging (`com.jakewharton.timber:timber`)** | Diagnostic logging during development | Local logcat debug messages (filtered in release builds) | **Zero PII**: ExamPrepOS does not log user email, password, authentication tokens, or test scores. | Ephemeral device RAM only. Logs are never uploaded to any remote server. | Automatic (disabled in production release builds via Proguard). |
| **Local SQLite Metrics (Room)** | Calculating study streak, accuracy rate, weak topic distribution | Question attempt duration, chosen options, score | Stored exclusively on local device storage | Device sandbox only (`examprep_database`) | N/A (100% on-device local calculation). |

---

## 3. Production Logging Safeguards

To prevent unintentional data leakage via Android `Logcat`:
1. **No Sensitive PII in Logs**: All Timber log statements have been audited to ensure no sensitive parameters (tokens, passwords, user email addresses) are printed.
2. **Release Build Proguard Rules**: In `app/proguard-rules.pro`, logcat stripping rules ensure debug statements do not emit to system logs:
   ```proguard
   -assumenosideeffects class android.util.Log {
       public static boolean isLoggable(java.lang.String, int);
       public static int v(...);
       public static int d(...);
   }
   ```

---

## 4. Policy for Future Analytics Integration

If the publisher decides to add product analytics (e.g., Firebase Analytics or PostHog) in the future, the following non-negotiable guidelines must be observed:
1. **Never Log PII**: Events must never contain `user_email`, `user_name`, `phone_number`, or exact question strings.
2. **Pseudonymous Identifiers Only**: Use randomly generated UUIDs, never predictable user data.
3. **Explicit Consent**: Consent must be requested prior to initializing third-party tracking in jurisdictions requiring consent (EU GDPR, India DPDPA).
4. **Update Google Play Data Safety Form**: Any addition of an analytics SDK requires updating the Google Play Data Safety declaration in the Play Console.
