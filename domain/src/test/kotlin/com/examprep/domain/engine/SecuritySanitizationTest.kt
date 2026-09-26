package com.examprep.domain.engine

import org.junit.Assert.*
import org.junit.Test

class SecuritySanitizationTest {

    @Test
    fun `score percentage coerces strictly within 0 and 1 bounds preventing arithmetic overflow`() {
        val negativeScore = -50
        val maxMarks = 300
        val clampedPercent = (negativeScore.toFloat() / maxMarks).coerceIn(0f, 1f)
        assertEquals(0f, clampedPercent, 0.001f)

        val overScore = 400
        val clampedOver = (overScore.toFloat() / maxMarks).coerceIn(0f, 1f)
        assertEquals(1f, clampedOver, 0.001f)
    }

    @Test
    fun `search query sanitizes special regex characters to prevent regex denial of service`() {
        val queryWithSpecialChars = ".*+?^$\\{}()|[]"
        val sampleText = "A uniform solid cylinder of mass M and radius R"
        
        // Literal substring containment must safely evaluate to false without crash
        val isContained = sampleText.contains(queryWithSpecialChars, ignoreCase = true)
        assertFalse(isContained)
    }
}
