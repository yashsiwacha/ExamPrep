package com.examprep.android.feature.leaderboard

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.examprep.core.designsystem.theme.ExamPrepColors
import com.examprep.core.designsystem.theme.Spacing
import com.examprep.domain.model.LeaderboardStudent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    onNavigateBack: () -> Unit,
    viewModel: LeaderboardViewModel = hiltViewModel()
) {
    val report by viewModel.reportState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "All-India Leaderboard",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${report.totalParticipants} Candidates Ranked",
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
                        Toast.makeText(context, "Scorecard link copied to clipboard!", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = ExamPrepColors.Primary)
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
            // Hero Score Comparison
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
                                    color = ExamPrepColors.Success.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        "PERCENTILE BENCHMARK",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = ExamPrepColors.Success,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "${report.userPercentile}%ile",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = ExamPrepColors.Primary
                                    )
                                )
                                Text(
                                    "Projected AIR: #${report.userRank} of ${report.totalParticipants}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(ExamPrepColors.Primary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = ExamPrepColors.Primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(Spacing.MD))

                        // Comparison Metrics Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ScoreMetricPill("Your Score", "${report.userScore}/300", ExamPrepColors.Primary)
                            ScoreMetricPill("AIR 1 Score", "${report.topperScore}/300", ExamPrepColors.Success)
                            ScoreMetricPill("National Avg", "${report.averageScore}/300", MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Leaderboard List Header
            item {
                Text(
                    text = "Top Ranks • All-India Standings",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            // Leaderboard Items
            items(report.leaderboard, key = { "${it.rank}_${it.name}" }) { student ->
                LeaderboardRow(student = student)
            }
        }
    }
}

@Composable
private fun ScoreMetricPill(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
            Text(value, style = MaterialTheme.typography.titleSmall.copy(color = color, fontWeight = FontWeight.Bold))
        }
    }
}

@Composable
private fun LeaderboardRow(student: LeaderboardStudent) {
    val isUser = student.isCurrentUser
    val cardBg = if (isUser)
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
    else MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isUser) Modifier.border(1.5.dp, ExamPrepColors.Primary, RoundedCornerShape(12.dp)) else Modifier
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isUser) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.MD),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Circle
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        when (student.rank) {
                            1 -> ExamPrepColors.Warning.copy(alpha = 0.2f)
                            2 -> MaterialTheme.colorScheme.surfaceVariant
                            3 -> ExamPrepColors.Primary.copy(alpha = 0.15f)
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#${student.rank}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = when (student.rank) {
                            1 -> ExamPrepColors.Warning
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = student.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isUser) FontWeight.Bold else FontWeight.SemiBold
                        )
                    )
                    Spacer(Modifier.width(6.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = student.badge,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ExamPrepColors.Primary,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
                Text(
                    text = "${student.stateLocation} • 🔥 ${student.streakDays}d Streak",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${student.score} pts",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isUser) ExamPrepColors.Primary else MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = "${student.accuracy.toInt()}% Acc",
                    style = MaterialTheme.typography.labelSmall.copy(color = ExamPrepColors.Success, fontWeight = FontWeight.Medium)
                )
            }
        }
    }
}
