# ExamPrepOS — Autonomous Marketing Studio

Welcome to the **ExamPrepOS Autonomous Marketing Studio**. This sub-system is a reusable, automated content generation, video production, and social publishing engine designed to convert product features, screenshots, release notes, and educational workflows into high-converting, platform-specific marketing assets with minimal manual work.

---

## 📁 Directory Architecture

```text
marketing/
├── README.md                      # This documentation
├── brand/                         # Brand identity, color tokens, typography & guidelines
│   └── BRAND_GUIDE.md             # Visual identity, typography, and tone of voice
├── scripts/                       # High-converting video scripts (Problem-Agitate-Solve)
│   ├── SCRIPT_01_DECISION_FATIGUE.md
│   ├── SCRIPT_02_OLED_FOCUS_TIMER.md
│   ├── SCRIPT_03_MOCK_TEST_NEGATIVE_MARKING.md
│   ├── SCRIPT_04_MISTAKE_VAULT.md
│   └── SCRIPT_05_SPACED_REPETITION.md
├── storyboards/                   # Scene-by-scene timing, animations, & visual directions
├── templates/                     # Reusable Python production scripts & engines
│   ├── voice_engine.py            # Neural TTS synthesis & FFmpeg audio mastering
│   ├── video_studio_engine.py     # Frame compositing, HUD overlays, typography & render engine
│   ├── build_initial_assets.py    # Master end-to-end video pipeline runner
│   └── generate_voice_samples.py  # Voice benchmarking & sample generation suite
├── assets/                        # Raw & source creative assets
│   ├── screenshots/               # Actual 1080x2400 device screenshots from ExamPrepOS app
│   ├── logos/                     # High-res vectors & 512x512 app icons
│   ├── audio/                     # Ambient background music & sound design assets
│   └── images/                    # UI graphics and brand illustrations
├── videos/                        # Video output staging & release archives
│   ├── drafts/                    # Work-in-progress renders & voice tests
│   ├── reviewed/                  # Staged keyframe batches for manual/automated QA
│   └── final/                     # Approved, mastered 1080x1920 MP4 deliverables & thumbnails
├── posts/                         # Platform-tailored social copy, hooks & hashtag packages
│   ├── youtube/                   # YouTube Shorts titles, descriptions, and tags
│   ├── instagram/                 # Instagram Reels copy, carousels, and audio tags
│   ├── tiktok/                    # TikTok hooks, SEO keywords, and engagement questions
│   ├── x/                         # X (Twitter) threads, punchy hooks, and launch teasers
│   ├── linkedin/                  # In-depth engineering & productivity breakdowns
│   ├── facebook/                  # Community-oriented group posts and study tips
│   └── PUBLISHING_WORKFLOW.md     # 4-stage publishing workflow SOP
├── calendar/                      # Content planning and queue tracking
│   ├── 30_DAY_CONTENT_CALENDAR.md # Strategic 30-day pre-launch & launch calendar
│   └── PUBLISHING_QUEUE.md        # Asset readiness & publication queue state
├── analytics/                     # Post-launch tracking templates & metrics
│   └── ANALYTICS_TRACKER.md       # Multi-platform attribution & conversion metrics
└── reports/                       # Quality assurance, audio benchmarks, & milestone audits
    ├── QUALITY_CHECKLIST.md       # Pre-publication checklist
    ├── VOICEOVER_QUALITY_REPORT.md# Neural TTS voice benchmarking audit
    ├── PRE_PUBLICATION_APPROVAL_REPORT.md # Pre-pub review & verification
    └── MILESTONE_1_EXECUTION_REPORT.md    # Production milestone report
```

---

## 🛠️ Production Pipelines & Tools

### 1. Voice Synthesis Engine (`templates/voice_engine.py`)
Synthesizes ultra-natural, conversational voiceovers:
- **Primary Provider**: Microsoft Edge Neural TTS (`edge-tts`), specifically configured with natural conversational tones (e.g. `en-US-ChristopherNeural` / `en-US-JennyNeural`).
- **Fallback Support**: Local macOS Speech Synthesis (`say` / Samantha) with automated format conversion.
- **Audio Mastering**: Automated FFmpeg processing pipeline with high-pass filter (80Hz), low-pass filter (12kHz), dynamic range compression, and EBU R128 (`loudnorm`) broadcast normalization.

```bash
# Test & benchmark voice generation:
python3 marketing/templates/generate_voice_samples.py
```

### 2. Video Studio Engine (`templates/video_studio_engine.py`)
Builds 1080x1920 (9:16 vertical) high-energy short-form videos:
- Dark OLED aesthetic (#0A0E17 background, Cyan #00D4B2 and Indigo #4F46E5 accents).
- Dynamic glowing badge headers, kinetic typography, and device frame mockups.
- Scene transition timing dynamically matched to voiceover duration.
- Subtitle generation and automated thumbnail capture at the primary climax frame.

### 3. End-to-End Master Pipeline (`templates/build_initial_assets.py`)
Executes the complete asset generation, voice synthesis, video compilation, thumbnail generation, and metadata manifest creation.

```bash
# Execute master video build:
python3 marketing/templates/build_initial_assets.py
```

Outputs will be saved directly into `marketing/videos/final/`:
- `VID_01_DECISION_FATIGUE.mp4` (+ `_thumb.jpg` & `_meta.json`)
- `VID_02_OLED_FOCUS_TIMER.mp4` (+ `_thumb.jpg` & `_meta.json`)

---

## 📱 Publishing Workflow

1. **Review Final Videos**: Inspect MP4 files in `marketing/videos/final/` to verify audio sync, visual clarity, and text accuracy.
2. **Select Platform Package**: Open relevant file in `marketing/posts/<platform>/` for copy, hashtags, and call-to-actions.
3. **Check Publishing Queue**: Update status in `marketing/calendar/PUBLISHING_QUEUE.md` from `READY_TO_PUBLISH` → `SCHEDULED` → `PUBLISHED`.
4. **Log Analytics**: Record 24h, 7d, and 30d impressions, watch time, and click-through metrics in `marketing/analytics/ANALYTICS_TRACKER.md`.

---

## 🔒 Content Integrity & Brand Rules

- **Zero Fake Claims**: Never advertise features that do not exist in the Android codebase.
- **Visual Accuracy**: Use actual screenshots captured from the app (`marketing/assets/screenshots/`).
- **Copyright Compliance**: Background music must be royalty-free or synthesized. All question content shown in previews must be original ExamPrepOS curriculum questions.
