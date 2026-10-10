#!/usr/bin/env python3
"""
ExamPrepOS Marketing Studio — Modular Voiceover Engine
Provides a pluggable, robust voice generation interface with:
- Primary: Edge Neural Voice Provider (Free Microsoft Neural Cognitive TTS with Indian English voices)
- Fallback: macOS Native Speech Synthesis (say -v Rishi)
- Phonetic text pre-processor for natural acronym pronunciation & breath pauses
- Studio audio mastering (High-pass 80Hz rumble filter, 3.5kHz presence EQ, -16 LUFS Loudnorm)
"""

import os
import sys
import re
import asyncio
import subprocess
from abc import ABC, abstractmethod
from pathlib import Path

# Common Indian English Neural Voices
DEFAULT_NEURAL_VOICE_MALE = "en-IN-PrabhatNeural"
DEFAULT_NEURAL_VOICE_FEMALE = "en-IN-NeerjaExpressiveNeural"
DEFAULT_FALLBACK_VOICE = "Rishi"

# Phonetic & Acronym Replacement Dictionary for Natural Speech
PRONUNCIATION_MAP = {
    r"\bExamPrepOS\b": "ExamPrep O-S",
    r"\bexamprepos\b": "ExamPrep O-S",
    r"\bJEE\b": "J-E-E",
    r"\bNEET\b": "Neet",
    r"\bUPSC\b": "U-P-S-C",
    r"\bOLED\b": "O-L-E-D",
    r"\bSQLite\b": "S-Q-L-lite",
    r"\bUI\b": "U-I",
    r"\bUX\b": "U-X",
    r"\bAPK\b": "A-P-K",
    r"\b25m\b": "25-minute",
    r"\b50m\b": "50-minute",
    r"\b90m\b": "90-minute",
}

def preprocess_narration_text(text: str) -> str:
    """
    Applies phonetic corrections, acronym expansions, and human pacing marks.
    """
    processed = text
    for pattern, replacement in PRONUNCIATION_MAP.items():
        processed = re.sub(pattern, replacement, processed)
    
    # Ensure dashes and ellipses create natural pauses
    processed = processed.replace("—", "... ")
    processed = processed.replace(" - ", "... ")
    return processed

def run_cmd(cmd: str, check: bool = True):
    res = subprocess.run(cmd, shell=True, capture_output=True, text=True)
    if check and res.returncode != 0:
        raise RuntimeError(f"Command failed ({res.returncode}): {cmd}\nStderr: {res.stderr}")
    return res

def get_audio_duration(audio_file: Path) -> float:
    res = run_cmd(f"ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 '{audio_file}'")
    return float(res.stdout.strip())

class VoiceProvider(ABC):
    """Abstract Base Class for Voiceover Providers"""

    @abstractmethod
    def synthesize(self, text: str, output_wav: Path, voice_id: str = None, rate: str = "+0%", pitch: str = "+0Hz") -> float:
        """
        Synthesizes speech and saves broadcast-quality 48kHz WAV to output_wav.
        Returns duration in seconds.
        """
        pass

class EdgeNeuralVoiceProvider(VoiceProvider):
    """
    High-Fidelity Neural TTS using Microsoft Edge Neural Cognitive Services.
    Zero paid API keys. Natural conversational prosody & Indian English accents.
    """
    def __init__(self, default_voice: str = DEFAULT_NEURAL_VOICE_MALE):
        self.default_voice = default_voice
        try:
            import edge_tts
            self._edge_tts = edge_tts
        except ImportError:
            self._edge_tts = None

    def is_available(self) -> bool:
        return self._edge_tts is not None

    def synthesize(self, text: str, output_wav: Path, voice_id: str = None, rate: str = "+3%", pitch: str = "+0Hz") -> float:
        if not self.is_available():
            raise RuntimeError("edge-tts library is not installed in the environment.")

        voice = voice_id or self.default_voice
        clean_text = preprocess_narration_text(text)
        temp_mp3 = output_wav.with_suffix(".temp.mp3")

        async def _generate():
            communicate = self._edge_tts.Communicate(clean_text, voice, rate=rate, pitch=pitch)
            await communicate.save(str(temp_mp3))

        asyncio.run(_generate())

        # Master audio via FFmpeg: Highpass 80Hz + 3.5kHz vocal clarity boost + -16 LUFS Loudnorm
        ffmpeg_cmd = (
            f"ffmpeg -y -i '{temp_mp3}' "
            f"-af \"highpass=f=80,equalizer=f=3500:t=q:w=1.2:g=1.5,loudnorm=I=-16:TP=-1.5:LRA=11\" "
            f"-ar 48000 -ac 2 '{output_wav}' 2>/dev/null"
        )
        run_cmd(ffmpeg_cmd)

        if temp_mp3.exists():
            temp_mp3.unlink()

        return get_audio_duration(output_wav)

class MacOSSynthesizeVoiceProvider(VoiceProvider):
    """
    macOS Native Speech Synthesis fallback (say command).
    """
    def __init__(self, default_voice: str = DEFAULT_FALLBACK_VOICE):
        self.default_voice = default_voice

    def synthesize(self, text: str, output_wav: Path, voice_id: str = None, rate: str = "+0%", pitch: str = "+0Hz") -> float:
        voice = voice_id or self.default_voice
        clean_text = text.replace('"', '').replace("'", "")
        temp_aiff = output_wav.with_suffix(".temp.aiff")

        run_cmd(f"say -v '{voice}' -o '{temp_aiff}' \"{clean_text}\"")

        # Convert AIFF and apply mastering
        ffmpeg_cmd = (
            f"ffmpeg -y -i '{temp_aiff}' "
            f"-af \"highpass=f=80,loudnorm=I=-16:TP=-1.5:LRA=11\" "
            f"-ar 48000 -ac 2 '{output_wav}' 2>/dev/null"
        )
        run_cmd(ffmpeg_cmd)

        if temp_aiff.exists():
            temp_aiff.unlink()

        return get_audio_duration(output_wav)

class VoiceEngineFactory:
    """Factory to instantiate the optimal available voice provider."""

    @staticmethod
    def get_provider(preferred: str = "neural", voice: str = DEFAULT_NEURAL_VOICE_MALE) -> VoiceProvider:
        if preferred == "neural":
            neural = EdgeNeuralVoiceProvider(default_voice=voice)
            if neural.is_available():
                return neural
            print("⚠️ Neural voice provider unavailable. Falling back to macOS native speech.")
        return MacOSSynthesizeVoiceProvider()

if __name__ == "__main__":
    print("Testing VoiceEngineFactory...")
    engine = VoiceEngineFactory.get_provider("neural")
    test_wav = Path("test_voice_engine.wav")
    dur = engine.synthesize("Welcome to ExamPrepOS. Know where you stand. Know what to study next.", test_wav)
    print(f"Generated test voiceover: {dur:.2f}s -> {test_wav}")
    if test_wav.exists():
        test_wav.unlink()
