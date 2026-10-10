# ExamPrepOS — Marketing Asset Quality Control Checklist

**Document Version:** 1.0.0  
**Date:** October 2026  
**Auditor / Role:** Media QA Engineer & Brand Safety Lead  
**Enforcement Standard:** Zero Defects Prior to Founder Publication Approval  

---

## 1. Technical Video & Audio Verification Gate

| Quality Parameter | Standard Requirement | Verification Method | Status (Batch 01) |
|---|---|---|---|
| **Resolution** | 1080 x 1920 pixels | `ffprobe -show_entries stream=width,height` | **PASS (1080x1920)** |
| **Aspect Ratio** | 9:16 Vertical | Calculated width / height | **PASS (9:16)** |
| **Frame Rate** | 30.0 fps progressive | `ffprobe -show_entries stream=r_frame_rate` | **PASS (30/1 fps)** |
| **Video Codec** | H.264 (yuv420p, High Profile) | `ffprobe -show_entries stream=codec_name` | **PASS (h264)** |
| **Audio Normalization** | -16 LUFS (±1.0 LUFS) | FFmpeg `loudnorm` filter | **PASS (-16.0 LUFS)** |
| **Audio Sync** | Audio and video streams equal duration | `ffprobe duration` comparison | **PASS (Exact match to 0.01s)** |
| **No Black Frames** | Zero unintended black frames | Automated frame inspection | **PASS (0 black frames)** |
| **File Compression** | Under 15MB for fast mobile loading | File system size check | **PASS (3.3MB & 6.0MB)** |

---

## 2. Content & Brand Safety Gate

| Quality Parameter | Verification Requirement | Status (Batch 01) |
|---|---|---|
| **Product Name** | Spelled strictly as `ExamPrepOS` (No ExamPrep Os or examprepOS) | **PASS** |
| **Accurate Claims** | All features shown (Pomodoro, auto-rebalance, negative marking) exist in active code | **PASS** |
| **Safe Margins** | Text and UI elements clear of top 250px and bottom 250px UI overlays | **PASS** |
| **Copyright Clearance** | Royalty-free synthesized audio bed; native speech; zero copyrighted music | **PASS** |
| **Educational Integrity** | Official NTA/NEET/UPSC marking schemes verified (+4/-1, +2/-0.66) | **PASS** |
| **Privacy Protection** | Demo user credentials only (`usr_seed_01`); zero private user data exposed | **PASS** |
| **Legal Disclaimers** | Prominent non-affiliation disclaimer present on end cards and descriptions | **PASS** |

---

## 3. QA Sign-Off Record

- **Asset 1:** `VID_01_DECISION_FATIGUE.mp4` ➔ **PASSED QA (Approved for Release)**
- **Asset 2:** `VID_02_OLED_FOCUS_TIMER.mp4` ➔ **PASSED QA (Approved for Release)**
