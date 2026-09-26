package com.examprep.android.feature.mocktest

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
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
import com.examprep.domain.model.MockSectionResult
import com.examprep.domain.model.Question

@Composable
fun MockTestResultScreen(
    attemptId: String,
    onNavigateHome: () -> Unit,
    onNavigateToNewTest: () -> Unit = {},
    viewModel: MockTestResultViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    BackHandler {
        onNavigateHome()
    }

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = Spacing.MD, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = onNavigateHome, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                        }
                        Text(
                            text = "Mock Exam Assessment",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Button(
                        onClick = onNavigateHome,
                        shape = MaterialTheme.shapes.small,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("Dashboard", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    ) { innerPadding ->
        val result = uiState.result
        if (result == null) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("No attempt records found.", style = MaterialTheme.typography.bodyMedium)
                    Button(onClick = onNavigateHome) { Text("Back to Dashboard") }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = Spacing.MD, vertical = Spacing.MD),
                verticalArrangement = Arrangement.spacedBy(Spacing.MD)
            ) {
                // ── Hero Scorecard Card ──────────────────────────────────────────────
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(modifier = Modifier.padding(Spacing.LG)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "NATIONAL PERFORMANCE SCORECARD",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp
                                    )
                                )
                                Surface(
                                    color = ExamPrepColors.Success.copy(alpha = 0.15f),
                                    shape = MaterialTheme.shapes.extraSmall
                                ) {
                                    Text(
                                        text = result.projectedRankRange,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = ExamPrepColors.Success,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }

                            Spacer(Modifier.height(Spacing.MD))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(
                                            text = "${result.totalScore}",
                                            style = MaterialTheme.typography.displayMedium.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (result.totalScore >= 0) ExamPrepColors.Success else ExamPrepColors.Error
                                            )
                                        )
                                        Text(
                                            text = " / ${result.maximumMarks} marks",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            ),
                                            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                                        )
                                    }
                                    Text(
                                        text = "Top ${String.format("%.1f", 100f - result.estimatedPercentile)}% Peer Tier (${String.format("%.1f", result.estimatedPercentile)}th Percentile)",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }

                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                    shape = MaterialTheme.shapes.medium,
                                    modifier = Modifier.size(54.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.EmojiEvents,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(Spacing.MD))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            Spacer(Modifier.height(Spacing.MD))

                            // Metric Counters Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                ResultMetricItem(
                                    label = "Correct (+4)",
                                    value = "${result.correctAnswers}",
                                    color = ExamPrepColors.Success
                                )
                                ResultMetricItem(
                                    label = "Incorrect (-1)",
                                    value = "${result.incorrectAnswers}",
                                    color = ExamPrepColors.Error
                                )
                                ResultMetricItem(
                                    label = "Unattempted",
                                    value = "${result.unattemptedQuestions}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                ResultMetricItem(
                                    label = "Accuracy",
                                    value = "${result.overallAccuracy.toInt()}%",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // ── Section-wise Score Breakdown ─────────────────────────────────────
                item {
                    Text(
                        text = "Section Performance & Negative Marks Breakdown",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(
                            modifier = Modifier.padding(Spacing.LG),
                            verticalArrangement = Arrangement.spacedBy(Spacing.MD)
                        ) {
                            result.sectionResults.forEach { sec ->
                                SectionResultRow(section = sec)
                            }
                        }
                    }
                }

                // ── Solutions & Explanations ─────────────────────────────────────────
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Question Solutions & Explanations",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    }
                }

                // Filter Chips (All, Correct, Incorrect, Unattempted)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SolutionFilterChip(
                            label = "All (${result.questions.size})",
                            isSelected = uiState.selectedFilter == SolutionFilter.ALL,
                            onClick = { viewModel.setFilter(SolutionFilter.ALL) }
                        )
                        SolutionFilterChip(
                            label = "Correct (${result.correctAnswers})",
                            isSelected = uiState.selectedFilter == SolutionFilter.CORRECT,
                            onClick = { viewModel.setFilter(SolutionFilter.CORRECT) }
                        )
                        SolutionFilterChip(
                            label = "Incorrect (${result.incorrectAnswers})",
                            isSelected = uiState.selectedFilter == SolutionFilter.INCORRECT,
                            onClick = { viewModel.setFilter(SolutionFilter.INCORRECT) }
                        )
                    }
                }

                // Question Solution Cards
                val filteredQuestions = result.questions.filter { q ->
                    val userAns = result.userAnswers[q.id]
                    val correctOpt = q.options.find { it.isCorrect }
                    when (uiState.selectedFilter) {
                        SolutionFilter.ALL -> true
                        SolutionFilter.CORRECT -> userAns != null && userAns == correctOpt?.id
                        SolutionFilter.INCORRECT -> userAns != null && userAns != correctOpt?.id
                        SolutionFilter.UNATTEMPTED -> userAns == null
                    }
                }

                itemsIndexed(filteredQuestions) { index, question ->
                    val userAnsId = result.userAnswers[question.id]
                    val correctOpt = question.options.find { it.isCorrect }
                    val isCorrect = userAnsId != null && userAnsId == correctOpt?.id
                    val isUnattempted = userAnsId == null

                    MockSolutionCard(
                        questionNumber = index + 1,
                        question = question,
                        userAnswerId = userAnsId,
                        isCorrect = isCorrect,
                        isUnattempted = isUnattempted
                    )
                }

                item {
                    Spacer(Modifier.height(Spacing.XL))
                }
            }
        }
    }
}

@Composable
private fun ResultMetricItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = color
            )
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

@Composable
private fun SectionResultRow(section: MockSectionResult) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = section.sectionName,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "${section.marksObtained} / ${section.totalPossibleMarks} marks",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (section.marksObtained >= 0) ExamPrepColors.Success else ExamPrepColors.Error
                )
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${section.correctCount} Correct (+${section.correctCount * 4}) · ${section.incorrectCount} Incorrect (-${section.incorrectCount})",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Text(
                text = "${section.accuracy.toInt()}% accuracy",
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary)
            )
        }
        LinearProgressIndicator(
            progress = { (section.accuracy / 100f).coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(4.dp),
            color = if (section.accuracy >= 70f) ExamPrepColors.Success else ExamPrepColors.Amber70,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun SolutionFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.border(
            width = 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
            shape = MaterialTheme.shapes.small
        )
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@Composable
private fun MockSolutionCard(
    questionNumber: Int,
    question: Question,
    userAnswerId: String?,
    isCorrect: Boolean,
    isUnattempted: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                when {
                    isCorrect -> ExamPrepColors.Success.copy(alpha = 0.5f)
                    isUnattempted -> MaterialTheme.colorScheme.outlineVariant
                    else -> ExamPrepColors.Error.copy(alpha = 0.5f)
                },
                MaterialTheme.shapes.medium
            ),
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
                    text = "Question $questionNumber",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                Surface(
                    color = when {
                        isCorrect -> ExamPrepColors.Success.copy(alpha = 0.15f)
                        isUnattempted -> MaterialTheme.colorScheme.surfaceVariant
                        else -> ExamPrepColors.Error.copy(alpha = 0.15f)
                    },
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Text(
                        text = when {
                            isCorrect -> "+4 Correct"
                            isUnattempted -> "0 Skipped"
                            else -> "-1 Incorrect"
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isCorrect -> ExamPrepColors.Success
                                isUnattempted -> MaterialTheme.colorScheme.onSurfaceVariant
                                else -> ExamPrepColors.Error
                            }
                        )
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = question.questionText,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
            )

            Spacer(Modifier.height(10.dp))

            // Options with highlight
            question.options.forEachIndexed { optIndex, option ->
                val isCorrectOption = option.isCorrect
                val isSelectedByUser = userAnswerId == option.id

                Surface(
                    color = when {
                        isCorrectOption -> ExamPrepColors.Success.copy(alpha = 0.15f)
                        isSelectedByUser -> ExamPrepColors.Error.copy(alpha = 0.15f)
                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    },
                    shape = MaterialTheme.shapes.extraSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .border(
                            1.dp,
                            when {
                                isCorrectOption -> ExamPrepColors.Success
                                isSelectedByUser -> ExamPrepColors.Error
                                else -> Color.Transparent
                            },
                            MaterialTheme.shapes.extraSmall
                        )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${('A' + optIndex)}. ${option.text}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (isCorrectOption || isSelectedByUser) FontWeight.Bold else FontWeight.Normal,
                                color = when {
                                    isCorrectOption -> ExamPrepColors.Success
                                    isSelectedByUser -> ExamPrepColors.Error
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        if (isCorrectOption) {
                            Text("✓ Correct", style = MaterialTheme.typography.labelSmall.copy(color = ExamPrepColors.Success, fontWeight = FontWeight.Bold))
                        } else if (isSelectedByUser) {
                            Text("✗ Your Pick", style = MaterialTheme.typography.labelSmall.copy(color = ExamPrepColors.Error, fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }

            if (!question.explanation.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "💡 Step-by-Step Solution:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = question.explanation ?: "",
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
