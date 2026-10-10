# ExamPrepOS — Official Brand & Content Production Guide

**Document Version:** 1.0.0  
**Date:** October 2026  
**Auditor / Role:** Brand Architect & Creative Director  
**Application Identity:** ExamPrepOS (Android)  

---

## 1. Brand Essence & Positioning

### The Core Promise:
> **"Know where you stand. Know what to study next. Focus, practice, measure progress, and stay on track until exam day."**

ExamPrepOS is not a video lecturing app or an entertainment feed. It is a **distraction-free study operating system** that organizes a student's preparation life through deterministic planning, offline syllabus mastery, Pomodoro deep-work sessions, authentic national mock tests, and scientific spaced repetition.

---

## 2. Target Audience & Student Personas

- **Primary Persona (Engineering):** Class 11, Class 12, and Dropper students preparing for **JEE Main & Advanced 2026** (Ages 16–20).
- **Secondary Persona (Medical):** Class 11, Class 12, and Repeaters preparing for **NEET UG 2026** (Ages 16–22).
- **Tertiary Personas:** UPSC CSE Prelims, CAT, and GATE aspirants seeking disciplined daily execution.
- **Psychographic Reality:** Anxious, overloaded with materials, experiencing decision fatigue every morning, constantly fearing syllabus backlog and formula forgetting.

---

## 3. Brand Voice & Editorial Tone

| Tone Pillar | What We Sound Like | What We NEVER Sound Like |
|---|---|---|
| **Calm & Grounded** | *"2 high-priority tasks scheduled for Physics today. Take it step-by-step."* | *"HURRY! Time is running out! You are falling behind!"* |
| **Objective & Honest** | *"Your rotational motion accuracy is 42%. 4 mistakes bookmarked for review."* | *"Guaranteed AIR 1! Boost your score 500% in 3 days!"* |
| **Anti-Guilt & Reassuring**| *"Missed 2 days? Life happens. We've rebalanced your timeline across remaining days."* | *"Streak broken! You failed your goal today."* |
| **Respectful & Focused** | *"Zero ads during your focus sessions. 100% offline-ready."* | *"Watch this sponsor ad to unlock your timer."* |

---

## 4. Visual Identity & Color Tokens

*Extracted directly from `:core` design tokens (`com.examprep.core.designsystem.theme.ExamPrepColors`):*

```
┌────────────────────────────────────────────────────────┐
│ Brand Palette Tokens                                   │
├────────────────────────────────────────────────────────┤
│ Background (OLED Dark) : #0B0F19 (Cosmic Charcoal)     │
│ Surface / Card         : #1E293B (Slate Slate-800)     │
│ Surface Light / Border : #334155 (Muted Border)        │
│ Primary Brand Accent   : #6366F1 (Electric Indigo)     │
│ Secondary / Focus Ring : #06B6D4 (Vibrant Cyan)        │
│ Text Primary           : #F8FAFC (High Contrast Crisp) │
│ Text Muted             : #94A3B8 (Calm Subtitle Slate) │
│ Success / Mastered     : #10B981 (Emerald Green)       │
│ Warning / Revision Due : #F59E0B (Amber Gold)          │
│ Error / Mistake Vault  : #EF4444 (Crimson Alert)       │
└────────────────────────────────────────────────────────┘
```

---

## 5. Typography Standards

- **Primary Font Family:** Inter / Roboto / Plus Jakarta Sans.
- **Short-Form Video Headings:** Bold, uppercase, heavy weight (`FontWeight.Bold` / `800`), font size 48–56pt.
- **Captions & Subtitles:** Medium / Semi-Bold (`FontWeight.SemiBold`), font size 36–42pt with black drop-shadow or frosted dark pill background (`rgba(11,15,25,0.85)`).
- **Tabular Data:** Use monospace / tabular figures for scores, timer countdowns, and marks (`+4 / -1`).

---

## 6. Logo & Icon Usage Rules

- **Source Asset:** [marketing/assets/logos/logo_512.png](file:///Users/yash/Yash-Workspace/projects/active/android/marketing/assets/logos/logo_512.png).
- **Safe Clearance:** Maintain at least 25% clear padding around the emblem in all marketing compositions.
- **Background Contrast:** Always display over dark backgrounds (`#0B0F19` or `#1E293B`). Never place raw icon artwork over busy, un-darkened photo footage.

---

## 7. Short-Form Video Safe Margins (9:16 Vertical Video)

```
0px ──────────────────────────────────────────────── 1080px
│                                                  │
│   [SAFE TOP ZONE: Top Hook Banner / Title]      │  0 – 250px
│                                                  │
│                                                  │
│                                                  │
│   [CORE FOCAL ZONE: Product Screen Recording]    │ 300 – 1450px
│   (Centered Phone Mockup / Live Feature Flow)   │
│                                                  │
│                                                  │
│   [DYNAMIC CAPTION ZONE: Kinetic 1-2 Line Text]  │ 1450 – 1650px
│                                                  │
│   [SAFE BOTTOM ZONE: Clear for UI / CTA Button]  │ 1650 – 1920px
│   (Avoid TikTok/Reels captions & share icons)    │
└──────────────────────────────────────────────────┘
```

---

## 8. Approved Calls-to-Action (CTAs)

- **Primary Conversion CTA:** *"Download ExamPrepOS Free on Google Play — Link in Bio"*
- **Utility / Problem CTA:** *"Track your full syllabus offline. Search 'ExamPrepOS' on Google Play."*
- **Revision CTA:** *"Never lose a formula to forgetting. Start your free spaced repetition plan today."*

---

## 9. Content Accuracy & Integrity Iron Law

1. **Zero Fabricated Testimonials:** Never create fictitious student personas or fake quotes claiming *"I scored AIR 42 using ExamPrepOS"*.
2. **Authentic Product Capabilities Only:** Every feature shown in videos (Mock timer, negative marking, Mistake Vault, Spaced Repetition) must be real code running in ExamPrepOS.
3. **Strict Official Disclaimers:** All promotional media referencing national exams must carry or link to the official disclaimer:
   > *"ExamPrepOS is an independent educational tool and is not affiliated with or endorsed by NTA, UPSC, IITs, or IIMs."*
