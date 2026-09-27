package com.examprep.android.feature.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.examprep.android.feature.auth.AuthStore
import com.examprep.core.designsystem.theme.ExamPrepColors
import com.examprep.core.designsystem.theme.Spacing
import kotlinx.coroutines.delay

enum class OnboardingStep(val stepNumber: Int, val title: String, val subtitle: String) {
    EXAM_SELECTION(1, "Target Exam & Year", "Which competitive exam are you targeting?"),
    ACADEMIC_STAGE(2, "Preparation Stage", "Help us tailor your syllabus pacing"),
    STUDY_ROUTINE(3, "Daily Study Habits", "Design your ideal daily focus routine"),
    SCORE_BOTTLENECKS(4, "Score Bottlenecks", "Identify what holds your marks back"),
    AI_BLUEPRINT(5, "AI Blueprint Calibration", "Synthesizing your personalized preparation engine")
}

data class ExamChoice(
    val id: String,
    val name: String,
    val category: String,
    val subjects: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentOnboardingScreen(
    onOnboardingComplete: () -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    val authStore = remember { AuthStore.get() }
    val currentUser by authStore.currentUser.collectAsState()

    var currentStep by remember { mutableStateOf(OnboardingStep.EXAM_SELECTION) }

    // User selections state
    var selectedExam by remember { mutableStateOf(currentUser?.targetExam ?: "JEE Main 2026") }
    var selectedYear by remember { mutableIntStateOf(currentUser?.targetYear ?: 2026) }
    var academicStage by remember { mutableStateOf("Class 12 / Competitive Sprints") }
    var baselineConfidence by remember { mutableStateOf("Moderate (40-60% covered)") }
    var dailyStudyHours by remember { mutableFloatStateOf(currentUser?.dailyStudyTargetHours ?: 5.0f) }
    var peakEnergyTime by remember { mutableStateOf("Early Bird (5 AM - 10 AM)") }
    var sessionFormat by remember { mutableStateOf("50-min Deep Block") }
    var primaryBottleneck by remember { mutableStateOf("Negative Marking & Conceptual Traps") }

    // AI Synthesis animated progress
    var calibrationProgress by remember { mutableFloatStateOf(0f) }
    var calibrationStageText by remember { mutableStateOf("Initializing neural diagnostic parameters...") }

    LaunchedEffect(currentStep) {
        if (currentStep == OnboardingStep.AI_BLUEPRINT) {
            calibrationProgress = 0f
            calibrationStageText = "Parsing official ${selectedExam} curriculum..."
            delay(600)
            calibrationProgress = 0.35f
            calibrationStageText = "Calibrating FSRS spaced recall intervals for ${dailyStudyHours.toInt()}h daily pace..."
            delay(700)
            calibrationProgress = 0.70f
            calibrationStageText = "Configuring AI Weakness Surgery for '${primaryBottleneck}'..."
            delay(700)
            calibrationProgress = 1.0f
            calibrationStageText = "Preparation blueprint calibrated! Ready to launch."
        }
    }

    val availableExams = remember {
        listOf(
            ExamChoice("jee_main", "JEE Main", "Engineering", "Physics • Chemistry • Mathematics", Icons.Default.Science),
            ExamChoice("jee_adv", "JEE Advanced", "IIT Admissions", "Multi-Concept Problem Sets", Icons.Default.Calculate),
            ExamChoice("neet_ug", "NEET UG", "Medical", "Physics • Chemistry • Biology", Icons.Default.Biotech),
            ExamChoice("upsc_cse", "UPSC CSE", "Civil Services", "GS • CSAT • Daily Current Affairs", Icons.Default.AccountBalance),
            ExamChoice("cat", "CAT", "Management", "VARC • DILR • Quantitative Aptitude", Icons.Default.TrendingUp),
            ExamChoice("gate", "GATE", "Engineering Post-Grad", "Technical Depth & Core Foundations", Icons.Default.Memory),
            ExamChoice("bitsat", "BITSAT", "Engineering & Speed", "Speed Simulator • English & Logic", Icons.Default.Speed)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Step ${currentStep.stepNumber} of 5",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ExamPrepColors.Sage70,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = currentStep.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    }
                },
                navigationIcon = {
                    if (currentStep != OnboardingStep.EXAM_SELECTION) {
                        IconButton(onClick = {
                            currentStep = when (currentStep) {
                                OnboardingStep.ACADEMIC_STAGE -> OnboardingStep.EXAM_SELECTION
                                OnboardingStep.STUDY_ROUTINE -> OnboardingStep.ACADEMIC_STAGE
                                OnboardingStep.SCORE_BOTTLENECKS -> OnboardingStep.STUDY_ROUTINE
                                OnboardingStep.AI_BLUEPRINT -> OnboardingStep.SCORE_BOTTLENECKS
                                else -> OnboardingStep.EXAM_SELECTION
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    } else {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
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
                .padding(horizontal = Spacing.LG)
        ) {
            // Overall Step Progress Bar
            LinearProgressIndicator(
                progress = { currentStep.stepNumber / 5f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(100.dp)),
                color = ExamPrepColors.Sage70,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(Modifier.height(Spacing.MD))

            Text(
                text = currentStep.subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(Modifier.height(Spacing.LG))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (currentStep) {
                    OnboardingStep.EXAM_SELECTION -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(Spacing.MD)
                        ) {
                            // Target Year Selector
                            Text(
                                text = "Target Exam Year",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                listOf(2025, 2026, 2027).forEach { year ->
                                    val isSelected = selectedYear == year
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedYear = year },
                                        label = {
                                            Text(
                                                "Target $year",
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            Spacer(Modifier.height(4.dp))

                            Text(
                                text = "Select Examination",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            availableExams.forEach { exam ->
                                val isSelected = selectedExam.startsWith(exam.name)
                                Surface(
                                    onClick = { selectedExam = "${exam.name} $selectedYear" },
                                    shape = MaterialTheme.shapes.large,
                                    color = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) ExamPrepColors.Sage70 else MaterialTheme.colorScheme.outlineVariant,
                                            shape = MaterialTheme.shapes.large
                                        )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(Spacing.MD),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(Spacing.MD)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(46.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isSelected) ExamPrepColors.Sage70.copy(alpha = 0.2f)
                                                    else MaterialTheme.colorScheme.surfaceVariant
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = exam.icon,
                                                contentDescription = null,
                                                tint = if (isSelected) ExamPrepColors.Sage70 else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }

                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(
                                                    text = exam.name,
                                                    style = MaterialTheme.typography.titleMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                )
                                                Surface(
                                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = exam.category,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = exam.subjects,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontSize = 12.sp
                                                )
                                            )
                                        }

                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Selected",
                                                tint = ExamPrepColors.Sage70,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(Spacing.LG))
                        }
                    }

                    OnboardingStep.ACADEMIC_STAGE -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(Spacing.LG)
                        ) {
                            Text(
                                text = "Current Academic Status",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )

                            listOf(
                                "Class 11 Foundation" to "Building first-principles conceptual bedrock",
                                "Class 12 / Competitive Sprints" to "Balancing Board exams with high-yield entrance drills",
                                "Dropper / Dedicated Repeater" to "100% full-time focus on test velocity & rank push",
                                "College / Working Professional" to "Targeted evening and weekend high-yield study blocks"
                            ).forEach { (title, desc) ->
                                val isSelected = academicStage == title
                                SelectableChoiceCard(
                                    title = title,
                                    description = desc,
                                    isSelected = isSelected,
                                    onClick = { academicStage = title }
                                )
                            }

                            Spacer(Modifier.height(Spacing.SM))

                            Text(
                                text = "Current Baseline Confidence",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )

                            listOf(
                                "Foundational (Need concept explanation first)" to "Best with step-by-step topic deep dives",
                                "Moderate (40-60% covered, need PYQ practice)" to "Best with adaptive problem sets and spaced tests",
                                "Advanced (Revision & Mock Test Mastery)" to "Best with All-India mocks & AI Weakness Surgery"
                            ).forEach { (title, desc) ->
                                val isSelected = baselineConfidence == title
                                SelectableChoiceCard(
                                    title = title,
                                    description = desc,
                                    isSelected = isSelected,
                                    onClick = { baselineConfidence = title }
                                )
                            }
                        }
                    }

                    OnboardingStep.STUDY_ROUTINE -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(Spacing.LG)
                        ) {
                            // Daily Hours Card with Slider
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.large,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(modifier = Modifier.padding(Spacing.LG)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "Target Daily Hours",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Surface(
                                            color = ExamPrepColors.Sage70.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                "${dailyStudyHours.toInt()}h / day",
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    color = ExamPrepColors.Sage70,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(Spacing.MD))

                                    Slider(
                                        value = dailyStudyHours,
                                        onValueChange = { dailyStudyHours = it },
                                        valueRange = 2f..12f,
                                        steps = 9,
                                        colors = SliderDefaults.colors(
                                            thumbColor = ExamPrepColors.Sage70,
                                            activeTrackColor = ExamPrepColors.Sage70
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("2 Hours", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                        Text("6 Hours (Ideal)", style = MaterialTheme.typography.labelSmall.copy(color = ExamPrepColors.Sage70, fontWeight = FontWeight.Bold))
                                        Text("12 Hours", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                    }
                                }
                            }

                            Text(
                                text = "Peak Cognitive Energy Time",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )

                            listOf(
                                "🌅 Early Bird (5 AM - 10 AM)" to "Peak clarity for physics derivations and difficult math",
                                "☀️ Afternoon Surge (1 PM - 6 PM)" to "Ideal for full-length mock simulation under exam timing",
                                "🌙 Night Owl (8 PM - 2 AM)" to "Deep silence for problem solving and formula revision"
                            ).forEach { (title, desc) ->
                                val isSelected = peakEnergyTime == title
                                SelectableChoiceCard(
                                    title = title,
                                    description = desc,
                                    isSelected = isSelected,
                                    onClick = { peakEnergyTime = title }
                                )
                            }

                            Text(
                                text = "Focus Timer Format",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )

                            listOf(
                                "25-min Pomodoro" to "Quick high-intensity focus sprints + 5 min rest",
                                "50-min Deep Block" to "Standard optimal cognitive retention block (Recommended)",
                                "90-min Full Marathon" to "Builds stamina for real 3-hour competitive exams"
                            ).forEach { (title, desc) ->
                                val isSelected = sessionFormat == title
                                SelectableChoiceCard(
                                    title = title,
                                    description = desc,
                                    isSelected = isSelected,
                                    onClick = { sessionFormat = title }
                                )
                            }
                        }
                    }

                    OnboardingStep.SCORE_BOTTLENECKS -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(Spacing.MD)
                        ) {
                            Text(
                                text = "What is your biggest obstacle?",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "ExamPrep AI will customize your error diagnostics and daily drills to target this directly.",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )

                            Spacer(Modifier.height(4.dp))

                            listOf(
                                "Negative Marking & Conceptual Traps" to "Losing marks due to misleading option traps & sign errors",
                                "Time Pressure & Low Speed in Full Mocks" to "Running out of time before reaching high-scoring sections",
                                "Formula Decay & Retention Backlogs" to "Forgetting formulas and reactions after 2-3 weeks",
                                "Study Consistency & Procrastination" to "Struggling to maintain steady daily momentum across subjects"
                            ).forEach { (title, desc) ->
                                val isSelected = primaryBottleneck == title
                                SelectableChoiceCard(
                                    title = title,
                                    description = desc,
                                    isSelected = isSelected,
                                    onClick = { primaryBottleneck = title }
                                )
                            }
                        }
                    }

                    OnboardingStep.AI_BLUEPRINT -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(Spacing.LG),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(Modifier.height(Spacing.SM))

                            // Glowing AI Processing Ring
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape)
                                    .background(ExamPrepColors.Sage70.copy(alpha = 0.15f))
                                    .border(2.dp, ExamPrepColors.Sage70.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = ExamPrepColors.Sage70,
                                    modifier = Modifier.size(42.dp)
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Synthesizing Preparation OS",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = calibrationStageText,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = ExamPrepColors.Sage70,
                                        textAlign = TextAlign.Center
                                    )
                                )
                            }

                            LinearProgressIndicator(
                                progress = { calibrationProgress },
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(100.dp)),
                                color = ExamPrepColors.Sage70,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )

                            Spacer(Modifier.height(Spacing.SM))

                            // Summary Blueprint Card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.large,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(modifier = Modifier.padding(Spacing.LG), verticalArrangement = Arrangement.spacedBy(Spacing.MD)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "Personalized Strategy Profile",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                        Surface(
                                            color = ExamPrepColors.Success.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                "OPTIMIZED",
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = ExamPrepColors.Success,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }

                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                                    BlueprintRow(Icons.Default.School, "Target Exam", selectedExam)
                                    BlueprintRow(Icons.Default.Schedule, "Daily Commitment", "${dailyStudyHours.toInt()} Hours / Day")
                                    BlueprintRow(Icons.Default.WbSunny, "Peak Energy Slot", peakEnergyTime.substringBefore(" ("))
                                    BlueprintRow(Icons.Default.Timer, "Focus Interval", sessionFormat)
                                    BlueprintRow(Icons.Default.Healing, "Primary Focus", primaryBottleneck.substringBefore(" &"))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(Spacing.MD))

            // Bottom Navigation Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.LG),
                horizontalArrangement = Arrangement.spacedBy(Spacing.MD)
            ) {
                Button(
                    onClick = {
                        when (currentStep) {
                            OnboardingStep.EXAM_SELECTION -> currentStep = OnboardingStep.ACADEMIC_STAGE
                            OnboardingStep.ACADEMIC_STAGE -> currentStep = OnboardingStep.STUDY_ROUTINE
                            OnboardingStep.STUDY_ROUTINE -> currentStep = OnboardingStep.SCORE_BOTTLENECKS
                            OnboardingStep.SCORE_BOTTLENECKS -> currentStep = OnboardingStep.AI_BLUEPRINT
                            OnboardingStep.AI_BLUEPRINT -> {
                                // Commit final profile to AuthStore
                                authStore.updateProfile(
                                    name = currentUser?.name ?: "Yash Siwach",
                                    targetExam = selectedExam,
                                    targetYear = selectedYear,
                                    dailyHours = dailyStudyHours
                                )
                                onOnboardingComplete()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ExamPrepColors.Sage70,
                        contentColor = Color(0xFF090B0E)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (currentStep == OnboardingStep.AI_BLUEPRINT) "Launch My Preparation Space" else "Continue",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Icon(
                            imageVector = if (currentStep == OnboardingStep.AI_BLUEPRINT) Icons.Default.RocketLaunch else Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectableChoiceCard(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) ExamPrepColors.Sage70 else MaterialTheme.colorScheme.outlineVariant,
                shape = MaterialTheme.shapes.medium
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.MD),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                )
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = ExamPrepColors.Sage70,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun BlueprintRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.SM)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ExamPrepColors.Sage70,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}
