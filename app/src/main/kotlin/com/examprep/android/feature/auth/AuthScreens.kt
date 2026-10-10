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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.examprep.core.designsystem.component.StatusChip
import com.examprep.core.designsystem.theme.ExamPrepColors
import com.examprep.core.designsystem.theme.Spacing
import com.examprep.domain.model.AuthUser
import com.examprep.domain.model.PremiumFeature
import com.examprep.domain.model.SubscriptionTier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    onSignUpSuccess: () -> Unit = onAuthSuccess,
    onBack: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel()
) {
    var isSignUp by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val verificationNotice by viewModel.verificationNotice.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            if (isSignUp) {
                onSignUpSuccess()
            } else {
                onAuthSuccess()
            }
        }
    }

    // Real-time password strength calculation
    val hasMinLength = password.length >= 8
    val hasLetter = password.any { it.isLetter() }
    val hasDigit = password.any { it.isDigit() }
    val hasSpecial = password.any { !it.isLetterOrDigit() }
    val strengthScore = listOf(hasMinLength, hasLetter, hasDigit, hasSpecial).count { it }
    val (strengthLabel, strengthColor) = when {
        password.isEmpty() -> "" to Color.Transparent
        strengthScore <= 1 -> "Weak" to ExamPrepColors.Error
        strengthScore in 2..3 -> "Moderate" to ExamPrepColors.Warning
        else -> "Strong" to ExamPrepColors.Success
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                .padding(horizontal = Spacing.LG),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── App Branding Header ─────────────────────────────────────────
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(16.dp),
                color = ExamPrepColors.Sage70.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, ExamPrepColors.Sage70.copy(alpha = 0.3f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = ExamPrepColors.Sage70,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(Modifier.height(Spacing.SM))

            Text(
                text = "ExamPrep Intelligence",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Text(
                text = if (isSignUp) "Create your account to configure your custom preparation blueprint" else "Sign in to resume your daily rank acceleration",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(Spacing.LG))

            // ── Segmented Tab Switcher (Sign In vs Create Account) ─────────
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(modifier = Modifier.fillMaxSize().padding(4.dp)) {
                    listOf(false to "Sign In", true to "Create Account").forEach { (tabIsSignUp, label) ->
                        val isSelected = isSignUp == tabIsSignUp
                        Surface(
                            onClick = {
                                isSignUp = tabIsSignUp
                                viewModel.clearError()
                            },
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(Spacing.LG))

            // Error Banner
            if (uiState is AuthUiState.Error) {
                Surface(
                    color = ExamPrepColors.ErrorContainer,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth().padding(bottom = Spacing.MD)
                ) {
                    Row(
                        modifier = Modifier.padding(Spacing.MD),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = ExamPrepColors.Error, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = (uiState as AuthUiState.Error).message,
                            style = MaterialTheme.typography.bodySmall.copy(color = ExamPrepColors.Error)
                        )
                    }
                }
            }

            // Verification Notice Banner
            if (verificationNotice != null) {
                Surface(
                    color = ExamPrepColors.Sage70.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ExamPrepColors.Sage70.copy(alpha = 0.4f)),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth().padding(bottom = Spacing.MD)
                ) {
                    Row(
                        modifier = Modifier.padding(Spacing.MD),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.MarkEmailRead, contentDescription = null, tint = ExamPrepColors.Sage70, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = verificationNotice ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                            )
                        }
                        IconButton(onClick = { viewModel.clearNotice() }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // ── Form Inputs ────────────────────────────────────────────────
            if (isSignUp) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )
                Spacer(Modifier.height(Spacing.SM))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            Spacer(Modifier.height(Spacing.SM))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password (min 8 characters)") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle password visibility"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            if (!isSignUp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            resetEmail = email
                            showResetDialog = true
                        }
                    ) {
                        Text(
                            "Forgot Password?",
                            style = MaterialTheme.typography.labelMedium.copy(color = ExamPrepColors.Sage70)
                        )
                    }
                }
            }

            // Password Strength Indicator for Sign Up
            if (isSignUp && password.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Password Strength: $strengthLabel",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = strengthColor,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        (1..4).forEach { level ->
                            Box(
                                modifier = Modifier
                                    .width(28.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(if (level <= strengthScore) strengthColor else MaterialTheme.colorScheme.surfaceVariant)
                            )
                        }
                    }
                }
            }

            if (isSignUp) {
                Spacer(Modifier.height(Spacing.SM))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    leadingIcon = { Icon(Icons.Default.LockClock, contentDescription = null) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    isError = confirmPassword.isNotEmpty() && confirmPassword != password
                )
            }

            if (showResetDialog) {
                AlertDialog(
                    onDismissRequest = { showResetDialog = false },
                    title = { Text("Reset Password") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Enter your account email to receive a secure password reset link via Firebase.")
                            OutlinedTextField(
                                value = resetEmail,
                                onValueChange = { resetEmail = it },
                                label = { Text("Email Address") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.sendPasswordReset(resetEmail)
                                showResetDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ExamPrepColors.Sage70, contentColor = Color(0xFF090B0E))
                        ) {
                            Text("Send Reset Link", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showResetDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            Spacer(Modifier.height(Spacing.LG))

            Button(
                onClick = {
                    if (isSignUp) {
                        viewModel.signUp(name, email, password)
                    } else {
                        viewModel.signIn(email, password)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ExamPrepColors.Sage70,
                    contentColor = Color(0xFF090B0E)
                ),
                enabled = uiState !is AuthUiState.Loading
            ) {
                if (uiState is AuthUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFF090B0E))
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isSignUp) "Create Account & Start Onboarding" else "Sign In",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            if (showPrivacyDialog) {
                AlertDialog(
                    onDismissRequest = { showPrivacyDialog = false },
                    title = { Text("Privacy Policy Summary", fontWeight = FontWeight.Bold) },
                    text = {
                        Text(
                            "ExamPrepOS stores your study progress, quiz history, and notes securely in your local encrypted device storage. " +
                            "Firebase Auth is used for account credentials. We never collect precise GPS location, contacts, or biometric data. " +
                            "You can export or permanently delete your account & data at any time from Settings."
                        )
                    },
                    confirmButton = {
                        Button(onClick = { showPrivacyDialog = false }) {
                            Text("I Understand")
                        }
                    }
                )
            }

            if (showTermsDialog) {
                AlertDialog(
                    onDismissRequest = { showTermsDialog = false },
                    title = { Text("Terms of Service Summary", fontWeight = FontWeight.Bold) },
                    text = {
                        Text(
                            "ExamPrepOS is an independent educational productivity tool. " +
                            "ExamPrepOS is NOT affiliated with, sponsored by, or endorsed by NTA, UPSC, IITs, IIMs, or CBSE. " +
                            "Content and practice tests are designed for preparation and do not guarantee marks, cutoff clearance, or selection."
                        )
                    },
                    confirmButton = {
                        Button(onClick = { showTermsDialog = false }) {
                            Text("Close")
                        }
                    }
                )
            }

            Spacer(Modifier.height(Spacing.MD))

            if (isSignUp) {
                Text(
                    text = "By creating an account, you agree to our Terms of Service and acknowledge our Privacy Policy. ExamPrepOS is an independent preparation platform.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = { showTermsDialog = true }) {
                        Text("Terms of Service", style = MaterialTheme.typography.labelSmall)
                    }
                    Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    TextButton(onClick = { showPrivacyDialog = true }) {
                        Text("Privacy Policy", style = MaterialTheme.typography.labelSmall)
                    }
                }
                Spacer(Modifier.height(Spacing.SM))
            }

            // ── 1-Tap Demo Access ───────────────────────────────────────────────
            if (!isSignUp) {
                OutlinedButton(
                    onClick = { viewModel.signInAsDemo() },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(18.dp), tint = ExamPrepColors.Warning)
                    Spacer(Modifier.width(8.dp))
                    Text("1-Tap Demo Aspirant Sign-In (Existing User)")
                }
            } else {
                OutlinedButton(
                    onClick = {
                        name = "Rohan Varma"
                        email = "rohan.varma@examprep.io"
                        password = "Password123!"
                        confirmPassword = "Password123!"
                    },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(18.dp), tint = ExamPrepColors.Sage70)
                    Spacer(Modifier.width(8.dp))
                    Text("⚡ Auto-Fill New Student Profile")
                }
            }

            Spacer(Modifier.height(Spacing.XL))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAuth: () -> Unit,
    onNavigateToHeroFeature: () -> Unit,
    onLoggedOut: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            icon = {
                Icon(
                    Icons.Default.Logout,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text(
                    "Log Out of ExamPrep?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    "You will need to sign in again with your email and password to resume your mock tests, target tasks, and AI Weakness Surgery drills.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.signOut()
                        onLoggedOut()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Log Out", color = MaterialTheme.colorScheme.onError, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile & Membership", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            Icons.Default.Logout,
                            contentDescription = "Log Out",
                            tint = MaterialTheme.colorScheme.error
                        )
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

            // User Profile Card
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.MD)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentUser?.name?.take(2)?.uppercase() ?: "YS",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                            Column {
                                Text(
                                    text = currentUser?.name ?: "Guest Aspirant",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = currentUser?.email ?: "guest@examprep.io",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }

                        // Membership Badge
                        val isPro = currentUser?.isPro == true
                        StatusChip(
                            text = if (isPro) "👑 PRO" else "FREE",
                            color = if (isPro) ExamPrepColors.Warning else MaterialTheme.colorScheme.onSurfaceVariant,
                            backgroundColor = if (isPro) ExamPrepColors.Warning.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                        )
                    }

                    Spacer(Modifier.height(Spacing.MD))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(Modifier.height(Spacing.MD))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Target Exam", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            Text(currentUser?.targetExam ?: "JEE Main 2026", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                        }
                        Column {
                            Text("Daily Goal", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            Text("${currentUser?.dailyStudyTargetHours ?: 4.5f} Hours/Day", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                        }
                    }
                }
            }

            Spacer(Modifier.height(Spacing.MD))

            // ── EMAIL VERIFICATION STATUS CARD ──────────────────────────────
            val isEmailVerified by viewModel.isEmailVerified.collectAsState()
            val verificationNotice by viewModel.verificationNotice.collectAsState()

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (isEmailVerified) ExamPrepColors.Success.copy(alpha = 0.3f) else ExamPrepColors.Warning.copy(alpha = 0.3f),
                        MaterialTheme.shapes.large
                    ),
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
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                if (isEmailVerified) Icons.Default.MarkEmailRead else Icons.Default.MarkEmailUnread,
                                contentDescription = null,
                                tint = if (isEmailVerified) ExamPrepColors.Success else ExamPrepColors.Warning,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = if (isEmailVerified) "Email Verified" else "Email Verification Pending",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = if (isEmailVerified) "Account verified via Firebase" else "Verify email to protect progress",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }

                        StatusChip(
                            text = if (isEmailVerified) "✓ VERIFIED" else "UNVERIFIED",
                            color = if (isEmailVerified) ExamPrepColors.Success else ExamPrepColors.Warning
                        )
                    }

                    if (!isEmailVerified) {
                        Spacer(Modifier.height(Spacing.SM))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.sendVerificationEmail() },
                                modifier = Modifier.weight(1f).height(36.dp),
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text("Resend Email", style = MaterialTheme.typography.labelSmall)
                            }
                            Button(
                                onClick = { viewModel.checkEmailVerificationStatus() },
                                modifier = Modifier.weight(1f).height(36.dp),
                                shape = MaterialTheme.shapes.small,
                                colors = ButtonDefaults.buttonColors(containerColor = ExamPrepColors.Sage70, contentColor = Color(0xFF090B0E))
                            ) {
                                Text("Check Status", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }

                    if (verificationNotice != null) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = verificationNotice ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(color = ExamPrepColors.Sage70)
                        )
                    }
                }
            }

            Spacer(Modifier.height(Spacing.LG))

            // ── HERO FEATURE BANNER ─────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToHeroFeature() }
                    .border(1.dp, ExamPrepColors.Warning.copy(alpha = 0.4f), MaterialTheme.shapes.large),
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
                                text = "HERO PRO FEATURE",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ExamPrepColors.Warning,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Spacer(Modifier.height(Spacing.SM))

                    Text(
                        text = "AI Weakness Surgery & Rank Predictor",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "Diagnose marks lost to conceptual traps and run 15-min precision drills for a +40 percentile boost.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }

            Spacer(Modifier.height(Spacing.LG))

            // ── TIER COMPARISON MATRIX ──────────────────────────────────────
            Text(
                text = "Plan Comparison",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Spacer(Modifier.height(Spacing.SM))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(Spacing.MD)) {
                    PremiumFeature.values().forEachIndexed { index, feat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Text(
                                    text = feat.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = feat.description,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                            Surface(
                                color = if (feat.isProOnly) ExamPrepColors.Warning.copy(alpha = 0.15f) else ExamPrepColors.Success.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (feat.isProOnly) "PRO" else "FREE",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (feat.isProOnly) ExamPrepColors.Warning else ExamPrepColors.Success,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                        if (index < PremiumFeature.values().size - 1) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        }
                    }
                }
            }

            Spacer(Modifier.height(Spacing.LG))

            // ── QUICK TOGGLE BUTTON FOR DEMO / PRO MANAGEMENT ──────────────
            val isPro = currentUser?.isPro == true
            Button(
                onClick = {
                    if (isPro) viewModel.downgradeToFree() else viewModel.upgradeToPro()
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPro) MaterialTheme.colorScheme.surfaceVariant else ExamPrepColors.Warning
                )
            ) {
                Icon(if (isPro) Icons.Default.Refresh else Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp), tint = if (isPro) MaterialTheme.colorScheme.onSurface else Color.Black)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (isPro) "Switch to Free Tier (Demo Toggle)" else "Upgrade to Pro (Demo 1-Tap Unlock)",
                    style = MaterialTheme.typography.titleSmall.copy(color = if (isPro) MaterialTheme.colorScheme.onSurface else Color.Black, fontWeight = FontWeight.Bold)
                )
            }

            Spacer(Modifier.height(Spacing.SM))

            OutlinedButton(
                onClick = onNavigateToAuth,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Switch / Create Another Account")
            }

            Spacer(Modifier.height(Spacing.SM))

            // ── DEDICATED LOG OUT BUTTON ───────────────────────────────────
            OutlinedButton(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f))
            ) {
                Icon(
                    Icons.Default.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Log Out",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(Spacing.XL))
        }
    }
}
