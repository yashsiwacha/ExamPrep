# Google Play Data Safety Form — Field-by-Field Declaration Guide

> **Target Application**: ExamPrepOS (`com.examprep.android`)  
> **Play Console Section**: App Content → Data Safety  
> **Assessment Scope**: Actual technical implementation in repository (`app`, `data`, `domain`, `core`)  
> **Date of Preparation**: October 2026

---

## 1. Overview Questions

| Play Console Question | Response | Technical Evidence / Justification |
|---|---|---|
| **Does your app collect or share any of the required user data types?** | **Yes** | App collects Name and Email (Authentication & Account Management) and uses Google Mobile Ads SDK (Device or other IDs). |
| **Is all of the user data collected by your app encrypted in transit?** | **Yes** | All network communications (Firebase Auth API calls and Google Mobile Ads network calls) use HTTPS/TLS 1.2+ protocols. Android `usesCleartextTraffic` is false by default. |
| **Do you provide a way for users to request that their data be deleted?** | **Yes** | An automated in-app deletion mechanism exists under **Settings → Data & Consent → Delete Account & All Data** which deletes cloud Firebase credentials and resets local Room databases. |
| **Do you provide a deletion request URL outside the app?** | **REQUIRES PUBLISHER CONFIRMATION** | Play Console mandates an external web URL where users can request account and data deletion. The publisher must host `https://[PUBLISHER_DOMAIN]/delete-account` and link it in Play Console. |

---

## 2. Data Types Field-by-Field Breakdown

### Category: Personal Info

#### 1. Name
- **Collected?**: **Yes**
- **Shared?**: **No**
- **Processed ephemerally?**: **No** (Stored in local database and Firebase Auth Profile)
- **Required or Optional?**: **Optional** (User can skip registration or leave name blank during sign-in)
- **Purposes**:
  - `Account management` (Primary)
  - `App functionality` (Displays personalization greeting on Home dashboard)
- **Is this data encrypted in transit?**: **Yes** (TLS/HTTPS)
- **Can users request this data be deleted?**: **Yes** (Via Settings → Delete Account & All Data)

#### 2. Email Address
- **Collected?**: **Yes**
- **Shared?**: **No** (Transferred only to Google Firebase Auth as service provider / data processor)
- **Processed ephemerally?**: **No** (Stored in Firebase Auth and local profile table)
- **Required or Optional?**: **Required for registered accounts; Optional for offline/guest learners**
- **Purposes**:
  - `Account management` (Sign in, password reset, account recovery)
- **Is this data encrypted in transit?**: **Yes** (TLS/HTTPS)
- **Can users request this data be deleted?**: **Yes** (Via in-app deletion button)

#### 3. User IDs (Account ID / Firebase UID)
- **Collected?**: **Yes**
- **Shared?**: **No**
- **Processed ephemerally?**: **No** (Stored locally and in Firebase)
- **Required or Optional?**: **Required for account functionality**
- **Purposes**:
  - `Account management`
- **Is this data encrypted in transit?**: **Yes** (TLS)
- **Can users request this data be deleted?**: **Yes**

---

### Category: App Activity

#### 1. App Interactions (Quiz attempts, syllabus checkmarks, study hours)
- **Collected?**: **Yes (Local on-device only)**
- **Shared?**: **No**
- **Processed ephemerally?**: **No** (Stored in local SQLite database)
- **Note on Google Play Guidance**: Google Play's Data Safety definition states that data processed and stored **solely on the user's device** without transmission off the device does *not* need to be declared as "Collected". However, because user diagnostic scores may be synced if cloud sync is activated in the future, we disclose it for maximum transparency:
- **Purposes**:
  - `App functionality` (Syllabus progress, mock test evaluation, spaced repetition revision)
- **Can users delete it?**: **Yes** (In-app data purge or clearing app cache)

---

### Category: Device or Other IDs

#### 1. Device or other IDs (Google Advertising ID - GAID)
- **Collected?**: **Yes** (Collected automatically by Google Mobile Ads SDK `play-services-ads:23.6.0`)
- **Shared?**: **Yes** (Shared with Google AdMob / authorized ad demand partners)
- **Processed ephemerally?**: **No** (Cached by Google Play Services)
- **Required or Optional?**: **Optional** (User can toggle OFF "Personalized Ads (AdMob)" in Settings, which instructs the SDK with `npa=1` non-personalized ads, and user can reset GAID in Android OS settings)
- **Purposes**:
  - `Advertising or marketing`
  - `Fraud prevention, security, and compliance`
- **Is this data encrypted in transit?**: **Yes** (TLS/HTTPS)
- **Can users request this data be deleted?**: **Governed by Google AdMob privacy controls**

---

### Categories NOT Collected (Explicit "No" Declarations)

The following categories must be declared as **NOT COLLECTED**:

1. **Location**:
   - Approximate Location: **No**
   - Precise Location: **No**
2. **Financial Info**:
   - Credit card, debit card, or bank account numbers: **No** (Purchases handled entirely by Google Play Billing)
   - Purchase history: **No** (Managed by Google Play)
   - Credit score: **No**
3. **Health and Fitness**:
   - Health info: **No**
   - Fitness info: **No**
4. **Messages**:
   - Emails: **No** (Except user's own email for login)
   - SMS/MMS: **No**
   - In-app messages: **No**
5. **Photos and Videos**:
   - Photos: **No**
   - Videos: **No**
6. **Audio Files**:
   - Voice or sound recordings: **No**
7. **Files and Docs**:
   - Files and documents: **No**
8. **Calendar**:
   - Calendar events: **No**
9. **Contacts**:
   - Contacts: **No**
10. **Web Browsing**:
    - Web browsing history: **No**
11. **App Info and Performance**:
    - Crash logs: **No** (No external crash reporting SDK like Crashlytics is configured)
    - Diagnostics: **No**
12. **Other Personal Info**:
    - Race, ethnicity, political or religious beliefs, sexual orientation: **No**

---

## 3. Account Deletion URL Requirement

Google Play Developer Policy mandates:
> *"If your app allows users to create an account from within the app, it must also allow users to request that their account be deleted. You must also provide a web link where users can request account and data deletion."*

### What has been implemented:
1. **In-App Deletion**: Available in `SettingsScreen.kt` via `deleteAllUserData`, which triggers `FirebaseAuthRepository.deleteAccount()` and local database wipe.

### Action Required by Publisher:
- Create a web page at `https://[PUBLISHER_DOMAIN]/delete-account`.
- The web page must specify:
  1. What data is deleted (Firebase user ID, email, study progress).
  2. How users without the app can request deletion (e.g., submitting an email to `[PRIVACY EMAIL]`).
  3. Enter this URL into the Google Play Console under **App Content → Data Safety → Account Deletion**.
