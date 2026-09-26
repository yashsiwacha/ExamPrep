package com.examprep.android.feature.flashcards

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FlashcardStoreTest {

    private lateinit var store: FlashcardStore

    @Before
    fun setUp() {
        store = FlashcardStore()
    }

    @Test
    fun testDefaultFlashcardsLoadedAcrossAllSubjects() {
        val cards = store.flashcards.value
        assertTrue("Store should have default seeded flashcards", cards.isNotEmpty())
        
        val subjects = cards.map { it.subject }.distinct()
        assertTrue("Should contain Physics", subjects.contains("Physics"))
        assertTrue("Should contain Chemistry", subjects.contains("Chemistry"))
        assertTrue("Should contain Mathematics", subjects.contains("Mathematics"))
    }

    @Test
    fun testToggleMasteredUpdatesStatusAndReviewCount() {
        val targetCardId = "fc_phy_1"
        val initialCard = store.flashcards.value.first { it.id == targetCardId }
        val initialMastered = initialCard.isMastered
        val initialReviews = initialCard.reviewCount

        store.toggleMastered(targetCardId)
        val updatedCard = store.flashcards.value.first { it.id == targetCardId }
        assertEquals(!initialMastered, updatedCard.isMastered)
        assertEquals(initialReviews + 1, updatedCard.reviewCount)
    }

    @Test
    fun testToggleBookmark() {
        val targetCardId = "fc_chem_1"
        val initialCard = store.flashcards.value.first { it.id == targetCardId }
        val initialBookmarked = initialCard.isBookmarked

        store.toggleBookmark(targetCardId)
        val updatedCard = store.flashcards.value.first { it.id == targetCardId }
        assertEquals(!initialBookmarked, updatedCard.isBookmarked)

        store.toggleBookmark(targetCardId)
        val revertedCard = store.flashcards.value.first { it.id == targetCardId }
        assertEquals(initialBookmarked, revertedCard.isBookmarked)
    }

    @Test
    fun testNoMutationOnInvalidCardId() {
        val initialCards = store.flashcards.value
        store.toggleMastered("invalid_non_existent_id")
        store.toggleBookmark("invalid_non_existent_id")
        assertEquals(initialCards, store.flashcards.value)
    }
}
