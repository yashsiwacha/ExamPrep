package com.examprep.android.feature.mocktest

import com.examprep.domain.model.*
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockTestStore @Inject constructor() {

    private val completedAttempts = ConcurrentHashMap<String, MockTestResult>()

    fun saveAttemptResult(result: MockTestResult) {
        completedAttempts[result.attemptId] = result
    }

    fun getAttemptResult(attemptId: String): MockTestResult? {
        return completedAttempts[attemptId]
    }

    private fun selectWithDifficulty(
        pool: List<Question>,
        targetCount: Int,
        targetEasy: Int,
        targetMed: Int,
        targetHard: Int
    ): List<Question> {
        if (pool.size <= targetCount) return pool

        val easyList = pool.filter { it.difficulty == Difficulty.EASY }
        val medList = pool.filter { it.difficulty == Difficulty.MEDIUM }
        val hardList = pool.filter { it.difficulty == Difficulty.HARD || it.difficulty == Difficulty.VERY_HARD }

        val selectedEasy = easyList.take(targetEasy)
        val selectedMed = medList.take(targetMed)
        val selectedHard = hardList.take(targetHard)

        val selected = (selectedEasy + selectedMed + selectedHard).toMutableList()
        val needed = targetCount - selected.size
        if (needed > 0) {
            val selectedIds = selected.map { it.id }.toSet()
            val remaining = pool.filterNot { selectedIds.contains(it.id) }
            selected.addAll(remaining.take(needed))
        }
        return selected.take(targetCount)
    }

    fun generateStandardMockTest(examId: String, allQuestions: List<Question>, mockTestId: String? = null): MockTest {
        return when (examId) {
            "exam_neet_ug" -> {
                val bioQuestions = allQuestions.filter { it.subjectId.contains("bio") || it.topicId.contains("bio") || it.tags.any { t -> t.contains("Biology", true) } }
                val chemQuestions = allQuestions.filter { it.subjectId.contains("chem") || it.topicId.contains("chem") || it.tags.any { t -> t.contains("Chemistry", true) } }
                val phyQuestions = allQuestions.filter { it.subjectId.contains("phy") || it.topicId.contains("phy") || it.tags.any { t -> t.contains("Physics", true) } }

                val isFullScale = allQuestions.size >= 140
                val targetBio = if (isFullScale) 90 else if (allQuestions.size >= 70) 30 else (allQuestions.size / 2).coerceAtLeast(1)
                val targetChem = if (isFullScale) 45 else if (allQuestions.size >= 70) 20 else (allQuestions.size / 4).coerceAtLeast(1)
                val targetPhy = if (isFullScale) 45 else if (allQuestions.size >= 70) 20 else (allQuestions.size / 4).coerceAtLeast(1)

                val finalBio = selectWithDifficulty(
                    pool = bioQuestions,
                    targetCount = targetBio,
                    targetEasy = (targetBio * 0.30f).toInt(),
                    targetMed = (targetBio * 0.50f).toInt(),
                    targetHard = (targetBio * 0.20f).toInt()
                )
                val finalChem = selectWithDifficulty(
                    pool = chemQuestions,
                    targetCount = targetChem,
                    targetEasy = (targetChem * 0.30f).toInt(),
                    targetMed = (targetChem * 0.50f).toInt(),
                    targetHard = (targetChem * 0.20f).toInt()
                )
                val finalPhy = selectWithDifficulty(
                    pool = phyQuestions,
                    targetCount = targetPhy,
                    targetEasy = (targetPhy * 0.30f).toInt(),
                    targetMed = (targetPhy * 0.50f).toInt(),
                    targetHard = (targetPhy * 0.20f).toInt()
                )

                val assignedIds = (finalBio + finalChem + finalPhy).map { it.id }.toSet()
                val unassigned = allQuestions.filterNot { assignedIds.contains(it.id) }

                val (secBio, rem1) = if (finalBio.isEmpty() && unassigned.isNotEmpty()) Pair(unassigned.take(targetBio), unassigned.drop(targetBio)) else Pair(finalBio, unassigned)
                val (secChem, rem2) = if (finalChem.isEmpty() && rem1.isNotEmpty()) Pair(rem1.take(targetChem), rem1.drop(targetChem)) else Pair(finalChem, rem1)
                val secPhy = if (finalPhy.isEmpty() && rem2.isNotEmpty()) rem2.take(targetPhy) else finalPhy

                val sections = listOf(
                    MockTestSection("sec_neet_bio", "Biology", "sub_neet_bio", secBio),
                    MockTestSection("sec_neet_chem", "Chemistry", "sub_jee_chem", secChem),
                    MockTestSection("sec_neet_phy", "Physics", "sub_jee_phy", secPhy)
                ).filter { it.questions.isNotEmpty() }.ifEmpty {
                    listOf(MockTestSection("sec_neet_gen", "NEET General", "sub_neet_bio", allQuestions))
                }

                val totalQs = sections.sumOf { it.questions.size }
                val totalMarks = if (totalQs == 180) 720 else totalQs * 4

                MockTest(
                    id = mockTestId ?: "mock_neet_all_india_1",
                    examId = examId,
                    title = "All-India NEET UG Full Mock Test #1",
                    description = "Comprehensive Medical Entrance Simulation · Biology (90 Qs), Chemistry (45 Qs), Physics (45 Qs) · (+4 / -1)",
                    durationMinutes = 200,
                    totalMarks = totalMarks,
                    positiveMarks = 4,
                    negativeMarks = 1,
                    sections = sections
                )
            }
            "exam_upsc_cse" -> {
                val target = if (allQuestions.size >= 80) 100 else allQuestions.size.coerceAtLeast(1)
                val qList = selectWithDifficulty(
                    pool = allQuestions,
                    targetCount = target,
                    targetEasy = 0,
                    targetMed = (target * 0.50f).toInt(),
                    targetHard = (target * 0.50f).toInt()
                )
                val sections = listOf(
                    MockTestSection("sec_upsc_gs1", "General Studies Paper I", "sub_upsc_gs1", qList)
                )
                val totalMarks = if (qList.size == 100) 200 else qList.size * 2
                MockTest(
                    id = mockTestId ?: "mock_upsc_all_india_1",
                    examId = examId,
                    title = "UPSC CSE Prelims Full GS Mock #1",
                    description = "Civil Services Preliminary Simulation · General Studies Paper I (100 Questions) · (+2 / -0.66)",
                    durationMinutes = 120,
                    totalMarks = totalMarks,
                    positiveMarks = 2,
                    negativeMarks = 1,
                    sections = sections
                )
            }
            "exam_cat_mba" -> {
                val varcPool = allQuestions.filter { it.tags.any { t -> t.contains("VARC", true) || t.contains("Verbal", true) } || it.topicId.contains("varc") }
                val dilrPool = allQuestions.filter { it.tags.any { t -> t.contains("DILR", true) || t.contains("Interpretation", true) || t.contains("Logical", true) } && !it.tags.any { t -> t.contains("VARC", true) } }
                val qaPool = allQuestions.filter { it.tags.any { t -> t.contains("Quantitative", true) || t.contains("Distance", true) || t.contains("Arithmetic", true) } || it.subjectId.contains("qa") }

                val isFullScale = allQuestions.size >= 55
                val targetVarc = if (isFullScale) 24 else (allQuestions.size * 24 / 66).coerceAtLeast(1)
                val targetDilr = if (isFullScale) 20 else (allQuestions.size * 20 / 66).coerceAtLeast(1)
                val targetQa = if (isFullScale) 22 else (allQuestions.size * 22 / 66).coerceAtLeast(1)

                val finalVarc = selectWithDifficulty(varcPool, targetVarc, 0, (targetVarc * 0.30f).toInt(), (targetVarc * 0.70f).toInt())
                val finalDilr = selectWithDifficulty(dilrPool, targetDilr, 0, (targetDilr * 0.30f).toInt(), (targetDilr * 0.70f).toInt())
                val finalQa = selectWithDifficulty(qaPool, targetQa, 0, (targetQa * 0.30f).toInt(), (targetQa * 0.70f).toInt())

                val assignedIds = (finalVarc + finalDilr + finalQa).map { it.id }.toSet()
                val unassigned = allQuestions.filterNot { assignedIds.contains(it.id) }

                val (secVarc, rem1) = if (finalVarc.isEmpty() && unassigned.isNotEmpty()) Pair(unassigned.take(targetVarc), unassigned.drop(targetVarc)) else Pair(finalVarc, unassigned)
                val (secDilr, rem2) = if (finalDilr.isEmpty() && rem1.isNotEmpty()) Pair(rem1.take(targetDilr), rem1.drop(targetDilr)) else Pair(finalDilr, rem1)
                val secQa = if (finalQa.isEmpty() && rem2.isNotEmpty()) rem2.take(targetQa) else finalQa

                val sections = listOf(
                    MockTestSection("sec_cat_varc", "VARC", "sub_cat_varc", secVarc),
                    MockTestSection("sec_cat_dilr", "DILR", "sub_cat_dilr", secDilr),
                    MockTestSection("sec_cat_qa", "Quantitative Aptitude", "sub_cat_qa", secQa)
                ).filter { it.questions.isNotEmpty() }.ifEmpty {
                    listOf(MockTestSection("sec_cat_gen", "CAT General", "sub_cat_qa", allQuestions))
                }

                val totalQs = sections.sumOf { it.questions.size }
                val totalMarks = if (totalQs == 66) 198 else totalQs * 3

                MockTest(
                    id = mockTestId ?: "mock_cat_all_india_1",
                    examId = examId,
                    title = "CAT National Percentile Mock #1",
                    description = "IIM Entrance Simulation · VARC (24), DILR (20) & Quantitative Aptitude (22) · (+3 / -1)",
                    durationMinutes = 120,
                    totalMarks = totalMarks,
                    positiveMarks = 3,
                    negativeMarks = 1,
                    sections = sections
                )
            }
            "exam_gate_cs" -> {
                val gaPool = allQuestions.filter { it.tags.any { t -> t.contains("Aptitude", true) || t.contains("Math", true) } || it.topicId.contains("math") }
                val csPool = allQuestions.filter { it.tags.any { t -> t.contains("Algorithms", true) || t.contains("Programming", true) || t.contains("Systems", true) || t.contains("Networks", true) || t.contains("GATE CS", true) } || it.subjectId.contains("dsa") || it.subjectId.contains("os") }

                val isFullScale = allQuestions.size >= 50
                val targetGa = if (isFullScale) 15 else (allQuestions.size * 15 / 65).coerceAtLeast(1)
                val targetCs = if (isFullScale) 50 else (allQuestions.size * 50 / 65).coerceAtLeast(1)

                val finalGa = selectWithDifficulty(gaPool, targetGa, (targetGa * 0.25f).toInt(), (targetGa * 0.50f).toInt(), (targetGa * 0.25f).toInt())
                val finalCs = selectWithDifficulty(csPool, targetCs, (targetCs * 0.25f).toInt(), (targetCs * 0.50f).toInt(), (targetCs * 0.25f).toInt())

                val assignedIds = (finalGa + finalCs).map { it.id }.toSet()
                val unassigned = allQuestions.filterNot { assignedIds.contains(it.id) }

                val (secGa, rem1) = if (finalGa.isEmpty() && unassigned.isNotEmpty()) Pair(unassigned.take(targetGa), unassigned.drop(targetGa)) else Pair(finalGa, unassigned)
                val secCs = if (finalCs.isEmpty() && rem1.isNotEmpty()) rem1.take(targetCs) else finalCs

                val sections = listOf(
                    MockTestSection("sec_gate_ga", "General Aptitude & Mathematics", "sub_gate_ga", secGa),
                    MockTestSection("sec_gate_cs", "Computer Science Core", "sub_gate_dsa", secCs)
                ).filter { it.questions.isNotEmpty() }.ifEmpty {
                    listOf(MockTestSection("sec_gate_gen", "GATE General", "sub_gate_dsa", allQuestions))
                }

                val totalQs = sections.sumOf { it.questions.size }
                val totalMarks = if (totalQs == 65) 100 else totalQs * 2

                MockTest(
                    id = mockTestId ?: "mock_gate_all_india_1",
                    examId = examId,
                    title = "GATE Computer Science All-India Mock #1",
                    description = "Comprehensive Technical Simulation · GA & Maths (15 Qs), CS Core (50 Qs) · (+2 / -0.66)",
                    durationMinutes = 180,
                    totalMarks = totalMarks,
                    positiveMarks = 2,
                    negativeMarks = 1,
                    sections = sections
                )
            }
            else -> { // Default JEE Main
                val physicsQuestions = allQuestions.filter { it.subjectId == "sub_jee_phy" || it.subjectId.contains("phy") || it.topicId.contains("phy") }
                val chemistryQuestions = allQuestions.filter { it.subjectId == "sub_jee_chem" || it.subjectId.contains("chem") || it.topicId.contains("chem") }
                val mathsQuestions = allQuestions.filter { it.subjectId == "sub_jee_math" || it.subjectId.contains("math") || it.topicId.contains("math") }

                val isFullScale = allQuestions.size >= 60
                val target = if (isFullScale) 25 else (allQuestions.size / 3).coerceAtLeast(1)

                val targetEasy = (target * 0.28f).toInt()
                val targetMed = (target * 0.52f).toInt()
                val targetHard = (target - targetEasy - targetMed).coerceAtLeast(1)

                val finalPhy = selectWithDifficulty(physicsQuestions, target, targetEasy, targetMed, targetHard)
                val finalChem = selectWithDifficulty(chemistryQuestions, target, targetEasy, targetMed, targetHard)
                val finalMath = selectWithDifficulty(mathsQuestions, target, targetEasy, targetMed, targetHard)

                val assignedIds = (finalPhy + finalChem + finalMath).map { it.id }.toSet()
                val unassigned = allQuestions.filterNot { assignedIds.contains(it.id) }

                val (secPhy, rem1) = if (finalPhy.isEmpty() && unassigned.isNotEmpty()) Pair(unassigned.take(target), unassigned.drop(target)) else Pair(finalPhy, unassigned)
                val (secChem, rem2) = if (finalChem.isEmpty() && rem1.isNotEmpty()) Pair(rem1.take(target), rem1.drop(target)) else Pair(finalChem, rem1)
                val secMath = if (finalMath.isEmpty() && rem2.isNotEmpty()) rem2.take(target) else finalMath

                val sections = listOf(
                    MockTestSection("sec_phy", "Physics", "sub_jee_phy", secPhy),
                    MockTestSection("sec_chem", "Chemistry", "sub_jee_chem", secChem),
                    MockTestSection("sec_math", "Mathematics", "sub_jee_math", secMath)
                ).filter { it.questions.isNotEmpty() }.ifEmpty {
                    listOf(MockTestSection("sec_jee_gen", "JEE General", "sub_jee_phy", allQuestions))
                }

                val totalQs = sections.sumOf { it.questions.size }
                val totalMarks = if (totalQs == 75) 300 else totalQs * 4

                MockTest(
                    id = mockTestId ?: "mock_jee_all_india_1",
                    examId = examId,
                    title = "All-India JEE Main Full Mock Test #1",
                    description = "Official 3-Hour Simulation · Physics (25), Chemistry (25) & Mathematics (25) · Marking Scheme (+4 / -1)",
                    durationMinutes = 180,
                    totalMarks = totalMarks,
                    positiveMarks = 4,
                    negativeMarks = 1,
                    sections = sections
                )
            }
        }
    }

    fun evaluateTest(
        mockTest: MockTest,
        userAnswers: Map<String, String>, // questionId -> selectedOptionId
        timeSpentSeconds: Long
    ): MockTestResult {
        val attemptId = UUID.randomUUID().toString()
        val allQuestions = mockTest.sections.flatMap { it.questions }

        var totalCorrect = 0
        var totalIncorrect = 0
        var totalAttempted = 0
        var totalUnattempted = 0
        var totalScore = 0

        val sectionResults = mockTest.sections.map { section ->
            var secCorrect = 0
            var secIncorrect = 0
            var secAttempted = 0
            var secUnattempted = 0
            var secScore = 0

            section.questions.forEach { q ->
                val selectedOption = userAnswers[q.id]
                if (selectedOption != null) {
                    secAttempted++
                    val correctOpt = q.options.find { it.isCorrect }
                    if (correctOpt != null && selectedOption == correctOpt.id) {
                        secCorrect++
                        secScore += mockTest.positiveMarks
                    } else {
                        secIncorrect++
                        secScore -= mockTest.negativeMarks
                        val studentAnswerText = q.options.find { it.id == selectedOption }?.text ?: selectedOption
                        val correctAnswerText = correctOpt?.text ?: "Option A"
                        com.examprep.android.feature.mistakes.MistakeVaultStore.get().recordMistake(
                            questionId = q.id,
                            questionText = q.questionText,
                            options = q.options.map { it.text },
                            correctAnswer = correctAnswerText,
                            studentAnswer = studentAnswerText,
                            explanation = q.explanation,
                            subject = section.name,
                            chapter = "Core Syllabus"
                        )
                    }
                } else {
                    secUnattempted++
                }
            }

            totalCorrect += secCorrect
            totalIncorrect += secIncorrect
            totalAttempted += secAttempted
            totalUnattempted += secUnattempted
            totalScore += secScore

            val secAccuracy = if (secAttempted > 0) (secCorrect.toFloat() / secAttempted) * 100f else 0f
            MockSectionResult(
                sectionName = section.name,
                totalQuestions = section.questions.size,
                attemptedCount = secAttempted,
                correctCount = secCorrect,
                incorrectCount = secIncorrect,
                unattemptedCount = secUnattempted,
                marksObtained = secScore,
                totalPossibleMarks = section.questions.size * mockTest.positiveMarks,
                accuracy = secAccuracy
            )
        }

        val accuracy = if (totalAttempted > 0) (totalCorrect.toFloat() / totalAttempted) * 100f else 0f
        val maxMarks = allQuestions.size * mockTest.positiveMarks
        val scorePercent = if (maxMarks > 0) (totalScore.toFloat() / maxMarks).coerceIn(0f, 1f) else 0f
        val percentile = (70f + (scorePercent * 29.8f)).coerceIn(60f, 99.8f)

        val rank = when {
            percentile >= 99f -> "AIR Top 500"
            percentile >= 95f -> "AIR 1,500 – 4,000"
            percentile >= 90f -> "AIR 5,000 – 12,000"
            percentile >= 80f -> "AIR 15,000 – 35,000"
            else -> "AIR 40,000+"
        }

        val examName = when (mockTest.examId) {
            "exam_neet_ug" -> "NEET UG 2026"
            "exam_upsc_cse" -> "UPSC CSE Prelims 2026"
            "exam_cat_mba" -> "CAT 2026"
            "exam_gate_cs" -> "GATE CS 2026"
            else -> "JEE Main 2026"
        }

        val result = MockTestResult(
            attemptId = attemptId,
            mockTestId = mockTest.id,
            title = mockTest.title,
            examName = examName,
            totalQuestions = allQuestions.size,
            attemptedQuestions = totalAttempted,
            correctAnswers = totalCorrect,
            incorrectAnswers = totalIncorrect,
            unattemptedQuestions = totalUnattempted,
            totalScore = totalScore,
            maximumMarks = maxMarks,
            overallAccuracy = accuracy,
            estimatedPercentile = percentile,
            projectedRankRange = rank,
            timeSpentSeconds = timeSpentSeconds,
            totalDurationSeconds = mockTest.durationMinutes * 60L,
            sectionResults = sectionResults,
            userAnswers = userAnswers,
            questions = allQuestions
        )

        saveAttemptResult(result)
        return result
    }
}
