package com.examprep.android.feature.revision

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.examprep.core.designsystem.component.ExamPrepErrorState
import com.examprep.core.designsystem.component.ExamPrepLoadingState
import com.examprep.core.designsystem.theme.ExamPrepColors
import com.examprep.core.designsystem.theme.Spacing

@Composable
fun RevisionScreen(
    onStartRevision: (String) -> Unit,
    viewModel: RevisionViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Box(modifier = Modifier.padding(horizontal = Spacing.MD, vertical = Spacing.MD)) {
            Column {
                Text(
                    text = "Spaced Revision Queue",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Review scheduled topics before memory decays",
                    style = MaterialTheme.typography.bodyMedium.copy(color = ExamPrepColors.Warning)
                )
            }
        }

        when (val uiState = state) {
            is RevisionUiState.Loading -> ExamPrepLoadingState()
            is RevisionUiState.Error -> ExamPrepErrorState(message = uiState.message)
            is RevisionUiState.Success -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = Spacing.MD),
                    verticalArrangement = Arrangement.spacedBy(Spacing.SM)
                ) {
                    if (uiState.dueRevisions.isNotEmpty()) {
                        items(uiState.dueRevisions, key = { it.id }) { record ->
                            RevisionCard(
                                title = "Topic ${record.topicId}",
                                subtitle = "Revision #${record.revisionNumber}",
                                dueLabel = "Due for Spaced Review",
                                isDueToday = true,
                                onStart = { onStartRevision("revision_${record.topicId}") }
                            )
                        }
                    } else {
                        val sampleRevisions = listOf(
                            SampleRevisionItem("Laws of Motion", "Physics", "Due Today", true),
                            SampleRevisionItem("Atomic Structure", "Chemistry", "Due Today", true),
                            SampleRevisionItem("Differential Calculus", "Mathematics", "Due in 2 days", false),
                            SampleRevisionItem("Thermodynamics", "Physics", "Due in 4 days", false)
                        )
                        items(sampleRevisions, key = { it.topicName }) { item ->
                            RevisionCard(
                                title = item.topicName,
                                subtitle = item.subject,
                                dueLabel = item.dueLabel,
                                isDueToday = item.isDueToday,
                                onStart = { onStartRevision("revision_${item.topicName}") }
                            )
                        }
                    }
                    item { Spacer(Modifier.height(Spacing.XL)) }
                }
            }
        }
    }
}

private data class SampleRevisionItem(
    val topicName: String,
    val subject: String,
    val dueLabel: String,
    val isDueToday: Boolean
)

@Composable
private fun RevisionCard(
    title: String,
    subtitle: String,
    dueLabel: String,
    isDueToday: Boolean,
    onStart: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isDueToday)
                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f)
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(Spacing.MD).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.SM), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isDueToday) Icons.Default.NotificationsActive else Icons.Default.Schedule,
                    contentDescription = null,
                    tint = if (isDueToday) ExamPrepColors.Warning else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Text(
                        dueLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isDueToday) ExamPrepColors.Warning else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isDueToday) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                }
            }
            FilledTonalButton(
                onClick = onStart,
                contentPadding = PaddingValues(horizontal = Spacing.MD, vertical = 8.dp)
            ) {
                Text("Revise", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
