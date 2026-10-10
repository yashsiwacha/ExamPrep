# ExamPrepOS — Unit Economics Model
**Date**: October 2026  
**Analyst Role**: Senior Financial Analyst  
**Purpose**: Build realistic unit economics for ExamPrepOS at multiple user scales.

> **IMPORTANT**: All numbers labeled as [FACT], [ESTIMATE], or [ASSUMPTION]. Do NOT treat estimates as facts.

---

## 1. Revenue Model Architecture

ExamPrepOS has three revenue streams:

| Stream | Mechanism | Note |
|--------|-----------|------|
| **Ad Revenue** | Google AdMob (free users) | Low RPM in India |
| **Ad-Free Purchase** | ₹30 / 2 months (one-time IAP) | Light monetization |
| **Pro Subscription** | ₹50/month | Core revenue driver |

---

## 2. Key Assumptions (All Must Be Validated)

### Monetization Assumptions
| Parameter | Value | Basis |
|-----------|-------|-------|
| Ad-Free conversion rate | 3–5% of free users | [ASSUMPTION] Industry range for India IAP |
| Pro subscription conversion | 2–4% of free users | [ASSUMPTION] Conservative for new app |
| Monthly ad RPM (India, education) | ₹25–₹50 per 1,000 impressions | [ESTIMATE] India eCPM is low |
| Sessions per active user per day | 1.5 sessions | [ASSUMPTION] |
| Avg session = ad impressions | 3 impressions/session | [ASSUMPTION] Banner + interstitial |
| Google Play 30% cut on IAP | Applied to all paid revenue | [FACT] |
| Pro monthly churn rate | 15–25% per month | [ASSUMPTION] High for India student apps |
| Ad-Free churn (one-time) | 0% (non-recurring) | [FACT] |

### Cost Assumptions (Bootstrapped, Offline-First)
| Parameter | Value | Basis |
|-----------|-------|-------|
| Firebase Spark plan (free tier) | ₹0 | [FACT] Up to ~1,000 active users |
| Firebase Blaze (10k+ users) | ₹1,500–₹3,000/month | [ESTIMATE] |
| Firebase Blaze (100k+ users) | ₹8,000–₹20,000/month | [ESTIMATE] |
| Founder cost (bootstrapped) | ₹0 (sweat equity) | [ASSUMPTION] |
| AI inference per user/month | ₹15–₹40 (if enabled) | [ESTIMATE] ~$0.20–$0.50 per user via Gemini API |
| Play Store transaction fee | 15–30% | [FACT] 15% after first ₹10L revenue |
| Marketing spend | ₹0 initially | [ASSUMPTION] Organic only |
| Domain + misc tools | ₹500–₹1,000/month | [ESTIMATE] |

---

## 3. Unit Economics Per User Type

### Free (Ad-Supported) User
| Metric | Value | Note |
|--------|-------|------|
| Monthly ad impressions | ~135 | 1.5 sessions × 3 impressions × 30 days |
| RPM assumption | ₹35 | [ESTIMATE] India education |
| Gross ad revenue/user/month | **₹4.73** | 135/1000 × ₹35 |
| Infrastructure cost/user | ₹0.50–₹1.50 | [ESTIMATE] Firebase at scale |
| **Net contribution/user/month** | **₹3–₹4** | |

> [!WARNING]
> ₹3–4 per free user per month is very low. You need **thousands** of free users just to cover basic costs.

### Ad-Free Buyer (₹30 / 2 months)
| Metric | Value | Note |
|--------|-------|------|
| Gross IAP revenue | ₹30 | Per purchase |
| Play Store cut (30%) | -₹9 | [FACT] |
| Net revenue per purchase | ₹21 | |
| Duration | 2 months | |
| Net per month during subscription | **₹10.50** | |
| Infrastructure cost | ₹1–₹2/month | |
| **Net contribution/user/month** | **₹8.50–₹9.50** | |

### Pro Subscriber (₹50/month)
| Metric | Value | Note |
|--------|-------|------|
| Monthly subscription price | ₹50 | |
| Play Store cut (15–30%) | -₹7.50–₹15 | |
| Net revenue per subscriber/month | ₹35–₹42.50 | |
| Infrastructure cost | ₹2–₹3/month | |
| AI cost (if features used) | ₹15–₹25/month | [ESTIMATE] |
| **Net contribution/user/month (No AI)** | **₹32–₹40** | |
| **Net contribution/user/month (With AI)** | **₹10–₹25** | Heavily reduces margin |

> [!IMPORTANT]
> AI features at ₹50/month subscription are **margin-destroying**. At ₹15–₹25 AI cost per Pro user per month, your effective margin is only ₹10–₹25. This is barely break-even when combined with other costs.

---

## 4. Revenue Scenarios

### Base User Distribution Assumption
| Cohort | % of MAU |
|--------|---------|
| Free (ad-supported) | 88% |
| Ad-Free buyers | 7% |
| Pro subscribers | 5% |

> [ASSUMPTION]: These are generous conversion rates for a new app in India with no brand. Actual rates may be 1–2% Pro initially.

---

### Scenario 1 — 1,000 MAU

| Cohort | Users | Monthly Revenue |
|--------|-------|----------------|
| Free (ad) | 880 | ₹4,162 |
| Ad-Free | 70 | ₹735 |
| Pro | 50 | ₹2,125 |
| **Total Gross** | | **₹7,022** |
| Firebase + infra | | -₹0 (Spark free tier) |
| AI costs (Pro) | | -₹750–₹1,250 |
| Misc tools | | -₹500 |
| **Net Monthly P&L** | | **₹5,272–₹5,772** |

**Verdict**: Barely profitable but only ₹5k/month. Not a business yet — a side project.

---

### Scenario 2 — 5,000 MAU

| Cohort | Users | Monthly Revenue |
|--------|-------|----------------|
| Free (ad) | 4,400 | ₹20,812 |
| Ad-Free | 350 | ₹3,675 |
| Pro | 250 | ₹10,625 |
| **Total Gross** | | **₹35,112** |
| Firebase (Blaze) | | -₹1,500 |
| AI costs (Pro users) | | -₹3,750–₹6,250 |
| Misc | | -₹700 |
| Marketing (organic still) | | ₹0 |
| **Net Monthly P&L** | | **₹26,662–₹29,162** |

**Verdict**: ~₹26–29k/month. Achievable at ₹5k MAU. This is a livable side-income, not yet a business.

---

### Scenario 3 — 10,000 MAU

| Cohort | Users | Monthly Revenue |
|--------|-------|----------------|
| Free (ad) | 8,800 | ₹41,624 |
| Ad-Free | 700 | ₹7,350 |
| Pro | 500 | ₹21,250 |
| **Total Gross** | | **₹70,224** |
| Firebase | | -₹2,500 |
| AI costs | | -₹7,500–₹12,500 |
| Marketing | | -₹5,000–₹10,000 |
| Misc | | -₹1,000 |
| **Net Monthly P&L** | | **₹44,224–₹54,224** |

**Verdict**: ~₹44–54k/month. This is a real income for a solo founder. Break-even with modest marketing.

---

### Scenario 4 — 50,000 MAU

| Cohort | Users | Monthly Revenue |
|--------|-------|----------------|
| Free (ad) | 44,000 | ₹208,120 |
| Ad-Free | 3,500 | ₹36,750 |
| Pro | 2,500 | ₹106,250 |
| **Total Gross** | | **₹351,120** |
| Firebase | | -₹8,000–₹12,000 |
| AI costs | | -₹37,500–₹62,500 |
| Marketing | | -₹30,000–₹60,000 |
| Misc | | -₹3,000 |
| **Net Monthly P&L** | | **₹213,620–₹272,620** |

**Verdict**: ₹2–2.7 lakh/month. This is a genuine small business. At this scale content investment is justified.

---

### Scenario 5 — 100,000 MAU

| Cohort | Users | Monthly Revenue |
|--------|-------|----------------|
| Free (ad) | 88,000 | ₹416,240 |
| Ad-Free | 7,000 | ₹73,500 |
| Pro | 5,000 | ₹212,500 |
| **Total Gross** | | **₹702,240** |
| Firebase + backend | | -₹18,000–₹25,000 |
| AI costs | | -₹75,000–₹125,000 |
| Marketing | | -₹80,000–₹150,000 |
| Content team | | -₹50,000–₹80,000 |
| Misc + legal | | -₹10,000 |
| **Net Monthly P&L** | | **₹312,240–₹469,240** |

**Verdict**: ₹3–4.7 lakh/month. This is the target that justifies the effort. However, 100k MAU for a new bootstrapped app is **extraordinarily difficult** and takes 2–4 years if it ever happens.

---

## 5. Break-Even Analysis

### Fixed Monthly Costs (Bootstrapped, ~1,000–5,000 MAU)
| Cost | Amount |
|------|--------|
| Firebase (Blaze) | ₹1,500 |
| Misc tools/domain | ₹700 |
| **Total Fixed** | **₹2,200/month** |

### Contribution Per User Type
| User Type | Net Contribution/Month |
|-----------|----------------------|
| Free user | ₹3–4 |
| Ad-Free buyer | ₹8.50–9.50 (averaged monthly over 2 months) |
| Pro subscriber | ₹32–40 (no AI) / ₹10–25 (with AI) |

### Break-Even: Cover ₹2,200/month
| Strategy | Users Required |
|----------|---------------|
| Ad revenue only | ~600 free users |
| Ad + 5% Pro | ~50 MAU (tiny) |
| **Reality**: Firebase free covers you until ~800–1,000 MAU | ₹0 infra cost |

### Revenue Targets
| Monthly Profit Target | Required MAU | Required Pro Users (5%) |
|----------------------|-------------|------------------------|
| ₹5,000/month | ~1,000 MAU | 50 Pro |
| ₹10,000/month | ~2,000 MAU | 100 Pro |
| ₹25,000/month | ~5,000 MAU | 250 Pro |
| ₹50,000/month | ~10,000 MAU | 500 Pro |
| ₹1,00,000/month | ~20,000 MAU | 1,000 Pro |

> [!NOTE]
> These targets assume 5% Pro conversion. **Real conversion for a new app is more likely 1–2%**, which roughly doubles the MAU requirement for each target.

---

## 6. Customer Lifetime Value

### Retention Assumptions (Exam-Cycle Behavior)
- Average competitive exam preparation cycle: **6–18 months**
- Students are highly seasonal; many will leave after exam
- D30 retention for study apps: typically **15–25%** [ESTIMATE]
- Monthly Pro churn estimate: **15–25%** (high, exam-cycle driven)

| User Type | LTV Calculation | LTV |
|-----------|----------------|-----|
| Free (ad) | ₹4/mo × avg 4 months active | **₹16** |
| Ad-Free buyer | ₹21 one-time + re-purchase? | **₹21–₹42** |
| Pro subscriber | ₹37.50 net × 4 months avg | **₹150** |
| Pro subscriber (6 months) | ₹37.50 net × 6 months | **₹225** |

### Maximum Sustainable CAC
| User Type | LTV | Max CAC (50% LTV rule) |
|-----------|-----|----------------------|
| Free user | ₹16 | ₹8 |
| Ad-Free | ₹30 | ₹15 |
| Pro | ₹150–₹225 | ₹75–₹112 |

> [!CAUTION]
> At ₹50/month Pro pricing, the maximum sustainable paid CAC is only **₹75–₹112 per user**. This makes paid advertising (Google Ads, Meta) essentially **not viable** unless you can dramatically improve LTV through higher pricing or retention.

---

## 7. The AI Cost Problem

### AI Inference Cost Modeling

Assuming Gemini API pricing at approximately $0.20–$0.50/1,000 tokens for medium models:

| Feature | Tokens/Request | Requests/Month/User | Monthly Cost/User |
|---------|---------------|--------------------|--------------------|
| Weakness analysis | ~3,000 | 10 | ₹15–₹35 |
| AI study plan | ~5,000 | 4 | ₹15–₹38 |
| Explanations per question | ~500 | 30 | ₹11–₹29 |
| **Total AI cost/Pro user** | | | **₹41–₹102** |

**At ₹50/month Pro pricing**: AI features cost **more than the subscription revenue** at moderate usage.

### Conclusion on AI
- **AI in V1 at ₹50/month is financially untenable** unless heavily rate-limited
- AI should be a V2 feature at a higher price point (₹149–₹199/month) or rate-limited to 3–5 requests/month
- The "AI Weakness Surgery" feature marketed in the app is a **negative-margin feature at current pricing**

---

*All revenue estimates are pre-tax. GST implications on app subscriptions (18%) should be factored in for Indian tax compliance.*
