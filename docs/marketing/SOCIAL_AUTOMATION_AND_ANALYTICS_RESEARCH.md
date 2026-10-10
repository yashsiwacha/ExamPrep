# ExamPrepOS — Social Publishing Automation & Analytics Research

**Document Version:** 1.0.0  
**Status:** Completed Architectural Assessment  
**Author:** Autonomous Marketing Operations & Release Engineering  
**Operating Principle:** $0 External Tooling / Maximize Native APIs & Open-Source Infrastructure  

---

## 1. Executive Summary

To scale ExamPrepOS marketing from local file generation to automated distribution, we evaluated the feasibility, rate limits, prerequisites, and cost profiles of multi-platform publishing, queue-based scheduling, and post-performance analytics.

Prioritizing zero subscription overhead, we analyzed:
1. **Direct Platform REST APIs** (Google, Meta, X, LinkedIn, TikTok)
2. **Free-Tier Aggregators** (Buffer Free Tier: 3 channels, 10 scheduled posts)
3. **Open-Source Self-Hosted Hubs** (Postiz / Mixpost / Blotato)
4. **Local GitOps & Playwright Headless Automation** (Deterministic browser execution with human oversight)

---

## 2. Platform-by-Platform API Feasibility Matrix

| Platform | Official API / Endpoint | Supported Media | Automated Scheduling | Free Tier Quota / Limitations | Setup Requirements |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **YouTube** | YouTube Data API v3 (`videos.insert`) | MP4 (1080x1920 Shorts) | **Yes** (`status.publishAt` ISO timestamp) | 10,000 units/day (~6 video uploads/day free) | Google Cloud Project, OAuth 2.0 Client Credentials |
| **Instagram** | Instagram Graph API (`/media`, `/media_publish`) | Reels MP4 + Cover JPG | **Yes** (`scheduled_publish_time` timestamp) | 200 calls/hour per user (50 Reels/day) | Meta Developer App, Facebook Page connected to Instagram Professional/Creator account |
| **Facebook** | Meta Graph API (`/{page-id}/videos`) | MP4 Video + Post Text | **Yes** (`scheduled_publish_time`) | Included in Meta Developer tier | Meta Developer App with `pages_manage_posts` permission |
| **LinkedIn** | LinkedIn REST API (`/rest/posts`) | Text, Images, MP4 | **No native API scheduling** (Publish immediately or via cron worker) | Generous personal/company post limits | LinkedIn Developer Portal App with `w_member_social` permission |
| **X (Twitter)** | Twitter API v2 (`/2/tweets`, `media/upload`) | Text threads, MP4 (<512MB) | **No native API scheduling** (Publish immediately or via cron worker) | Free tier: 1,500 tweets/month (write-only) | X Developer Portal (Free Project App), OAuth 1.0a or 2.0 User Context |
| **TikTok** | TikTok Content Posting API | 9:16 MP4 Vertical Video | Direct Post or Inbox Share | Standard rate limits (Requires app audit for Direct Post) | TikTok for Developers Creator/Business Account |

---

## 3. Evaluated Architecture Options

### Option A: Direct REST API Micro-Publisher (Recommended Native Path)
- **Architecture:** Lightweight Python CLI module (`marketing/publisher/`) leveraging `google-api-python-client`, `requests`, and official OAuth tokens.
- **Cost:** **$0.00**
- **Pros:**
  - Zero third-party dependencies or external service vulnerabilities.
  - Direct control over scheduling, metadata, tags, and UTM tracking links.
  - Automatic error reporting and token refresh.
- **Cons:**
  - Requires initial one-time developer app setup across Google Cloud, Meta Developer, and X Developer portals.

### Option B: Buffer Free Tier Integration
- **Architecture:** Push approved posts to Buffer's REST API endpoint (`https://api.bufferapp.com/1/updates/create.json`).
- **Cost:** **$0.00** (Free plan covers up to 3 channels and 10 queued updates at any time).
- **Pros:**
  - Visual calendar web dashboard included out of the box.
  - Handles scheduling queue automatically without running background cron jobs.
- **Cons:**
  - Hard limit of 3 channels (e.g., YouTube, Instagram, LinkedIn only; excludes X and Facebook unless upgraded to $6/mo per channel).
  - API token access for new free accounts is restricted in recent Buffer developer terms.

### Option C: Open-Source Self-Hosted Engine (Mixpost / Postiz)
- **Architecture:** Run an open-source social media management engine inside a lightweight local Docker container.
- **Cost:** **$0.00** (Uses local machine resources).
- **Pros:**
  - Unlimited channels, unlimited scheduled posts, centralized calendar UI.
  - Fully transparent data storage (local PostgreSQL / SQLite).
- **Cons:**
  - Requires running Docker daemon locally during scheduled posting windows.

---

## 4. Post-Performance Analytics Architecture

A high-performing marketing engine requires measuring downstream product acquisition, not just vanity impressions.

```
┌──────────────────────────────────────────────────────────┐
│                   MARKETING ASSET                        │
│   (Shorts / Reels / Post with UTM Campaign Tracking)     │
└────────────────────────────┬─────────────────────────────┘
                             │
                             ▼
┌──────────────────────────────────────────────────────────┐
│                  GOOGLE PLAY STORE                       │
│        (Play Console Acquisition Funnel Analytics)       │
│  - Impressions / Store Listing Visitors                 │
│  - Install Conversions by utm_source & utm_campaign      │
└────────────────────────────┬─────────────────────────────┘
                             │
                             ▼
┌──────────────────────────────────────────────────────────┐
│              EXAMPREPOS ANDROID CLIENT                   │
│   - Play InstallReferrerClient logs campaign locally     │
│   - First focus session completion attribution           │
│   - Day-1 / Day-7 cohort retention mapping               │
└──────────────────────────────────────────────────────────┘
```

### Metrics Tracked:
1. **Top-of-Funnel (Organic Engagement):**
   - Video Retention Curve (% watched at 3s, 10s, 100%)
   - Click-Through Rate (CTR) on bio link or description link
2. **Middle-of-Funnel (Google Play Store):**
   - Store Listing Conversion Rate (benchmark: >28% for educational apps in India)
   - Installs grouped by `utm_source` (`youtube`, `instagram`, `linkedin`, `x_twitter`)
3. **Bottom-of-Funnel (Product Activation):**
   - First Study Session Completion Rate (>65% target)
   - Onboarding Goal Setup Rate (>80% target)

---

## 5. Next Milestone Proposal & Roadmap

**Milestone Name:** `Autonomous Publishing Integration & Content Scheduler`

1. **Step 1: Local Credential & Token Store**
   - Create secure `.env.marketing` template (git-ignored) with placeholders for:
     - `YOUTUBE_CLIENT_SECRET_FILE`
     - `META_ACCESS_TOKEN` / `INSTAGRAM_ACCOUNT_ID`
     - `X_API_KEY` / `X_API_SECRET` / `X_ACCESS_TOKEN`
     - `LINKEDIN_ACCESS_TOKEN`
2. **Step 2: Unified Publisher CLI Tool (`publish_content.py`)**
   - Mode `--dry-run`: Validates tokens, media format, text lengths, and links without calling network.
   - Mode `--schedule`: Queues post to YouTube / Instagram native schedulers.
   - Mode `--now`: Publishes approved asset upon explicit human confirmation.
3. **Step 3: UTM Campaign Ledger (`marketing/analytics/CAMPAIGN_LEDGER.json`)**
   - Automatically registers unique tracking parameters for every generated video and post.
