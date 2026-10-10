# ExamPrepOS — Privacy-Conscious Analytics Implementation Plan

**Document Version:** 1.0.0  
**Date:** October 2026  
**Auditor / Role:** Lead Analytics & Telemetry Engineer  
**Privacy Framework:** ISO/IEC 27701, GDPR Data Minimization, India DPDP Act 2023  

---

## 1. Analytics Philosophy & Data Minimization Iron Law

ExamPrepOS is an educational tool, not an ad-tracking surveillance apparatus. Our measurement system strictly adheres to the principle of **Purpose Limitation & Data Minimization**:
1. **Zero Personally Identifiable Information (PII):** Never log student names, email addresses, phone numbers, precise GPS locations, IP addresses, or device IMEIs.
2. **Zero Sensitive Educational Histories:** Never transmit raw question-and-answer texts, personal notes, or individual mistake records to external cloud endpoints.
3. **Local-First Aggregation:** All performance analytics (accuracies, total study minutes, streaks) are computed locally via Room SQLite. Only aggregated, pseudonymized lifecycle events are dispatched to telemetry.
4. **Kill Switch:** Telemetry is disabled by default in debug/development builds and respects user opt-out in Settings.

---

## 2. Telemetry Event Taxonomy & Funnel Mapping

Every event is designed to measure a specific phase of the **AARRR (Acquisition, Activation, Retention, Revenue, Referral)** funnel:

| Event Name | Exact Trigger | Required Payload Properties | Funnel Phase | Local Only vs Aggregated |
|---|---|---|---|---|
| `app_open` | `MainActivity.onResume()` on cold start | `session_id`, `app_version`, `is_first_open` | Acquisition / Retention | Aggregated |
| `onboarding_started` | User advances past Welcome Screen | `entry_point` (`direct`, `referral_demo`) | Activation | Aggregated |
| `exam_selected` | Exam card tapped on Onboarding Screen | `exam_id` (`JEE_MAIN`, `NEET_UG`, etc.) | Activation | Aggregated |
| `exam_setup_completed`| Target score & hours saved | `exam_id`, `daily_hours_target`, `days_to_exam` | Activation | Aggregated |
| `study_plan_generated`| `StudyPlanningEngine` saves plan | `exam_id`, `total_phases`, `total_tasks_count` | Activation | Aggregated |
| `first_task_viewed` | Dashboard renders generated task list | `task_id_hash`, `subject` | Activation | Local / Aggregated |
| `study_session_started`| Focus timer starts countdown | `session_mode` (`POMODORO`, `DEEP_WORK`), `duration_minutes` | Retention | Aggregated |
| `study_session_completed`| Focus timer successfully ends | `session_mode`, `duration_minutes`, `completed_ratio` | Retention (Aha!) | Aggregated |
| `quiz_started` | Practice quiz or mock test launched | `exam_id`, `quiz_type` (`PRACTICE`, `MOCK`), `question_count` | Retention | Aggregated |
| `quiz_completed` | Test submitted & score displayed | `exam_id`, `accuracy_bucket` (`0-40%`, `40-70%`, `70-100%`), `time_spent_secs` | Retention | Aggregated (No Q-text) |
| `revision_completed` | Spaced repetition item reviewed | `subject`, `interval_day` (`1`, `3`, `7`, `14`, `30`, `60`) | Retention | Aggregated |
| `reminder_enabled` | Notification toggle enabled | `reminder_time_hour` | Retention | Aggregated |
| `premium_screen_viewed`| Paywall / Pro features modal opens | `trigger_surface` (`mock_test_limit`, `cloud_backup`) | Revenue | Aggregated |
| `purchase_started` | Google Play Billing sheet launched | `sku_id`, `billing_period` | Revenue | Aggregated |
| `purchase_completed` | Billing client confirms purchase token| `sku_id`, `order_id_hash` | Revenue | Aggregated |

---

## 3. Architecture: Privacy-Guarded Analytics Abstraction

The presentation layer interacts exclusively with a domain contract `AnalyticsTracker`:

```kotlin
package com.examprep.domain.analytics

interface AnalyticsTracker {
    fun trackEvent(name: String, params: Map<String, Any> = emptyMap())
    fun setUserProperty(key: String, value: String)
}
```

### In-App Implementations:
1. `LocalAnalyticsTrackerImpl`: Writes event logs to local SQLite table for in-app diagnostic viewing and zero-leak operation.
2. `FirebaseAnalyticsTrackerImpl`: Wraps Firebase Analytics with `analytics.setAnalyticsCollectionEnabled(userConsentGranted)`. Automatically scrubs any accidentally passed string resembling an email, phone number, or IP address.

---

## 4. Privacy & Compliance Guarantees

- **Retention Window:** Aggregated event telemetry automatically expires in Google Analytics / BigQuery after **14 months** (maximum retention window set in Google Analytics admin).
- **Offline Queuing:** If the student is offline (e.g., studying in airplane mode), events are stored in SQLite and dispatched in batch upon reconnection, preventing battery wake-lock drain.
- **Account Deletion:** Invoking `"Delete All My Data"` in Settings fires an immediate purge request to local storage and invalidates the anonymous analytical installation identifier.
