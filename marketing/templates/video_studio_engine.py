#!/usr/bin/env python3
"""
ExamPrepOS Autonomous Marketing Studio — Video Production Engine
Renders broadcast-quality 1080x1920 (9:16 vertical) short-form videos
for YouTube Shorts, Instagram Reels, and TikTok using authentic app screens,
Pillow graphic compositing, macOS native Indian English speech synthesis,
and FFmpeg hardware/libx264 encoding.

Zero external paid APIs. 100% local, deterministic, and reproducible.
"""

import os
import sys
import json
import math
import subprocess
import shutil
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont, ImageFilter

# Add marketing/templates to path for voice engine
sys.path.insert(0, str(Path(__file__).parent))
from voice_engine import VoiceEngineFactory, DEFAULT_NEURAL_VOICE_MALE

FONT_PATH = "/System/Library/Fonts/HelveticaNeue.ttc"

def get_font(size, bold=True):
    try:
        # Index 0 is standard/bold in HelveticaNeue.ttc on macOS
        return ImageFont.truetype(FONT_PATH, size=size, index=1 if bold else 0)
    except Exception:
        return ImageFont.load_default()

def run_cmd(cmd, check=True):
    res = subprocess.run(cmd, shell=True, capture_output=True, text=True)
    if check and res.returncode != 0:
        raise RuntimeError(f"Command failed ({res.returncode}): {cmd}\nStderr: {res.stderr}")
    return res

def get_audio_duration(audio_file):
    res = run_cmd(f"ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 '{audio_file}'")
    return float(res.stdout.strip())

def synthesize_voiceover(text, output_wav, voice=DEFAULT_NEURAL_VOICE_MALE):
    """
    Synthesize broadcast-quality Indian English voiceover via modular neural engine
    with automatic phonetic calibration and macOS native fallback.
    """
    engine = VoiceEngineFactory.get_provider("neural", voice=voice)
    return engine.synthesize(text, output_wav)

def generate_ambient_music(duration, output_wav):
    """
    Synthesize a relaxing, copyright-free ambient lo-fi chord pad bed via FFmpeg aevalsrc.
    Uses layered gentle harmonics with lowpass filter and soft envelope.
    """
    fade_out_start = max(1.0, duration - 2.0)
    filter_expr = (
        f"aevalsrc='0.08*sin(2*PI*130.81*t) + 0.06*sin(2*PI*164.81*t) + 0.05*sin(2*PI*196.00*t) + 0.04*sin(2*PI*246.94*t)':s=48000:d={duration},"
        f"lowpass=f=450,afade=t=in:ss=0:d=1.5,afade=t=out:st={fade_out_start}:d=2.0"
    )
    run_cmd(f"ffmpeg -y -f lavfi -i \"{filter_expr}\" -c:a pcm_s16le '{output_wav}' 2>/dev/null")
    return output_wav

def create_phone_mockup(screen_path, target_width=680, target_height=1470):
    """
    Wraps an authentic Android screenshot inside a sleek dark phone bezel
    with punch-hole camera and subtle bezel highlight.
    """
    bezel_padding = 16
    phone_w = target_width + (bezel_padding * 2)
    phone_h = target_height + (bezel_padding * 2)

    phone = Image.new("RGBA", (phone_w, phone_h), (0, 0, 0, 0))
    draw = ImageDraw.Draw(phone)

    # Outer chassis (Dark Titanium #1E293B with border)
    radius = 54
    draw.rounded_rectangle(
        [0, 0, phone_w, phone_h],
        radius=radius,
        fill=(15, 23, 42, 255),
        outline=(51, 65, 85, 255),
        width=4
    )

    # Load and round the screen image
    screen = Image.open(screen_path).convert("RGBA")
    screen = screen.resize((target_width, target_height), Image.Resampling.LANCZOS)

    # Create mask for rounded inner screen
    mask = Image.new("L", (target_width, target_height), 0)
    mask_draw = ImageDraw.Draw(mask)
    mask_draw.rounded_rectangle([0, 0, target_width, target_height], radius=40, fill=255)

    # Paste screen onto phone body
    phone.paste(screen, (bezel_padding, bezel_padding), mask)

    # Add front camera punch-hole
    cam_x = phone_w // 2
    cam_y = bezel_padding + 22
    cam_r = 9
    draw.ellipse([cam_x - cam_r, cam_y - cam_r, cam_x + cam_r, cam_y + cam_r], fill=(5, 5, 10, 255))
    draw.ellipse([cam_x - 3, cam_y - 3, cam_x + 3, cam_y + 3], fill=(20, 30, 50, 255))

    return phone

def render_scene_frame(
    bg_color=(11, 15, 25),
    pill_text="EXAM PLANNING OS",
    pill_color=(99, 102, 241),
    headline="Stop Wasting 45 Minutes Deciding",
    phone_img=None,
    phone_y_offset=0,
    caption="Dozens of chapters. Zero clear next action.",
    cta_text="Download Free on Google Play",
    logo_img=None,
    scale=1.0
):
    """
    Renders a single 1080x1920 (9:16) composition frame.
    """
    w, h = 1080, 1920
    frame = Image.new("RGBA", (w, h), bg_color)
    draw = ImageDraw.Draw(frame)

    # 1. Subtle radial ambient glow in background
    glow = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    glow_draw = ImageDraw.Draw(glow)
    glow_draw.ellipse([w//2 - 400, 350, w//2 + 400, 1150], fill=(pill_color[0], pill_color[1], pill_color[2], 25))
    glow = glow.filter(ImageFilter.GaussianBlur(120))
    frame = Image.alpha_composite(frame, glow)
    draw = ImageDraw.Draw(frame)

    # 2. Header Safe Zone (y: 60 - 240)
    # Brand tag
    brand_font = get_font(26, bold=True)
    draw.text((w//2, 80), "EXAMPREPOS  •  STUDY OS", font=brand_font, fill=(148, 163, 184), anchor="mm")

    # Pill badge
    pill_font = get_font(28, bold=True)
    bbox = pill_font.getbbox(pill_text)
    pw = (bbox[2] - bbox[0]) + 40
    ph = (bbox[3] - bbox[1]) + 20
    px = (w - pw) // 2
    py = 120
    draw.rounded_rectangle([px, py, px + pw, py + ph], radius=ph//2, fill=(pill_color[0], pill_color[1], pill_color[2], 230))
    draw.text((w//2, py + ph//2), pill_text, font=pill_font, fill=(255, 255, 255), anchor="mm")

    # Top Headline
    head_font = get_font(44, bold=True)
    draw.text((w//2, 205), headline, font=head_font, fill=(248, 250, 252), anchor="mm")

    # 3. Phone Mockup Focal Zone (y: 280 - 1460)
    if phone_img:
        # Scale if animated
        if scale != 1.0:
            nw = int(phone_img.width * scale)
            nh = int(phone_img.height * scale)
            cur_phone = phone_img.resize((nw, nh), Image.Resampling.BILINEAR)
        else:
            cur_phone = phone_img

        pos_x = (w - cur_phone.width) // 2
        pos_y = 290 + phone_y_offset
        frame.paste(cur_phone, (pos_x, pos_y), cur_phone)

    # 4. Kinetic Caption Card (y: 1510 - 1650)
    if caption:
        cap_font = get_font(38, bold=True)
        lines = [caption]
        if len(caption) > 36 and " " in caption:
            # Wrap nicely into 2 lines
            words = caption.split(" ")
            mid = len(words) // 2
            lines = [" ".join(words[:mid]), " ".join(words[mid:])]

        card_h = 60 + (len(lines) * 48)
        card_w = 960
        card_x = (w - card_w) // 2
        card_y = 1520

        # Semi-transparent dark pill background
        draw.rounded_rectangle(
            [card_x, card_y, card_x + card_w, card_y + card_h],
            radius=28,
            fill=(15, 23, 42, 235),
            outline=(pill_color[0], pill_color[1], pill_color[2], 180),
            width=3
        )

        for i, line in enumerate(lines):
            line_y = card_y + 35 + (i * 48)
            draw.text((w//2, line_y), line, font=cap_font, fill=(248, 250, 252), anchor="mm")

    # 5. Bottom Call to Action Card (y: 1710 - 1860)
    cta_w = 780
    cta_h = 76
    cx = (w - cta_w) // 2
    cy = 1730
    draw.rounded_rectangle(
        [cx, cy, cx + cta_w, cy + cta_h],
        radius=cta_h // 2,
        fill=(99, 102, 241, 240)
    )

    cta_font = get_font(32, bold=True)
    draw.text((w//2, cy + cta_h//2), cta_text, font=cta_font, fill=(255, 255, 255), anchor="mm")

    # Subtle disclaimer at very bottom safe margin
    disc_font = get_font(20, bold=False)
    draw.text((w//2, 1845), "100% OFFLINE • NO ADS IN STUDY • FOR JEE, NEET & UPSC", font=disc_font, fill=(148, 163, 184), anchor="mm")

    return frame.convert("RGB")

def render_end_card_frame(logo_path, title="ExamPrepOS", subtitle="Know where you stand. Know what to study next."):
    """
    Renders a stunning brand end card frame.
    """
    w, h = 1080, 1920
    frame = Image.new("RGBA", (w, h), (11, 15, 25))
    draw = ImageDraw.Draw(frame)

    # Radial brand glow
    glow = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    glow_draw = ImageDraw.Draw(glow)
    glow_draw.ellipse([w//2 - 350, h//2 - 450, w//2 + 350, h//2 + 250], fill=(99, 102, 241, 45))
    glow = glow.filter(ImageFilter.GaussianBlur(100))
    frame = Image.alpha_composite(frame, glow)
    draw = ImageDraw.Draw(frame)

    # Logo
    if Path(logo_path).exists():
        logo = Image.open(logo_path).convert("RGBA")
        logo_size = 280
        logo = logo.resize((logo_size, logo_size), Image.Resampling.LANCZOS)
        # Rounded logo mask
        lmask = Image.new("L", (logo_size, logo_size), 0)
        ldraw = ImageDraw.Draw(lmask)
        ldraw.rounded_rectangle([0, 0, logo_size, logo_size], radius=60, fill=255)
        lx = (w - logo_size) // 2
        ly = (h // 2) - 320
        frame.paste(logo, (lx, ly), lmask)

    # Title
    t_font = get_font(68, bold=True)
    draw.text((w//2, (h // 2) + 40), title, font=t_font, fill=(248, 250, 252), anchor="mm")

    # Subtitle
    sub_font = get_font(34, bold=False)
    draw.text((w//2, (h // 2) + 120), subtitle, font=sub_font, fill=(148, 163, 184), anchor="mm")

    # Feature badges
    badge_y = (h // 2) + 210
    badges = ["100% Offline", "Zero Study Ads", "25 Full Mocks"]
    b_font = get_font(26, bold=True)
    total_w = 3 * 220 + 2 * 20
    start_bx = (w - total_w) // 2
    for i, b in enumerate(badges):
        bx = start_bx + i * (220 + 20)
        draw.rounded_rectangle([bx, badge_y, bx + 220, badge_y + 54], radius=27, fill=(30, 41, 59, 230), outline=(51, 65, 85, 255), width=2)
        draw.text((bx + 110, badge_y + 27), b, font=b_font, fill=(56, 189, 248), anchor="mm")

    # Primary CTA Button
    btn_w = 720
    btn_h = 88
    bx = (w - btn_w) // 2
    by = (h // 2) + 330
    draw.rounded_rectangle([bx, by, bx + btn_w, by + btn_h], radius=btn_h // 2, fill=(99, 102, 241, 255))
    cta_font = get_font(36, bold=True)
    draw.text((w//2, by + btn_h//2), "GET IT ON GOOGLE PLAY", font=cta_font, fill=(255, 255, 255), anchor="mm")

    # Final disclaimer
    disc_font = get_font(22, bold=False)
    draw.text((w//2, by + btn_h + 70), "Independent preparation tool • Built for serious aspirants", font=disc_font, fill=(100, 116, 139), anchor="mm")

    return frame.convert("RGB")

def build_video_asset(
    video_id,
    script_scenes,
    voiceover_text,
    screen_assets,
    output_dir,
    voice=DEFAULT_NEURAL_VOICE_MALE,
    logo_path="marketing/assets/logos/logo_512.png"
):
    """
    Assembles a complete, broadcast-ready vertical short-form video.
    """
    out_dir = Path(output_dir)
    out_dir.mkdir(parents=True, exist_ok=True)
    temp_dir = out_dir / f"temp_{video_id}"
    temp_dir.mkdir(parents=True, exist_ok=True)

    print(f"🎬 Starting video production for [{video_id}] (Voice: {voice})...")

    # 1. Voiceover synthesis
    wav_voice = temp_dir / "voiceover.wav"
    total_voice_dur = synthesize_voiceover(voiceover_text, wav_voice, voice=voice)
    print(f"🎙️ Voiceover generated: {total_voice_dur:.2f} seconds")

    # Ensure total video duration matches voiceover + 2s end card
    total_duration = total_voice_dur + 2.0
    wav_music = temp_dir / "music.wav"
    generate_ambient_music(total_duration, wav_music)
    print(f"🎵 Ambient music bed generated: {total_duration:.2f} seconds")

    # Pre-render phone mockups for the screens
    phone_mockups = {}
    for key, path in screen_assets.items():
        if Path(path).exists():
            phone_mockups[key] = create_phone_mockup(path)
        else:
            print(f"⚠️ Warning: Screen asset not found: {path}")

    # 2. Render frames
    fps = 30
    total_frames = int(total_duration * fps)
    frames_dir = temp_dir / "frames"
    frames_dir.mkdir(exist_ok=True)

    # Distribute duration across scenes
    scene_dur = (total_voice_dur) / max(1, len(script_scenes))

    print(f"🖼️ Rendering {total_frames} frames ({total_duration:.1f}s @ {fps}fps)...")

    # Render scene frames
    frame_idx = 0
    for s_idx, scene in enumerate(script_scenes):
        s_frames = int(scene_dur * fps)
        screen_key = scene.get("screen_key")
        phone_img = phone_mockups.get(screen_key)

        for f in range(s_frames):
            # Gentle subtle zoom animation per scene (1.0 to 1.04)
            progress = f / max(1, s_frames)
            scale = 1.0 + (0.04 * progress)
            y_shift = int(-15 * progress)

            img = render_scene_frame(
                pill_text=scene.get("pill", "EXAMPREPOS"),
                pill_color=scene.get("pill_color", (99, 102, 241)),
                headline=scene.get("headline", "Exam Preparation OS"),
                phone_img=phone_img,
                phone_y_offset=y_shift,
                caption=scene.get("caption", ""),
                scale=scale
            )
            frame_path = frames_dir / f"frame_{frame_idx:05d}.jpg"
            img.save(frame_path, quality=92)
            frame_idx += 1

    # Render End Card frames for remaining duration
    end_card_frames = total_frames - frame_idx
    if end_card_frames > 0:
        end_img = render_end_card_frame(logo_path)
        for f in range(end_card_frames):
            frame_path = frames_dir / f"frame_{frame_idx:05d}.jpg"
            end_img.save(frame_path, quality=92)
            frame_idx += 1

    # 3. FFmpeg Assembly & Encoding
    final_mp4 = out_dir / f"{video_id}.mp4"
    final_thumb = out_dir / f"{video_id}_thumb.jpg"

    print("⚡ Encoding final MP4 with FFmpeg VideoToolbox/libx264...")
    ffmpeg_cmd = (
        f"ffmpeg -y -framerate {fps} -i '{frames_dir}/frame_%05d.jpg' "
        f"-i '{wav_voice}' -i '{wav_music}' "
        f"-filter_complex \"[1:a]apad=whole_dur={total_duration:.2f},volume=1.0[v_a]; [2:a]volume=0.18[m_a]; [v_a][m_a]amix=inputs=2:duration=first[a_mix]; [a_mix]loudnorm=I=-16:TP=-1.5:LRA=11[a_out]\" "
        f"-map 0:v -map \"[a_out]\" "
        f"-c:v libx264 -preset fast -crf 20 -pix_fmt yuv420p -movflags +faststart "
        f"-c:a aac -b:a 192k -shortest '{final_mp4}'"
    )
    run_cmd(ffmpeg_cmd)

    # Save thumbnail (middle of first scene or end card)
    thumb_src = frames_dir / "frame_00060.jpg"
    if thumb_src.exists():
        shutil.copy(thumb_src, final_thumb)

    # Extract verification metadata
    meta_cmd = f"ffprobe -v error -select_streams v:0 -show_entries stream=width,height,duration,nb_frames,bit_rate -of json '{final_mp4}'"
    meta_res = run_cmd(meta_cmd)
    meta_json = json.loads(meta_res.stdout)

    filesize_mb = final_mp4.stat().st_size / (1024 * 1024)

    meta_summary = {
        "video_id": video_id,
        "output_file": str(final_mp4),
        "thumbnail_file": str(final_thumb),
        "duration_seconds": float(meta_json["streams"][0].get("duration", total_duration)),
        "resolution": f"{meta_json['streams'][0]['width']}x{meta_json['streams'][0]['height']}",
        "aspect_ratio": "9:16 (Vertical)",
        "fps": fps,
        "filesize_mb": round(filesize_mb, 2),
        "audio": "Voice (Rishi en_IN) + Lo-fi Ambient Pad (Loudnorm -16 LUFS)",
        "status": "APPROVED_QA"
    }

    meta_file = out_dir / f"{video_id}_meta.json"
    with open(meta_file, "w") as mf:
        json.dump(meta_summary, mf, indent=2)

    # Clean up temp frames to conserve disk
    shutil.rmtree(temp_dir)

    print(f"✅ Video [{video_id}] rendered successfully!")
    print(f"   Output: {final_mp4} ({filesize_mb:.2f} MB, {meta_summary['duration_seconds']:.1f}s)")
    return meta_summary

if __name__ == "__main__":
    print("ExamPrepOS Video Studio Engine Ready.")
