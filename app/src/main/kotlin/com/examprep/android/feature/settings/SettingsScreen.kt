package com.examprep.android.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
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

@Composable
fun SettingsScreen(
    onLoggedOut: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDarkMode by ThemeState.isDarkMode.collectAsState()
    var showReferralDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val authStore = remember { com.examprep.android.feature.auth.AuthStore.get() }
    val currentUser by authStore.currentUser.collectAsState()

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    Icons.Default.Logout,
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
                    text = "Manage your study habits, notifications and subscription",
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

            // ── Viral Referral Card (Growth Flywheel) ───────────────────────────
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

            // ── Pro Membership & Monetization Tier ─────────────────────────────
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large)
                ) {
                    Column(modifier = Modifier.padding(Spacing.MD)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    color = ExamPrepColors.Amber70.copy(alpha = 0.2f),
                                    shape = MaterialTheme.shapes.extraSmall
                                ) {
                                    Text(
                                        text = "PRO PLAN",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = ExamPrepColors.Amber70,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                                Text(
                                    "ExamPrep Pro Active",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                            Text(
                                "Trial Active",
                                style = MaterialTheme.typography.labelSmall.copy(color = ExamPrepColors.Success)
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Full 10-Year PYQ Papers, Step-by-Step AI Explanations & Error Analytics included.",
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
                        value = currentUser?.name ?: "Guest Aspirant"
                    )
                    SettingsTile(
                        icon = Icons.Default.Email,
                        label = "Email Address",
                        value = currentUser?.email ?: "guest@examprep.io"
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
                                    Icons.Default.Logout,
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

            item { SectionHeader("Study Schedule") }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SettingsTile(Icons.Default.AccessTime, "Target Daily Hours", "${uiState.dailyHours.toInt()} hours / day")
                    SettingsTile(Icons.Default.CalendarToday, "Study Days per Week", "6 days")
                    SettingsTile(Icons.Default.Schedule, "Focus Session Length", "50 minutes")
                    SettingsTile(Icons.Default.WbSunny, "Preferred Study Time", "Morning")
                }
            }

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

            item { SectionHeader("Appearance") }
            item {
                SwitchSettingsTile(
                    icon = Icons.Default.DarkMode,
                    label = "Dark Mode (Eye Comfort)",
                    checked = isDarkMode,
                    onCheckedChange = { ThemeState.setDarkMode(it) }
                )
            }

            item { SectionHeader("Data & Privacy") }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SettingsTile(Icons.Default.Download, "Export Preparation Progress", null)
                    SettingsTile(Icons.Default.DeleteForever, "Reset All Local Data", null, isDestructive = true)
                }
            }

            item { SectionHeader("About") }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SettingsTile(Icons.Default.Info, "ExamPrep OS Version", "1.2.0 (Build 2026.1)")
                    SettingsTile(Icons.Default.Policy, "Privacy Policy", null)
                    SettingsTile(Icons.Default.Gavel, "Terms of Service", null)
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
private fun SettingsTile(icon: ImageVector, label: String, value: String?, isDestructive: Boolean = false) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.medium)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.MD, vertical = 12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.SM)) {
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
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SwitchSettingsTile(icon: ImageVector, label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.medium)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.MD, vertical = 6.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.SM)) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary)
                Text(label, style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface))
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}
