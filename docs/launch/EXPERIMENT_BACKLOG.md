# ExamPrepOS — Post-Launch Continuous Experimentation Backlog

**Document Version:** 1.0.0  
**Date:** October 2026  
**Auditor / Role:** Growth Experimentation Lead & Conversion Rate Optimization (CRO) Specialist  
**Framework:** Scientific Hypothesis-Driven Testing with Guardrail Protection  

---

## 1. Experimentation Philosophy & Ethics

1. **Zero Hallucination / Statistical Rigor:** Experiments must achieve minimum 95% statistical confidence ($p < 0.05$) and adequate sample sizes before declaring winners.
2. **Student Well-Being Guardrails:** Never run deceptive pricing tests, fake scarcity timers ("Only 3 spots left!"), or manipulative notifications.
3. **Rollback Criteria:** Any variant that causes a statistically significant degradation in Day-1 retention (>3% drop) or app crash rate is rolled back within 2 hours.

---

## 2. Prioritized Experiment Cards

### EXP-01: Google Play Store Icon (Dark Monolith vs Electric Indigo Focus)
- **Hypothesis:** High-contrast electric indigo background with a glowing white book and cyan pulse increases visual distinctiveness against dark/blue competitors, driving higher search CTR.
- **Target Segment:** All organic Indian Google Play Store search & category browse visitors.
- **Primary Metric:** Store Listing Impressions to Detail Page Views CTR (Target: +12.0%).
- **Guardrail Metrics:** Store Page Install CVR (>28%), D1 Retention (>42%).
- **Implementation Effort:** Zero code changes (Managed via Google Play Console Store Listing Experiments).
- **Required Sample Size:** 3,000 unique visitors per variant (~14-day duration).
- **Success Criteria:** $p < 0.05$ with minimum +8% lift in installed users.
- **Rollback Criteria:** Any negative impact on install volume after 1,000 visitors.

---

### EXP-02: Screenshot 1 Framing ("Feature Clarity" vs "Relief Framing")
- **Hypothesis:** Leading with emotional relief (*"Stop worrying about your exam backlog"*) resonates more strongly with anxious aspirants than generic feature descriptions (*"Syllabus & Study Planner"*), increasing install conversion.
- **Target Segment:** Mobile visitors on Google Play Store detail page.
- **Primary Metric:** Page View to Install Conversion Rate (CVR).
- **Variant A (Control):** *"Know What to Study Every Single Day"* (Hero Dashboard).
- **Variant B (Candidate):** *"Stop Stressing Over Unfinished Chapters. Know Exactly What to Study Today."*
- **Guardrail Metric:** Day-1 retention (ensure we don't attract misaligned users).
- **Required Sample Size:** 2,500 visitors per variant.
- **Success Criteria:** +10% higher CVR with steady D1 retention.

---

### EXP-03: Onboarding Streamlining (4-Step Standard vs 2-Step Fast-Track)
- **Hypothesis:** Deferring the Target Score & Availability sliders until the first session and using sensible defaults (6h/day, 99 percentile) will reduce onboarding abandonment by +15%.
- **Target Segment:** First-time app opens on new installs.
- **Variant A (Control):** 4 screens: Welcome ➔ Exam Select ➔ Date & Target ➔ Study Plan.
- **Variant B (Fast-Track):** 2 screens: Welcome ➔ Exam Select (Auto-defaults applied) ➔ Dashboard.
- **Primary Metric:** Onboarding Completion Rate (Target: 82% ➔ 94%).
- **Guardrail Metric:** First Study Session Completion Rate within 24h (Ensure students still feel ownership of their plan).
- **Implementation Effort:** Low (4 hours refactoring in `OnboardingViewModel`).
- **Success Criteria:** Net increase in Day-1 active users who complete a study session.

---

### EXP-04: Default Focus Session Duration (25m Pomodoro vs 50m Deep Work)
- **Hypothesis:** While college students often prefer 50-minute blocks, high school/coaching students achieve 2x higher completion rates on 25-minute Pomodoro sessions, accelerating the habit loop.
- **Target Segment:** First-time focus timer users.
- **Variant A (Control):** Default selection set to 25 minutes.
- **Variant B:** Default selection set to 50 minutes.
- **Primary Metric:** First Focus Session Completion Rate (`study_session_completed` / `study_session_started`).
- **Guardrail Metric:** Total daily minutes studied per active user.
- **Success Criteria:** Completion rate > 80% with higher Day-3 return rate.

---

### EXP-05: Dashboard Primary Card Ordering (Next Action vs Due Revision)
- **Hypothesis:** On days when spaced repetition items are due, placing the *"3-Minute Quick Revision"* card above the *"2.5-Hour Deep Study"* card lowers cognitive barrier to entry, increasing morning DAU engagement.
- **Target Segment:** Returning users with active revision schedules on Days 3, 7, 14, 30.
- **Variant A (Control):** Deep Study task card always on top.
- **Variant B (Adaptive):** Micro-revision card appears on top only when items are due today.
- **Primary Metric:** Morning session start rate before 12:00 PM.
- **Guardrail Metric:** Daily study task completion rate.
- **Success Criteria:** +18% increase in morning active sessions with no drop in total study duration.

---

### EXP-06: Pro Season Pass Presentation (Single Paywall vs Tier Comparison)
- **Hypothesis:** Highlighting the *Exam Season Pass (6-Month)* as the single "Recommended by 94% of Aspirants" option with simple ₹83/month breakdown converts better than presenting three equal subscription columns.
- **Target Segment:** Users hitting the free mock test limit (1 mock test completed).
- **Primary Metric:** Paywall View to Checkout Conversion Rate.
- **Guardrail Metric:** Refund / cancellation rate.
- **Success Criteria:** +22% lift in gross subscription revenue per paywall impression.
