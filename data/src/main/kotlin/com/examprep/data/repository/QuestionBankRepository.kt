package com.examprep.data.repository

import com.examprep.data.local.dao.QuestionDao
import com.examprep.data.local.dao.QuestionPaperDao
import com.examprep.data.local.entity.QuestionPaperDownloadEntity
import com.examprep.data.local.entity.QuestionPaperMetaEntity
import com.examprep.data.mapper.toEntity
import com.examprep.domain.model.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

// ─────────────────────────────────────────────────────────────────────────────
// DOWNLOAD STATE
// ─────────────────────────────────────────────────────────────────────────────

sealed class DownloadResult {
    data object Idle : DownloadResult()
    data class Downloading(val progress: Int) : DownloadResult()       // 0–100
    data class Success(val questionCount: Int) : DownloadResult()
    data class Error(val message: String) : DownloadResult()
    data object AlreadyDownloaded : DownloadResult()
}

// ─────────────────────────────────────────────────────────────────────────────
// CATALOG CONSTANTS
//
// These map to JSON files hosted on GitHub raw content.
// File naming: content/<examId>/paper_<N>.json
// Each file contains a JSON array of Question domain objects.
//
// TODO: Replace <OWNER> and <REPO> with your actual GitHub username + repo name.
// ─────────────────────────────────────────────────────────────────────────────

private const val GITHUB_BASE_URL =
    "https://raw.githubusercontent.com/OWNER/examprep-content/main/content"

/**
 * Downloads and caches question papers from GitHub-hosted JSON files.
 *
 * Architecture decisions:
 * - OkHttp for simple one-shot HTTP GET (no Retrofit needed for static JSON)
 * - Questions inserted directly into the existing `question` Room table
 * - [QuestionPaperDownloadEntity] tracks which papers the user has cached
 * - All network I/O runs on Dispatchers.IO
 * - Questions deleted from Room when user removes a downloaded paper
 *
 * GitHub free tier is sufficient for serving static JSON at this scale.
 * At 100k+ downloads, migrate to a CDN (Cloudflare R2 or Firebase Storage).
 */
@Singleton
class QuestionBankRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val questionPaperDao: QuestionPaperDao,
    private val questionDao: QuestionDao,
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    // ── Catalog ──────────────────────────────────────────────────────────────

    /** Observe all available papers from local catalog. */
    fun observeAllPapers(): Flow<List<QuestionPaperMetaEntity>> =
        questionPaperDao.observeAllPapers()

    /** Observe papers for a specific exam. */
    fun observePapersForExam(examId: String): Flow<List<QuestionPaperMetaEntity>> =
        questionPaperDao.observePapersForExam(examId)

    /** Observe download status map: paperId → downloaded? */
    fun observeDownloads(): Flow<List<QuestionPaperDownloadEntity>> =
        questionPaperDao.observeAllDownloads()

    fun observeDownloadedPapers() = questionPaperDao.observeAllDownloads()

    /** Returns true if the user already downloaded the given paper. */
    suspend fun isPaperDownloaded(paperId: String): Boolean =
        questionPaperDao.isDownloaded(paperId)

    // ── Catalog seeding ──────────────────────────────────────────────────────

    /**
     * Seeds the local catalog with the 25 available papers (5 per exam × 5 exams).
     * Called once when the database is created (in DatabaseModule callback).
     *
     * This does NOT download questions — it only seeds the metadata catalog so
     * the UI can show what's available before the user chooses to download.
     */
    suspend fun seedCatalogIfEmpty() = withContext(Dispatchers.IO) {
        val existing = questionPaperDao.observeAllPapers().first()
        if (existing.isNotEmpty()) {
            Timber.d("Question paper catalog already seeded (${existing.size} papers)")
            return@withContext
        }

        Timber.d("Seeding question paper catalog…")
        val catalog = buildCatalog()
        questionPaperDao.insertPapers(catalog)
        Timber.d("Seeded ${catalog.size} question paper entries")
    }

    /**
     * Ensures Paper 1 for the given exam is seeded into Room from bundled assets.
     * Guarantees 100+ authentic questions are immediately available for mock tests and practice.
     */
    suspend fun ensureDefaultPaperLoaded(examId: String): DownloadResult = withContext(Dispatchers.IO) {
        val paperId = when (examId) {
            "exam_neet_ug" -> "neet_paper_1"
            "exam_upsc_cse" -> "upsc_paper_1"
            "exam_cat_mba" -> "cat_paper_1"
            "exam_gate_cs" -> "gate_cs_paper_1"
            else -> "jee_main_paper_1"
        }
        seedCatalogIfEmpty()
        if (!isPaperDownloaded(paperId)) {
            downloadPaper(paperId)
        } else {
            DownloadResult.AlreadyDownloaded
        }
    }

    /**
     * Preloads all 5 bundled papers for the specified exam (up to 500 questions)
     * into Room SQLite, enabling full-scale 180-question NEET and 75-question JEE mock tests.
     */
    suspend fun ensureAllPapersLoadedForExam(examId: String): Int = withContext(Dispatchers.IO) {
        seedCatalogIfEmpty()
        val paperPrefix = when (examId) {
            "exam_neet_ug" -> "neet_paper_"
            "exam_upsc_cse" -> "upsc_paper_"
            "exam_cat_mba" -> "cat_paper_"
            "exam_gate_cs" -> "gate_cs_paper_"
            else -> "jee_main_paper_"
        }
        var newlyLoadedQuestions = 0
        for (i in 1..5) {
            val paperId = "$paperPrefix$i"
            if (!isPaperDownloaded(paperId)) {
                val res = downloadPaper(paperId)
                if (res is DownloadResult.Success) {
                    newlyLoadedQuestions += res.questionCount
                }
            }
        }
        newlyLoadedQuestions
    }

    // ── Download ─────────────────────────────────────────────────────────────

    /**
     * Downloads a question paper from GitHub and saves questions to Room.
     *
     * Emits [DownloadResult] states during the process so the UI can show progress.
     * Marks the paper as downloaded only after ALL questions are saved successfully.
     * If interrupted, no partial download state is left — idempotent.
     *
     * @param paperId  The paper ID (e.g. "jee_main_paper_1")
     * @param onProgress  Called with progress 0–100 during download
     * @return [DownloadResult]
     */
    suspend fun downloadPaper(
        paperId: String,
        onProgress: (Int) -> Unit = {}
    ): DownloadResult = withContext(Dispatchers.IO) {
        // Guard: already downloaded
        if (questionPaperDao.isDownloaded(paperId)) {
            return@withContext DownloadResult.AlreadyDownloaded
        }

        val meta = questionPaperDao.getPaperById(paperId)
            ?: return@withContext DownloadResult.Error("Paper not found in catalog: $paperId")

        return@withContext try {
            onProgress(5)
            Timber.d("Downloading paper: ${meta.title} from ${meta.remoteUrl}")

            var body: String? = null

            // 1. Try bundled offline asset first (all 25 base papers are bundled in APK)
            val assetPath = "content/${meta.examId}/${meta.id}.json"
            try {
                context.assets.open(assetPath).use { stream ->
                    body = stream.bufferedReader().use { it.readText() }
                    Timber.d("Loaded paper from local bundle: $assetPath")
                }
            } catch (assetEx: Exception) {
                Timber.d("Local bundle not available for $assetPath, trying network...")
            }

            // 2. Fallback to remote network fetch if not found in local assets
            if (body.isNullOrBlank() && !meta.remoteUrl.contains("OWNER")) {
                try {
                    val request = Request.Builder().url(meta.remoteUrl).build()
                    val response = httpClient.newCall(request).execute()
                    if (response.isSuccessful) {
                        body = response.body?.string()
                    }
                } catch (netEx: Exception) {
                    Timber.w(netEx, "Remote fetch failed for ${meta.remoteUrl}")
                }
            }

            if (body.isNullOrBlank()) {
                return@withContext DownloadResult.Error("Unable to load question paper (offline & remote unavailable)")
            }

            onProgress(50)
            val questions: List<Question> = json.decodeFromString(body!!)
            Timber.d("Parsed ${questions.size} questions for paper ${meta.id}")

            onProgress(75)
            // Batch insert questions and their options into Room
            questionDao.insertAll(questions.map { it.toEntity() })
            val allOptions = questions.flatMap { q -> q.options.map { it.toEntity() } }
            questionDao.insertOptions(allOptions)

            onProgress(95)
            // Mark paper as downloaded
            questionPaperDao.markDownloaded(
                QuestionPaperDownloadEntity(
                    paperId = paperId,
                    downloadedAt = System.currentTimeMillis(),
                    questionCount = questions.size,
                    fileSizeKb = (body!!.length / 1024).coerceAtLeast(1)
                )
            )

            onProgress(100)
            Timber.i("Paper downloaded successfully: ${meta.title} (${questions.size} questions)")
            DownloadResult.Success(questions.size)

        } catch (e: Exception) {
            Timber.e(e, "Unexpected error downloading paper $paperId")
            DownloadResult.Error("Failed to process questions: ${e.message ?: "Unknown"}")
        }
    }

    /**
     * Deletes a downloaded paper from Room (removes questions with matching paper tag).
     *
     * NOTE: Since questions are keyed by ID and shared with the base seed,
     * we only delete questions whose IDs start with the paper's prefix.
     * This ensures seed questions are never accidentally deleted.
     */
    suspend fun deletePaper(paperId: String) = withContext(Dispatchers.IO) {
        questionPaperDao.deleteDownload(paperId)
        // Questions whose IDs belong to this paper follow naming convention: <paperId>_q_<N>
        questionDao.deleteByIdPrefix("${paperId}_q_")
        Timber.d("Deleted paper and its questions: $paperId")
    }

    // ── Catalog builder ──────────────────────────────────────────────────────

    private fun buildCatalog(): List<QuestionPaperMetaEntity> = listOf(
        // ── JEE Main (5 papers) ──────────────────────────────────────────────
        paper("jee_main_paper_1", "exam_jee_main", "JEE Main", 1,
            "Paper 1 — Mechanics & Thermodynamics",
            "Kinematics, Newton's Laws, Work-Energy, Thermodynamics, Heat Transfer. 100 curated MCQs.",
            "[\"Physics\",\"Chemistry\",\"Mathematics\"]", "Mixed", 78),
        paper("jee_main_paper_2", "exam_jee_main", "JEE Main", 2,
            "Paper 2 — Electrostatics & Waves",
            "Electrostatics, Current Electricity, Electromagnetic Induction, Waves, Optics. 100 MCQs.",
            "[\"Physics\",\"Chemistry\",\"Mathematics\"]", "Mixed", 79),
        paper("jee_main_paper_3", "exam_jee_main", "JEE Main", 3,
            "Paper 3 — Organic Chemistry Focus",
            "Organic Reactions, Mechanisms, Stereochemistry, Biomolecules. 100 MCQs.",
            "[\"Physics\",\"Chemistry\",\"Mathematics\"]", "Advanced", 82),
        paper("jee_main_paper_4", "exam_jee_main", "JEE Main", 4,
            "Paper 4 — Calculus & Coordinate Geometry",
            "Differential Calculus, Integral Calculus, Conic Sections, 3D Geometry. 100 MCQs.",
            "[\"Physics\",\"Chemistry\",\"Mathematics\"]", "Advanced", 81),
        paper("jee_main_paper_5", "exam_jee_main", "JEE Main", 5,
            "Paper 5 — Full Syllabus Mock",
            "Complete JEE Main pattern: 90 questions, 3 hours. All subjects. Timed practice.",
            "[\"Physics\",\"Chemistry\",\"Mathematics\"]", "Full Mock", 95),

        // ── NEET UG (5 papers) ───────────────────────────────────────────────
        paper("neet_paper_1", "exam_neet_ug", "NEET UG", 1,
            "Paper 1 — Cell Biology & Genetics",
            "Cell Structure, Biomolecules, Cell Division, Genetics, Heredity. 100 MCQs.",
            "[\"Biology\",\"Chemistry\",\"Physics\"]", "Mixed", 76),
        paper("neet_paper_2", "exam_neet_ug", "NEET UG", 2,
            "Paper 2 — Human Physiology",
            "Digestive, Respiratory, Circulatory, Excretory, Neural Systems. 100 MCQs.",
            "[\"Biology\",\"Chemistry\",\"Physics\"]", "Mixed", 77),
        paper("neet_paper_3", "exam_neet_ug", "NEET UG", 3,
            "Paper 3 — Organic Chemistry & Biochemistry",
            "Amino Acids, Nucleic Acids, Enzyme Kinetics, Metabolic Pathways. 100 MCQs.",
            "[\"Biology\",\"Chemistry\",\"Physics\"]", "Advanced", 80),
        paper("neet_paper_4", "exam_neet_ug", "NEET UG", 4,
            "Paper 4 — Ecology & Evolution",
            "Ecosystem, Population, Biodiversity, Evolution, Animal Behaviour. 100 MCQs.",
            "[\"Biology\",\"Chemistry\",\"Physics\"]", "Mixed", 74),
        paper("neet_paper_5", "exam_neet_ug", "NEET UG", 5,
            "Paper 5 — Full Syllabus NEET Mock",
            "Complete NEET pattern: 200 questions, 3h 20min. All sections.",
            "[\"Biology\",\"Chemistry\",\"Physics\"]", "Full Mock", 96),

        // ── UPSC CSE Prelims (5 papers) ──────────────────────────────────────
        paper("upsc_paper_1", "exam_upsc_cse", "UPSC CSE", 1,
            "Paper 1 — Indian Polity & Governance",
            "Constitution, Parliament, Executive, Judiciary, Panchayati Raj. 100 MCQs.",
            "[\"GS-I\",\"GS-II\"]", "Mixed", 72),
        paper("upsc_paper_2", "exam_upsc_cse", "UPSC CSE", 2,
            "Paper 2 — History & Culture",
            "Ancient, Medieval, Modern Indian History, Art & Culture, World History. 100 MCQs.",
            "[\"GS-I\"]", "Mixed", 74),
        paper("upsc_paper_3", "exam_upsc_cse", "UPSC CSE", 3,
            "Paper 3 — Economy & Environment",
            "Indian Economy, Budget, Monetary Policy, Environment, Ecology. 100 MCQs.",
            "[\"GS-III\"]", "Advanced", 78),
        paper("upsc_paper_4", "exam_upsc_cse", "UPSC CSE", 4,
            "Paper 4 — Geography & Science",
            "Physical Geography, Indian Geography, Science & Technology, Space. 100 MCQs.",
            "[\"GS-I\",\"GS-III\"]", "Mixed", 73),
        paper("upsc_paper_5", "exam_upsc_cse", "UPSC CSE", 5,
            "Paper 5 — Full CSAT + GS Mock",
            "GS Paper I pattern: 100 MCQs. Includes CSAT aptitude section.",
            "[\"GS-I\",\"GS-II\",\"GS-III\",\"CSAT\"]", "Full Mock", 91),

        // ── CAT (5 papers) ───────────────────────────────────────────────────
        paper("cat_paper_1", "exam_cat_mba", "CAT", 1,
            "Paper 1 — Quantitative Aptitude",
            "Number Systems, Algebra, Geometry, Arithmetic, Modern Math. 100 MCQs.",
            "[\"QA\"]", "Mixed", 75),
        paper("cat_paper_2", "exam_cat_mba", "CAT", 2,
            "Paper 2 — Verbal Ability & Reading Comprehension",
            "RC Passages, Para Jumbles, Summary, Odd One Out, Fill in the Blanks. 100 Qs.",
            "[\"VARC\"]", "Advanced", 76),
        paper("cat_paper_3", "exam_cat_mba", "CAT", 3,
            "Paper 3 — Data Interpretation & Logical Reasoning",
            "Tables, Graphs, Sets, Arrangements, Puzzles, Syllogisms. 100 Qs.",
            "[\"DILR\"]", "Advanced", 77),
        paper("cat_paper_4", "exam_cat_mba", "CAT", 4,
            "Paper 4 — Mixed Sectional Practice",
            "Balanced mix: QA 35 + VARC 35 + DILR 30. Time-pressured practice.",
            "[\"QA\",\"VARC\",\"DILR\"]", "Mixed", 79),
        paper("cat_paper_5", "exam_cat_mba", "CAT", 5,
            "Paper 5 — Full CAT Mock",
            "CAT pattern: 66 questions, 2 hours. All sections with sectional timer.",
            "[\"QA\",\"VARC\",\"DILR\"]", "Full Mock", 88),

        // ── GATE CS (5 papers) ───────────────────────────────────────────────
        paper("gate_cs_paper_1", "exam_gate_cs", "GATE CS", 1,
            "Paper 1 — Data Structures & Algorithms",
            "Arrays, Trees, Graphs, Sorting, Dynamic Programming, Complexity. 100 MCQs.",
            "[\"CS Core\",\"Engineering Maths\"]", "Advanced", 83),
        paper("gate_cs_paper_2", "exam_gate_cs", "GATE CS", 2,
            "Paper 2 — OS & Computer Networks",
            "Process Management, Memory, File Systems, TCP/IP, Routing. 100 MCQs.",
            "[\"CS Core\"]", "Advanced", 82),
        paper("gate_cs_paper_3", "exam_gate_cs", "GATE CS", 3,
            "Paper 3 — DBMS & Compiler Design",
            "ER Model, Normalization, SQL, Parsing, Code Generation, Optimization. 100 MCQs.",
            "[\"CS Core\"]", "Advanced", 80),
        paper("gate_cs_paper_4", "exam_gate_cs", "GATE CS", 4,
            "Paper 4 — Digital Logic & Computer Architecture",
            "Boolean Algebra, Combinational, Sequential Circuits, Pipelining, Cache. 100 MCQs.",
            "[\"CS Core\",\"Engineering Maths\"]", "Mixed", 78),
        paper("gate_cs_paper_5", "exam_gate_cs", "GATE CS", 5,
            "Paper 5 — Full GATE CS Mock",
            "GATE CS pattern: 65 questions, 3 hours. General Aptitude + Technical.",
            "[\"CS Core\",\"Engineering Maths\",\"General Aptitude\"]", "Full Mock", 94)
    )

    private fun paper(
        id: String, examId: String, examName: String, num: Int,
        title: String, desc: String, subjects: String,
        difficulty: String, sizeKb: Int
    ) = QuestionPaperMetaEntity(
        id = id,
        examId = examId,
        examName = examName,
        paperNumber = num,
        title = title,
        description = desc,
        questionCount = 100,
        subjectsCovered = subjects,
        difficultyLabel = difficulty,
        remoteUrl = "$GITHUB_BASE_URL/$examId/$id.json",
        fileSizeKb = sizeKb
    )
}
