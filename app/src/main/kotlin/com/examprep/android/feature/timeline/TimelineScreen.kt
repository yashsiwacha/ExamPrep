package com.examprep.android.feature.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.examprep.core.designsystem.theme.Spacing
import com.examprep.domain.model.StudyPhase
import com.examprep.domain.model.StudyTask

@Composable
fun TimelineScreen(
    onStartTask: (String) -> Unit,
    viewModel: TimelineViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(modifier = Modifier.padding(horizontal = Spacing.MD, vertical = Spacing.MD)) {
            Column {
                Text(
                    text = "Preparation Timeline",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Personalized dynamic study phases",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        when (val uiState = state) {
            is TimelineUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is TimelineUiState.Error -> {
                Text("Error: ${uiState.message}", modifier = Modifier.padding(Spacing.MD))
            }
            is TimelineUiState.Success -> {
                val phases = uiState.plan?.phases ?: emptyList()
                if (phases.isEmpty()) {
                    val fallbackPhases = listOf(
                        PhaseItem("Foundation", "15% · High weightage fundamentals", 100f, "Completed"),
                        PhaseItem("Core Learning", "40% · Comprehensive syllabus coverage", 45f, "In Progress"),
                        PhaseItem("Practice & Problem Solving", "20% · Topic wise tests & exercises", 0f, "Upcoming"),
                        PhaseItem("PYQ Practice", "10% · Previous 10 years exam questions", 0f, "Upcoming"),
                        PhaseItem("Spaced Revision & Mocks", "15% · Full length timed test series", 0f, "Upcoming")
                    )
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = Spacing.MD),
                        verticalArrangement = Arrangement.spacedBy(Spacing.SM)
                    ) {
                        items(fallbackPhases, key = { it.name }) { phase ->
                            FallbackPhaseCard(phase = phase, onStartTask = onStartTask)
                        }
                        item { Spacer(Modifier.height(Spacing.XL)) }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = Spacing.MD),
                        verticalArrangement = Arrangement.spacedBy(Spacing.SM)
                    ) {
                        items(phases, key = { it.id }) { phase ->
                            RealPhaseCard(phase = phase, onStartTask = onStartTask)
                        }
                        item { Spacer(Modifier.height(Spacing.XL)) }
                    }
                }
            }
        }
    }
}

private data class PhaseItem(
    val name: String,
    val description: String,
    val completion: Float,
    val status: String
)

@Composable
private fun RealPhaseCard(phase: StudyPhase, onStartTask: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(Spacing.MD)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = phase.phaseType.displayName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "${phase.tasks.size} study tasks scheduled",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
                StatusBadge(status = if (phase.orderIndex == 1) "Active" else "Upcoming")
            }
            if (phase.tasks.isNotEmpty()) {
                Spacer(Modifier.height(Spacing.SM))
                OutlinedButton(
                    onClick = { onStartTask(phase.tasks.first().id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Start Task (${phase.tasks.first().estimatedMinutes} mins)")
                }
            }
        }
    }
}

@Composable
private fun FallbackPhaseCard(phase: PhaseItem, onStartTask: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (phase.status) {
                "In Progress" -> MaterialTheme.colorScheme.primaryContainer
                "Completed" -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(Spacing.MD)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = phase.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = phase.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
                StatusBadge(status = phase.status)
            }
            if (phase.status == "In Progress") {
                Spacer(Modifier.height(Spacing.SM))
                Button(
                    onClick = { onStartTask("task_today") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Start Today's Tasks")
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    Surface(color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), shape = MaterialTheme.shapes.extraSmall) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
        )
    }
}
