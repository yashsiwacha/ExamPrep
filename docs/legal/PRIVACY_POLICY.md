# Privacy Policy for ExamPrepOS

> **Effective Date**: October 9, 2026  
> **Last Updated**: October 2026  
> **Operated By**: `[LEGAL ENTITY NAME]` ("we", "us", "our")  
> **Application**: ExamPrepOS (Android Package: `com.examprep.android`)  
> **Contact Email**: `[PRIVACY EMAIL]`  
> **Grievance Officer**: `[GRIEVANCE OFFICER NAME / DESIGNATION]`  
> **Registered Address**: `[BUSINESS ADDRESS]`

---

## 1. Introduction & Scope

Welcome to **ExamPrepOS**. We are committed to protecting the privacy and personal data of our users ("you", "aspirants", "learners"). ExamPrepOS is an independent educational productivity and test preparation application designed to assist students preparing for competitive examinations (including JEE Main, NEET UG, UPSC Civil Services, CAT, and GATE CS).

This Privacy Policy explains how we collect, store, process, share, and protect your information when you download, install, and interact with the ExamPrepOS Android application.

This policy has been drafted in compliance with:
- **Google Play Developer Program Policies** (specifically the User Data Policy and Data Safety requirements);
- The **Digital Personal Data Protection Act, 2023 (DPDPA)** (India);
- The **Information Technology Act, 2000** and the **Information Technology (Reasonable Security Practices and Procedures and Sensitive Personal Data or Information) Rules, 2011** (India);
- Applicable global data protection principles regarding user control, data minimization, and purpose limitation.

---

## 2. Information We Collect

We follow the principle of **strict data minimization**. We only collect the minimal information necessary to deliver educational functionality.

### A. Information You Provide to Us
1. **Account Registration Data**: When you voluntarily create an account, we collect:
   - Your full name (or chosen display pseudonym);
   - Your email address;
   - Your password (stored exclusively as a salted cryptographic hash by Google Firebase Authentication; we never have access to your plaintext password).
2. **Study Preferences & Targets**:
   - Chosen target examination (e.g., JEE Main 2026, NEET UG 2026, UPSC CSE);
   - Target daily study hours (e.g., 4 hours/day);
   - Daily study reminder time preferences;
   - Sound and haptic feedback toggles.

### B. Information Generated Through App Usage (Stored Locally on Device)
1. **Study & Revision Progress**:
   - Topics marked as studied, revision counters, and syllabus completion percentages;
   - Spaced repetition flashcard review ratings and intervals (SM-2 algorithm);
   - Custom question bookmarks and notes.
2. **Diagnostic & Quiz Performance**:
   - Mock test and quiz attempts;
   - Selected answers, submission timestamps, and calculated scores;
   - Accuracy percentages by topic and chapter.

### C. Automatically Collected Technical Data
1. **Advertising Identifiers**:
   - When network connectivity is active, the **Google Mobile Ads SDK (AdMob)** may process your Google Advertising ID (GAID) and IP address for fraud prevention, frequency capping, and ad delivery.
   - You can control whether personalized ads are served using the in-app **Data & Consent** toggle in Settings or via Android OS system privacy settings.
2. **Diagnostic Logs (Development Builds Only)**:
   - Internal diagnostic logs (via Timber) run only in debug versions and are stripped from production release builds. No personal identifiers are logged.

---

## 3. Information We DO NOT Collect

ExamPrepOS does **NOT** collect, request, or process:
- Precise or approximate GPS location data;
- Camera access or photo library contents;
- Microphone or audio recordings;
- Contacts, address book, or calendar events;
- Government identification numbers (Aadhaar, PAN, SSN, Passport);
- Biometric information;
- Financial payment card details or bank account credentials (all subscriptions and purchases are processed directly and securely by Google Play Billing);
- Sensitive personal data relating to health, religion, or sexual orientation.

---

## 4. Purposes of Processing & Legal Basis

We process your data exclusively for lawful purposes:

| Purpose of Processing | Data Used | Legal Basis (DPDPA 2023 / Play Policy) |
|---|---|---|
| User Authentication & Account Access | Email address, password hash | Performance of contract / user request |
| Syllabus Tracking & Study Scheduling | Selected exam, topic checkmarks, target hours | Legitimate educational purpose / user consent |
| Test Performance Analysis | Quiz attempts, answers, scores | Legitimate educational purpose / app functionality |
| Notification Reminders | Reminder time, notification preferences | User consent (configurable in Settings) |
| Ad Serving & Monetization | GAID, IP address (via Google AdMob) | Legitimate interest (contextual) / User consent (personalized) |
| Customer Support & Grievance Redressal | Email correspondence | Legal obligation / customer service |

---

## 5. Storage Architecture: Local-First Principle

- **Local Storage (Primary)**: The vast majority of your educational data (quiz history, flashcard ratings, syllabus checkmarks, and downloaded question papers) is stored **locally on your device** in a private SQLite database managed by AndroidX Room (`/data/user/0/com.examprep.android/databases/`). This data is sandboxed by the Android OS and inaccessible to other apps.
- **Cloud Storage (Limited)**: Cloud processing is restricted to **Google Firebase Authentication** servers for validating email and password credentials.

---

## 6. Artificial Intelligence (AI) Processing

- **Current Production Status**: AI processing is **not currently active in production**.
- **Future AI Guidance**: If experimental generative AI features (e.g., automated question hints or syllabus planners) are enabled in future updates, they will operate with explicit notices. No personal identifiable information (PII) will be passed to third-party AI models without explicit opt-in consent.

---

## 7. Third-Party Services & Data Sharing

We do not sell, rent, or trade your personal data. We disclose information only to service providers acting as data processors under strict contractual obligations:

1. **Google Firebase Authentication (Google LLC)**:
   - *Purpose*: Secure user identity management and authentication.
   - *Data Shared*: Email address and account creation timestamps.
   - *Privacy Policy*: [Google Privacy Policy](https://policies.google.com/privacy)
2. **Google Mobile Ads / AdMob (Google LLC)**:
   - *Purpose*: In-app advertising.
   - *Data Shared*: Device identifiers (GAID), IP address, app interaction for ad metrics.
   - *Opt-Out*: Toggle "Personalized Ads (AdMob)" OFF under Settings → Data & Consent to request Non-Personalized Ads (`npa=1`).
   - *Privacy Policy*: [Google Advertising Policies](https://policies.google.com/technologies/ads)
3. **Google Play In-App Billing (Google LLC)**:
   - *Purpose*: Handling future digital subscriptions.
   - *Data Handled*: Payment tokens and entitlement states managed directly by Google Play.

---

## 8. Data Retention & Erasure

1. **Local Data**: Retained on your device until you:
   - Use the in-app **"Delete Account & All Data"** option in Settings;
   - Clear app storage in Android OS settings; or
   - Uninstall the application.
2. **Cloud Authentication Records**: Retained until you request account deletion.
3. **Deletion Mechanism**: You can immediately trigger a complete purge of your Firebase authentication account and reset local databases by navigating to:  
   **Settings → Data & Consent → Delete Account & All Data**.

---

## 9. User Rights under DPDPA, 2023 & Google Play Policies

As a data principal, you have the following rights:
- **Right to Access & Portability**: You may view and review your data summary in **Settings → Data & Consent → Export Preparation Progress**.
- **Right to Correction**: You may update your profile preferences, target exams, and study hours at any time.
- **Right to Erasure ("Right to be Forgotten")**: You may permanently delete your account and all associated data directly within the app.
- **Right to Withdraw Consent**: You may toggle off ad personalization or notification reminders at any time without penalty.
- **Right of Grievance Redressal**: You have the right to register a grievance with our designated Grievance Officer.

---

## 10. Children's Privacy (Google Play Families Policy Compliance)

- ExamPrepOS is designed for **secondary, higher-secondary, college students, and adult aspirants** preparing for competitive entrance examinations (typically ages 16 and older).
- We do not knowingly collect personal data from children under the age of 13 (or under the age of 18 without verifiable parental consent in jurisdictions where DPDPA requirements apply to minors).
- If you believe a minor under 13 has provided personal data through our app without parental consent, please contact our Grievance Officer immediately at `[PRIVACY EMAIL]`, and we will expeditiously delete the account and purge all associated records.

---

## 11. Security Safeguards

We implement industry-standard administrative, physical, and technical safeguards:
- All data transmission between the app and external services (Firebase, AdMob) is encrypted using **Transport Layer Security (TLS 1.2+)**;
- Plaintext passwords are never stored or transmitted;
- On-device databases reside within Android's sandboxed private storage;
- We follow Google Play's security guidelines against injection, insecure cleartext traffic, and credential leakage.

---

## 12. Grievance Officer & Contact Information

In compliance with Section 12 of the Digital Personal Data Protection Act, 2023 and Rule 3(2) of the Information Technology (Intermediary Guidelines and Digital Media Ethics Code) Rules, 2021, our designated contact for privacy matters is:

```text
Grievance Officer:
Name/Designation: Grievance Officer, ExamPrepOS
Legal Entity: [LEGAL ENTITY NAME]
Business Address: [BUSINESS ADDRESS]
Email: [PRIVACY EMAIL]
Support Inquiries: [SUPPORT EMAIL]
```

**SLA**: All privacy complaints, DPDPA inquiries, and deletion requests will be acknowledged within **48 hours** and resolved within **30 calendar days**.

---

## 13. Changes to this Privacy Policy

We may update this Privacy Policy from time to time to reflect operational, legal, or regulatory updates. When significant changes occur, we will notify you through an in-app notice or by updating the "Last Updated" date at the top of this document. Continued use of the application following the posting of changes constitutes acceptance of those changes.
