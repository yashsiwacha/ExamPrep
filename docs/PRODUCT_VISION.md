# ExamPrep OS — Product Vision

**Version:** 1.0  
**Last Updated:** September 2025  
**Status:** Approved

---

## Executive Summary

ExamPrep OS is a commercial Android application designed to be the operating system for a student's exam preparation journey. It is not a content library, flashcard app, or static study guide. It is an active preparation manager — a continuous, adaptive system that models a student's current preparation state and acts as their strategic co-pilot until exam day.

---

## Problem Statement

### The core problem

Students preparing for competitive exams (JEE, NEET, UPSC, CAT, GATE, etc.) face a shared, fundamental challenge: **they don't know what to do next**.

The market is full of content:
- Thousands of YouTube channels
- Hundreds of coaching apps
- Unlimited mock test providers
- PDFs and notes from every forum

But content abundance creates **decision paralysis**. Students study the wrong topics, over-prepare their strengths, under-prepare their weaknesses, and run out of time before the exam with huge portions untouched.

### What's missing

No application currently:
1. **Maintains a model of the student's preparation** — continuously tracking mastery per topic, not just completion
2. **Generates a personalized, dynamic study plan** — based on exam date, available time, and individual performance gaps
3. **Continuously re-plans** — adjusting when students fall behind or accelerate
4. **Provides transparent progress intelligence** — answering "Am I on track?" with data, not platitudes
5. **Integrates study scheduling, active practice, and revision** into a unified experience

---

## Vision Statement

> **"ExamPrep OS should not simply tell students what to study. It should continuously maintain a model of their preparation and help them understand where they are, what comes next, and whether they are on track."**

---

## Target Users

### Primary Persona: The Independent Preparer

- **Profile:** Preparing for JEE/NEET/UPSC without coaching, or supplementing coaching
- **Age:** 17–28
- **Pain:** No structured guidance; doesn't know how to allocate time; can't assess readiness
- **Behavior:** Studies inconsistently; spends too much on YouTube; doesn't track progress
- **Need:** A system that thinks for them and shows them what to do each day

### Secondary Persona: The Coaching Supplement User

- **Profile:** Enrolled in coaching, uses app as a systematic companion
- **Age:** 16–22
- **Pain:** Coaching provides content but not adaptive scheduling or gap analysis
- **Need:** Mastery tracking on top of classroom content, plus revision scheduling

---

## Core Product Principles

1. **State Over Content**: The most valuable asset is the student's preparation state — not the content we show them.
2. **Deterministic Planning**: The study plan is generated from explicit rules, not opaque AI. Students should understand *why* they're studying what they're studying.
3. **Mastery Over Completion**: Topics are not "done" when studied once. Mastery requires demonstrated performance.
4. **Transparency**: Every recommendation must explain its reasoning. "You should revise Thermodynamics because your last 3 quiz attempts showed 40% accuracy."
5. **Daily Use**: The app must earn daily opens. If a student doesn't open it every day, it has failed.
6. **Content Rights Integrity**: Never use copyrighted exam questions without explicit licensing. All questions are original, licensed, or clearly attributed.

---

## Feature Pillars

### Pillar 1: The Preparation Model
- Syllabus completion tracking at topic level
- Per-topic mastery state (Not Started → In Progress → Learned → Practiced → Mastered)
- Performance analytics (accuracy, speed, trends)
- Weak area identification

### Pillar 2: The Study Plan
- Personalized plan generation based on exam date + available time
- Daily and weekly task scheduling
- Phase-based structure (Foundation → Core → Practice → Revision → Mock)
- Dynamic re-planning when student falls behind

### Pillar 3: Active Practice
- Quick quizzes (topic-level)
- Subject tests
- PYQ (Previous Year Questions) practice — original questions only
- Full mock tests (future)

### Pillar 4: Revision Intelligence
- Spaced repetition scheduling
- Revision due alerts
- Performance-based revision prioritization

### Pillar 5: Focus Session
- Pomodoro-style timed study sessions
- Study streak tracking
- Daily study goal and progress

---

## Business Model

### Freemium with Ad-Supported Free Tier

| Tier | Access | Monetization |
|------|--------|-------------|
| Free | Full features, with banner + interstitial ads | AdMob |
| Ad-Free Trial | 7-day trial, no ads | User acquisition |
| Ad-Free | ₹99/month or ₹799/year | Subscription |

**Rationale:** Ads are the simplest path to early monetization without gating core features. Ad-free subscription serves students who find ads disruptive during study sessions.

**Future (Phase 5):** Premium content packs (curated original question banks per exam), AI tutoring add-on.

---

## Content Strategy

### Legal / IP Position

All questions in the ExamPrep OS question bank must be:
1. **Original** (created by ExamPrep team) — preferred
2. **Officially released** (by the exam authority in public domain) — with attribution
3. **Explicitly licensed** — from a rights holder with a written agreement

**Questions that CANNOT be used:**
- Questions from coaching institutes (T.I.M.E., Allen, Resonance, Aakash, etc.)
- Questions from textbooks without publisher agreement
- Questions scraped from websites

---

## Success Metrics

| Metric | Target (Month 6) |
|--------|-----------------|
| Daily Active Users | 5,000 |
| D7 Retention | 40% |
| D30 Retention | 20% |
| Ad-Free Conversion | 5% |
| Daily Session Length | 35+ minutes |
| Plans Generated | 80% of new users |
| Ratings (Play Store) | ≥4.2 |

---

## Out of Scope (MVP)

- Online multiplayer/competitive features
- Live classes or video content integration
- Social features (peer comparison, leaderboards)
- AI-generated questions (Phase 5+)
- iOS app
- Web app
