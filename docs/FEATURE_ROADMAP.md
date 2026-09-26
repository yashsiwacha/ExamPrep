# ExamPrep OS — Feature Roadmap

**Version:** 1.0  
**Last Updated:** September 2025

---

## Development Phases

### Phase 0 — Foundation (CURRENT)
**Goal:** Buildable, navigable scaffold with complete architecture

| Item | Status |
|------|--------|
| Multi-module Gradle project | ✅ Complete |
| Version catalog (libs.versions.toml) | ✅ Complete |
| Domain models (all entities, enums) | ✅ Complete |
| Repository interfaces | ✅ Complete |
| Engine interfaces | ✅ Complete |
| Room entities + DAOs + Database | ✅ Complete |
| Hilt DI configuration | ✅ Complete |
| Entity ↔ Domain mappers | ✅ Complete |
| Repository implementations (stub) | ✅ Complete |
| Navigation graph | ✅ Complete |
| All screen composables (scaffold) | ✅ Complete |
| Design system (colors, typography, shapes) | ✅ Complete |
| Product documentation | ✅ Complete |
| ADR documentation | ✅ Complete |

---

### Phase 1 — Core User Journey (Weeks 1–4)
**Goal:** A student can onboard, get a plan, and start their first study session

#### Onboarding
- [ ] `OnboardingViewModel` — orchestrates 4-step flow
- [ ] Exam selection from Room (pre-seeded exam configs)
- [ ] Exam date picker (Material 3 DatePicker)
- [ ] Daily hours + session length slider
- [ ] Diagnostic assessment (10 questions per subject)
- [ ] `markOnboardingComplete` → DataStore flag

#### Plan Generation
- [ ] `StudyPlanningEngineImpl` — core algorithm
- [ ] Plan preview screen before accepting
- [ ] Persist plan to Room

#### Dashboard
- [ ] `DashboardViewModel` — live data from Room
- [ ] Exam countdown (real dates)
- [ ] Today's task (from StudyPlan)
- [ ] Plan status (ON_TRACK, BEHIND, AHEAD)
- [ ] Study streak from sessions

#### Focus Session
- [ ] `FocusSessionViewModel` — timer with coroutine
- [ ] Session start/end → Room
- [ ] Session outcome collection
- [ ] WorkManager session backup (survives process death)

#### Settings
- [ ] `SettingsViewModel` — DataStore preferences
- [ ] Reminder scheduling via WorkManager

---

### Phase 2 — Mastery Layer (Weeks 5–8)
**Goal:** The app tracks mastery, not just completion

#### Syllabus + Mastery
- [ ] Full syllabus tree from Room
- [ ] Per-topic mastery state display
- [ ] `SyllabusViewModel` — live mastery data
- [ ] Mastery state visual indicators

#### Timeline (Full)
- [ ] `TimelineViewModel` — live plan data
- [ ] Day-level task view
- [ ] Phase completion percentage
- [ ] Rescheduling on missed days

#### Mastery Engine
- [ ] `MasteryEngineImpl` — multi-signal calculation
- [ ] Background mastery recalculation via WorkManager
- [ ] Mastery transition notifications

---

### Phase 3 — Practice Engine (Weeks 9–12)
**Goal:** Students can practice with a proper quiz engine

#### Question Bank Seeding
- [ ] JSON schema for question import (`ExamContentBundle`)
- [ ] Content pipeline: validate → import → verify
- [ ] First batch: Original JEE questions (100 per subject)

#### Quiz Engine
- [ ] `QuizViewModel` — question loading, timer, scoring
- [ ] `QuizScoringEngineImpl` — exam-specific marking schemes
- [ ] Question attempt recording
- [ ] Quiz result analytics

#### Performance Screen
- [ ] `PerformanceViewModel` — aggregated stats
- [ ] Subject accuracy bars
- [ ] Weak topic identification
- [ ] Basic charts (accuracy over time)

---

### Phase 4 — Revision + Intelligence (Weeks 13–16)
**Goal:** The app gets smarter about what to study next

#### Revision Engine
- [ ] `RevisionEngineImpl` — spaced repetition
- [ ] Revision scheduling on topic mastery
- [ ] Revision due notifications

#### Recommendation Engine
- [ ] `RecommendationEngineImpl` — rule-based suggestions
- [ ] Recommendation display on Dashboard
- [ ] Recommendation actioning + dismissal
- [ ] Daily recommendation refresh (WorkManager)

#### Analytics (Advanced)
- [ ] Trend charts (accuracy over 7/30 days)
- [ ] Readiness score (0–100)
- [ ] Predicted exam score estimate

---

### Phase 5 — Monetization + AI (Weeks 17–24)
**Goal:** Revenue and premium features

#### Subscription
- [ ] Google Play Billing integration
- [ ] AdMob banner + interstitial integration
- [ ] Entitlement management (local)
- [ ] Upgrade flow

#### AI Integration (Optional)
- [ ] `AiService` implementation (Gemini API)
- [ ] Concept explanation
- [ ] Performance summary in natural language
- [ ] Graceful degradation when AI unavailable

#### Content Expansion
- [ ] NEET question bank
- [ ] UPSC question bank
- [ ] PYQ year-wise filter

---

## User Stories (Priority Order)

### MUST HAVE (Phase 1)
- **US-001:** As a student, I can select my exam and exam date so the app knows my deadline.
- **US-002:** As a student, I can see how many days until my exam on the home screen.
- **US-003:** As a student, I receive a personalized study plan based on my available time.
- **US-004:** As a student, I can see what I need to study today.
- **US-005:** As a student, I can start a timed focus session for any task.
- **US-006:** As a student, the app records how long I study.

### SHOULD HAVE (Phase 2–3)
- **US-007:** As a student, I can see the mastery state of each topic in the syllabus.
- **US-008:** As a student, I can take a quick quiz on any topic.
- **US-009:** As a student, I can see my accuracy and weak areas.
- **US-010:** As a student, I receive a detailed quiz result with question-level feedback.
- **US-011:** As a student, I know if I'm on track to finish my syllabus before the exam.

### NICE TO HAVE (Phase 4–5)
- **US-012:** As a student, the app schedules revision for me automatically.
- **US-013:** As a student, the app tells me which topic to study with a reason.
- **US-014:** As a student, I can remove ads with a subscription.
- **US-015:** As a student, the app explains difficult concepts in plain language (AI).
