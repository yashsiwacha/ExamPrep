package com.examprep.android.feature.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.examprep.core.designsystem.theme.ExamPrepColors
import com.examprep.core.designsystem.theme.Spacing
import com.examprep.domain.model.Question

@Composable
fun QuizScreen(
    quizId: String,
    onQuizComplete: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val question = uiState.currentQuestion
    var showExitDialog by remember { mutableStateOf(false) }

    // Single source of truth for completion navigation
    val submittedAttemptId = uiState.submittedAttemptId
    LaunchedEffect(submittedAttemptId) {
        if (submittedAttemptId != null) {
            onQuizComplete(submittedAttemptId)
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Quit Assessment?") },
            text = { Text("Your progress in this practice session will be lost if you exit now.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("Exit", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                Button(onClick = { showExitDialog = false }) {
                    Text("Continue Quiz")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Quiz header
        QuizHeader(
            onExit = { showExitDialog = true },
            questionIndex = uiState.currentQuestionIndex,
            totalQuestions = uiState.questions.size.coerceAtLeast(1),
            timeRemaining = uiState.remainingSeconds
        )

        // Question Palette Quick-Jump Bar
        if (uiState.questions.isNotEmpty()) {
            QuestionPaletteBar(
                totalQuestions = uiState.questions.size,
                currentIndex = uiState.currentQuestionIndex,
                selectedOptions = uiState.selectedOptions,
                onSelectQuestion = { viewModel.goToQuestion(it) }
            )
        }

        if (uiState.isLoading || question == null) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Text(
                        text = "Preparing assessment...",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
        } else {
            val selectedOptionId = uiState.selectedOptions[uiState.currentQuestionIndex]

            // Question and options
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(Spacing.MD),
                verticalArrangement = Arrangement.spacedBy(Spacing.SM)
            ) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large)
                    ) {
                        Column(modifier = Modifier.padding(Spacing.LG)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = MaterialTheme.shapes.small
                                    ) {
                                        Text(
                                            text = "Q${uiState.currentQuestionIndex + 1}",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                    if (question.tags.isNotEmpty()) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            shape = MaterialTheme.shapes.extraSmall
                                        ) {
                                            Text(
                                                text = question.tags.first(),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontSize = 11.sp
                                                )
                                            )
                                        }
                                    }
                                }
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                    shape = MaterialTheme.shapes.extraSmall
                                ) {
                                    Text(
                                        text = "+${question.marks.toInt()} / ${question.negativeMarks.toInt()}",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                            Spacer(Modifier.height(Spacing.MD))
                            Text(
                                text = question.questionText,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 24.sp
                                )
                            )
                        }
                    }
                }

                items(
                    items = question.options.sortedBy { it.orderIndex },
                    key = { it.id }
                ) { option ->
                    val label = ('A' + (option.orderIndex - 1)).toString()
                    OptionCard(
                        label = label,
                        text = option.text,
                        isSelected = selectedOptionId == option.id,
                        onClick = { viewModel.selectOption(option.id) }
                    )
                }

                if (selectedOptionId != null) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { viewModel.clearOption() },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Clear Selection", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }

        // Navigation footer
        QuizNavigationFooter(
            onPrevious = { viewModel.previousQuestion() },
            onNext = {
                if (uiState.isLastQuestion) {
                    viewModel.submitQuiz()
                } else {
                    viewModel.nextQuestion()
                }
            },
            isLastQuestion = uiState.isLastQuestion,
            isSubmitting = uiState.isSubmitting,
            hasPrevious = uiState.currentQuestionIndex > 0
        )
    }
}

@Composable
private fun QuestionPaletteBar(
    totalQuestions: Int,
    currentIndex: Int,
    selectedOptions: Map<Int, String>,
    onSelectQuestion: (Int) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = Spacing.MD, vertical = Spacing.SM),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(totalQuestions) { index ->
                val isCurrent = index == currentIndex
                val isAnswered = selectedOptions.containsKey(index)

                val backgroundColor = when {
                    isCurrent -> MaterialTheme.colorScheme.primaryContainer
                    isAnswered -> ExamPrepColors.Sage70.copy(alpha = 0.2f)
                    else -> MaterialTheme.colorScheme.surface
                }

                val textColor = when {
                    isCurrent -> MaterialTheme.colorScheme.primary
                    isAnswered -> ExamPrepColors.Sage70
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }

                val borderColor = when {
                    isCurrent -> MaterialTheme.colorScheme.primary
                    isAnswered -> ExamPrepColors.Sage70.copy(alpha = 0.5f)
                    else -> MaterialTheme.colorScheme.outlineVariant
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(backgroundColor, shape = MaterialTheme.shapes.small)
                        .border(
                            width = if (isCurrent) 1.5.dp else 1.dp,
                            color = borderColor,
                            shape = MaterialTheme.shapes.small
                        )
                        .clickable { onSelectQuestion(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isCurrent || isAnswered) FontWeight.Bold else FontWeight.Normal,
                            color = textColor
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun QuizHeader(
    onExit: () -> Unit,
    questionIndex: Int,
    totalQuestions: Int,
    timeRemaining: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.MD, vertical = Spacing.SM),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onExit) {
                Icon(Icons.Default.Close, contentDescription = "Exit Assessment", tint = MaterialTheme.colorScheme.onSurface)
            }
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.extraSmall
            ) {
                Text(
                    text = "QUESTION ${questionIndex + 1} OF $totalQuestions",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.8.sp
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Surface(
                color = if (timeRemaining <= 60) MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.border(
                    1.dp,
                    if (timeRemaining <= 60) MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    MaterialTheme.shapes.small
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (timeRemaining <= 60) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = formatTime(timeRemaining),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = if (timeRemaining <= 60) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
        LinearProgressIndicator(
            progress = { ((questionIndex + 1).toFloat() / totalQuestions.toFloat()).coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(3.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun OptionCard(label: String, text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                shape = MaterialTheme.shapes.medium
            ),
        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        else MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isSelected) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                ),
                modifier = Modifier.weight(1f)
            )
            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun QuizNavigationFooter(
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    isLastQuestion: Boolean,
    isSubmitting: Boolean,
    hasPrevious: Boolean
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.MD),
            horizontalArrangement = Arrangement.spacedBy(Spacing.SM)
        ) {
            OutlinedButton(
                onClick = onPrevious,
                enabled = hasPrevious && !isSubmitting,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Previous")
            }
            Button(
                onClick = onNext,
                enabled = !isSubmitting,
                modifier = Modifier.weight(1f)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Grading...")
                } else {
                    Text(if (isLastQuestion) "Submit Quiz" else "Next")
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

private fun formatTime(seconds: Int) = "%02d:%02d".format(seconds / 60, seconds % 60)

// ════════════════════════════════════════════════════════════════════════════════
// QUIZ RESULT SCREEN
// ════════════════════════════════════════════════════════════════════════════════

@Composable
fun QuizResultScreen(
    attemptId: String,
    onNavigateHome: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: QuizResultViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.MD),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Assessment Result",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        when (val uiState = state) {
            is QuizResultUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            is QuizResultUiState.Error -> {
                Box(Modifier.fillMaxSize().padding(Spacing.LG), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Error: ${uiState.message}", style = MaterialTheme.typography.bodyLarge)
                        Button(onClick = onNavigateHome) {
                            Text("Back to Dashboard")
                        }
                    }
                }
            }
            is QuizResultUiState.Success -> {
                val accuracy = uiState.attempt.accuracy
                val percentileTier = when {
                    accuracy >= 80f -> "🏆 Top 5% National Tier"
                    accuracy >= 60f -> "⚡ Top 15% Aspirant Tier"
                    else -> "📈 Building Foundational Mastery"
                }

                LazyColumn(
                    contentPadding = PaddingValues(horizontal = Spacing.MD),
                    verticalArrangement = Arrangement.spacedBy(Spacing.MD)
                ) {
                    // Scorecard Hero Card
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = MaterialTheme.shapes.extraLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.extraLarge)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(Spacing.XL),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                    shape = MaterialTheme.shapes.extraSmall
                                ) {
                                    Text(
                                        text = percentileTier,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                                Spacer(Modifier.height(Spacing.MD))
                                Text(
                                    text = "${uiState.attempt.score.toInt()} / ${uiState.attempt.maxScore.toInt()}",
                                    style = MaterialTheme.typography.displayMedium.copy(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                                Text(
                                    text = "Marks Scored",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                                Spacer(Modifier.height(Spacing.LG))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                Spacer(Modifier.height(Spacing.MD))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    ResultStat("${uiState.attempt.accuracy.toInt()}%", "Accuracy")
                                    ResultStat("${uiState.totalTimeSecs}s", "Time Spent")
                                    ResultStat("${uiState.correctCount}", "Correct")
                                }
                            }
                        }
                    }

                    // Breakdown chips
                    item {
                        Text(
                            "Performance Breakdown",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.SM)) {
                            BreakdownChip("✓ ${uiState.correctCount} Correct", ExamPrepColors.Success)
                            BreakdownChip("✗ ${uiState.incorrectCount} Wrong", ExamPrepColors.Error)
                            BreakdownChip("– ${uiState.skippedCount} Skipped", MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    // Detailed question analysis
                    itemsIndexed(
                        items = uiState.questionResults,
                        key = { _, item -> item.attempt.id }
                    ) { index, item ->
                        QuestionReviewCard(index = index + 1, item = item)
                    }

                    item {
                        Button(
                            onClick = onNavigateHome,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text("Back to Dashboard", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                    item { Spacer(Modifier.height(Spacing.XL)) }
                }
            }
        }
    }
}

@Composable
private fun QuestionReviewCard(index: Int, item: QuestionResultItem) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.medium),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(Spacing.MD)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Question $index",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
                val statusText = when {
                    item.attempt.isSkipped -> "Skipped (0)"
                    item.attempt.isCorrect -> "✓ Correct (+${item.attempt.marksEarned.toInt()})"
                    else -> "✗ Incorrect (${item.attempt.marksEarned.toInt()})"
                }
                val statusColor = when {
                    item.attempt.isSkipped -> MaterialTheme.colorScheme.onSurfaceVariant
                    item.attempt.isCorrect -> ExamPrepColors.Success
                    else -> ExamPrepColors.Error
                }
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(Modifier.height(Spacing.SM))
            Text(
                text = item.question.questionText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(Modifier.height(Spacing.SM))

            // Options summary
            item.question.options.sortedBy { it.orderIndex }.forEach { option ->
                val label = ('A' + (option.orderIndex - 1)).toString()
                val isSelected = item.attempt.selectedOptionId == option.id
                val isCorrect = option.isCorrect

                val (bgColor, txtColor) = when {
                    isCorrect -> ExamPrepColors.Success.copy(alpha = 0.15f) to ExamPrepColors.Success
                    isSelected -> ExamPrepColors.Error.copy(alpha = 0.15f) to ExamPrepColors.Error
                    else -> Color.Transparent to MaterialTheme.colorScheme.onSurfaceVariant
                }

                Surface(
                    color = bgColor,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "$label.",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = txtColor)
                        )
                        Text(
                            text = option.text,
                            style = MaterialTheme.typography.bodySmall.copy(color = txtColor),
                            modifier = Modifier.weight(1f)
                        )
                        if (isCorrect) {
                            Text("✓ Correct", style = MaterialTheme.typography.labelSmall.copy(color = ExamPrepColors.Success, fontWeight = FontWeight.Bold))
                        } else if (isSelected) {
                            Text("Your Choice", style = MaterialTheme.typography.labelSmall.copy(color = ExamPrepColors.Error))
                        }
                    }
                }
            }

            val explanation = item.question.explanation
            if (!explanation.isNullOrBlank()) {
                Spacer(Modifier.height(Spacing.SM))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "💡 Step-by-Step Explanation",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = explanation,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

@Composable
private fun BreakdownChip(label: String, color: Color) {
    Surface(color = color.copy(alpha = 0.15f), shape = MaterialTheme.shapes.small) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium.copy(color = color, fontWeight = FontWeight.SemiBold)
        )
    }
}

