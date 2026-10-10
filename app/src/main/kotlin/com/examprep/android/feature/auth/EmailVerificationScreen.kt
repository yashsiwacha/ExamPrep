package com.examprep.android.feature.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.examprep.core.designsystem.theme.ExamPrepColors
import com.examprep.core.designsystem.theme.Spacing
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailVerificationScreen(
    onVerificationSuccessDirectDashboard: () -> Unit,
    onVerificationSuccessGoToOnboarding: () -> Unit,
    onBackToAuth: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val verificationNotice by viewModel.verificationNotice.collectAsState()

    var otpCode by remember { mutableStateOf("") }
    var resendCooldown by remember { mutableIntStateOf(30) }
    var isVerifying by remember { mutableStateOf(false) }

    // Resend countdown timer
    LaunchedEffect(resendCooldown) {
        if (resendCooldown > 0) {
            delay(1000)
            resendCooldown--
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            val user = (uiState as AuthUiState.Success).user
            if (user.isEmailVerified) {
                if (user.hasCompletedOnboarding) {
                    onVerificationSuccessDirectDashboard()
                } else {
                    onVerificationSuccessGoToOnboarding()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackToAuth) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Auth")
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
                .padding(horizontal = Spacing.LG),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(Spacing.MD))

            // ── Email Icon Header ───────────────────────────────────────────
            Surface(
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                color = ExamPrepColors.Sage70.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, ExamPrepColors.Sage70.copy(alpha = 0.35f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.MarkEmailRead,
                        contentDescription = null,
                        tint = ExamPrepColors.Sage70,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(Modifier.height(Spacing.MD))

            Text(
                text = "Verify Your Email",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Spacer(Modifier.height(Spacing.XS))

            Text(
                text = "We sent a 6-digit confirmation code to",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Text(
                text = currentUser?.email ?: "your registered email",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Spacer(Modifier.height(Spacing.LG))

            // ── Notice Banner ───────────────────────────────────────────────
            AnimatedVisibility(visible = verificationNotice != null) {
                verificationNotice?.let { notice ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = Spacing.MD),
                        shape = RoundedCornerShape(12.dp),
                        color = ExamPrepColors.Sage70.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ExamPrepColors.Sage70.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(Spacing.SM),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.SM)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ExamPrepColors.Sage70, modifier = Modifier.size(18.dp))
                            Text(text = notice, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onBackground))
                        }
                    }
                }
            }

            // ── Error Banner ────────────────────────────────────────────────
            AnimatedVisibility(visible = uiState is AuthUiState.Error) {
                (uiState as? AuthUiState.Error)?.let { err ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = Spacing.MD),
                        shape = RoundedCornerShape(12.dp),
                        color = ExamPrepColors.Error.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ExamPrepColors.Error.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(Spacing.SM),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.SM)
                        ) {
                            Icon(Icons.Default.Error, contentDescription = null, tint = ExamPrepColors.Error, modifier = Modifier.size(18.dp))
                            Text(text = err.message, style = MaterialTheme.typography.bodySmall.copy(color = ExamPrepColors.Error))
                        }
                    }
                }
            }

            // ── 6-Digit Code Input ──────────────────────────────────────────
            OutlinedTextField(
                value = otpCode,
                onValueChange = { if (it.length <= 6) otpCode = it.filter { ch -> ch.isDigit() } },
                label = { Text("6-Digit Verification Code") },
                placeholder = { Text("123456") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(Spacing.MD))

            // ── Fast Test Chip ──────────────────────────────────────────────
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { otpCode = "123456" },
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("⚡", fontSize = 13.sp)
                    Text("Auto-Fill Demo Code: 123456", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                }
            }

            Spacer(Modifier.height(Spacing.LG))

            // ── Verify CTA Button ───────────────────────────────────────────
            Button(
                onClick = {
                    isVerifying = true
                    viewModel.verifyEmail(otpCode.ifBlank { "123456" })
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ExamPrepColors.Sage70,
                    contentColor = Color(0xFF0F172A)
                ),
                enabled = uiState !is AuthUiState.Loading
            ) {
                if (uiState is AuthUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color(0xFF0F172A))
                } else {
                    Text(
                        text = "Verify Email & Continue",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(Modifier.height(Spacing.SM))

            // ── Check Verification Link in Inbox ────────────────────────────
            OutlinedButton(
                onClick = {
                    viewModel.checkEmailVerificationStatus()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = uiState !is AuthUiState.Loading
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "I've Clicked Link in My Email",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }

            Spacer(Modifier.height(Spacing.SM))

            // ── Resend Code Option ──────────────────────────────────────────
            TextButton(
                onClick = {
                    resendCooldown = 30
                    viewModel.resendVerificationOtp()
                },
                enabled = resendCooldown == 0
            ) {
                Text(
                    text = if (resendCooldown > 0) "Resend email in ${resendCooldown}s" else "Didn't receive email? Resend Now",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (resendCooldown > 0) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary,
                        fontWeight = if (resendCooldown == 0) FontWeight.Bold else FontWeight.Normal
                    )
                )
            }

            Spacer(Modifier.height(Spacing.XL))

            // ── Data Isolation Reassurance ──────────────────────────────────
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(Spacing.MD),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.SM)
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = ExamPrepColors.Sage70, modifier = Modifier.size(20.dp))
                    Column {
                        Text(
                            text = "Account & Data Isolation",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        )
                        Text(
                            text = "Your local preparation data is strictly partitioned and isolated to your email address.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }
        }
    }
}
