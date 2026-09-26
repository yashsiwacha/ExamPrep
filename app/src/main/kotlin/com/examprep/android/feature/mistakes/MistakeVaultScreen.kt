package com.examprep.android.feature.mistakes

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.examprep.core.designsystem.theme.ExamPrepColors
import com.examprep.core.designsystem.theme.Spacing
import com.examprep.domain.model.MistakeItem
import com.examprep.domain.model.MistakeMasteryStatus
import com.examprep.domain.model.MistakeReason

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MistakeVaultScreen(
    onNavigateBack: () -> Unit,
    viewModel: MistakeVaultViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var expandedMistakeId by remember { mutableStateOf<String?>(null) }
    var showSprintSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Mistake Vault",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Smart Error Remediation & Mastery",
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
                    IconButton(onClick = {
                        viewModel.startSprint()
                        showSprintSheet = true
                    }) {
                        Icon(Icons.Default.FlashOn, contentDescription = "Sprint", tint = ExamPrepColors.Warning)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.MD, vertical = Spacing.SM)
                        .navigationBarsPadding()
                ) {
                    Button(
                        onClick = {
                            viewModel.startSprint()
                            showSprintSheet = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = ExamPrepColors.Primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Start 5-Min Remediation Sprint (${state.activeCount} Active)",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(Spacing.MD),
            verticalArrangement = Arrangement.spacedBy(Spacing.MD)
        ) {
            // Hero Mastery Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(Spacing.MD)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Error Resolution Rate",
                                    style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                                Text(
                                    "${state.masteryPercentage.toInt()}% Mastered",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (state.masteryPercentage > 60f) ExamPrepColors.Success else ExamPrepColors.Warning
                                    )
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(ExamPrepColors.Primary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = ExamPrepColors.Primary,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(Spacing.SM))
                        LinearProgressIndicator(
                            progress = { state.masteryPercentage / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = ExamPrepColors.Success,
                            trackColor = MaterialTheme.colorScheme.surface
                        )

                        Spacer(Modifier.height(Spacing.SM))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Total Errors: ${state.totalCount}",
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Text(
                                "Pending: ${state.activeCount}",
                                style = MaterialTheme.typography.labelSmall.copy(color = ExamPrepColors.Warning, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                "Mastered: ${state.masteredCount}",
                                style = MaterialTheme.typography.labelSmall.copy(color = ExamPrepColors.Success, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            // Subject Filter Row
            item {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.XS)) {
                    Text(
                        "Filter by Subject",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.SM)
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
                }
            }

            // Status Filter Row
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.SM)
                ) {
                    val statuses = listOf("All", "Active", "Reviewing", "Mastered")
                    items(statuses) { st ->
                        FilterChip(
                            selected = state.selectedStatus == st,
                            onClick = { viewModel.setStatus(st) },
                            label = { Text(st) }
                        )
                    }
                }
            }

            // List of Mistakes
            if (state.filteredMistakes.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.CheckCircleOutline,
                                contentDescription = null,
                                modifier = Modifier.size(56.dp),
                                tint = ExamPrepColors.Success
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "No errors found in this filter!",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                "Keep practicing quizzes to strengthen your retention.",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                }
            } else {
                items(state.filteredMistakes, key = { it.id }) { mistake ->
                    MistakeCard(
                        mistake = mistake,
                        isExpanded = expandedMistakeId == mistake.id,
                        onToggleExpand = {
                            expandedMistakeId = if (expandedMistakeId == mistake.id) null else mistake.id
                        },
                        onMarkMastered = {
                            viewModel.markPracticed(mistake.id, true)
                        },
                        onUpdateReason = { reason ->
                            viewModel.updateReason(mistake.id, reason)
                        }
                    )
                }
            }
        }
    }

    // Sprint Bottom Sheet / Dialog
    if (showSprintSheet) {
        val q = state.activeSprintQuestion
        AlertDialog(
            onDismissRequest = {
                showSprintSheet = false
                viewModel.closeSprint()
            },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (state.isSprintFinished) "Sprint Completed! 🎉" else "Mistake Sprint (${state.sprintIndex + 1}/${state.sprintTotal})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            },
            text = {
                if (state.isSprintFinished) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Celebration,
                            contentDescription = null,
                            tint = ExamPrepColors.Success,
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(Modifier.height(Spacing.SM))
                        Text(
                            "Great job reinforcing weak concepts!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "Your mistake mastery score has been updated.",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else if (q != null) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(Spacing.SM)
                    ) {
                        Text(
                            text = "[${q.subject} • ${q.chapterName}]",
                            style = MaterialTheme.typography.labelSmall.copy(color = ExamPrepColors.Primary, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = q.questionText,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                        )
                        Spacer(Modifier.height(Spacing.XS))

                        q.options.forEach { opt ->
                            val isSelected = state.sprintSelectedOption == opt
                            val isCorrect = opt == q.correctAnswer
                            val isChecked = state.isSprintAnswerChecked

                            val cardBg = when {
                                isChecked && isCorrect -> ExamPrepColors.Success.copy(alpha = 0.15f)
                                isChecked && isSelected && !isCorrect -> ExamPrepColors.Error.copy(alpha = 0.15f)
                                isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                else -> MaterialTheme.colorScheme.surface
                            }
                            val borderCol = when {
                                isChecked && isCorrect -> ExamPrepColors.Success
                                isChecked && isSelected && !isCorrect -> ExamPrepColors.Error
                                isSelected -> ExamPrepColors.Primary
                                else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = !isChecked) {
                                        viewModel.selectSprintOption(opt)
                                    }
                                    .border(1.dp, borderCol, RoundedCornerShape(8.dp)),
                                colors = CardDefaults.cardColors(containerColor = cardBg),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        opt,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                }
                            }
                        }

                        if (state.isSprintAnswerChecked && q.explanation != null) {
                            Spacer(Modifier.height(Spacing.XS))
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "💡 ${q.explanation}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (state.isSprintFinished) {
                    Button(onClick = {
                        showSprintSheet = false
                        viewModel.closeSprint()
                    }) {
                        Text("Finish")
                    }
                } else if (!state.isSprintAnswerChecked) {
                    Button(
                        onClick = { viewModel.checkSprintAnswer() },
                        enabled = state.sprintSelectedOption != null
                    ) {
                        Text("Check Answer")
                    }
                } else {
                    Button(onClick = { viewModel.nextSprintQuestion() }) {
                        Text("Next Question")
                    }
                }
            },
            dismissButton = {
                if (!state.isSprintFinished) {
                    TextButton(onClick = {
                        showSprintSheet = false
                        viewModel.closeSprint()
                    }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
}

@Composable
private fun MistakeCard(
    mistake: MistakeItem,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onMarkMastered: () -> Unit,
    onUpdateReason: (MistakeReason) -> Unit
) {
    val statusColor = when (mistake.masteryStatus) {
        MistakeMasteryStatus.MASTERED -> ExamPrepColors.Success
        MistakeMasteryStatus.REVIEWING -> ExamPrepColors.Warning
        MistakeMasteryStatus.ACTIVE -> ExamPrepColors.Error
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleExpand() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(Spacing.MD)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.XS),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = ExamPrepColors.Primary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            mistake.subject,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ExamPrepColors.Primary,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            mistake.chapterName,
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        mistake.masteryStatus.name,
                        style = MaterialTheme.typography.labelSmall.copy(color = statusColor, fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(Modifier.height(Spacing.SM))
            Text(
                text = mistake.questionText,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )

            if (!isExpanded) {
                Spacer(Modifier.height(Spacing.XS))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Reason: ${mistake.reason.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }}",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Text(
                        "Tap to review solution ▾",
                        style = MaterialTheme.typography.labelSmall.copy(color = ExamPrepColors.Primary, fontWeight = FontWeight.SemiBold)
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Spacing.MD),
                    verticalArrangement = Arrangement.spacedBy(Spacing.SM)
                ) {
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Student Answer vs Correct
                    if (mistake.studentAnswer != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = ExamPrepColors.Error, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "Your Choice: ${mistake.studentAnswer}",
                                style = MaterialTheme.typography.bodySmall.copy(color = ExamPrepColors.Error)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = ExamPrepColors.Success, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Correct Answer: ${mistake.correctAnswer}",
                            style = MaterialTheme.typography.bodySmall.copy(color = ExamPrepColors.Success, fontWeight = FontWeight.Bold)
                        )
                    }

                    val expl = mistake.explanation
                    if (!expl.isNullOrBlank()) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(Spacing.SM)) {
                                Text(
                                    "Step-by-Step Solution:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    expl,
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }
                    }

                    // Reason tagging row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Mistake Cause:", style = MaterialTheme.typography.labelSmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            MistakeReason.values().take(3).forEach { reason ->
                                AssistChip(
                                    onClick = { onUpdateReason(reason) },
                                    label = { Text(reason.name.take(4), style = MaterialTheme.typography.labelSmall) },
                                    leadingIcon = if (mistake.reason == reason) {
                                        { Icon(Icons.Default.Check, null, Modifier.size(12.dp)) }
                                    } else null
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = onMarkMastered,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp), tint = ExamPrepColors.Success)
                            Spacer(Modifier.width(4.dp))
                            Text("Mark as Mastered", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}
