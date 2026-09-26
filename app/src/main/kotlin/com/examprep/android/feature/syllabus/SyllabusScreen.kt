package com.examprep.android.feature.syllabus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.examprep.core.designsystem.theme.ExamPrepColors
import com.examprep.core.designsystem.theme.Spacing
import com.examprep.domain.model.*

@Composable
fun SyllabusScreen(
    onStartQuiz: (String) -> Unit,
    viewModel: SyllabusViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (val uiState = state) {
            is SyllabusUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Text(
                            "Loading syllabus syllabus & mastery...",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }
            is SyllabusUiState.Error -> {
                Box(Modifier.fillMaxSize().padding(Spacing.MD), contentAlignment = Alignment.Center) {
                    Text("Error: ${uiState.message}", color = MaterialTheme.colorScheme.error)
                }
            }
            is SyllabusUiState.Success -> {
                var selectedSubjectIndex by remember { mutableIntStateOf(0) }
                val subjects = uiState.exam.subjects
                val activeSubject = subjects.getOrNull(selectedSubjectIndex) ?: subjects.firstOrNull()

                LazyColumn(
                    contentPadding = PaddingValues(horizontal = Spacing.MD, vertical = Spacing.MD),
                    verticalArrangement = Arrangement.spacedBy(Spacing.MD)
                ) {
                    // ── Screen Header ──────────────────────────────────────────────
                    item {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                        shape = MaterialTheme.shapes.extraSmall
                                    ) {
                                        Text(
                                            text = "SYLLABUS & TOPIC MASTERY",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 0.8.sp
                                            )
                                        )
                                    }
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        text = uiState.exam.name,
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            color = MaterialTheme.colorScheme.onBackground,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // ── Rapid Diagnostic / 10-Q Sprint Trigger Card ────────────────
                    item {
                        Card(
                            onClick = { onStartQuiz("quick_quiz") },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                            ),
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
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = MaterialTheme.shapes.medium,
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Default.Bolt,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                    Column {
                                        Text(
                                            text = "Mixed Diagnostic Quiz",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                        Text(
                                            text = "5 High-Yield Questions · Real Scoring",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                }
                                Button(
                                    onClick = { onStartQuiz("quick_quiz") },
                                    shape = MaterialTheme.shapes.medium,
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text("Start", style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }

                    // ── Subject Switcher Pill Tabs ─────────────────────────────────
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(subjects) { index, subject ->
                                val isSelected = index == selectedSubjectIndex
                                val subjColor = Color(subject.color)
                                Surface(
                                    onClick = { selectedSubjectIndex = index },
                                    shape = MaterialTheme.shapes.small,
                                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                        shape = MaterialTheme.shapes.small
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Surface(
                                            color = subjColor,
                                            shape = MaterialTheme.shapes.extraSmall,
                                            modifier = Modifier.size(8.dp)
                                        ) {}
                                        Text(
                                            text = subject.name,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ── Selected Subject Chapters and Topics ───────────────────────
                    if (activeSubject != null) {
                        item {
                            SubjectProgressHeader(
                                subject = activeSubject,
                                masteryMap = uiState.masteryMap
                            )
                        }

                        items(activeSubject.chapters, key = { it.id }) { chapter ->
                            ChapterAccordionCard(
                                chapter = chapter,
                                masteryMap = uiState.masteryMap,
                                onStartQuiz = onStartQuiz
                            )
                        }
                    }

                    item { Spacer(Modifier.height(Spacing.XL)) }
                }
            }
        }
    }
}

@Composable
private fun SubjectProgressHeader(
    subject: Subject,
    masteryMap: Map<String, MasteryRecord>
) {
    val totalTopics = subject.chapters.flatMap { it.topics }
    val mastered = totalTopics.count { masteryMap[it.id]?.masteryState == MasteryState.MASTERED }
    val practicing = totalTopics.count {
        val s = masteryMap[it.id]?.masteryState
        s == MasteryState.PRACTICING || s == MasteryState.IN_PROGRESS || s == MasteryState.LEARNED
    }
    val progress = if (totalTopics.isNotEmpty()) mastered.toFloat() / totalTopics.size else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                        text = "${subject.name} Progress",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "$mastered of ${totalTopics.size} topics mastered (${(progress * 100).toInt()}%)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
                Surface(
                    color = ExamPrepColors.Success.copy(alpha = 0.15f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "${(progress * 100).toInt()}% Done",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ExamPrepColors.Success,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
            Spacer(Modifier.height(Spacing.SM))
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = Color(subject.color),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
private fun ChapterAccordionCard(
    chapter: Chapter,
    masteryMap: Map<String, MasteryRecord>,
    onStartQuiz: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(Spacing.MD),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = chapter.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "${chapter.topics.size} Topics · Est. ${chapter.estimatedHours.toInt()}h study",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    chapter.topics.forEachIndexed { index, topic ->
                        val record = masteryMap[topic.id]
                        val state = record?.masteryState ?: topic.masteryState
                        TopicItemRow(
                            topic = topic,
                            mastery = state,
                            onPractice = { onStartQuiz("quiz_${topic.id}") }
                        )
                        if (index < chapter.topics.size - 1) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f),
                                modifier = Modifier.padding(start = Spacing.MD)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopicItemRow(
    topic: Topic,
    mastery: MasteryState,
    onPractice: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.MD, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = Spacing.SM)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MasteryStatusBadge(mastery = mastery)
                if (topic.importanceWeight >= 2.0f) {
                    Surface(
                        color = ExamPrepColors.Amber70.copy(alpha = 0.15f),
                        shape = MaterialTheme.shapes.extraSmall
                    ) {
                        Text(
                            text = "High Yield",
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ExamPrepColors.Amber70,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = topic.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }

        OutlinedButton(
            onClick = onPractice,
            shape = MaterialTheme.shapes.small,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text("Practice", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun MasteryStatusBadge(mastery: MasteryState) {
    val (label, color) = when (mastery) {
        MasteryState.MASTERED -> "Mastered" to ExamPrepColors.MasteryMastered
        MasteryState.PRACTICING -> "Practicing" to ExamPrepColors.MasteryPracticing
        MasteryState.LEARNED -> "Learned" to ExamPrepColors.MasteryLearned
        MasteryState.IN_PROGRESS -> "In Progress" to ExamPrepColors.MasteryInProgress
        MasteryState.REVISION_DUE -> "Revision Due" to ExamPrepColors.MasteryRevisionDue
        MasteryState.NOT_STARTED -> "Not Started" to ExamPrepColors.MasteryNotStarted
    }

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Surface(
                color = color,
                shape = MaterialTheme.shapes.extraSmall,
                modifier = Modifier.size(6.dp)
            ) {}
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = color,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                )
            )
        }
    }
}

