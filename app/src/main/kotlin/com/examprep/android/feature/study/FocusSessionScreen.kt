package com.examprep.android.feature.study

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.examprep.core.designsystem.theme.Spacing

@Suppress("UNUSED_PARAMETER")
@Composable
fun FocusSessionScreen(
    taskId: String,
    onSessionComplete: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: FocusSessionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCompleteDialog by remember { mutableStateOf(false) }
    var sessionFinalized by remember { mutableStateOf(false) }
    val isCompleted = uiState.timerStatus == TimerStatus.COMPLETED

    if ((isCompleted || showCompleteDialog) && !sessionFinalized) {
        SessionCompleteDialog(
            onCompleted = {
                sessionFinalized = true
                showCompleteDialog = false
                if (uiState.timerStatus != TimerStatus.COMPLETED) {
                    viewModel.completeSessionEarly()
                }
                onSessionComplete()
            },
            onNeedMoreTime = {
                showCompleteDialog = false
                viewModel.resetTimer()
                viewModel.startTimer()
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.MD),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Focus Session",
                    style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onBackground)
                )
                Text(
                    text = uiState.taskTitle,
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }

        // Timer — the focal point of the screen
        Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.MD)
            ) {
                // Circular progress indicator
                Box(contentAlignment = Alignment.Center) {
                    val progress = if (uiState.totalSeconds > 0) {
                        uiState.remainingSeconds.toFloat() / uiState.totalSeconds
                    } else 0f

                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(220.dp),
                        strokeWidth = 8.dp,
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = formatTime(uiState.remainingSeconds),
                            style = MaterialTheme.typography.displayMedium.copy(
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = when (uiState.timerStatus) {
                                TimerStatus.RUNNING -> "Focusing"
                                TimerStatus.PAUSED -> "Paused"
                                TimerStatus.COMPLETED -> "Completed"
                                else -> "Ready"
                            },
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                // Controls
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.LG),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedIconButton(
                        onClick = { viewModel.resetTimer() },
                        modifier = Modifier.size(52.dp)
                    ) {
                        Icon(Icons.Default.RestartAlt, contentDescription = "Reset")
                    }

                    FilledIconButton(
                        onClick = {
                            if (uiState.timerStatus == TimerStatus.RUNNING) {
                                viewModel.pauseTimer()
                            } else {
                                viewModel.startTimer()
                            }
                        },
                        modifier = Modifier.size(72.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.timerStatus == TimerStatus.RUNNING) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (uiState.timerStatus == TimerStatus.RUNNING) "Pause" else "Resume",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    OutlinedIconButton(
                        onClick = { showCompleteDialog = true },
                        modifier = Modifier.size(52.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Complete")
                    }
                }
            }
        }

        // Session notes (bottom)
        Card(
            modifier = Modifier.padding(Spacing.MD).fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.padding(Spacing.MD)) {
                Text(
                    text = "Session Context",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Stay focused on core concepts and active recall exercises.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    val min = seconds / 60
    val sec = seconds % 60
    return "%02d:%02d".format(min, sec)
}

@Composable
private fun SessionCompleteDialog(
    onCompleted: () -> Unit,
    onNeedMoreTime: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Session Complete!") },
        text = { Text("Great work! Your study time has been logged toward mastery.") },
        confirmButton = {
            Button(onClick = onCompleted) { Text("Finish") }
        },
        dismissButton = {
            TextButton(onClick = onNeedMoreTime) { Text("Need More Time") }
        }
    )
}
