#!/usr/bin/env python3
"""
Executes Video Generation for Milestone 1:
Produces VID_01_DECISION_FATIGUE and VID_02_OLED_FOCUS_TIMER
Saving final MP4s, thumbnails, and JSON metadata into marketing/videos/final/
"""

import sys
from pathlib import Path

# Add marketing/templates to path
sys.path.insert(0, str(Path(__file__).parent))
from video_studio_engine import build_video_asset

WORKSPACE_ROOT = Path(__file__).resolve().parents[2]
ASSETS_DIR = WORKSPACE_ROOT / "marketing" / "assets"
FINAL_DIR = WORKSPACE_ROOT / "marketing" / "videos" / "final"
LOGOS_PATH = ASSETS_DIR / "logos" / "logo_512.png"

def run_production():
    print("🚀 Initiating ExamPrepOS Autonomous Video Production Pipeline...")

    # --- VIDEO 1 ---
    vid1_scenes = [
        {
            "pill": "THE 7:00 AM PROBLEM",
            "pill_color": (239, 68, 68),
            "headline": "Stop Morning Decision Fatigue",
            "caption": "Dozens of chapters. Zero clear next action.",
            "screen_key": "dash1"
        },
        {
            "pill": "DECISION PARALYSIS",
            "pill_color": (245, 158, 11),
            "headline": "Physics, Chemistry, Math Chaos",
            "caption": "Syllabus confusion kills focus before you start.",
            "screen_key": "dash1"
        },
        {
            "pill": "EXAM PLANNING OS",
            "pill_color": (99, 102, 241),
            "headline": "Exact Daily Study Tasks",
            "caption": "Calibrated to your exam date and target score.",
            "screen_key": "dash1"
        },
        {
            "pill": "ONE-TAP FOCUS",
            "pill_color": (16, 185, 129),
            "headline": "Start Your Focus Session",
            "caption": "100% Offline. Zero ads during active study.",
            "screen_key": "dash1"
        }
    ]

    vid1_voice = (
        "Ever wake up at 7 AM... stare at your books... and waste forty-five minutes deciding what to study? "
        "When you're juggling dozens of chapters across Physics, Chemistry, and Math, morning decision fatigue kills your focus before you even start. "
        "ExamPrepOS fixes this completely. "
        "Wake up to your exact daily study tasks, calibrated to your exam date and weak topics. "
        "One tap to start your focus session. Download ExamPrepOS free on Google Play."
    )

    screens_vid1 = {
        "dash": str(ASSETS_DIR / "screenshots" / "screen_dashboard2.png"),
        "focus": str(ASSETS_DIR / "screenshots" / "screen_focus_session.png")
    }

    # Update scene 4 in VID_01 to show the focus screen
    vid1_scenes[3]["screen_key"] = "focus"
    vid1_scenes[0]["screen_key"] = "dash"
    vid1_scenes[1]["screen_key"] = "dash"
    vid1_scenes[2]["screen_key"] = "dash"

    meta1 = build_video_asset(
        video_id="VID_01_DECISION_FATIGUE",
        script_scenes=vid1_scenes,
        voiceover_text=vid1_voice,
        screen_assets=screens_vid1,
        output_dir=FINAL_DIR,
        voice="en-IN-PrabhatNeural",
        logo_path=str(LOGOS_PATH)
    )

    # --- VIDEO 2 ---
    vid2_scenes = [
        {
            "pill": "NO POPUP ADS",
            "pill_color": (239, 68, 68),
            "headline": "Tired of Ad Interruptions?",
            "caption": "Most timers show popup ads right in the middle of study.",
            "screen_key": "timer_ready"
        },
        {
            "pill": "TRUE OLED DARK MODE",
            "pill_color": (6, 182, 212),
            "headline": "Built for Marathon Study",
            "caption": "Pitch-black battery saver. Clean distraction-free interface.",
            "screen_key": "timer_ready"
        },
        {
            "pill": "STUDY PRESETS",
            "pill_color": (99, 102, 241),
            "headline": "Pomodoro • Deep Work • Flow",
            "caption": "Choose 25m Pomodoro, 50m Deep Work, or 90m Flow State.",
            "screen_key": "timer_ready"
        },
        {
            "pill": "AUTO-LOGGED SESSIONS",
            "pill_color": (16, 185, 129),
            "headline": "Track Your Real Study Streak",
            "caption": "Sessions auto-log to your study history. Zero ads.",
            "screen_key": "timer_active"
        }
    ]

    vid2_voice = (
        "Nothing ruins deep focus like a loud popup ad right in the middle of your study session. "
        "ExamPrepOS is built with one sacred rule: zero ad interruptions. "
        "You get a true pitch-black OLED dark timer that saves battery and protects your eyes during marathon night study. "
        "Choose twenty-five minute Pomodoro, fifty minute Deep Work, or ninety minute Flow State. "
        "Every session auto-logs to your study history. Download ExamPrepOS free on Google Play."
    )

    screens_vid2 = {
        "timer_ready": str(ASSETS_DIR / "screenshots" / "screen_focus_session.png"),
        "timer_active": str(ASSETS_DIR / "screenshots" / "screen_focus_active.png")
    }

    meta2 = build_video_asset(
        video_id="VID_02_OLED_FOCUS_TIMER",
        script_scenes=vid2_scenes,
        voiceover_text=vid2_voice,
        screen_assets=screens_vid2,
        output_dir=FINAL_DIR,
        voice="en-IN-PrabhatNeural",
        logo_path=str(LOGOS_PATH)
    )

    print("\n🎉 PRODUCTION BATCH COMPLETED!")
    print(f"1. {meta1['output_file']} ({meta1['duration_seconds']}s, {meta1['resolution']})")
    print(f"2. {meta2['output_file']} ({meta2['duration_seconds']}s, {meta2['resolution']})")

if __name__ == "__main__":
    run_production()
