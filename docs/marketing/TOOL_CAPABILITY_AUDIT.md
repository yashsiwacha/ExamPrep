# ExamPrepOS — Marketing Tool & Production Capability Audit

**Document Version:** 1.0.0  
**Audit Date:** October 10, 2026  
**Auditor / Role:** Autonomous Marketing Operations Engineer & Media Architect  
**Host Environment:** Apple Silicon macOS Darwin 24.3.0, Homebrew, Android SDK API 35  

---

## 1. Executive Summary

This audit evaluates the technical tools, runtimes, command-line utilities, media processors, and generative engines available in the local environment for building the **ExamPrepOS Autonomous Marketing Studio**.

All evaluations in this audit are based on **direct execution verification**, not assumptions or marketing claims. The studio is architected around a **$0.00 operating cost principle**, maximizing local deterministic media processing, native device emulation, and offline synthesis.

---

## 2. Verified Capabilities Inventory

| Tool / Runtime | Version / Path | Verified Functionality | Studio Role |
|---|---|---|---|
| **FFmpeg** | `8.1.2` (`/opt/homebrew/bin/ffmpeg`) | Hardware-accelerated Apple VideoToolbox (`h264_videotoolbox`), AudioToolbox, `libx264`, `libmp3lame`, `libopus`, complex filtergraphs (`filter_complex`), subtitle burning (`drawtext`, `subtitles`), audio normalization (`loudnorm`). | Core Video Assembly & Encoding Engine |
| **FFprobe** | `8.1.2` (`/opt/homebrew/bin/ffprobe`) | Stream inspection, duration extraction, resolution validation, aspect ratio verification, bit rate check. | Video QA & Validation Gate |
| **Python** | `3.13.2` (`/Library/Frameworks/Python.framework/...`) | Rich ecosystem: Pillow 12.0, OpenCV 4.11, CairoSVG 2.8, Soundfile, NumPy, PyYAML. | Video Assembly Automation & Graphic Compositing |
| **Node.js & npm** | `v26.5.0` (`/opt/homebrew/bin/node`) | Modern JS/TS execution, CLI scripts, potential Remotion or web rendering. | Secondary Web / Animation Tooling |
| **macOS Speech Synthesis (`say`)** | Native `/usr/bin/say` | High-quality offline Indian English neural/formant voices: `Aman (en_IN)`, `Rishi (en_IN)`, `Tara (en_IN)`. Zero latency, zero API cost. | Voiceover & Narration Audio Generation |
| **Android Debug Bridge (`adb`)** | `API 35` (`~/Library/Android/sdk/platform-tools/adb`) | `/system/bin/screenrecord` on `emulator-5554`. Captures 1080x2400 60fps native application sessions directly from Room SQLite. | Primary Authentic Product Footage Source |
| **Antigravity Image Engine** | Native tool `generate_image` | AI image generation for high-impact social banners, conceptual thumbnails, and custom illustrative assets. | Promotional Graphic & Thumbnail Generation |
| **Antigravity Browser Subagent** | Native tool `browser_subagent` | Autonomous browser testing, DOM inspection, store preview verification, and WebP session capture. | Web Preview & Verification |

---

## 3. Verified Limitations & Mitigation Strategies

1. **Third-Party Video Generation (Sora / Runway / Kling / Google Flow Direct API):**
   - *Status:* No direct external video generation REST API token is mounted in the local environment.
   - *Mitigation:* We do not rely on paid external generative video APIs. Real product value in edtech is proven through **authentic screen recordings of the real app**, framed in professional 3D-style phone mockups, backed by crisp Indian English voiceovers, animated kinetic typography, and smooth pan/zoom transitions assembled locally via FFmpeg and Python.
2. **Direct Social Media Auto-Publishing (Instagram / YouTube / X APIs):**
   - *Status:* Official publishing APIs (Meta Graph API, YouTube Data API v3, X API v2) require developer app registration, OAuth2 user authorization, and token secrets that must not be hardcoded.
   - *Mitigation:* The studio implements a **Structured Publishing Queue** (`IDEA ➔ SCRIPTED ➔ ASSET_PENDING ➔ EDITING ➔ QA_PENDING ➔ APPROVED ➔ SCHEDULED ➔ PUBLISHED`). For initial launch, the studio generates the complete publication package (rendered MP4, platform-specific caption, hashtags, thumbnail, and step-by-step instructions) ready for 1-click human approval and publishing.
3. **Audio Normalization:**
   - *Status:* Raw synthesized voice tracks can vary in gain.
   - *Mitigation:* The FFmpeg pipeline applies `loudnorm` filter (targeting -16 LUFS for YouTube Shorts and Instagram Reels) to ensure consistent, studio-quality sound.

---

## 4. Cost Breakdown: Existing vs Additional

| Layer | Recommended Tool | Cost per Asset | Monthly Infrastructure Cost |
|---|---|---|---|
| **Footage Capture** | `adb screenrecord` on Android Emulator | **₹0.00** | **₹0.00** |
| **Voiceover Synthesis** | Native macOS `say -v Rishi (en_IN)` | **₹0.00** | **₹0.00** |
| **Video Assembly** | FFmpeg 8.1.2 + Python 3.13 pipeline | **₹0.00** | **₹0.00** |
| **Graphic Compositing** | Pillow + CairoSVG + Antigravity AI | **₹0.00** | **₹0.00** |
| **Publishing Queue** | Markdown / JSON Git-backed pipeline | **₹0.00** | **₹0.00** |
| **Total Production Cost** | **100% Local & Free** | **₹0.00** | **₹0.00** |

---

## 5. Recommended Initial Architecture

```
┌────────────────────────────────────────────────────────┐
│ 1. Content Definition & Scripting                      │
│ - Markdown script with timestamps, hook, voiceover, CTA │
└──────────────────────────┬─────────────────────────────┘
                           ▼
┌────────────────────────────────────────────────────────┐
│ 2. Dual-Track Asset Generation                         │
│ ├─ A. Authentic Product Video: adb screenrecord (app)  │
│ └─ B. Voiceover: macOS say (Rishi en_IN) ➔ aac audio   │
└──────────────────────────┬─────────────────────────────┘
                           ▼
┌────────────────────────────────────────────────────────┐
│ 3. Automated Local Assembly Pipeline (Python + FFmpeg)  │
│ - Resizes/crops to 1080x1920 (9:16 vertical short-form)│
│ - Composites phone frame & dark OLED aesthetic         │
│ - Overlays dynamic top hook banner & kinetic captions  │
│ - Mixes voiceover with background lo-fi audio track    │
│ - Adds branded end-card & Google Play CTA badge        │
│ - Exports web-optimized MP4 + JPG thumbnail            │
└──────────────────────────┬─────────────────────────────┘
                           ▼
┌────────────────────────────────────────────────────────┐
│ 4. Platform Tailoring & Staging                        │
│ - YouTube Shorts / Instagram Reels / LinkedIn / X      │
│ - Quality Gate Review (Resolution, audio, safe margins)│
└────────────────────────────────────────────────────────┘
```
