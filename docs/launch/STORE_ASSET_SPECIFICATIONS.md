# ExamPrepOS — Google Play Store Visual Asset Specifications

**Document Version:** 1.0.0  
**Date:** October 2026  
**Auditor / Role:** Senior Visual Designer & Store Listing Art Director  
**Specification Standard:** Official Google Play Console Developer Guidelines (October 2026)  

---

## 1. App Icon Specification

| Property | Requirement | Verified Specification |
|---|---|---|
| **Format** | 32-bit PNG | PNG-24 with alpha disabled / flat background |
| **Dimensions** | 512 x 512 pixels | Exact 512 x 512 px |
| **Maximum File Size** | 1024 KB | ~120 KB |
| **Color Profile** | sRGB | Standard sRGB |
| **Shape** | Full square (No rounded corners in source) | Google Play automatically applies the rounded squircle mask (20% corner radius). Keep all key artwork within a **384 x 384 px safe center circle**. |

### Icon Visual Design Concept
- **Background:** Deep Cosmic Charcoal (`#0B0F19`) with a subtle radial gradient toward midnight blue (`#1E293B`).
- **Foreground Motif:** Minimalist, precision-crafted dual-layer emblem:
  1. An open academic book base rendered in sleek frosted electric indigo (`#6366F1`).
  2. A vibrant cyan focus pulse (`#06B6D4`) rising from the center, symbolizing upward momentum and peak cognitive clarity.
- **Tone:** Academic, serious, distraction-free, premium. Distinct from cartoonish gamified badges.

---

## 2. Feature Graphic Specification

| Property | Requirement | Verified Specification |
|---|---|---|
| **Format** | JPEG or 24-bit PNG (no alpha) | PNG-24 |
| **Dimensions** | 1024 x 500 pixels | Exact 1024 x 500 px |
| **Maximum File Size** | 15 MB | ~480 KB |
| **Safe Zone** | Center 800 x 380 px | Keep all text and core graphics within the center safe zone to avoid cutoff across tablet, web, and TV viewports. |

### Feature Graphic Composition
- **Left Side (Focal Area):**
  - High-resolution angled 3D floating render of the Google Pixel 6a displaying the ExamPrepOS Dashboard (`screen_dashboard.png`).
- **Right Side (Value Typography):**
  - **Main Tagline (64pt, Bold, White):** *"Master Your Exam Syllabus."*
  - **Sub-Tagline (32pt, Medium, Cyan `#38BDF8`):** *"Daily Smart Plans • Focus Timer • National Mocks"*
  - **Pill Badges (Bottom Right):** `100% Offline` • `JEE & NEET 2026` • `No Intrusive Ads`

---

## 3. Phone Screenshots Sequence (8 Verified Captures)

All screenshots must be minimum **1080 x 2400 pixels** (20:9 aspect ratio), captured directly from authentic running emulator builds to comply with Google Play's truth-in-advertising guidelines.

### Screenshot Order, Copy & Visual Content

| Slide # | Screenshot Title (Headline Banner) | Sub-Text | Screen Capture Source | Visual Callout |
|---|---|---|---|---|
| **1 (Hero)** | **"Know What to Study Every Single Day"** | Auto-scheduled daily tasks calibrated to your exam date. | `screen_dashboard.png` | Focus card with "Start Focus Session" highlighted. |
| **2** | **"Track Complete Syllabus Mastery"** | Real 6-tier progression: Not Started ➔ Mastered. | `SyllabusScreen` | Topic status chips (`MASTERED`, `PRACTICING`). |
| **3** | **"Distraction-Free OLED Focus Timer"** | 25m Pomodoro, 50m Deep Work & Flow presets. | `FocusSessionScreen` | Dark OLED timer countdown with elapsed circular ring. |
| **4** | **"National Pattern Timed Mock Tests"** | Exact JEE (75Q/300M) & NEET (180Q/720M) patterns. | `screen_mocktest_active.png` | Question palette, section tabs, and countdown timer. |
| **5** | **"Authentic Scoring & Negative Marking"** | Official +4 / -1 marking scheme & speed analysis. | `MockTestResultScreen` | Scorecard showing Marks, Accuracy %, and Percentile. |
| **6** | **"Scientific Spaced Repetition (SRS)"** | Never forget formulas with automated 1, 3, 7, 30-day reviews. | `RevisionScreen` | "Due Today for Review" card with 1-tap quick quiz. |
| **7** | **"Automatic Mistake Vault"** | Every wrong question saved with step-by-step solutions. | `MistakeVaultScreen` | Categorized error breakdown (Conceptual vs Calculation). |
| **8** | **"100% Offline-First & Private"** | Study anywhere without internet. Zero data tracking. | `SettingsScreen` | Offline badge, in-app data deletion, and clean preferences. |

---

## 4. Promo Video Storyboard (30-Second Short Form)

- **00:00 – 00:04 (The Hook):** Student waking up, staring at a mountain of chaotic books and sticky notes. Text: *"Feeling overwhelmed by your exam syllabus?"*
- **00:05 – 00:11 (The Relief):** Screen capture of ExamPrepOS opening. Dashboard displays: *"Today's Target: Physics — Rotational Dynamics (2.5 hrs)"*. Text: *"Zero decision fatigue. One tap to start."*
- **00:12 – 00:18 (The Focus):** Focus timer activating in pitch-black OLED dark mode. Circular progress smooth countdown. Text: *"Deep focus without social media distractions."*
- **00:19 – 00:25 (The Benchmark):** Timed mock test question solving, palette navigation, instant score calculation (+4/-1). Text: *"Authentic national exam patterns & Mistake Vault."*
- **00:26 – 00:30 (Call to Action):** ExamPrepOS logo resolves with tagline: *"Know where you stand. Know what to study next."* Button: *"Install Free on Google Play"*.
