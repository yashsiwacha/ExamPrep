package com.examprep.android.feature.flashcards

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.examprep.core.designsystem.theme.ExamPrepColors
import com.examprep.core.designsystem.theme.Spacing
import com.examprep.domain.model.Flashcard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsScreen(
    onNavigateBack: () -> Unit,
    viewModel: FlashcardsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val currentCard = state.currentCard

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Formula Flashcards",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Quick high-yield concept review",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.shuffleDeck() }) {
                        Icon(Icons.Default.Shuffle, contentDescription = "Shuffle", tint = ExamPrepColors.Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            if (currentCard != null) {
                Surface(
                    tonalElevation = 6.dp,
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.MD, vertical = Spacing.SM)
                            .navigationBarsPadding(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.prevCard() },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Prev")
                        }

                        Button(
                            onClick = { viewModel.markMastered() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (currentCard.isMastered) ExamPrepColors.Success else ExamPrepColors.Primary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                if (currentCard.isMastered) Icons.Default.CheckCircle else Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(if (currentCard.isMastered) "Mastered ✓" else "Mark Mastered")
                        }

                        Button(
                            onClick = { viewModel.nextCard() },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Next")
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = Spacing.MD)
        ) {
            Spacer(Modifier.height(Spacing.SM))

            // Subject Filter Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.SM),
                modifier = Modifier.fillMaxWidth()
            ) {
                val subjects = listOf("All", "Physics", "Chemistry", "Mathematics")
                items(subjects) { sub ->
                    FilterChip(
                        selected = state.selectedSubject == sub,
                        onClick = { viewModel.setSubject(sub) },
                        label = { Text(sub) }
                    )
                }
            }

            Spacer(Modifier.height(Spacing.SM))

            // Progress Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Card ${state.currentIndex + 1} of ${state.totalInDeck}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = "${state.masteredCount} / ${state.totalInDeck} Mastered",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = ExamPrepColors.Success,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(Modifier.height(Spacing.XS))

            LinearProgressIndicator(
                progress = {
                    if (state.totalInDeck > 0) state.masteredCount.toFloat() / state.totalInDeck.toFloat() else 0f
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = ExamPrepColors.Success,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(Modifier.height(Spacing.MD))

            // 3D Flip Card Container
            if (currentCard != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(bottom = Spacing.MD),
                    contentAlignment = Alignment.Center
                ) {
                    FlipCard(
                        card = currentCard,
                        isFlipped = state.isFlipped,
                        onFlip = { viewModel.flipCard() },
                        onToggleBookmark = { viewModel.toggleBookmark() }
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No flashcards found in this subject deck.")
                }
            }
        }
    }
}

@Composable
private fun FlipCard(
    card: Flashcard,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    onToggleBookmark: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "cardRotation"
    )

    Card(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable { onFlip() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (rotation > 90f) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        if (rotation <= 90f) {
            // Front Side (Prompt / Question)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Spacing.LG)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = ExamPrepColors.Primary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "${card.subject} • ${card.topic}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ExamPrepColors.Primary,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    IconButton(onClick = onToggleBookmark) {
                        Icon(
                            if (card.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (card.isBookmarked) ExamPrepColors.Warning else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Spacing.MD),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = card.title,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(Spacing.MD))
                    Text(
                        text = card.frontPrompt,
                        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                        textAlign = TextAlign.Center
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.TouchApp, contentDescription = null, modifier = Modifier.size(16.dp), tint = ExamPrepColors.Primary)
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Tap card to reveal formula & derivation",
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                }
            }
        } else {
            // Back Side (Answer / Formula / Derivation)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }
                    .padding(Spacing.LG)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = ExamPrepColors.Success.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Formula & Key Takeaway",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ExamPrepColors.Success,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    IconButton(onClick = onToggleBookmark) {
                        Icon(
                            if (card.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (card.isBookmarked) ExamPrepColors.Warning else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Spacing.SM),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val formula = card.keyFormula
                    if (formula != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = formula,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ExamPrepColors.Primary
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(Spacing.MD)
                            )
                        }
                        Spacer(Modifier.height(Spacing.MD))
                    }

                    Text(
                        text = card.backAnswer,
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                        textAlign = TextAlign.Center
                    )

                    if (card.mnemonicTip != null) {
                        Spacer(Modifier.height(Spacing.MD))
                        Surface(
                            color = ExamPrepColors.Warning.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "💡 Memory Tip: ${card.mnemonicTip}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.TouchApp, contentDescription = null, modifier = Modifier.size(16.dp), tint = ExamPrepColors.Primary)
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Tap card to flip back",
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                }
            }
        }
    }
}
