package com.examprep.android.feature.subscription

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.examprep.android.feature.auth.AuthStore
import com.examprep.core.designsystem.theme.ExamPrepColors
import com.examprep.core.designsystem.theme.Spacing
import com.examprep.domain.model.SubscriptionPlan
import com.examprep.domain.model.SubscriptionTier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    onNavigateBack: () -> Unit,
    authStore: AuthStore = remember { AuthStore.get() }
) {
    val currentUser by authStore.currentUser.collectAsState()
    var selectedPlan by remember { mutableStateOf<SubscriptionPlan>(SubscriptionPlan.PRO_MONTHLY) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showComingSoonDialog by remember { mutableStateOf(false) }
    var activatedPlanName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Preparation Plans",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
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
                .padding(horizontal = Spacing.MD, vertical = Spacing.SM),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.MD)
        ) {
            // ── Hero Banner ─────────────────────────────────────────────────────
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF13171F),
                border = BorderStroke(1.dp, Color(0xFF232B3B))
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.MD),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = ExamPrepColors.Amber70.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, ExamPrepColors.Amber70.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.HourglassTop,
                                contentDescription = null,
                                tint = ExamPrepColors.Amber70,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "PREMIUM TIERS COMING SOON",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ExamPrepColors.Amber70,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }
                    }

                    Text(
                        text = "Premium Plans & AI Upgrades",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    )

                    Text(
                        text = "All core features, offline question library downloads (500 Qs per exam), and study plans are 100% free during our launch phase, supported by AdMob ads. Paid tiers will launch soon!",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF94A3B8),
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(horizontal = Spacing.SM)
                    )

                    // Current Active Status
                    val currentTier = currentUser?.tier ?: SubscriptionTier.FREE
                    Surface(
                        modifier = Modifier.padding(top = 4.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF1C222D)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (currentTier) {
                                            SubscriptionTier.PRO -> ExamPrepColors.Amber70
                                            SubscriptionTier.AD_FREE -> Color(0xFF06B6D4)
                                            SubscriptionTier.FREE -> Color(0xFF94A3B8)
                                        }
                                    )
                            )
                            Text(
                                text = "Current Status: ${
                                    when (currentTier) {
                                        SubscriptionTier.PRO -> "Pro Rank Accelerator Active"
                                        SubscriptionTier.AD_FREE -> "Ad-Free Lite Pass Active"
                                        SubscriptionTier.FREE -> "Free Tier"
                                    }
                                }",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFFE2E8F0),
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }

            // ── Plan Cards ──────────────────────────────────────────────────────

            // Plan 1: Ad-Free Lite Pass (₹30 for 2 Months)
            PlanCard(
                plan = SubscriptionPlan.AD_FREE_2_MONTHS,
                isSelected = selectedPlan == SubscriptionPlan.AD_FREE_2_MONTHS,
                accentColor = Color(0xFF06B6D4),
                onSelect = { selectedPlan = SubscriptionPlan.AD_FREE_2_MONTHS }
            )

            // Plan 2: Pro Rank Accelerator (₹50 / Month)
            PlanCard(
                plan = SubscriptionPlan.PRO_MONTHLY,
                isSelected = selectedPlan == SubscriptionPlan.PRO_MONTHLY,
                accentColor = ExamPrepColors.Amber70,
                onSelect = { selectedPlan = SubscriptionPlan.PRO_MONTHLY }
            )

            // ── Action Button ───────────────────────────────────────────────────
            Button(
                onClick = {
                    showComingSoonDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedPlan == SubscriptionPlan.PRO_MONTHLY) ExamPrepColors.Amber70 else Color(0xFF06B6D4),
                    contentColor = Color(0xFF090B0E)
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.HourglassTop,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Coming Soon • Early Access (100% Free)",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // ── Feature Comparison Table ────────────────────────────────────────
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF13171F),
                border = BorderStroke(1.dp, Color(0xFF232B3B))
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.MD),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Plan Comparison Breakdown",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    ComparisonRow(feature = "100% Ad-Free Study Sessions", free = false, adFree = true, pro = true)
                    ComparisonRow(feature = "Focus Timer & Offline Caching", free = true, adFree = true, pro = true)
                    ComparisonRow(feature = "AI Weakness Surgery & Trap Radar", free = false, adFree = false, pro = true)
                    ComparisonRow(feature = "Unlimited National Full Mocks", free = false, adFree = false, pro = true)
                    ComparisonRow(feature = "AIR Percentile & Rank Prediction", free = false, adFree = false, pro = true)
                    ComparisonRow(feature = "Step-by-Step AI Solutions", free = false, adFree = false, pro = true)
                }
            }

            // Trust badge
            Row(
                modifier = Modifier.padding(bottom = Spacing.LG),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "100% Secure Simulated Checkout • Instant Activation",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF64748B)
                    )
                )
            }
        }
    }

    if (showComingSoonDialog) {
        AlertDialog(
            onDismissRequest = { showComingSoonDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.HourglassTop,
                        contentDescription = null,
                        tint = ExamPrepColors.Amber70,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Premium Tiers Coming Soon 🚀",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "ExamPrep OS is currently 100% free during our launch phase, supported by Google AdMob ads.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                    )
                    Text(
                        text = "All question papers (500 curated questions per exam), offline downloads, study planner, and full-length timed mocks are unlocked at zero cost for every student.\n\nPaid subscriptions with ad-free mode and AI-driven deep diagnostics will launch in an upcoming release!",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showComingSoonDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ExamPrepColors.Amber70)
                ) {
                    Text("Awesome! Continue Free", color = Color(0xFF090B0E), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showComingSoonDialog = false
                        authStore.upgradeSubscription(selectedPlan)
                        activatedPlanName = selectedPlan.title
                        showSuccessDialog = true
                    }
                ) {
                    Text("Simulate Unlock (Dev)", color = Color(0xFF94A3B8))
                }
            },
            containerColor = Color(0xFF13171F),
            textContentColor = Color(0xFFE2E8F0),
            titleContentColor = Color.White
        )
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onNavigateBack()
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = ExamPrepColors.Success,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Subscription Active!",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            },
            text = {
                Text(
                    text = "Congratulations! Your $activatedPlanName is now active. All corresponding features and capabilities have been unlocked on your account.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExamPrepColors.Sage70)
                ) {
                    Text("Go to Workspace", color = Color.White)
                }
            },
            containerColor = Color(0xFF13171F),
            textContentColor = Color(0xFFE2E8F0),
            titleContentColor = Color.White
        )
    }
}

@Composable
private fun PlanCard(
    plan: SubscriptionPlan,
    isSelected: Boolean,
    accentColor: Color,
    onSelect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(18.dp),
        color = if (isSelected) Color(0xFF181F2C) else Color(0xFF13171F),
        border = BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) accentColor else Color(0xFF232B3B)
        )
    ) {
        Column(
            modifier = Modifier.padding(Spacing.MD),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header with badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = plan.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = plan.billingCycleText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = accentColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                plan.badge?.let { badgeText ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = accentColor.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = accentColor,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = Color(0xFF232B3B), thickness = 1.dp)

            // Features list
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                plan.highlights.forEach { highlight ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = highlight,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFCBD5E1)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ComparisonRow(
    feature: String,
    free: Boolean,
    adFree: Boolean,
    pro: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = feature,
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFF94A3B8),
                fontSize = 12.sp
            ),
            modifier = Modifier.weight(1.5f)
        )
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusIcon(free, Color(0xFF64748B))
            StatusIcon(adFree, Color(0xFF06B6D4))
            StatusIcon(pro, ExamPrepColors.Amber70)
        }
    }
}

@Composable
private fun StatusIcon(enabled: Boolean, tint: Color) {
    if (enabled) {
        Icon(
            Icons.Default.Check,
            contentDescription = "Included",
            tint = tint,
            modifier = Modifier.size(16.dp)
        )
    } else {
        Icon(
            Icons.Default.Close,
            contentDescription = "Not included",
            tint = Color(0xFF475569),
            modifier = Modifier.size(14.dp)
        )
    }
}
