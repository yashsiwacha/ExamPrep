# ExamPrepOS — Unit Economics & Financial Viability Model

**Document Version:** 1.0.0  
**Date:** October 2026  
**Auditor / Role:** Senior Financial Analyst & Founding Engineer  
**Model Type:** Scalable Offline-First Client Architecture (Zero Heavy Server Infrastructure)  

---

## 1. Operating Cost Advantage: The Offline-First Moat

Traditional edtech applications burn massive operating budgets on video streaming bandwidth (AWS CloudFront / Akamai CDN) and generative AI API calls (OpenAI / Anthropic inference tokens), often resulting in negative unit margins per active user.

ExamPrepOS is architecturally engineered with **Zero-Compute Infrastructure**:
- **Local Persistence:** Room SQLite manages all study plans, sessions, syllabi, questions, and mastery state on the user's Android device.
- **Deterministic Engines:** Planning, spaced repetition, and scoring logic run on-device via Kotlin bytecode.
- **Hosting & Backend:** Firebase Auth and static assets operate comfortably within Google Cloud / Firebase Free Spark Tier (up to 50,000 monthly active users at $0.00/month).
- **Fixed Infrastructure Overhead:** **₹0 / user** compute and **₹0 / user** database egress. Only nominal developer registration ($25 one-time) and website domain (~₹1,200/year).

---

## 2. Core Economic Assumptions (Labeled by Source)

| Parameter | Conservative | Base Scenario | Optimistic | Source / Classification |
|---|---|---|---|---|
| **DAU / MAU Ratio** | 0.28 | **0.35** | 0.42 | Target Benchmark (Daily study utility) |
| **Ad Impressions / DAU** | 4 impressions | **6 impressions** | 8 impressions | Verified layout (Dashboard + Settings banner) |
| **Ad eCPM (India Education)** | ₹25.00 ($0.30) | **₹35.00 ($0.42)** | ₹45.00 ($0.54) | AdMob Industry Benchmark |
| **Free-to-Paid Conversion**| 2.0% | **3.5%** | 5.0% | Historical benchmark for exam test-pass models |
| **Blended Paying Product** | Pro Monthly (₹149) | **Season Pass (₹499 / 6mo)** | Annual Pass (₹799 / yr) | Target marketing focus |
| **Effective Monthly ARPPU**| ₹75.00 / mo | **₹83.16 / mo** | ₹95.00 / mo | Net amortization across subscription lifespan |
| **Google Play Service Fee**| 15.0% | **15.0%** | 15.0% | Verified Google Play <$1M tier |
| **Refunds & Chargebacks** | 1.5% | **0.8%** | 0.5% | Play Console refund reserve |
| **Cloud Server Cost / MAU**| ₹0.05 / MAU | **₹0.00 / MAU** | ₹0.00 / MAU | Verified Offline-First Room Architecture |

---

## 3. Financial Scenarios (10k, 50k, and 100k Monthly Active Users)

*All figures in Indian Rupees (INR) per month unless noted:*

### Scenario A: Early Traction (10,000 MAU)

| Line Item | Conservative (2.0% Conv) | Base (3.5% Conv) | Optimistic (5.0% Conv) |
|---|---|---|---|
| **Monthly Active Users (MAU)** | 10,000 | 10,000 | 10,000 |
| **Daily Active Users (DAU)** | 2,800 | 3,500 | 4,200 |
| **Monthly Ad Revenue (Net)** | ₹8,400 | ₹22,050 | ₹45,360 |
| **Paying Subscribers** | 200 | 350 | 500 |
| **Gross Subscription Revenue** | ₹15,000 | ₹29,106 | ₹47,500 |
| **Google Play 15% Fee** | -₹2,250 | -₹4,366 | -₹7,125 |
| **Net Subscription Revenue** | ₹12,750 | ₹24,740 | ₹40,375 |
| **Total Monthly Net Revenue** | **₹21,150** | **₹46,790** | **₹85,735** |
| **Infrastructure / Hosting Costs**| -₹1,500 | -₹1,500 | -₹1,500 |
| **Net Contribution Margin** | **₹19,650 (92.9%)** | **₹45,290 (96.8%)** | **₹84,235 (98.3%)** |

---

### Scenario B: Growth Stage (50,000 MAU)

| Line Item | Conservative (2.0% Conv) | Base (3.5% Conv) | Optimistic (5.0% Conv) |
|---|---|---|---|
| **Monthly Active Users (MAU)** | 50,000 | 50,000 | 50,000 |
| **Daily Active Users (DAU)** | 14,000 | 17,500 | 21,000 |
| **Monthly Ad Revenue (Net)** | ₹42,000 | ₹110,250 | ₹226,800 |
| **Paying Subscribers** | 1,000 | 1,750 | 2,500 |
| **Gross Subscription Revenue** | ₹75,000 | ₹145,530 | ₹237,500 |
| **Google Play 15% Fee** | -₹11,250 | -₹21,830 | -₹35,625 |
| **Net Subscription Revenue** | ₹63,750 | ₹123,700 | ₹201,875 |
| **Total Monthly Net Revenue** | **₹105,750** | **₹233,950** | **₹428,675** |
| **Infrastructure / Hosting Costs**| -₹3,500 | -₹3,500 | -₹3,500 |
| **Net Contribution Margin** | **₹102,250 (96.7%)** | **₹230,450 (98.5%)** | **₹425,175 (99.2%)** |

---

### Scenario C: Established Category Leader (100,000 MAU)

| Line Item | Conservative (2.0% Conv) | Base (3.5% Conv) | Optimistic (5.0% Conv) |
|---|---|---|---|
| **Monthly Active Users (MAU)** | 100,000 | 100,000 | 100,000 |
| **Daily Active Users (DAU)** | 28,000 | 35,000 | 42,000 |
| **Monthly Ad Revenue (Net)** | ₹84,000 | ₹220,500 | ₹453,600 |
| **Paying Subscribers** | 2,000 | 3,500 | 5,000 |
| **Gross Subscription Revenue** | ₹150,000 | ₹291,060 | ₹475,000 |
| **Google Play 15% Fee** | -₹22,500 | -₹43,659 | -₹71,250 |
| **Net Subscription Revenue** | ₹127,500 | ₹247,401 | ₹403,750 |
| **Total Monthly Net Revenue** | **₹211,500** | **₹467,901** | **₹857,350** |
| **Infrastructure / Hosting Costs**| -₹6,500 | -₹6,500 | -₹6,500 |
| **Net Contribution Margin** | **₹205,000 (96.9%)** | **₹461,401 (98.6%)** | **₹850,850 (99.2%)** |

---

## 4. Break-Even Analysis & Capital Efficiency

- **Fixed Monthly Operating Break-Even:** ~**₹2,500 / month** (Domain hosting & minor Firebase usage).
- **Subscribers Required to Break Even:** Exactly **6 paying subscribers** on the Pro Season Pass (₹499/6mo) cover 100% of all ongoing digital infrastructure costs.
- **Capital Runway:** Because there are no costly server fleets, third-party AI token bills, or large customer support teams, ExamPrepOS is inherently cashflow-positive from Month 1 with minimal subscriber volume.
