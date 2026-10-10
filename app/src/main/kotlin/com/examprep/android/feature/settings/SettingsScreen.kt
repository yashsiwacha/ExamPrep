package com.examprep.android.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.examprep.core.designsystem.theme.ExamPrepColors
import com.examprep.core.designsystem.theme.Spacing
import com.examprep.core.designsystem.theme.ThemeState
import com.examprep.domain.model.SubscriptionTier

@Composable
fun SettingsScreen(
    onLoggedOut: () -> Unit = {},
    onNavigateToSubscription: () -> Unit = {},
    onNavigateToQuestionLibrary: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDarkMode by ThemeState.isDarkMode.collectAsState()
    var showReferralDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showDisclaimerDialog by remember { mutableStateOf(false) }
    var showLicensesDialog by remember { mutableStateOf(false) }
    var showContactPrivacyDialog by remember { mutableStateOf(false) }
    var showExportDataDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    val authStore = remember { com.examprep.android.feature.auth.AuthStore.get() }
    val currentUser by authStore.currentUser.collectAsState()

    // ── Dialogs ─────────────────────────────────────────────────────────────

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = { Text("Log Out of ExamPrep?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Are you sure you want to log out? Your local study plans and offline cards will remain safely stored on this device.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        authStore.signOut()
                        onLoggedOut()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Log Out", color = MaterialTheme.colorScheme.onError, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showReferralDialog) {
        AlertDialog(
            onDismissRequest = { showReferralDialog = false },
            title = { Text("Invite Study Buddies", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Share your unique study link with friends preparing for JEE, NEET, UPSC, CAT, or GATE.")
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "https://examprep.app/join/ASPIRANT2026",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                    Text(
                        text = "🎁 When 2 friends join, you both unlock 1 Month of ExamPrep Pro for free!",
                        style = MaterialTheme.typography.bodySmall.copy(color = ExamPrepColors.Success)
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showReferralDialog = false }) {
                    Text("Share Invite Link")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReferralDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showPrivacyPolicyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyPolicyDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Policy, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Privacy Policy", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "ExamPrepOS Privacy Policy (Summary)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        "Last Updated: October 2026\n" +
                        "Applicable to: ExamPrepOS Android Application\n" +
                        "Governing Frameworks: Google Play User Data Policy, Indian DPDP Act 2023, IT Act 2000.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text("1. Data Collected & Stored Locally", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "• Account Info: Name, email address, password hash (via Firebase Auth).\n" +
                        "• Study Records: Syllabus progress, target daily study hours, quiz attempts, question bookmarks, flashcards.\n" +
                        "• Device & Diagnostics: Crash traces (Timber/Android OS), non-identifying telemetry.\n" +
                        "• All user study data is stored securely in an isolated SQLite database on your device.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text("2. Data NOT Collected", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "We do NOT collect precise GPS location, contacts, camera feeds, microphone audio, financial card data, or biometric data.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text("3. Third-Party Services", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "• Google Mobile Ads (AdMob): Displays ads. You can disable ad personalization in Settings anytime to request non-personalized ads (NPA).\n" +
                        "• Google Firebase: Used for secure user authentication and account credential management.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text("4. Your Privacy Rights", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "Under the Digital Personal Data Protection Act, 2023 (DPDPA) and Google Play policies, you have the right to:\n" +
                        "• Access and export your study records.\n" +
                        "• Withdraw consent for personalized advertising.\n" +
                        "• Request complete and irreversible deletion of your account and all associated data via Settings -> Delete All Data.\n" +
                        "• Lodge a grievance with our Grievance Officer at [PRIVACY EMAIL].",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showPrivacyPolicyDialog = false }) {
                    Text("I Understand")
                }
            }
        )
    }

    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Gavel, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Terms of Service", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "ExamPrepOS Terms of Service (Summary)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        "Effective Date: October 2026\nOperated by: [LEGAL ENTITY NAME]",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text("1. Independent Educational Platform", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "ExamPrepOS is an independent educational productivity application. It is NOT affiliated with, authorized by, or endorsed by NTA, UPSC, IITs, IIMs, CBSE, or any examination board.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text("2. No Guarantee of Exam Outcomes", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "Practice questions, AI analysis, mock exams, and analytics are provided strictly as preparatory study aids. We do NOT guarantee exam selection, rank, cutoff clearance, or marks.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text("3. Acceptable Use", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "You agree not to reverse engineer the application, scrape mock question datasets, or distribute pirated examination material.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text("4. Purchases & Subscriptions", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "Paid subscription plans (when enabled) are processed exclusively through Google Play Billing and are subject to Google Play Refund Policies.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showTermsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showDisclaimerDialog) {
        AlertDialog(
            onDismissRequest = { showDisclaimerDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = ExamPrepColors.Amber70)
                    Text("Official Disclaimer", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = ExamPrepColors.Amber70.copy(alpha = 0.15f),
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "NON-AFFILIATION NOTICE",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ExamPrepColors.Amber70,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Text(
                        "ExamPrepOS is an independent preparation platform designed to help students organize syllabus tracking, revision schedules, and practice tests.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        "ExamPrepOS is NOT an official application of, nor is it associated with, sponsored by, or endorsed by:\n" +
                        "• National Testing Agency (NTA)\n" +
                        "• Union Public Service Commission (UPSC)\n" +
                        "• Indian Institutes of Technology (IITs / JEE Apex Board)\n" +
                        "• Indian Institutes of Management (IIMs / CAT Committee)\n" +
                        "• Central Board of Secondary Education (CBSE)\n" +
                        "• Any Central or State Government Department.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        "All exam names, acronyms, and curriculum titles are property of their respective owners. Their mention in this app is strictly for descriptive categorization under fair use.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showDisclaimerDialog = false }) {
                    Text("Understood")
                }
            }
        )
    }

    if (showLicensesDialog) {
        AlertDialog(
            onDismissRequest = { showLicensesDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Code, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Open-Source Licenses", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "ExamPrepOS is built using open-source software and open frameworks:",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    val licenses = listOf(
                        "Android Jetpack Compose" to "Apache License 2.0 (Google LLC)",
                        "Kotlin Coroutines & Serialization" to "Apache License 2.0 (JetBrains s.r.o.)",
                        "AndroidX Room & WorkManager" to "Apache License 2.0 (Google LLC)",
                        "Dagger Hilt" to "Apache License 2.0 (Google LLC)",
                        "Firebase Android SDK" to "Apache License 2.0 (Google LLC)",
                        "Google Mobile Ads SDK (AdMob)" to "Google Play Services Terms of Service",
                        "Timber" to "Apache License 2.0 (Jake Wharton)",
                        "Coil Image Loader" to "Apache License 2.0 (Coil Contributors)",
                        "Lottie Compose" to "Apache License 2.0 (Airbnb Inc.)"
                    )
                    licenses.forEach { (lib, lic) ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(lib, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                Text(lic, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showLicensesDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showContactPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showContactPrivacyDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.SupportAgent, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Privacy & Grievance Contact", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "In accordance with the Digital Personal Data Protection Act, 2023 (DPDPA) and Information Technology Rules:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Data Protection & Grievance Officer:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                            Text("Name: Grievance Officer, ExamPrepOS", style = MaterialTheme.typography.bodySmall)
                            Text("Legal Entity: [LEGAL ENTITY NAME]", style = MaterialTheme.typography.bodySmall)
                            Text("Privacy Email: [PRIVACY EMAIL]", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium))
                            Text("Support Email: [SUPPORT EMAIL]", style = MaterialTheme.typography.bodySmall)
                            Text("Office Address: [BUSINESS ADDRESS]", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Text(
                        "⏱️ Grievance SLA: All inquiries and deletion requests are acknowledged within 48 hours and resolved within 30 days.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showContactPrivacyDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showExportDataDialog) {
        AlertDialog(
            onDismissRequest = { showExportDataDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Download, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Export Preparation Data", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Under DPDPA 2023 right to data portability, your local data summary is:")
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("User Name: ${currentUser?.name ?: "Guest Learner"}", style = MaterialTheme.typography.bodySmall)
                            Text("Email: ${currentUser?.email ?: "Offline Mode"}", style = MaterialTheme.typography.bodySmall)
                            Text("Target Study Hours: ${uiState.dailyHours.toInt()} hrs/day", style = MaterialTheme.typography.bodySmall)
                            Text("Active Tier: ${currentUser?.tier?.name ?: "FREE"}", style = MaterialTheme.typography.bodySmall)
                            Text("Storage: SQLite Room Database (Local encrypted app sandbox)", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Text(
                        "All your test sessions, bookmarks, and revision logs are stored on this device in private app storage.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showExportDataDialog = false }) {
                    Text("Done")
                }
            }
        )
    }

    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { if (!uiState.isDeletingData) showDeleteAccountDialog = false },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = { Text("Delete Account & All Data?", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "This action is permanent and IRREVERSIBLE. In accordance with Google Play's User Data & Account Deletion Policy and Indian DPDPA 2023, initiating this will immediately:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        "• Permanently delete your Firebase authentication record\n" +
                        "• Erase all quiz attempts, accuracy records, and marks\n" +
                        "• Clear your customized study plan, streak, and daily targets\n" +
                        "• Delete all saved bookmarks and offline question downloads\n" +
                        "• Reset local databases to default catalog state",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                    )
                    if (uiState.isDeletingData) {
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.width(12.dp))
                            Text("Purging all user records...", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAllUserData {
                            showDeleteAccountDialog = false
                            onLoggedOut()
                        }
                    },
                    enabled = !uiState.isDeletingData,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Permanently Delete Everything", color = MaterialTheme.colorScheme.onError, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteAccountDialog = false },
                    enabled = !uiState.isDeletingData
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // ── Main UI Layout ──────────────────────────────────────────────────────

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Box(modifier = Modifier.padding(horizontal = Spacing.MD, vertical = Spacing.MD)) {
            Column {
                Text(
                    text = "Settings & Preferences",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Manage your study habits, privacy controls, and legal disclosures",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = Spacing.MD),
            verticalArrangement = Arrangement.spacedBy(Spacing.SM)
        ) {

            // ── Viral Referral Card ───────────────────────────────────────────
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), MaterialTheme.shapes.large)
                ) {
                    Row(
                        modifier = Modifier.padding(Spacing.MD).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.MD),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("🤝", style = MaterialTheme.typography.titleLarge)
                                }
                            }
                            Column {
                                Text(
                                    text = "Study Partner Program",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = "Invite a friend & unlock 1 month of Pro features",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                        Button(
                            onClick = { showReferralDialog = true },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Invite", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

            // ── Offline Question Bank & Downloads ─────────────────────────────
            item { SectionHeader("Question Bank & Offline Downloads") }
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToQuestionLibrary() }
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), MaterialTheme.shapes.large)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.MD),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Download,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Question Library",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Surface(
                                    color = ExamPrepColors.Success.copy(alpha = 0.15f),
                                    shape = MaterialTheme.shapes.extraSmall
                                ) {
                                    Text(
                                        text = "500 Qs / Exam",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = ExamPrepColors.Success,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Download curated 100-question papers for 100% offline practice anytime.",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ── Pro Membership & Monetization Tier ─────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionHeader("Subscription & Plans")
                    Surface(
                        color = ExamPrepColors.Amber70.copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.extraSmall
                    ) {
                        Text(
                            text = "COMING SOON",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ExamPrepColors.Amber70,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
            item {
                val currentTier = currentUser?.tier ?: SubscriptionTier.FREE
                val tierBadgeColor = when (currentTier) {
                    SubscriptionTier.PRO -> ExamPrepColors.Amber70
                    SubscriptionTier.AD_FREE -> androidx.compose.ui.graphics.Color(0xFF06B6D4)
                    SubscriptionTier.FREE -> androidx.compose.ui.graphics.Color(0xFF94A3B8)
                }
                val tierTitle = when (currentTier) {
                    SubscriptionTier.PRO -> "Pro Rank Accelerator"
                    SubscriptionTier.AD_FREE -> "Ad-Free Lite Pass"
                    SubscriptionTier.FREE -> "Free Starter Plan"
                }
                val tierDesc = "Early Access Beta: All core features, offline downloads, and syllabus trackers are free! Supported by AdMob ads. Premium AI tiers launching soon."

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToSubscription() }
                        .border(1.dp, tierBadgeColor.copy(alpha = 0.5f), MaterialTheme.shapes.large)
                ) {
                    Column(modifier = Modifier.padding(Spacing.MD)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    color = tierBadgeColor.copy(alpha = 0.2f),
                                    shape = MaterialTheme.shapes.extraSmall
                                ) {
                                    Text(
                                        text = currentTier.name,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = tierBadgeColor,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                                Text(
                                    text = tierTitle,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                            Text(
                                text = "Preview Plans →",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ExamPrepColors.Amber70,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = tierDesc,
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }

            // ── Account & Session Section ──────────────────────────────────────
            item { SectionHeader("Account & Session") }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SettingsTile(
                        icon = Icons.Default.Person,
                        label = "Signed In As",
                        value = currentUser?.name ?: "Not Signed In"
                    )
                    SettingsTile(
                        icon = Icons.Default.Email,
                        label = "Email Address",
                        value = currentUser?.email ?: "No Active Account"
                    )
                    SettingsTile(
                        icon = Icons.Default.MarkEmailRead,
                        label = "Email Verification",
                        value = if (currentUser?.isEmailVerified == true) "Verified ✓" else "Pending Verification ⚠️"
                    )
                    SettingsTile(
                        icon = Icons.Default.Security,
                        label = "Local Data Ownership",
                        value = if (currentUser != null) "Verified (${currentUser?.email})" else "Isolated Workspace"
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f), MaterialTheme.shapes.medium)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Spacing.MD, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Spacing.SM)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    "Log Out Account",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                            Button(
                                onClick = { showLogoutDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.15f)),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp)
                            ) {
                                Text("Log Out", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }
            }

            // ── Study Schedule ────────────────────────────────────────────────
            item { SectionHeader("Study Schedule") }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SettingsTile(Icons.Default.AccessTime, "Target Daily Hours", "${uiState.dailyHours.toInt()} hours / day")
                    SettingsTile(Icons.Default.CalendarToday, "Study Days per Week", "6 days")
                    SettingsTile(Icons.Default.Schedule, "Focus Session Length", "50 minutes")
                    SettingsTile(Icons.Default.WbSunny, "Preferred Study Time", "Morning")
                }
            }

            // ── Notifications ─────────────────────────────────────────────────
            item { SectionHeader("Notifications") }
            item {
                SwitchSettingsTile(
                    icon = Icons.Default.NotificationsActive,
                    label = "Daily Study & Revision Reminder",
                    checked = uiState.studyReminderEnabled,
                    onCheckedChange = { viewModel.toggleStudyReminder(it) }
                )
            }
            item {
                SettingsTile(Icons.Default.AlarmOn, "Reminder Time", "8:00 AM")
            }

            // ── Sound & Feedback ──────────────────────────────────────────────
            item { SectionHeader("Sound & Feedback") }
            item {
                SwitchSettingsTile(
                    icon = Icons.Default.Notifications,
                    label = "Audio Cues & Chimes",
                    checked = uiState.soundEnabled,
                    onCheckedChange = { viewModel.toggleSound(it) }
                )
            }
            item {
                SwitchSettingsTile(
                    icon = Icons.Default.Vibration,
                    label = "Haptic Feedback",
                    checked = uiState.vibrationEnabled,
                    onCheckedChange = { viewModel.toggleVibration(it) }
                )
            }

            // ── Appearance ────────────────────────────────────────────────────
            item { SectionHeader("Appearance") }
            item {
                SwitchSettingsTile(
                    icon = Icons.Default.DarkMode,
                    label = "Dark Mode (Eye Comfort)",
                    checked = isDarkMode,
                    onCheckedChange = { ThemeState.setDarkMode(it) }
                )
            }

            // ── Data & Consent Controls ───────────────────────────────────────
            item { SectionHeader("Data & Consent") }
            item {
                SwitchSettingsTile(
                    icon = Icons.Default.AdsClick,
                    label = "Personalized Ads (AdMob)",
                    subtitle = "When disabled, non-personalized ads (NPA) are requested per Google Play policy.",
                    checked = uiState.adPersonalizationEnabled,
                    onCheckedChange = { viewModel.toggleAdPersonalization(it) }
                )
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SettingsTile(
                        icon = Icons.Default.Download,
                        label = "Export Preparation Progress",
                        value = null,
                        onClick = { showExportDataDialog = true }
                    )
                    SettingsTile(
                        icon = Icons.Default.DeleteForever,
                        label = "Delete Account & All Data",
                        value = null,
                        isDestructive = true,
                        onClick = { showDeleteAccountDialog = true }
                    )
                }
            }

            // ── Privacy & Legal ───────────────────────────────────────────────
            item { SectionHeader("Privacy & Legal") }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SettingsTile(
                        icon = Icons.Default.Policy,
                        label = "Privacy Policy",
                        value = null,
                        onClick = { showPrivacyPolicyDialog = true }
                    )
                    SettingsTile(
                        icon = Icons.Default.Gavel,
                        label = "Terms of Service",
                        value = null,
                        onClick = { showTermsDialog = true }
                    )
                    SettingsTile(
                        icon = Icons.Default.VerifiedUser,
                        label = "Official Disclaimer & Non-Affiliation",
                        value = null,
                        onClick = { showDisclaimerDialog = true }
                    )
                    SettingsTile(
                        icon = Icons.Default.Code,
                        label = "Open-Source Licenses",
                        value = null,
                        onClick = { showLicensesDialog = true }
                    )
                    SettingsTile(
                        icon = Icons.Default.SupportAgent,
                        label = "Contact Privacy & Grievance Team",
                        value = null,
                        onClick = { showContactPrivacyDialog = true }
                    )
                }
            }

            // ── About ─────────────────────────────────────────────────────────
            item { SectionHeader("About") }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SettingsTile(
                        icon = Icons.Default.Info,
                        label = "ExamPrep OS Version",
                        value = "1.2.0 (Build 2026.1)"
                    )
                }
            }

            item { Spacer(Modifier.height(Spacing.XL)) }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        modifier = Modifier.padding(top = Spacing.MD, bottom = 4.dp),
        style = MaterialTheme.typography.labelSmall.copy(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            letterSpacing = TextUnit(1f, TextUnitType.Sp)
        )
    )
}

@Composable
private fun SettingsTile(
    icon: ImageVector,
    label: String,
    value: String? = null,
    isDestructive: Boolean = false,
    onClick: () -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(
                1.dp,
                if (isDestructive) MaterialTheme.colorScheme.error.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outlineVariant,
                MaterialTheme.shapes.medium
            )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.MD, vertical = 12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.SM),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
                Text(
                    label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                )
            }
            if (value != null) {
                Text(value, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
            } else {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SwitchSettingsTile(
    icon: ImageVector,
    label: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.medium)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.MD, vertical = if (subtitle != null) 10.dp else 6.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.SM),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Column {
                    Text(label, style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface))
                    if (subtitle != null) {
                        Text(
                            subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}
