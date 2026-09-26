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
// EXAMPREP REFINED PREMIUM PALETTE (STUDENT FOCUS & COGNITIVE COMFORT)
//
// Design philosophy:
//   - Subtle, calm, distraction-free matte aesthetic
//   - Zero neon fatigue during multi-hour late-night study sessions
//   - Slate Obsidian dark mode with refined optical contrast
//   - Elegant muted Indigo, Sage Green, and Honey Amber accents
// ═══════════════════════════════════════════════════════════════════════════════

object ExamPrepColors {

    // Primary — Refined Slate-Indigo (Intellect & Focus)
    val Indigo10 = Color(0xFF090B14)
    val Indigo20 = Color(0xFF131728)
    val Indigo30 = Color(0xFF1F2440)
    val Indigo40 = Color(0xFF333C66)
    val Indigo50 = Color(0xFF4F5B93)
    val Indigo60 = Color(0xFF6371B3)
    val Indigo70 = Color(0xFF818FD4)   // Subtle primary light/accent
    val Indigo80 = Color(0xFFA5B1E8)
    val Indigo90 = Color(0xFFD2D8F7)
    val Indigo95 = Color(0xFFE9ECFA)
    val Indigo99 = Color(0xFFF7F8FD)

    // Secondary — Muted Nordic Sage / Soft Jade (Growth, Calm Mastery)
    val Sage10 = Color(0xFF051510)
    val Sage20 = Color(0xFF0C241B)
    val Sage30 = Color(0xFF163A2D)
    val Sage40 = Color(0xFF235543)
    val Sage50 = Color(0xFF367A62)
    val Sage60 = Color(0xFF4FA083)
    val Sage70 = Color(0xFF6FC4A5)   // Subtle progress accent
    val Sage80 = Color(0xFF98DECA)
    val Sage90 = Color(0xFFC7EFE3)
    val Sage95 = Color(0xFFE5F8F2)

    // Tertiary — Warm Muted Honey (Urgency, Streaks, Focus)
    val Amber10 = Color(0xFF1E1303)
    val Amber20 = Color(0xFF332007)
    val Amber30 = Color(0xFF52330D)
    val Amber40 = Color(0xFF754B17)
    val Amber50 = Color(0xFF9E6824)
    val Amber60 = Color(0xFFC48635)
    val Amber70 = Color(0xFFE5A955)   // Subtle warm highlight
    val Amber80 = Color(0xFFF3C788)
    val Amber90 = Color(0xFFFBE4C4)
    val Amber95 = Color(0xFFFCF2E4)

    // Neutral Surface — Deep Velvet Obsidian & Slate
    val Neutral0 = Color(0xFF000000)
    val Neutral10 = Color(0xFF0E1015)  // App Background (Soft Obsidian)
    val Neutral15 = Color(0xFF141720)  // Elevated Surface / Cards
    val Neutral20 = Color(0xFF1C202C)  // Card secondary / Containers
    val Neutral30 = Color(0xFF282D3D)  // Borders / Dividers
    val Neutral40 = Color(0xFF3B4358)  // Subdued outlines
    val Neutral50 = Color(0xFF5A637C)
    val Neutral60 = Color(0xFF7D87A1)  // Tertiary captions
    val Neutral70 = Color(0xFFA2ABC0)  // Secondary text
    val Neutral80 = Color(0xFFC9D0E0)  // Body text
    val Neutral90 = Color(0xFFE6EAF2)  // Primary titles & headers
    val Neutral95 = Color(0xFFF2F4F8)
    val Neutral99 = Color(0xFFFAFBFD)
    val Neutral100 = Color(0xFFFFFFFF)

    // Semantic Status Colors (Matte, accessible WCAG AA)
    val Success = Color(0xFF38B285)
    val SuccessContainer = Color(0xFF112E22)
    val Warning = Color(0xFFD98A2C)
    val WarningContainer = Color(0xFF331E05)
    val Error = Color(0xFFD9534F)
    val ErrorContainer = Color(0xFF331312)

    // Mastery State Colors (Harmonious & Professional)
    val MasteryNotStarted = Color(0xFF4A5268)
    val MasteryInProgress = Color(0xFF5E7FD9)
    val MasteryLearned = Color(0xFF818FD4)
    val MasteryPracticing = Color(0xFF57B896)
    val MasteryMastered = Color(0xFF38B285)
    val MasteryRevisionDue = Color(0xFFD98A2C)

    // Legacy aliases for backward compatibility across screens
    val Primary = Indigo70
    val BrandPrimary = Indigo70
    val Teal60 = Sage60
    val Teal70 = Sage70
}

// ═══════════════════════════════════════════════════════════════════════════════
// MATERIAL 3 COLOR SCHEMES
// ═══════════════════════════════════════════════════════════════════════════════

private val DarkColorScheme = darkColorScheme(
    primary = ExamPrepColors.Indigo70,
    onPrimary = ExamPrepColors.Indigo10,
    primaryContainer = ExamPrepColors.Indigo30,
    onPrimaryContainer = ExamPrepColors.Indigo90,

    secondary = ExamPrepColors.Sage70,
    onSecondary = ExamPrepColors.Sage10,
    secondaryContainer = ExamPrepColors.Sage30,
    onSecondaryContainer = ExamPrepColors.Sage90,

    tertiary = ExamPrepColors.Amber70,
    onTertiary = ExamPrepColors.Amber10,
    tertiaryContainer = ExamPrepColors.Amber30,
    onTertiaryContainer = ExamPrepColors.Amber90,

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
    primary = ExamPrepColors.Indigo50,
    onPrimary = ExamPrepColors.Neutral100,
    primaryContainer = ExamPrepColors.Indigo95,
    onPrimaryContainer = ExamPrepColors.Indigo20,

    secondary = ExamPrepColors.Sage50,
    onSecondary = ExamPrepColors.Neutral100,
    secondaryContainer = ExamPrepColors.Sage95,
    onSecondaryContainer = ExamPrepColors.Sage20,

    tertiary = ExamPrepColors.Amber50,
    onTertiary = ExamPrepColors.Neutral100,
    tertiaryContainer = ExamPrepColors.Amber95,
    onTertiaryContainer = ExamPrepColors.Amber20,

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

