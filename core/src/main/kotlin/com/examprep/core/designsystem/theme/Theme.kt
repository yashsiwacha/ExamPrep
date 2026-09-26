package com.examprep.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


// ═══════════════════════════════════════════════════════════════════════════════
// EXAMPREP SUBTLE MINIMALIST PALETTE (DISTRACTION-FREE COGNITIVE COMFORT)
//
// Design philosophy:
//   - Understated, matte, minimalist monochrome-with-whisper-accents aesthetic
//   - Zero neon or harsh saturation to eliminate cognitive fatigue
//   - Pure deep slate-noir background with frosted subtle borders
//   - Subtle Platinum Slate, Muted Lichen Green, and Soft Champagne Sand accents
// ═══════════════════════════════════════════════════════════════════════════════

object ExamPrepColors {

    // Primary — Subtle Platinum-Slate Steel (Focus & Clarity)
    val Slate10 = Color(0xFF090B0E)
    val Slate20 = Color(0xFF111419)
    val Slate30 = Color(0xFF1B2028)
    val Slate40 = Color(0xFF2C3440)
    val Slate50 = Color(0xFF455060)
    val Slate60 = Color(0xFF64748B)
    val Slate70 = Color(0xFF94A3B8)   // Subtle primary accent
    val Slate80 = Color(0xFFCBD5E1)
    val Slate90 = Color(0xFFE2E8F0)
    val Slate95 = Color(0xFFF1F5F9)
    val Slate99 = Color(0xFFF8FAFC)

    // Secondary — Muted Lichen Sage (Calm Growth & Mastery)
    val Sage10 = Color(0xFF070E0B)
    val Sage20 = Color(0xFF101C16)
    val Sage30 = Color(0xFF1B2D24)
    val Sage40 = Color(0xFF2C4538)
    val Sage50 = Color(0xFF456554)
    val Sage60 = Color(0xFF638974)
    val Sage70 = Color(0xFF86A795)   // Subtle progress & mastery
    val Sage80 = Color(0xFFAEC8BA)
    val Sage90 = Color(0xFFD6E6DE)
    val Sage95 = Color(0xFFEDF5F1)

    // Tertiary — Soft Champagne Sand (Streaks & Warm Accents)
    val Sand10 = Color(0xFF0F0C08)
    val Sand20 = Color(0xFF1C1710)
    val Sand30 = Color(0xFF2E261B)
    val Sand40 = Color(0xFF483C2C)
    val Sand50 = Color(0xFF6A5B44)
    val Sand60 = Color(0xFF948063)
    val Sand70 = Color(0xFFBFAB8D)   // Subtle warm highlight
    val Sand80 = Color(0xFFD8C9B3)
    val Sand90 = Color(0xFFECE4D7)
    val Sand95 = Color(0xFFF7F4EE)

    // Neutral Surfaces — Deep Velvet Slate Obsidian
    val Neutral0 = Color(0xFF000000)
    val Neutral10 = Color(0xFF0B0D10)  // App Background (Matte Slate Noir)
    val Neutral15 = Color(0xFF13161C)  // Elevated Surface / Cards
    val Neutral20 = Color(0xFF1A1E26)  // Card Secondary / Containers
    val Neutral30 = Color(0xFF242A35)  // Borders / Dividers
    val Neutral40 = Color(0xFF333B49)  // Subdued Outlines
    val Neutral50 = Color(0xFF4C5668)
    val Neutral60 = Color(0xFF6E7B91)  // Tertiary captions
    val Neutral70 = Color(0xFF94A3B8)  // Secondary text
    val Neutral80 = Color(0xFFCBD5E1)  // Body text
    val Neutral90 = Color(0xFFF1F5F9)  // Primary titles & headers
    val Neutral95 = Color(0xFFF8FAFC)
    val Neutral99 = Color(0xFFFCFDFE)
    val Neutral100 = Color(0xFFFFFFFF)

    // Semantic Status Colors (Matte, Low-Fatigue, WCAG AA Compliant)
    val Success = Color(0xFF68B294)
    val SuccessContainer = Color(0xFF132820)
    val Warning = Color(0xFFCCA564)
    val WarningContainer = Color(0xFF2A2213)
    val Error = Color(0xFFC26E6E)
    val ErrorContainer = Color(0xFF2B1616)

    // Mastery State Colors (Muted & Harmonious)
    val MasteryNotStarted = Color(0xFF3E4654)
    val MasteryInProgress = Color(0xFF667890)
    val MasteryLearned = Color(0xFF8697AB)
    val MasteryPracticing = Color(0xFF759A87)
    val MasteryMastered = Color(0xFF68B294)
    val MasteryRevisionDue = Color(0xFFCCA564)

    // Aliases for compatibility
    val Primary = Slate80
    val BrandPrimary = Slate80
    val Indigo70 = Slate70
    val Indigo80 = Slate80
    val Amber70 = Sand70
    val Teal60 = Sage60
    val Teal70 = Sage70
}

// ═══════════════════════════════════════════════════════════════════════════════
// MATERIAL 3 COLOR SCHEMES
// ═══════════════════════════════════════════════════════════════════════════════

private val DarkColorScheme = darkColorScheme(
    primary = ExamPrepColors.Slate80,
    onPrimary = ExamPrepColors.Slate10,
    primaryContainer = ExamPrepColors.Slate30,
    onPrimaryContainer = ExamPrepColors.Slate90,

    secondary = ExamPrepColors.Sage70,
    onSecondary = ExamPrepColors.Sage10,
    secondaryContainer = ExamPrepColors.Sage20,
    onSecondaryContainer = ExamPrepColors.Sage90,

    tertiary = ExamPrepColors.Sand70,
    onTertiary = ExamPrepColors.Sand10,
    tertiaryContainer = ExamPrepColors.Sand20,
    onTertiaryContainer = ExamPrepColors.Sand90,

    background = ExamPrepColors.Neutral10,
    onBackground = ExamPrepColors.Neutral90,

    surface = ExamPrepColors.Neutral15,
    onSurface = ExamPrepColors.Neutral90,
    surfaceVariant = ExamPrepColors.Neutral20,
    onSurfaceVariant = ExamPrepColors.Neutral70,

    outline = ExamPrepColors.Neutral40,
    outlineVariant = ExamPrepColors.Neutral30,

    error = ExamPrepColors.Error,
    onError = ExamPrepColors.Neutral100,
    errorContainer = ExamPrepColors.ErrorContainer,
    onErrorContainer = Color(0xFFFFD4D0)
)

private val LightColorScheme = lightColorScheme(
    primary = ExamPrepColors.Slate50,
    onPrimary = ExamPrepColors.Neutral100,
    primaryContainer = ExamPrepColors.Slate95,
    onPrimaryContainer = ExamPrepColors.Slate20,

    secondary = ExamPrepColors.Sage50,
    onSecondary = ExamPrepColors.Neutral100,
    secondaryContainer = ExamPrepColors.Sage95,
    onSecondaryContainer = ExamPrepColors.Sage20,

    tertiary = ExamPrepColors.Sand50,
    onTertiary = ExamPrepColors.Neutral100,
    tertiaryContainer = ExamPrepColors.Sand95,
    onTertiaryContainer = ExamPrepColors.Sand20,

    background = ExamPrepColors.Neutral99,
    onBackground = ExamPrepColors.Neutral10,

    surface = ExamPrepColors.Neutral100,
    onSurface = ExamPrepColors.Neutral10,
    surfaceVariant = ExamPrepColors.Neutral95,
    onSurfaceVariant = ExamPrepColors.Neutral50,

    outline = ExamPrepColors.Neutral60,
    outlineVariant = ExamPrepColors.Neutral80,

    error = ExamPrepColors.Error,
    onError = ExamPrepColors.Neutral100,
    errorContainer = Color(0xFFFFECEB),
    onErrorContainer = Color(0xFF4A100F)
)

object ThemeState {
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }
}

@Composable
fun ExamPrepTheme(
    darkTheme: Boolean = true,  // Default dark for focus & long study sessions
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ExamPrepTypography,
        shapes = ExamPrepShapes,
        content = content
    )
}

