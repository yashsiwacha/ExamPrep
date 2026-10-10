package com.examprep.android.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
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
import androidx.compose.foundation.BorderStroke
import androidx.hilt.navigation.compose.hiltViewModel
import com.examprep.android.ads.AdUnitIds
import com.examprep.android.ads.BannerAdView
import com.examprep.core.designsystem.component.ActionBannerCard
import com.examprep.core.designsystem.component.MetricStatBox
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
    onNavigateToProfile: () -> Unit = {},
    onNavigateToHeroFeature: () -> Unit = {},
    onNavigateToQuestionLibrary: () -> Unit = {},
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
            val currentUser by com.examprep.android.feature.auth.AuthStore.get().currentUser.collectAsState()
            val activeExamTarget = currentUser?.targetExam ?: uiState.examName

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.MD)
            ) {
                Spacer(Modifier.height(Spacing.MD))

                // ── Student Greeting & Header ─────────────────────────────────────────
                StudentGreetingHeader(
                    examName = uiState.examName,
                    onProfileClick = onNavigateToProfile
                )

                Spacer(Modifier.height(Spacing.MD))

                // ── Daily Momentum & Percentile Benchmark (Viral Growth Flywheel) ─────
                DailyMomentumCard(
                    streakDays = uiState.studyStreakDays,
                    quizAccuracy = uiState.quizAccuracy
                )

                Spacer(Modifier.height(Spacing.MD))

                // ── HERO FEATURE: AI WEAKNESS SURGERY & RANK ACCELERATOR ───────────────
                AIWeaknessSurgeryCard(
                    onClick = onNavigateToHeroFeature
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
                val mockBanner = getMockExamBannerConfig(uiState.examId, activeExamTarget)
                ActionBannerCard(
                    badgeText = mockBanner.badgeText,
                    badgeColor = ExamPrepColors.Amber70,
                    tagText = mockBanner.tagText,
                    title = mockBanner.title,
                    subtitle = mockBanner.subtitle,
                    buttonText = mockBanner.buttonText,
                    buttonIcon = Icons.Default.Speed,
                    onClick = onStartMockTest
                )

                Spacer(Modifier.height(Spacing.MD))

                // ── Offline Question Library (500 Questions per Exam) ────────────────
                ActionBannerCard(
                    badgeText = "OFFLINE PRACTICE • 500 QUESTIONS",
                    badgeColor = ExamPrepColors.Success,
                    tagText = "5 Papers × 100 Qs",
                    title = "Download Question Papers",
                    subtitle = "Fetch curated 100-question papers with answer keys & explanations for 100% offline study without internet.",
                    buttonText = "Explore Question Library",
                    buttonIcon = Icons.Default.Download,
                    onClick = onNavigateToQuestionLibrary
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

                // ── Sponsored AdMob Banner (Non-intrusive) ───────────────────────────
                DashboardAdBanner()

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
private fun StudentGreetingHeader(
    examName: String,
    onProfileClick: () -> Unit = {}
) {
    val currentUser by com.examprep.android.feature.auth.AuthStore.get().currentUser.collectAsState()
    val isPro = currentUser?.isPro == true

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Welcome back, ${currentUser?.name?.split(" ")?.firstOrNull() ?: "Aspirant"}",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Text(
                text = "Targeting ${currentUser?.targetExam ?: examName}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
        Surface(
            color = if (isPro) ExamPrepColors.Warning.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.clickable { onProfileClick() }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    if (isPro) Icons.Default.Star else Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (isPro) ExamPrepColors.Warning else MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (isPro) "👑 PRO" else "FREE",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = if (isPro) ExamPrepColors.Warning else MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
private fun AIWeaknessSurgeryCard(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(1.dp, ExamPrepColors.Warning.copy(alpha = 0.35f), MaterialTheme.shapes.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(Spacing.MD)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ExamPrepColors.Warning.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = ExamPrepColors.Warning, modifier = Modifier.size(18.dp))
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "AI Weakness Surgery",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Surface(
                                color = ExamPrepColors.Warning.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "PRO HERO",
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(color = ExamPrepColors.Warning, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                                )
                            }
                        }
                        Text(
                            text = "3 Critical Blindspots Detected · +3,850 AIR Boost",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
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
                    text = "${examName.uppercase()} COUNTDOWN",
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
                MetricStatBox(value = "${syllabusPercent.toInt()}%", label = "Syllabus\nMastery")
                MetricStatBox(value = "$streakDays", label = "Day\nStreak")
                MetricStatBox(value = if (quizAccuracy > 0f) "${quizAccuracy.toInt()}%" else "--", label = "Quiz\nAccuracy")
                MetricStatBox(value = "${studyMinutes / 60}h ${studyMinutes % 60}m", label = "Study\nToday")
            }
        }
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
            icon = Icons.AutoMirrored.Filled.MenuBook,
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
private fun DashboardAdBanner() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.SM),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "SPONSORED",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                letterSpacing = 1.sp
            ),
            modifier = Modifier.padding(bottom = 2.dp)
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                BannerAdView(adUnitId = AdUnitIds.BANNER_DASHBOARD)
            }
        }
    }
}

private data class MockExamBannerConfig(
    val badgeText: String,
    val tagText: String,
    val title: String,
    val subtitle: String,
    val buttonText: String
)

private fun getMockExamBannerConfig(examId: String?, examName: String): MockExamBannerConfig {
    val id = examId?.lowercase() ?: ""
    val name = examName.lowercase()
    return when {
        id == "exam_jee_main" || name.contains("jee") -> MockExamBannerConfig(
            badgeText = "NATIONAL SIMULATOR • 75 QUESTIONS",
            tagText = "+4 / -1 Marking · 300 Marks",
            title = "JEE Main All-India Mock Exam #1",
            subtitle = "180-Min Timed Simulation · 25 Physics, 25 Chemistry, 25 Mathematics · Standard NTA Pattern",
            buttonText = "Launch 180-Min Mock Exam (75 Qs)"
        )
        id == "exam_neet_ug" || name.contains("neet") -> MockExamBannerConfig(
            badgeText = "NATIONAL SIMULATOR • 180 QUESTIONS",
            tagText = "+4 / -1 Marking · 720 Marks",
            title = "NEET UG All-India Mock Exam #1",
            subtitle = "200-Min Timed Simulation · 45 Physics, 45 Chemistry, 90 Biology · Standard NTA Pattern",
            buttonText = "Launch 200-Min Mock Exam (180 Qs)"
        )
        id == "exam_upsc_cse" || name.contains("upsc") -> MockExamBannerConfig(
            badgeText = "NATIONAL SIMULATOR • 100 QUESTIONS",
            tagText = "+2 / -0.66 Marking · 200 Marks",
            title = "UPSC CSE Prelims GS Paper I Mock #1",
            subtitle = "120-Min Timed Simulation · General Studies · 200 Marks · Standard UPSC Pattern",
            buttonText = "Launch 120-Min Mock Exam (100 Qs)"
        )
        id == "exam_cat" || name.contains("cat") -> MockExamBannerConfig(
            badgeText = "NATIONAL SIMULATOR • 66 QUESTIONS",
            tagText = "+3 / -1 Marking · 198 Marks",
            title = "CAT All-India Mock Exam #1",
            subtitle = "120-Min Timed Simulation · 24 VARC, 20 DILR, 22 QA · Standard IIM Pattern",
            buttonText = "Launch 120-Min Mock Exam (66 Qs)"
        )
        id == "exam_gate_cs" || name.contains("gate") -> MockExamBannerConfig(
            badgeText = "NATIONAL SIMULATOR • 65 QUESTIONS",
            tagText = "+2 / -0.66 Marking · 100 Marks",
            title = "GATE CS All-India Mock Exam #1",
            subtitle = "180-Min Timed Simulation · 15 GA/Maths, 50 CS Core · Standard IIT Pattern",
            buttonText = "Launch 180-Min Mock Exam (65 Qs)"
        )
        else -> MockExamBannerConfig(
            badgeText = "NATIONAL SIMULATOR",
            tagText = "Standard Marking",
            title = "$examName All-India Mock Exam #1",
            subtitle = "Timed Full-Length National Simulation with Standard Exam Pattern & Difficulty",
            buttonText = "Launch Full-Length Mock Exam"
        )
    }
}



