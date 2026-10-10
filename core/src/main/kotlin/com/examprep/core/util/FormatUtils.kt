package com.examprep.core.util

import java.util.Locale

/**
 * Shared formatting and calculation utilities for ExamPrepOS.
 * Centralizes time, string, and numeric formatting across modules.
 */
object FormatUtils {

    /**
     * Formats elapsed or remaining seconds into "MM:SS".
     * Example: 125 seconds -> "02:05".
     */
    fun formatTimeSeconds(totalSeconds: Long): String {
        val safeSeconds = totalSeconds.coerceAtLeast(0L)
        val minutes = safeSeconds / 60
        val seconds = safeSeconds % 60
        return String.format(Locale.ROOT, "%02d:%02d", minutes, seconds)
    }

    fun formatTimeSeconds(totalSeconds: Int): String = formatTimeSeconds(totalSeconds.toLong())

    /**
     * Formats seconds into "HH:MM:SS" format for multi-hour sessions and mock tests.
     * Example: 3665 seconds -> "01:01:05".
     */
    fun formatDurationHms(totalSeconds: Long): String {
        val safeSeconds = totalSeconds.coerceAtLeast(0L)
        val hours = safeSeconds / 3600
        val minutes = (safeSeconds % 3600) / 60
        val seconds = safeSeconds % 60
        return String.format(Locale.ROOT, "%02d:%02d:%02d", hours, minutes, seconds)
    }

    fun formatDurationHms(totalSeconds: Int): String = formatDurationHms(totalSeconds.toLong())

    /**
     * Calculates percentage safely, returning 0f if total <= 0.
     */
    fun calculatePercentage(part: Number, total: Number): Float {
        val t = total.toFloat()
        if (t <= 0f) return 0f
        return (part.toFloat() / t * 100f).coerceIn(0f, 100f)
    }
}

/**
 * Extension function to capitalize the first character of each word.
 * Consolidates duplicate private capitalize extensions across screens.
 */
fun String.capitalizeFirst(): String = replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
}
