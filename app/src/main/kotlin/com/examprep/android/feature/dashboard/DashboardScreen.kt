package com.examprep.android.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.examprep.core.designsystem.theme.ExamPrepColors
import com.examprep.core.designsystem.theme.Spacing
import com.examprep.domain.model.StudyTask

/**
 * Dashboard — the primary daily driver screen.
 *
 * Designed with a calm, premium aesthetic for student focus, daily habit
 * retention loops, peer percentile tracking, and 1-tap focus workflows.
 */
@Composable
fun DashboardScreen(
    onStartFocusSession: (String) -> Unit,
    onNavigateToTimeline: () -> Unit,
    onNavigateToPractice: () -> Unit = {},
    onNavigateToRevision: () -> Unit = {},
    onNavigateToSyllabus: () -> Unit = {},
    onNavigateToPerformance: () -> Unit = {},
    onStartMockTest: () -> Unit = {},
    onNavigateToMistakeVault: () -> Unit = {},
    onNavigateToFlashcards: () -> Unit = {},
    onNavigateToRoadmap: () -> Unit = {},
    onNavigateToLeaderboard: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    when (val uiState = state) {
        is DashboardUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Text("Loading your preparation space...", style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                }
            }
        }
        is DashboardUiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error loading dashboard: ${uiState.message}")
            }
        }
        is DashboardUiState.Success -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.MD)
            ) {
                Spacer(Modifier.height(Spacing.MD))

                // ── Student Greeting & Header ─────────────────────────────────────────
                StudentGreetingHeader(examName = uiState.examName)

                Spacer(Modifier.height(Spacing.MD))

                // ── Daily Momentum & Percentile Benchmark (Viral Growth Flywheel) ─────
                DailyMomentumCard(
                    streakDays = uiState.studyStreakDays,
                    quizAccuracy = uiState.quizAccuracy
                )

                Spacer(Modifier.height(Spacing.MD))

                // ── Today's Primary Target ────────────────────────────────────────────
                TodayTargetCard(
                    task = uiState.currentTask,
                    onStartFocusSession = {
                        val id = uiState.currentTask?.id ?: "task_sample"
                        onStartFocusSession(id)
                    }
                )

                Spacer(Modifier.height(Spacing.MD))

                // ── Full-Length Mock Exam Banner ───────────────────────────────────────
                MockExamBannerCard(
                    onStartMockTest = onStartMockTest
                )

                Spacer(Modifier.height(Spacing.MD))

                // ── High-Yield Study Arsenal Grid ─────────────────────────────────────
                PreparationArsenalGrid(
                    onMistakeVault = onNavigateToMistakeVault,
                    onFlashcards = onNavigateToFlashcards,
                    onRoadmap = onNavigateToRoadmap,
                    onLeaderboard = onNavigateToLeaderboard
                )

                Spacer(Modifier.height(Spacing.MD))

                // ── Exam Countdown & Milestone ────────────────────────────────────────
                ExamCountdownCard(
                    examName = uiState.examName,
                    daysRemaining = uiState.daysRemaining,
                    syllabusPercent = uiState.syllabusProgressPercentage
                )

                Spacer(Modifier.height(Spacing.MD))

                // ── Overall Progress Overview ─────────────────────────────────────────
                ProgressOverviewCard(
                    syllabusPercent = uiState.syllabusProgressPercentage,
                    studyMinutes = uiState.todayMinutesStudied,
                    quizAccuracy = uiState.quizAccuracy,
                    streakDays = uiState.studyStreakDays,
                    onClick = onNavigateToPerformance
                )

                Spacer(Modifier.height(Spacing.MD))

                // ── Quick Study Actions ───────────────────────────────────────────────
                QuickActionsSection(
                    onQuickQuiz = onNavigateToPractice,
                    onRevise = onNavigateToRevision,
                    onSyllabus = onNavigateToSyllabus,
                    onTimeline = onNavigateToTimeline
                )

                Spacer(Modifier.height(Spacing.XL))
            }
        }
    }
}

@Composable
private fun PreparationArsenalGrid(
    onMistakeVault: () -> Unit,
    onFlashcards: () -> Unit,
    onRoadmap: () -> Unit,
    onLeaderboard: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Preparation Arsenal",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        Text(
            text = "High-impact tools to accelerate your rank",
            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
        Spacer(Modifier.height(Spacing.SM))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.SM)
        ) {
            ArsenalActionCard(
                title = "Mistake Vault",
                subtitle = "Error Book & Remediation",
                icon = Icons.Default.Replay,
                badge = "4 Active",
                badgeColor = ExamPrepColors.Warning,
                modifier = Modifier.weight(1f),
                onClick = onMistakeVault
            )
            ArsenalActionCard(
                title = "Flashcards",
                subtitle = "Formula & Concept Decks",
                icon = Icons.Default.FlashOn,
                badge = "9 Decks",
                badgeColor = ExamPrepColors.Primary,
                modifier = Modifier.weight(1f),
                onClick = onFlashcards
            )
        }

        Spacer(Modifier.height(Spacing.SM))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.SM)
        ) {
            ArsenalActionCard(
                title = "AI Roadmap",
                subtitle = "Daily Goal Checklist",
                icon = Icons.Default.Map,
                badge = "114 Days",
                badgeColor = ExamPrepColors.Success,
                modifier = Modifier.weight(1f),
                onClick = onRoadmap
            )
            ArsenalActionCard(
                title = "Leaderboard",
                subtitle = "National Percentile & AIR",
                icon = Icons.Default.Leaderboard,
                badge = "97.1 %ile",
                badgeColor = ExamPrepColors.Primary,
                modifier = Modifier.weight(1f),
                onClick = onLeaderboard
            )
        }
    }
}

@Composable
private fun ArsenalActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badge: String,
    badgeColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(Spacing.MD)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(badgeColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = badgeColor, modifier = Modifier.size(20.dp))
                }
                Surface(
                    color = badgeColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = badgeColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(Modifier.height(Spacing.SM))

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun StudentGreetingHeader(examName: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Welcome back, Aspirant",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Text(
                text = "Targeting $examName",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
            shape = MaterialTheme.shapes.small
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    Icons.Default.School,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "2026 Batch",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Composable
private fun DailyMomentumCard(
    streakDays: Int,
    quizAccuracy: Float
) {
    val percentile = if (quizAccuracy > 0) (85 + (quizAccuracy * 0.14f)).toInt().coerceIn(75, 99) else 88

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier.padding(Spacing.MD).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.MD)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🔥", style = MaterialTheme.typography.titleLarge)
                    }
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "$streakDays-Day Streak",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Surface(
                            color = ExamPrepColors.Success.copy(alpha = 0.15f),
                            shape = MaterialTheme.shapes.extraSmall
                        ) {
                            Text(
                                text = "Top $percentile% Tier",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ExamPrepColors.Success,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                    Text(
                        text = "Consistency multiplier active · Study daily to keep shield",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun TodayTargetCard(
    task: StudyTask?,
    onStartFocusSession: () -> Unit
) {
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
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        shape = MaterialTheme.shapes.extraSmall,
                        modifier = Modifier.size(8.dp)
                    ) {}
                    Text(
                        text = "TODAY'S TARGET",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "Priority: ${task?.priority?.name ?: "HIGH"}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(Modifier.height(Spacing.SM))

            Text(
                text = task?.notes ?: "Core Concept Learning & Active Recall",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Text(
                text = "Target Duration: ${task?.estimatedMinutes ?: 50} mins · Structured Pomodoro",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(Modifier.height(Spacing.MD))

            LinearProgressIndicator(
                progress = { ((task?.completionPercentage ?: 0f) / 100f).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            Spacer(Modifier.height(Spacing.MD))

            Button(
                onClick = onStartFocusSession,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Start Focus Session", style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}

@Composable
private fun ExamCountdownCard(
    examName: String,
    daysRemaining: Int,
    syllabusPercent: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier.padding(Spacing.LG),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "TARGET COUNTDOWN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$daysRemaining",
                        style = MaterialTheme.typography.displaySmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "days remaining",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
                Text(
                    text = "${syllabusPercent.toInt()}% syllabus covered on schedule",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = ExamPrepColors.Success
                    )
                )
            }

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressOverviewCard(
    syllabusPercent: Float,
    studyMinutes: Int,
    quizAccuracy: Float,
    streakDays: Int,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
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
                    text = "PREPARATION VITAL SIGNS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Detailed Analytics",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "View Detailed Analytics",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Spacer(Modifier.height(Spacing.MD))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ProgressStat("${syllabusPercent.toInt()}%", "Syllabus\nMastery")
                ProgressStat("$streakDays", "Day\nStreak")
                ProgressStat(if (quizAccuracy > 0f) "${quizAccuracy.toInt()}%" else "--", "Quiz\nAccuracy")
                ProgressStat("${studyMinutes / 60}h ${studyMinutes % 60}m", "Study\nToday")
            }
        }
    }
}

@Composable
private fun ProgressStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            ),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun QuickActionsSection(
    onQuickQuiz: () -> Unit,
    onRevise: () -> Unit,
    onSyllabus: () -> Unit,
    onTimeline: () -> Unit
) {
    Text(
        text = "Quick Actions",
        style = MaterialTheme.typography.titleSmall.copy(
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
    )
    Spacer(Modifier.height(Spacing.SM))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.SM)
    ) {
        QuickActionCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Quiz,
            label = "Practice",
            onClick = onQuickQuiz
        )
        QuickActionCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Refresh,
            label = "Revise",
            onClick = onRevise
        )
        QuickActionCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.MenuBook,
            label = "Syllabus",
            onClick = onSyllabus
        )
        QuickActionCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Timeline,
            label = "Roadmap",
            onClick = onTimeline
        )
    }
}

@Composable
private fun QuickActionCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.medium),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
private fun MockExamBannerCard(
    onStartMockTest: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), MaterialTheme.shapes.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(Spacing.LG)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        color = ExamPrepColors.Amber70.copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.extraSmall
                    ) {
                        Text(
                            text = "NATIONAL SIMULATOR",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ExamPrepColors.Amber70,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        )
                    }
                }
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Text(
                        text = "+4 / -1 Marking",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(Modifier.height(Spacing.SM))

            Text(
                text = "All-India Full-Length Mock Exam #1",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Text(
                text = "3-Hour Timed Simulation · Physics, Chemistry & Mathematics · 300 Marks",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(Modifier.height(Spacing.MD))

            Button(
                onClick = onStartMockTest,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Launch 3-Hour Mock Exam", style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}

