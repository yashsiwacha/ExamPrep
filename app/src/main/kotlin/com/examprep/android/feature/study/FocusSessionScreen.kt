package com.examprep.android.feature.study

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.examprep.core.designsystem.theme.ExamPrepColors
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

    // Infinite breathing pulse for AOD glow when running
    val infiniteTransition = rememberInfiniteTransition(label = "AOD_Pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAlpha"
    )

    val isRunning = uiState.timerStatus == TimerStatus.RUNNING
    val activeBorderColor by animateColorAsState(
        targetValue = when (uiState.timerStatus) {
            TimerStatus.RUNNING -> ExamPrepColors.Sage70
            TimerStatus.PAUSED -> ExamPrepColors.Warning
            TimerStatus.COMPLETED -> ExamPrepColors.Success
            else -> ExamPrepColors.Neutral40
        },
        label = "BorderColor"
    )

    val minutes = uiState.remainingSeconds / 60
    val seconds = uiState.remainingSeconds % 60
    val minutesStr = "%02d".format(minutes)
    val secondsStr = "%02d".format(seconds)

    val progress = if (uiState.totalSeconds > 0) {
        (uiState.remainingSeconds.toFloat() / uiState.totalSeconds).coerceIn(0f, 1f)
    } else 0f

    // OLED Pitch Black AOD Canvas
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050709))
    ) {
        // Subtle ambient radial glow behind the clock
        if (isRunning) {
            Box(
                modifier = Modifier
                    .size(340.dp)
                    .align(Alignment.Center)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                ExamPrepColors.Sage70.copy(alpha = 0.08f * pulseAlpha),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = Spacing.LG, vertical = Spacing.MD),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ── Top Ambient AOD Status Bar ─────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color(0x14FFFFFF), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFFCBD5E1),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Ambient Status Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0x12FFFFFF), RoundedCornerShape(100.dp))
                        .border(1.dp, Color(0x1FFFFFFF), RoundedCornerShape(100.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(
                                color = if (isRunning) activeBorderColor.copy(alpha = pulseAlpha) else activeBorderColor,
                                shape = CircleShape
                            )
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = when (uiState.timerStatus) {
                            TimerStatus.RUNNING -> "FOCUS ACTIVE"
                            TimerStatus.PAUSED -> "PAUSED"
                            TimerStatus.COMPLETED -> "FINISHED"
                            else -> "READY"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFFE2E8F0),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    )
                }

                // Subtle session timer info badge
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color(0x14FFFFFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${uiState.totalSeconds / 60}m",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            // ── Centerpiece: Massive Stacked AOD Rectangle Clock ───────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                // Task info chip
                Text(
                    text = uiState.taskTitle,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.4.sp
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 18.dp)
                )

                // ── The Rectangular Time Container ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.82f)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0xFF0C0F14))
                        .border(
                            width = 2.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    activeBorderColor.copy(alpha = if (isRunning) pulseAlpha else 0.7f),
                                    activeBorderColor.copy(alpha = 0.25f)
                                )
                            ),
                            shape = RoundedCornerShape(32.dp)
                        )
                        .padding(vertical = 24.dp, horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Ambient Header Tag inside the card
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "MINUTES",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF64748B),
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.sp,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        // Line 1: Minutes (Giant)
                        Text(
                            text = minutesStr,
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 92.sp,
                                lineHeight = 94.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF8FAFC),
                                letterSpacing = (-2).sp,
                                fontFamily = FontFamily.SansSerif
                            ),
                            textAlign = TextAlign.Center
                        )

                        // Subtle AOD Glowing Separator Line
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(1.dp)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                Color.Transparent,
                                                activeBorderColor.copy(alpha = 0.4f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                        }

                        // Line 2: Seconds (Giant, directly under minutes)
                        Text(
                            text = secondsStr,
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 92.sp,
                                lineHeight = 94.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isRunning) activeBorderColor else Color(0xFFCBD5E1),
                                letterSpacing = (-2).sp,
                                fontFamily = FontFamily.SansSerif
                            ),
                            textAlign = TextAlign.Center
                        )

                        // Ambient Footer Tag inside the card
                        Text(
                            text = "SECONDS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Spacer(Modifier.height(14.dp))

                        // Sleek Linear Progress Track embedded in the bottom of the rectangle
                        Column(
                            modifier = Modifier.fillMaxWidth(0.85f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(100.dp)),
                                color = activeBorderColor,
                                trackColor = Color(0xFF1E242E)
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "${((1f - progress) * 100).toInt()}% completed",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF64748B),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                // ── AOD Minimalist Glowing Controls ─────────────────────────
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Reset Button (Minimalist Glass Circle)
                    IconButton(
                        onClick = { viewModel.resetTimer() },
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF131720))
                            .border(1.dp, Color(0x22FFFFFF), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Reset Timer",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Main Play/Pause Button (Hero Glowing Pill)
                    Button(
                        onClick = {
                            if (isRunning) {
                                viewModel.pauseTimer()
                            } else {
                                viewModel.startTimer()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRunning) Color(0xFF1E2530) else ExamPrepColors.Sage70,
                            contentColor = if (isRunning) Color(0xFFF1F5F9) else Color(0xFF090B0E)
                        ),
                        shape = CircleShape,
                        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 18.dp),
                        modifier = Modifier
                            .height(64.dp)
                            .border(
                                1.dp,
                                if (isRunning) activeBorderColor.copy(alpha = 0.5f) else Color.Transparent,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isRunning) "Pause" else "Start",
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = if (isRunning) "PAUSE" else "START FOCUS",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    // Complete Session Button (Checkmark Glass Circle)
                    IconButton(
                        onClick = { showCompleteDialog = true },
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF131720))
                            .border(1.dp, Color(0x22FFFFFF), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Complete Session",
                            tint = Color(0xFF86A795),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // ── Bottom Ambient Footer ──────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color(0xFF475569),
                    modifier = Modifier.size(13.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Always-On Focus Mode • Distraction Blocked",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF475569),
                        fontSize = 11.sp,
                        letterSpacing = 0.3.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun SessionCompleteDialog(
    onCompleted: () -> Unit,
    onNeedMoreTime: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        containerColor = Color(0xFF111419),
        title = {
            Text(
                "Focus Session Complete!",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color(0xFFF1F5F9),
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Text(
                "High-yield study time recorded. Your retention metrics and mastery status have been updated.",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF94A3B8))
            )
        },
        confirmButton = {
            Button(
                onClick = onCompleted,
                colors = ButtonDefaults.buttonColors(containerColor = ExamPrepColors.Sage70)
            ) {
                Text("Log & Return", color = Color(0xFF090B0E), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onNeedMoreTime) {
                Text("Extend +5m", color = Color(0xFFCBD5E1))
            }
        }
    )
}

