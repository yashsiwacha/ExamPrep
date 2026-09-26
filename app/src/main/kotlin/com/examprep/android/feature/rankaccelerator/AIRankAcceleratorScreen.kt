package com.examprep.android.feature.rankaccelerator

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.examprep.android.feature.auth.AuthStore
import com.examprep.core.designsystem.theme.ExamPrepColors
import com.examprep.core.designsystem.theme.Spacing
import com.examprep.domain.model.AIRankAcceleratorReport
import com.examprep.domain.model.WeaknessSurgicalNode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIRankAcceleratorScreen(
    onNavigateBack: () -> Unit,
    onStartSurgicalDrill: (String) -> Unit = {},
    onUpgradeToPro: () -> Unit = {}
) {
    val report = remember { AIRankAcceleratorStore.get().report.value }
    val currentUser by AuthStore.get().currentUser.collectAsState()
    val isPro = currentUser?.isPro == true

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Weakness Surgery", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.MD)
        ) {
            Spacer(Modifier.height(Spacing.SM))

            // ── HERO BANNER: PROJECTED PERCENTILE JUMP ─────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ExamPrepColors.Warning.copy(alpha = 0.5f), MaterialTheme.shapes.large),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(Spacing.LG)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = ExamPrepColors.Warning.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "👑 PRO HERO ENGINE",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ExamPrepColors.Warning,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                            )
                        }
                        Text(
                            text = "AIR Forecast Engine",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }

                    Spacer(Modifier.height(Spacing.MD))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Current Est.", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            Text(
                                "${report.currentEstimatedPercentile}%ile",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = ExamPrepColors.Success, modifier = Modifier.size(28.dp))
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Post-Surgery Target", style = MaterialTheme.typography.labelSmall.copy(color = ExamPrepColors.Success))
                            Text(
                                "${report.projectedPercentileAfterSurgery}%ile",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ExamPrepColors.Success
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(Spacing.MD))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(Modifier.height(Spacing.MD))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Marks Lost to Traps", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            Text("-${report.totalMarksBleeding} Marks", style = MaterialTheme.typography.bodyMedium.copy(color = ExamPrepColors.Error, fontWeight = FontWeight.Bold))
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Predicted Rank Gain", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            Text("+${report.predictedAirGain} AIR Jump", style = MaterialTheme.typography.bodyMedium.copy(color = ExamPrepColors.Success, fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }

            Spacer(Modifier.height(Spacing.LG))

            // ── DIAGNOSED VULNERABILITY NODES ─────────────────────────────
            Text(
                text = "Identified Cognitive Traps",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Text(
                text = "Recurring error clusters detected across previous tests",
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )

            Spacer(Modifier.height(Spacing.SM))

            report.topVulnerabilities.forEach { node ->
                VulnerabilityCard(
                    node = node,
                    isPro = isPro,
                    onLaunchDrill = { onStartSurgicalDrill(node.surgicalActionQuizId) }
                )
                Spacer(Modifier.height(Spacing.SM))
            }

            Spacer(Modifier.height(Spacing.MD))

            // ── PRO PAYWALL GATE / ACTION BUTTON ───────────────────────────
            if (!isPro) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, ExamPrepColors.Warning, MaterialTheme.shapes.large),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.LG),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = ExamPrepColors.Warning, modifier = Modifier.size(32.dp))
                        Spacer(Modifier.height(Spacing.SM))
                        Text(
                            text = "Unlock AI Weakness Surgery (Pro)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Free tier provides full chapter practice & mock tests. Upgrade to Pro for custom AI surgical drills & rank forecasting.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            modifier = Modifier.padding(horizontal = Spacing.SM, vertical = 4.dp)
                        )
                        Spacer(Modifier.height(Spacing.MD))
                        Button(
                            onClick = {
                                AuthStore.get().upgradeToPro()
                                onUpgradeToPro()
                            },
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(containerColor = ExamPrepColors.Warning)
                        ) {
                            Text("Unlock Pro Features · 1-Tap Demo", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Button(
                    onClick = { onStartSurgicalDrill("surg_all_nodes") },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Launch 15-Min Precision Surgical Sprint", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(Spacing.XL))
        }
    }
}

@Composable
private fun VulnerabilityCard(
    node: WeaknessSurgicalNode,
    isPro: Boolean,
    onLaunchDrill: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(Spacing.MD)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = node.subject,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        )
                    }
                    Text(
                        text = node.chapter,
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }

                Surface(
                    color = ExamPrepColors.ErrorContainer,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "-${node.marksBleeding} Marks",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ExamPrepColors.Error,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Spacer(Modifier.height(Spacing.SM))

            Text(
                text = node.topicTrap,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Spacer(Modifier.height(4.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = ExamPrepColors.Warning, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Strategy: ${node.remedyStrategy}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}
