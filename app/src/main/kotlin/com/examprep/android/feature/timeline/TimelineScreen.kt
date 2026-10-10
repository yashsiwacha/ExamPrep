package com.examprep.android.feature.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.examprep.core.designsystem.component.ExamPrepErrorState
import com.examprep.core.designsystem.component.ExamPrepLoadingState
import com.examprep.core.designsystem.component.StatusChip
import com.examprep.core.designsystem.theme.ExamPrepColors
import com.examprep.core.designsystem.theme.Spacing
import com.examprep.domain.model.StudyPhase

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
            is TimelineUiState.Loading -> ExamPrepLoadingState()
            is TimelineUiState.Error -> ExamPrepErrorState(message = uiState.message)
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
                            PhaseCard(
                                title = phase.name,
                                description = phase.description,
                                status = phase.status,
                                containerColor = when (phase.status) {
                                    "In Progress" -> MaterialTheme.colorScheme.primaryContainer
                                    "Completed" -> MaterialTheme.colorScheme.secondaryContainer
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                },
                                buttonText = if (phase.status == "In Progress") "Start Today's Tasks" else null,
                                isPrimaryButton = true,
                                onButtonClick = { onStartTask("task_today") }
                            )
                        }
                        item { Spacer(Modifier.height(Spacing.XL)) }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = Spacing.MD),
                        verticalArrangement = Arrangement.spacedBy(Spacing.SM)
                    ) {
                        items(phases, key = { it.id }) { phase ->
                            PhaseCard(
                                title = phase.phaseType.displayName,
                                description = "${phase.tasks.size} study tasks scheduled",
                                status = if (phase.orderIndex == 1) "Active" else "Upcoming",
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                buttonText = phase.tasks.firstOrNull()?.let { "Start Task (${it.estimatedMinutes} mins)" },
                                isPrimaryButton = false,
                                onButtonClick = { phase.tasks.firstOrNull()?.let { onStartTask(it.id) } }
                            )
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
private fun PhaseCard(
    title: String,
    description: String,
    status: String,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    buttonText: String? = null,
    isPrimaryButton: Boolean = false,
    onButtonClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
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
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
                StatusChip(
                    text = status,
                    color = MaterialTheme.colorScheme.onSurface,
                    backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                )
            }
            if (buttonText != null) {
                Spacer(Modifier.height(Spacing.SM))
                if (isPrimaryButton) {
                    Button(
                        onClick = onButtonClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ExamPrepColors.Sage70,
                            contentColor = Color(0xFF090B0E)
                        )
                    ) {
                        Text(
                            text = buttonText,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = onButtonClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(buttonText)
                    }
                }
            }
        }
    }
}
