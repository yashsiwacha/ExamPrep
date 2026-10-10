#!/usr/bin/env python3
"""
Generates comparison samples for voiceover evaluation:
Sample A: Baseline macOS say (Rishi)
Sample B: Neural en-IN-PrabhatNeural (Male, Friendly/Positive)
Sample C: Neural en-IN-NeerjaExpressiveNeural (Female, Expressive)
Sample D: Neural en-IN-NeerjaNeural (Female, Friendly/Positive)
"""

import os
import sys
import subprocess
import asyncio
from pathlib import Path
import edge_tts

OUTPUT_DIR = Path("/Users/yash/Yash-Workspace/projects/active/android/marketing/videos/drafts/voice_tests")
OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

SAMPLE_TEXT_RAW = (
    "Ever sit down to study and spend ten minutes deciding what to study first? "
    "ExamPrepOS helps you organize your preparation, see your next task, and focus on what matters. "
    "Less time planning. More time learning."
)

# Text with phonetic optimization for natural acronym pronunciation and human breathing pauses
SAMPLE_TEXT_PHONETIC = (
    "Ever sit down to study... and spend ten minutes deciding what to study first? "
    "ExamPrep O S helps you organize your preparation, see your next task, and focus on what matters. "
    "Less time planning. More time learning."
)

def run_cmd(cmd):
    res = subprocess.run(cmd, shell=True, capture_output=True, text=True)
    if res.returncode != 0:
        print(f"Error running: {cmd}\nStderr: {res.stderr}")
    return res

def generate_sample_a():
    """Baseline: macOS native say (Rishi)"""
    out_wav = OUTPUT_DIR / "SAMPLE_A_BASELINE_RISHI.wav"
    temp_aiff = OUTPUT_DIR / "temp_rishi.aiff"
    run_cmd(f"say -v 'Rishi' -o '{temp_aiff}' \"{SAMPLE_TEXT_RAW}\"")
    run_cmd(f"ffmpeg -y -i '{temp_aiff}' -af 'loudnorm=I=-16:TP=-1.5:LRA=11' -ar 48000 -ac 2 '{out_wav}' 2>/dev/null")
    if temp_aiff.exists():
        temp_aiff.unlink()
    print(f"✅ Generated Sample A: {out_wav}")
    return out_wav

async def generate_neural_sample(voice, out_name, rate="+0%", pitch="+0Hz", text=SAMPLE_TEXT_PHONETIC):
    """Neural Voice using Microsoft Edge Neural TTS service"""
    temp_mp3 = OUTPUT_DIR / f"temp_{out_name}.mp3"
    out_wav = OUTPUT_DIR / f"{out_name}.wav"
    
    communicate = edge_tts.Communicate(text, voice, rate=rate, pitch=pitch)
    await communicate.save(str(temp_mp3))
    
    # Process audio: gentle highpass (80Hz rumble cut), vocal presence EQ, and -16 LUFS loudness normalization
    ffmpeg_cmd = (
        f"ffmpeg -y -i '{temp_mp3}' "
        f"-af 'highpass=f=80,equalizer=f=3500:t=q:w=1.2:g=1.5,loudnorm=I=-16:TP=-1.5:LRA=11' "
        f"-ar 48000 -ac 2 '{out_wav}' 2>/dev/null"
    )
    run_cmd(ffmpeg_cmd)
    if temp_mp3.exists():
        temp_mp3.unlink()
    print(f"✅ Generated Neural Sample: {out_wav} (Voice: {voice})")
    return out_wav

async def main():
    print("🎙️ Generating Voiceover Comparison Samples for ExamPrepOS...")
    generate_sample_a()
    await generate_neural_sample("en-IN-PrabhatNeural", "SAMPLE_B_NEURAL_PRABHAT", rate="+3%", pitch="+0Hz")
    await generate_neural_sample("en-IN-NeerjaExpressiveNeural", "SAMPLE_C_NEURAL_NEERJA_EXPRESSIVE", rate="+2%", pitch="+0Hz")
    await generate_neural_sample("en-IN-NeerjaNeural", "SAMPLE_D_NEURAL_NEERJA", rate="+2%", pitch="+0Hz")
    print("\n🎉 All samples generated successfully in:", OUTPUT_DIR)

if __name__ == "__main__":
    asyncio.run(main())
