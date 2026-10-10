package com.examprep.android.feature.questionlibrary

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.examprep.core.designsystem.component.ExamPrepLoadingState
import com.examprep.core.designsystem.component.StatusChip
import com.examprep.data.local.entity.QuestionPaperMetaEntity

// ─────────────────────────────────────────────────────────────────────────────
// QUESTION LIBRARY SCREEN
//
// Shows 5 downloadable question papers per exam.
// Users can download any paper (100 questions each) while connected to Wi-Fi/data.
// Downloaded papers are available offline immediately after download.
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionLibraryScreen(
    onBack: () -> Unit,
    onStartMockTest: (paperId: String) -> Unit = {},
    viewModel: QuestionLibraryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val examTabs = remember(state.papers) {
        state.papers.map { it.examId to it.examName }.distinct()
    }

    val filteredPapers = remember(state.papers, state.selectedExamFilter) {
        if (state.selectedExamFilter == null) state.papers
        else state.papers.filter { it.examId == state.selectedExamFilter }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Question Library",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            "5 papers × 100 questions per exam",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ── Download Stats Banner ────────────────────────────────────────
            DownloadStatsBanner(
                totalPapers = state.papers.size,
                downloadedCount = state.downloads.size
            )

            // ── Exam Filter Tabs ─────────────────────────────────────────────
            if (examTabs.isNotEmpty()) {
                ScrollableTabRow(
                    selectedTabIndex = examTabs.indexOfFirst {
                        it.first == state.selectedExamFilter
                    }.takeIf { it >= 0 } ?: 0,
                    edgePadding = 16.dp,
                    divider = {},
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    // "All" tab
                    Tab(
                        selected = state.selectedExamFilter == null,
                        onClick = { viewModel.setExamFilter(null) },
                        text = { Text("All", fontWeight = FontWeight.SemiBold) }
                    )
                    examTabs.forEach { (examId, examName) ->
                        Tab(
                            selected = state.selectedExamFilter == examId,
                            onClick = { viewModel.setExamFilter(examId) },
                            text = {
                                Text(
                                    examName.take(12),
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                            }
                        )
                    }
                }

                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
            }

            // ── Papers List ──────────────────────────────────────────────────
            if (state.isLoading) {
                ExamPrepLoadingState()
            } else if (filteredPapers.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Group papers by exam for the "All" view
                    if (state.selectedExamFilter == null) {
                        val grouped = filteredPapers.groupBy { it.examId }
                        grouped.forEach { (_, papers) ->
                            item {
                                ExamGroupHeader(papers.first())
                            }
                            items(papers, key = { it.id }) { paper ->
                                PaperCard(
                                    paper = paper,
                                    isDownloaded = state.downloads.containsKey(paper.id),
                                    downloadProgress = state.downloadProgress[paper.id],
                                    errorMessage = state.downloadErrors[paper.id],
                                    onDownload = { viewModel.downloadPaper(paper.id) },
                                    onDelete = { viewModel.deletePaper(paper.id) },
                                    onDismissError = { viewModel.clearError(paper.id) },
                                    onStartMockTest = { onStartMockTest(paper.id) }
                                )
                            }
                        }
                    } else {
                        items(filteredPapers, key = { it.id }) { paper ->
                            PaperCard(
                                paper = paper,
                                isDownloaded = state.downloads.containsKey(paper.id),
                                downloadProgress = state.downloadProgress[paper.id],
                                errorMessage = state.downloadErrors[paper.id],
                                onDownload = { viewModel.downloadPaper(paper.id) },
                                onDelete = { viewModel.deletePaper(paper.id) },
                                onDismissError = { viewModel.clearError(paper.id) },
                                onStartMockTest = { onStartMockTest(paper.id) }
                            )
                        }
                    }

                    item { Spacer(Modifier.height(32.dp)) }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENTS
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DownloadStatsBanner(totalPapers: Int, downloadedCount: Int) {
    val gradient = Brush.horizontalGradient(
        listOf(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.secondaryContainer
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(gradient)
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "$downloadedCount of $totalPapers papers downloaded",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    "Each paper = 100 practice questions • Offline access",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.LibraryBooks,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun ExamGroupHeader(firstPaper: QuestionPaperMetaEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(0.1f),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        )
        Text(
            firstPaper.examName,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        )
    }
}

@Composable
private fun PaperCard(
    paper: QuestionPaperMetaEntity,
    isDownloaded: Boolean,
    downloadProgress: Int?,
    errorMessage: String?,
    onDownload: () -> Unit,
    onDelete: () -> Unit,
    onDismissError: () -> Unit,
    onStartMockTest: () -> Unit = {}
) {
    val isDownloading = downloadProgress != null
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isDownloaded -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                errorMessage != null -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ── Header row ────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Paper number pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "P${paper.paperNumber}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        // Difficulty badge
                        StatusChip(
                            text = paper.difficultyLabel,
                            color = difficultyColor(paper.difficultyLabel)
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    Text(
                        paper.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // ── Action button ─────────────────────────────────────────────
                Spacer(Modifier.width(12.dp))
                when {
                    isDownloading -> {
                        Box(
                            modifier = Modifier.size(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = { (downloadProgress ?: 0) / 100f },
                                modifier = Modifier.size(36.dp),
                                strokeWidth = 3.dp
                            )
                            Text(
                                "${downloadProgress}%",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    isDownloaded -> {
                        FilledIconButton(
                            onClick = { showDeleteConfirm = true },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Downloaded — tap to remove",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    errorMessage != null -> {
                        FilledIconButton(
                            onClick = onDownload,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Retry download",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    else -> {
                        FilledIconButton(
                            onClick = onDownload,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.Download,
                                contentDescription = "Download",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // ── Description ───────────────────────────────────────────────────
            Text(
                paper.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // ── Stats row ─────────────────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatChip(Icons.Outlined.Quiz, "${paper.questionCount} Questions")
                StatChip(Icons.Outlined.FolderZip, "~${paper.fileSizeKb} KB")
                if (isDownloaded) {
                    StatChip(Icons.Outlined.WifiOff, "Offline Ready", MaterialTheme.colorScheme.primary)
                }
            }

            // ── Take Mock Test CTA if downloaded ─────────────────────────────
            if (isDownloaded) {
                Button(
                    onClick = onStartMockTest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Take Mock Exam (${paper.questionCount} Questions)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            // ── Error message ─────────────────────────────────────────────────
            AnimatedVisibility(visible = errorMessage != null) {
                if (errorMessage != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            errorMessage,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = onDismissError, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Close, null,
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            // ── Download progress bar ─────────────────────────────────────────
            AnimatedVisibility(visible = isDownloading) {
                if (downloadProgress != null) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        LinearProgressIndicator(
                            progress = { downloadProgress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                        )
                        Text(
                            "Downloading ${paper.title}…",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }

    // ── Delete confirm dialog ─────────────────────────────────────────────────
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Remove Paper?") },
            text = {
                Text("\"${paper.title}\" and its ${paper.questionCount} questions will be removed from your device. You can re-download anytime.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun StatChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    iconTint: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, modifier = Modifier.size(12.dp), tint = iconTint)
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
    }
}

@Composable
private fun EmptyState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Outlined.LibraryBooks,
                null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
            Text("No papers available yet", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            Text(
                "Connect to the internet to load the catalog",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}

private fun difficultyColor(label: String): Color = when (label) {
    "Advanced" -> Color(0xFFE53935)
    "Full Mock" -> Color(0xFF5E35B1)
    "Mixed"    -> Color(0xFF1E88E5)
    else       -> Color(0xFF43A047)
}
