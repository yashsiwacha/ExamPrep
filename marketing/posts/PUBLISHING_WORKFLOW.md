# ExamPrepOS — Multi-Platform Publishing Workflow & Governance

**Document Version:** 1.0.0  
**Date:** October 2026  
**Auditor / Role:** Social Media Operations Lead & Release Governance Engineer  
**Policy:** Human-in-the-Loop Approval Gate (Zero Unauthorized Automated Publishing)  

---

## 1. Publishing Lifecycle State Machine

Every marketing asset moves through 9 strictly governed states:

```
┌──────┐      ┌──────────┐      ┌───────────────┐      ┌─────────┐
│ IDEA │ ───> │ SCRIPTED │ ───> │ ASSET_PENDING │ ───> │ EDITING │
└──────┘      └──────────┘      └───────────────┘      └─────────┘
                                                            │
┌──────────────┐      ┌───────────┐      ┌────────────┐     ▼
│  PUBLISHED   │ <─── │ SCHEDULED │ <─── │  APPROVED  │ <─ ┌────────────┐
└──────┬───────┘      └───────────┘      └────────────┘    │ QA_PENDING │
       │                                  [FOUNDER GATE]   └────────────┘
       ▼
┌──────────────┐
│   ANALYZED   │
└──────────────┘
```

### State Definitions:
1. `IDEA`: Strategic topic identified in content calendar aligned with target audience pain points.
2. `SCRIPTED`: Complete voiceover script, timestamps, visual directions, and kinetic captions documented.
3. `ASSET_PENDING`: Authentic screen recordings or static assets captured from app.
4. `EDITING`: Python + FFmpeg automated assembly pipeline rendering frames and encoding MP4.
5. `QA_PENDING`: Quality checks verified (Resolution 1080x1920, audio normalization -16 LUFS, safe zones, accurate claims).
6. `APPROVED`: Explicit human authorization granted by founder to publish or schedule.
7. `SCHEDULED`: Asset queued in platform native scheduler (YouTube Studio / Meta Business Suite / Buffer).
8. `PUBLISHED`: Content is live on public platform feed.
9. `ANALYZED`: 7-day performance metrics (impressions, completion rate, clicks) logged to learning loop.

---

## 2. Platform-Specific Publishing Protocols

### YouTube Shorts
- Upload via [studio.youtube.com](https://studio.youtube.com).
- File: `marketing/videos/final/<VIDEO_ID>.mp4`.
- Title: Pull from `marketing/posts/youtube/YOUTUBE_POSTS_BATCH_01.md`.
- Visibility: Unlisted ➔ Verify on mobile phone ➔ Set to Public at scheduled time.
- Sound: Audio track already mixed with voiceover + ambient music; select "Original Audio".

### Instagram Reels
- Upload via Instagram App or Meta Business Suite.
- File: `marketing/videos/final/<VIDEO_ID>.mp4`.
- Cover: Select `marketing/videos/final/<VIDEO_ID>_thumb.jpg`.
- Caption: Copy tailored caption from `marketing/posts/instagram/INSTAGRAM_POSTS_BATCH_01.md`.
- Share to Feed: Enabled.

### LinkedIn
- Post via Founder Profile or Official Company Page.
- Attach high-resolution infographic or video MP4.
- Copy text from `marketing/posts/linkedin/LINKEDIN_POSTS_BATCH_01.md`.
- Place Google Play link in first pinned comment to maximize algorithmic reach.

### X (Twitter)
- Post thread using text from `marketing/posts/x/X_POSTS_BATCH_01.md`.
- Attach MP4 video to Tweet 1.

---

## 3. Credential & Security Iron Laws

1. **Zero Secret Hardcoding:** API keys, access tokens, and passwords must never be committed to git repositories.
2. **Environment Variables:** All programmatic social integrations must read from environment variables or encrypted secret stores (`1Password CLI` / `.env` excluded from VCS).
3. **No Paid Tools Without Approval:** Utilize free native scheduling tools (YouTube Studio Scheduler, Meta Business Suite) rather than purchasing third-party SaaS schedulers.
