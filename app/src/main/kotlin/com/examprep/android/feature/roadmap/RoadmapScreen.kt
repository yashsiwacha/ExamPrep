package com.examprep.android.feature.roadmap

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.examprep.core.designsystem.theme.ExamPrepColors
import com.examprep.core.designsystem.theme.Spacing
import com.examprep.domain.model.DailyStudyTask
import com.examprep.domain.model.StudyRoadmapPlan
import com.examprep.domain.model.StudyTaskType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoadmapScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRoute: (String) -> Unit,
    viewModel: RoadmapViewModel = hiltViewModel()
) {
    val plan by viewModel.planState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "AI Study Roadmap",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${plan.daysRemaining} Days to ${plan.targetExamName}",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
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
            // Hero Pacing Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(Spacing.MD)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Surface(
                                    color = ExamPrepColors.Primary.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        "EXAM COUNTDOWN",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = ExamPrepColors.Primary,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "${plan.daysRemaining} Days Left",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(ExamPrepColors.Primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(Spacing.MD))
                        Text(
                            text = plan.weeklyPacingVerdict,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = ExamPrepColors.Success,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Spacer(Modifier.height(Spacing.SM))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Daily Target: ${plan.hoursStudiedToday}h / ${plan.dailyTargetHours}h",
                                style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Text(
                                "Syllabus: ${plan.syllabusCompletionPct}%",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = ExamPrepColors.Primary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Spacer(Modifier.height(Spacing.XS))
                        LinearProgressIndicator(
                            progress = { plan.hoursStudiedToday / plan.dailyTargetHours.coerceAtLeast(1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = ExamPrepColors.Primary,
                            trackColor = MaterialTheme.colorScheme.surface
                        )
                    }
                }
            }

            // Daily Tasks Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Today's High-Yield Plan",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        "${plan.dailyTasks.count { it.isCompleted }}/${plan.dailyTasks.size} Done",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ExamPrepColors.Success,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // Task List
            items(plan.dailyTasks, key = { it.id }) { task ->
                DailyTaskCard(
                    task = task,
                    onToggle = { viewModel.toggleTask(task.id) },
                    onLaunch = {
                        task.deepLinkRoute?.let { onNavigateToRoute(it) }
                    }
                )
            }

            // High Yield Focus Topic Chips
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(Spacing.MD)) {
                        Text(
                            "High Weightage Topic Radar",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            "AI-prioritized topics predicted to yield 40%+ of paper marks:",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Spacer(Modifier.height(Spacing.SM))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.XS)
                        ) {
                            plan.highPriorityTopics.forEach { topic ->
                                Surface(
                                    color = ExamPrepColors.Warning.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        topic,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyTaskCard(
    task: DailyStudyTask,
    onToggle: () -> Unit,
    onLaunch: () -> Unit
) {
    val taskIcon = when (task.taskType) {
        StudyTaskType.MOCK_TEST -> Icons.Default.Quiz
        StudyTaskType.QUIZ, StudyTaskType.SOLVE_PYQ -> Icons.Default.MenuBook
        StudyTaskType.REVISE -> Icons.Default.FlashOn
        StudyTaskType.REVIEW_MISTAKES -> Icons.Default.Replay
        StudyTaskType.LEARN, StudyTaskType.PRACTICE -> Icons.Default.LibraryBooks
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.MD),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() }
            )

            Spacer(Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.XS),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = ExamPrepColors.Primary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            task.subject,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ExamPrepColors.Primary,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                        )
                    }
                    Text(
                        "• ${task.estimatedMinutes} mins",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }

                Spacer(Modifier.height(2.dp))

                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.SemiBold,
                        color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                )

                Text(
                    text = task.priorityLabel,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else ExamPrepColors.Warning,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            if (!task.isCompleted && task.deepLinkRoute != null) {
                IconButton(onClick = onLaunch) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Start Task",
                        tint = ExamPrepColors.Primary
                    )
                }
            }
        }
    }
}
