package com.examprep.android.feature.flashcards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examprep.domain.model.Flashcard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class FlashcardsUiState(
    val allCards: List<Flashcard> = emptyList(),
    val currentDeck: List<Flashcard> = emptyList(),
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val selectedSubject: String = "All",
    val masteredCount: Int = 0,
    val totalInDeck: Int = 0,
    val currentCard: Flashcard? = null
)

@HiltViewModel
class FlashcardsViewModel @Inject constructor(
    private val store: FlashcardStore
) : ViewModel() {

    private val _selectedSubject = MutableStateFlow("All")
    private val _currentIndex = MutableStateFlow(0)
    private val _isFlipped = MutableStateFlow(false)

    val uiState: StateFlow<FlashcardsUiState> = combine(
        store.flashcards,
        _selectedSubject,
        _currentIndex,
        _isFlipped
    ) { cards, subject, index, flipped ->
        val filtered = if (subject == "All") cards else cards.filter { it.subject.equals(subject, ignoreCase = true) }
        val safeIndex = if (filtered.isEmpty()) 0 else index.coerceIn(0, (filtered.size - 1).coerceAtLeast(0))
        val current = if (filtered.isNotEmpty() && safeIndex in filtered.indices) filtered[safeIndex] else null
        val mastered = filtered.count { it.isMastered }

        FlashcardsUiState(
            allCards = cards,
            currentDeck = filtered,
            currentIndex = safeIndex,
            isFlipped = flipped,
            selectedSubject = subject,
            masteredCount = mastered,
            totalInDeck = filtered.size,
            currentCard = current
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FlashcardsUiState()
    )

    fun setSubject(subject: String) {
        _selectedSubject.value = subject
        _currentIndex.value = 0
        _isFlipped.value = false
    }

    fun flipCard() {
        _isFlipped.value = !_isFlipped.value
    }

    fun nextCard() {
        val deck = uiState.value.currentDeck
        if (deck.isNotEmpty()) {
            _currentIndex.value = (_currentIndex.value + 1) % deck.size
            _isFlipped.value = false
        }
    }

    fun prevCard() {
        val deck = uiState.value.currentDeck
        if (deck.isNotEmpty()) {
            _currentIndex.value = if (_currentIndex.value > 0) _currentIndex.value - 1 else deck.size - 1
            _isFlipped.value = false
        }
    }

    fun markMastered() {
        val card = uiState.value.currentCard ?: return
        store.toggleMastered(card.id)
        nextCard()
    }

    fun toggleBookmark() {
        val card = uiState.value.currentCard ?: return
        store.toggleBookmark(card.id)
    }

    fun shuffleDeck() {
        _currentIndex.value = 0
        _isFlipped.value = false
    }
}
