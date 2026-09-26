package com.examprep.android.feature.mocktest

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.examprep.domain.model.MockQuestionStatus
import com.examprep.domain.model.Question
import com.examprep.domain.model.QuestionOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MockTestScreen(
    onTestSubmitted: (attemptId: String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: MockTestViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showPaletteSheet by remember { mutableStateOf(false) }

    // Intercept back button to confirm before leaving active test
    BackHandler {
        viewModel.showSubmitConfirmation(true)
    }

    if (uiState.showSubmitDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showSubmitConfirmation(false) },
            title = {
                Text(
                    text = "Submit Mock Test?",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Are you sure you want to finish your test? Here is your summary:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(Spacing.MD), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Questions:", style = MaterialTheme.typography.bodySmall)
                                Text("${uiState.totalQuestionsCount}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            }
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Answered:", style = MaterialTheme.typography.bodySmall)
                                Text("${uiState.answeredCount}", color = ExamPrepColors.Success, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            }
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Marked for Review:", style = MaterialTheme.typography.bodySmall)
                                Text("${uiState.markedCount}", color = ExamPrepColors.Amber70, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            }
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Unattempted:", style = MaterialTheme.typography.bodySmall)
                                Text("${uiState.unattemptedCount}", color = ExamPrepColors.Error, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                    Text(
                        text = "⚠️ Marking Scheme: +4 for Correct, -1 for Incorrect.",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitTest(onTestSubmitted)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Yes, Final Submit")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.showSubmitConfirmation(false) }) {
                    Text("Resume Test")
                }
            }
        )
    }

    if (showPaletteSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPaletteSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.LG, vertical = Spacing.MD)
            ) {
                Text(
                    text = "Question Palette & Navigator",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(Modifier.height(Spacing.SM))

                // Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PaletteLegendItem(MockQuestionStatus.ANSWERED, "Answered")
                    PaletteLegendItem(MockQuestionStatus.MARKED_FOR_REVIEW, "Review")
                    PaletteLegendItem(MockQuestionStatus.NOT_ANSWERED, "Skipped")
                    PaletteLegendItem(MockQuestionStatus.NOT_VISITED, "Unvisited")
                }

                Spacer(Modifier.height(Spacing.MD))

                val currentSec = uiState.currentSection
                if (currentSec != null) {
                    Text(
                        text = "Section: ${currentSec.name}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(Modifier.height(Spacing.SM))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.heightIn(max = 280.dp)
                    ) {
                        itemsIndexed(currentSec.questions) { qIdx, question ->
                            val status = uiState.getQuestionStatus(question.id)
                            val isSelected = qIdx == uiState.currentQuestionIndex
                            PaletteNumberTile(
                                number = qIdx + 1,
                                status = status,
                                isSelected = isSelected,
                                onClick = {
                                    viewModel.jumpToQuestion(uiState.currentSectionIndex, qIdx)
                                    showPaletteSheet = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(Spacing.LG))
            }
        }
    }

    Scaffold(
        topBar = {
            MockTestTopBar(
                title = uiState.mockTest?.title ?: "Full Mock Test",
                remainingSeconds = uiState.remainingSeconds,
                onPaletteClick = { showPaletteSheet = true },
                onSubmitClick = { viewModel.showSubmitConfirmation(true) },
                onBackClick = { viewModel.showSubmitConfirmation(true) }
            )
        },
        bottomBar = {
            MockTestBottomBar(
                canGoPrevious = uiState.currentQuestionIndex > 0 || uiState.currentSectionIndex > 0,
                isMarkedForReview = uiState.currentQuestion?.id?.let { uiState.markedForReview.contains(it) } == true,
                hasSelectedAnswer = uiState.currentQuestion?.id?.let { uiState.userAnswers.containsKey(it) } == true,
                onPrevious = { viewModel.previousQuestion() },
                onClearResponse = {
                    uiState.currentQuestion?.id?.let { viewModel.clearResponse(it) }
                },
                onToggleMarkReview = {
                    uiState.currentQuestion?.id?.let { viewModel.toggleMarkForReview(it) }
                },
                onSaveAndNext = { viewModel.nextQuestion() }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
            ) {
                // Section Tabs (Physics, Chemistry, Mathematics)
                uiState.mockTest?.let { test ->
                    SectionTabBar(
                        sections = test.sections.map { it.name },
                        selectedIndex = uiState.currentSectionIndex,
                        onSectionSelected = { viewModel.selectSection(it) }
                    )
                }

                val currentQ = uiState.currentQuestion
                if (currentQ != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = Spacing.MD, vertical = Spacing.SM)
                    ) {
                        // Question Header Info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Question ${uiState.currentQuestionIndex + 1} of ${uiState.currentSection?.questions?.size ?: 0}",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            )
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                shape = MaterialTheme.shapes.extraSmall
                            ) {
                                Text(
                                    text = "+4 / -1 Marking",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Spacer(Modifier.height(Spacing.MD))

                        // Question Prompt Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = MaterialTheme.shapes.large
                        ) {
                            Column(modifier = Modifier.padding(Spacing.LG)) {
                                Text(
                                    text = currentQ.questionText,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 26.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }

                        Spacer(Modifier.height(Spacing.MD))

                        // Options Area
                        Text(
                            text = "SELECT ONE OPTION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        )
                        Spacer(Modifier.height(Spacing.SM))

                        val selectedOptionId = uiState.userAnswers[currentQ.id]

                        currentQ.options.forEachIndexed { optIndex, option ->
                            val isSelected = selectedOptionId == option.id
                            MockOptionCard(
                                letter = ('A' + optIndex).toString(),
                                text = option.text,
                                isSelected = isSelected,
                                onClick = { viewModel.selectOption(currentQ.id, option.id) }
                            )
                            Spacer(Modifier.height(8.dp))
                        }

                        Spacer(Modifier.height(Spacing.XL))
                    }
                }
            }
        }
    }
}

@Composable
private fun MockTestTopBar(
    title: String,
    remainingSeconds: Long,
    onPaletteClick: () -> Unit,
    onSubmitClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val hours = remainingSeconds / 3600
    val minutes = (remainingSeconds % 3600) / 60
    val seconds = remainingSeconds % 60
    val timerText = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    val isTimerLow = remainingSeconds < 300 // under 5 minutes

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        modifier = Modifier.fillMaxWidth().border(0.dp, Color.Transparent, MaterialTheme.shapes.extraSmall)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = Spacing.MD, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onBackClick, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Exit Test", tint = MaterialTheme.colorScheme.onSurface)
                }
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1
                    )
                    Text(
                        text = "National Simulation Mode",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Countdown Timer Pill
                Surface(
                    color = if (isTimerLow) ExamPrepColors.Error.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (isTimerLow) ExamPrepColors.Error else MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = timerText,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isTimerLow) ExamPrepColors.Error else MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }

                // Grid Palette Icon
                IconButton(onClick = onPaletteClick, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.GridView, contentDescription = "Palette", tint = MaterialTheme.colorScheme.onSurface)
                }

                // Submit Button
                Button(
                    onClick = onSubmitClick,
                    colors = ButtonDefaults.buttonColors(containerColor = ExamPrepColors.Success),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text("Submit", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}

@Composable
private fun SectionTabBar(
    sections: List<String>,
    selectedIndex: Int,
    onSectionSelected: (Int) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        modifier = Modifier.fillMaxWidth()
    ) {
        ScrollableTabRow(
            selectedTabIndex = selectedIndex,
            edgePadding = Spacing.MD,
            divider = {}
        ) {
            sections.forEachIndexed { index, name ->
                Tab(
                    selected = index == selectedIndex,
                    onClick = { onSectionSelected(index) },
                    text = {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = if (index == selectedIndex) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun MockOptionCard(
    letter: String,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                shape = MaterialTheme.shapes.medium
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.MD, vertical = 12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.MD)
        ) {
            Surface(
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.extraSmall,
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = letter,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.weight(1f)
            )

            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
private fun MockTestBottomBar(
    canGoPrevious: Boolean,
    isMarkedForReview: Boolean,
    hasSelectedAnswer: Boolean,
    onPrevious: () -> Unit,
    onClearResponse: () -> Unit,
    onToggleMarkReview: () -> Unit,
    onSaveAndNext: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth().border(0.dp, Color.Transparent, MaterialTheme.shapes.extraSmall)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = Spacing.MD, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (canGoPrevious) {
                    OutlinedButton(
                        onClick = onPrevious,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
                if (hasSelectedAnswer) {
                    TextButton(
                        onClick = onClearResponse,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Clear", style = MaterialTheme.typography.labelSmall)
                    }
                }
                OutlinedButton(
                    onClick = onToggleMarkReview,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    shape = MaterialTheme.shapes.small,
                    colors = if (isMarkedForReview) ButtonDefaults.outlinedButtonColors(contentColor = ExamPrepColors.Amber70) else ButtonDefaults.outlinedButtonColors()
                ) {
                    Text(if (isMarkedForReview) "Unmark" else "Review", style = MaterialTheme.typography.labelSmall)
                }
            }

            Button(
                onClick = onSaveAndNext,
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                shape = MaterialTheme.shapes.small
            ) {
                Text("Save & Next", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun PaletteLegendItem(status: MockQuestionStatus, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Surface(
            color = when (status) {
                MockQuestionStatus.ANSWERED -> ExamPrepColors.Success
                MockQuestionStatus.MARKED_FOR_REVIEW -> ExamPrepColors.Amber70
                MockQuestionStatus.NOT_ANSWERED -> ExamPrepColors.Error.copy(alpha = 0.3f)
                MockQuestionStatus.NOT_VISITED -> MaterialTheme.colorScheme.surfaceVariant
                MockQuestionStatus.ANSWERED_AND_MARKED_FOR_REVIEW -> ExamPrepColors.Amber70
            },
            shape = MaterialTheme.shapes.extraSmall,
            modifier = Modifier.size(10.dp)
        ) {}
        Text(label, style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
    }
}

@Composable
private fun PaletteNumberTile(
    number: Int,
    status: MockQuestionStatus,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = when (status) {
        MockQuestionStatus.ANSWERED -> ExamPrepColors.Success
        MockQuestionStatus.MARKED_FOR_REVIEW, MockQuestionStatus.ANSWERED_AND_MARKED_FOR_REVIEW -> ExamPrepColors.Amber70
        MockQuestionStatus.NOT_ANSWERED -> ExamPrepColors.Error.copy(alpha = 0.25f)
        MockQuestionStatus.NOT_VISITED -> MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = when (status) {
        MockQuestionStatus.ANSWERED, MockQuestionStatus.MARKED_FOR_REVIEW, MockQuestionStatus.ANSWERED_AND_MARKED_FOR_REVIEW -> Color.White
        else -> MaterialTheme.colorScheme.onSurface
    }

    Surface(
        onClick = onClick,
        color = bgColor,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .size(44.dp)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = MaterialTheme.shapes.small
            )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "$number",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = contentColor
                )
            )
        }
    }
}
