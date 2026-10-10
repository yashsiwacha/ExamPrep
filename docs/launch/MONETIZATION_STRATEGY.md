# ExamPrepOS — Sustainable Monetization Strategy

**Document Version:** 1.0.0  
**Date:** October 2026  
**Auditor / Role:** Principal Monetization Strategist & Commercial Product Lead  
**Monetization Model:** Ethical Freemium (Ad-Supported Free Tier + High-Utility Pro Pass)  

---

## 1. Monetization Philosophy: Respect First, Value Always

The Indian student demographic is highly sensitive to deceptive pricing, aggressive paywalls, and intrusive advertisements. Edtech companies that employ manipulative urgency or aggressive sales telemarketing suffer from severe brand distrust and high churn.

ExamPrepOS adheres to four commercial commitments:
1. **Never Lock Core Daily Execution:** Every student gets full access to the syllabus tree, daily study task scheduler, distraction-free focus timer, and basic spaced repetition for free forever.
2. **Never Interrupt Active Study with Ads:** Zero interstitial pop-ups or video ads during an active Pomodoro focus countdown or during a timed mock test.
3. **No Recurring Dark Patterns:** Transparent cancel-anytime subscriptions managed via Google Play Subscriptions, with clear upfront pricing and zero hidden auto-renewal traps.
4. **Offline-First Cost Advantage:** Because ExamPrepOS executes study planning, mock testing, and spaced repetition on-device using Room SQLite, our cost-to-serve an active user is virtually **₹0.00**. We do not burn expensive GPU tokens or database egress per session.

---

## 2. Feature Entitlement Tier Architecture

Managed through the centralized domain contract `EntitlementRepository` (`EntitlementState.FREE` vs `EntitlementState.PRO`):

| Feature / Capability | Free Tier (Ad-Supported) | Pro Tier (Exam Season Pass) | Rationale & Commercial Justification |
|---|---|---|---|
| **Syllabus Navigation & Tracking** | Full Access (All 5 Exams) | Full Access (All 5 Exams) | Core utility must remain universal to drive organic word-of-mouth. |
| **Daily Study Planner & Tasks** | Full Access | Full Access | Fundamental habit loop. Locking it would kill D7 retention. |
| **OLED Focus Timer (Pomodoro)** | Unlimited (All 3 Presets) | Unlimited (All 3 Presets) | Study timer must remain sacred and unhindered. |
| **National Pattern Mock Tests** | 1 Full Paper per Exam (5 total) | **All 25 Full Papers Unlocked** | Mock test density is the primary high-willingness-to-pay upgrade trigger. |
| **Mistake Vault Capacity** | Up to 30 bookmarked mistakes | **Unlimited Mistake Vault + Export** | High switching cost; power students preparing for 6+ months readily pay. |
| **Performance Analytics** | Subject Accuracy & Basic Scores | **Deep Percentile & Speed Benchmarks**| Advanced diagnostics for rank-oriented competitive aspirants. |
| **Advertising Experience** | Non-disruptive Banner Ads | **100% Ad-Free Clean Interface** | Peace of mind and maximum visual purity. |
| **Spaced Repetition (SRS)** | Basic 1, 3, 7-Day Intervals | **Full 1, 3, 7, 14, 30, 60-Day Intervals** | Advanced long-term retention scheduler. |

---

## 3. Subscription & Pricing Matrix (India Market Optimized)

*Pricing validated against typical Indian student pocket money budgets (₹150–₹500/month) and competitor benchmarks (Testbook Pass: ~₹400–₹800/yr, PW Pi: ₹300/mo):*

| SKU ID | Product Name | Target Student Context | Proposed Price (INR) | Effective Monthly | Google Play 15% Net |
|---|---|---|---|---|---|
| `examprep_pro_monthly` | **Pro Monthly** | Short sprint / Last-mile mock practice | **₹149 / mo** | ₹149 / mo | **₹126.65** |
| `examprep_pro_season_pass` | **Exam Season Pass (6-Mo)** | Recommended: Covers typical 6-mo exam prep cycle | **₹499 / 6 mo** | **₹83.16 / mo** | **₹424.15** |
| `examprep_pro_annual` | **Pro Annual Pass (12-Mo)** | Droppers & 2-year Class 11/12 aspirants | **₹799 / yr** | **₹66.58 / mo** | **₹679.15** |

### Google Play Billing Compliance
- Handled through official Google Play In-App Billing Library (v6+).
- Qualifies for Google Play's **15% Service Fee Tier** (available for developer accounts earning under $1 Million USD annually, compared to standard 30%).
- Grace periods and account hold states supported natively through Google Play.

---

## 4. Advertising Strategy (Free Tier Monetization)

- **Provider:** Google AdMob (Google Mobile Ads SDK 23.0.0).
- **Format:** Adaptive Banner Ads (`AdSize.BANNER`) integrated into non-intrusive bottom screen anchors on the Dashboard and Settings screens.
- **Privacy Mode:** Configured with `npa=1` (Non-Personalized Ads) for complete privacy compliance and zero user-tracking liability.
- **Projected eCPM:** Conservative estimate of **₹25.00 – ₹45.00** ($0.30 – $0.55 USD) per 1,000 banner impressions in the Indian education sector.
- **Estimated Banner Impressions:** ~6 impressions per daily active user (2 sessions × 3 screen visits).
- **Estimated Ad Revenue / DAU:** ~₹0.18 – ₹0.27 / day = **₹5.40 – ₹8.10 / monthly active user**.
