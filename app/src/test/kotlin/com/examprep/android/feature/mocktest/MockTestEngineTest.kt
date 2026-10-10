package com.examprep.android.feature.mocktest

import com.examprep.domain.model.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MockTestEngineTest {

    private lateinit var mockTestStore: MockTestStore

    @Before
    fun setUp() {
        mockTestStore = MockTestStore()
    }

    private fun createQuestion(
        id: String,
        examId: String,
        subjectId: String,
        topicId: String,
        difficulty: Difficulty = Difficulty.MEDIUM,
        tags: List<String> = emptyList()
    ): Question {
        return Question(
            id = id,
            examId = examId,
            subjectId = subjectId,
            chapterId = "chap_$subjectId",
            topicId = topicId,
            questionText = "Question $id",
            options = listOf(
                QuestionOption("${id}_opt_1", id, "Option A", true, 0),
                QuestionOption("${id}_opt_2", id, "Option B", false, 1),
                QuestionOption("${id}_opt_3", id, "Option C", false, 2),
                QuestionOption("${id}_opt_4", id, "Option D", false, 3)
            ),
            correctOptionId = "${id}_opt_1",
            explanation = "Explanation for $id",
            type = QuestionType.MCQ,
            difficulty = difficulty,
            source = ContentSource.ORIGINAL,
            sourceReference = "MockTestEngineTest",
            year = 2026,
            marks = 4f,
            negativeMarks = -1f,
            tags = tags
        )
    }

    @Test
    fun `generateStandardMockTest for JEE Main creates official 75-question 300-mark test with balanced difficulty`() {
        val questions = mutableListOf<Question>()
        // 35 Physics with Easy/Med/Hard distribution
        for (i in 1..10) questions.add(createQuestion("q_phy_e_$i", "exam_jee_main", "sub_jee_phy", "top_phy", Difficulty.EASY))
        for (i in 1..18) questions.add(createQuestion("q_phy_m_$i", "exam_jee_main", "sub_jee_phy", "top_phy", Difficulty.MEDIUM))
        for (i in 1..7) questions.add(createQuestion("q_phy_h_$i", "exam_jee_main", "sub_jee_phy", "top_phy", Difficulty.HARD))

        // 35 Chemistry
        for (i in 1..10) questions.add(createQuestion("q_chem_e_$i", "exam_jee_main", "sub_jee_chem", "top_chem", Difficulty.EASY))
        for (i in 1..18) questions.add(createQuestion("q_chem_m_$i", "exam_jee_main", "sub_jee_chem", "top_chem", Difficulty.MEDIUM))
        for (i in 1..7) questions.add(createQuestion("q_chem_h_$i", "exam_jee_main", "sub_jee_chem", "top_chem", Difficulty.HARD))

        // 35 Mathematics
        for (i in 1..10) questions.add(createQuestion("q_math_e_$i", "exam_jee_main", "sub_jee_math", "top_math", Difficulty.EASY))
        for (i in 1..18) questions.add(createQuestion("q_math_m_$i", "exam_jee_main", "sub_jee_math", "top_math", Difficulty.MEDIUM))
        for (i in 1..7) questions.add(createQuestion("q_math_h_$i", "exam_jee_main", "sub_jee_math", "top_math", Difficulty.HARD))

        val mockTest = mockTestStore.generateStandardMockTest("exam_jee_main", questions)

        assertEquals("All-India JEE Main Full Mock Test #1", mockTest.title)
        assertEquals(3, mockTest.sections.size)
        assertEquals(180, mockTest.durationMinutes)
        assertEquals(300, mockTest.totalMarks)
        assertEquals(4, mockTest.positiveMarks)
        assertEquals(1, mockTest.negativeMarks)

        val phySec = mockTest.sections.find { it.name == "Physics" }
        val chemSec = mockTest.sections.find { it.name == "Chemistry" }
        val mathSec = mockTest.sections.find { it.name == "Mathematics" }

        assertNotNull(phySec)
        assertNotNull(chemSec)
        assertNotNull(mathSec)

        assertEquals(25, phySec!!.questions.size)
        assertEquals(25, chemSec!!.questions.size)
        assertEquals(25, mathSec!!.questions.size)
        assertEquals(75, mockTest.sections.sumOf { it.questions.size })

        // Verify difficulty calibration in Physics section
        val phyEasy = phySec.questions.count { it.difficulty == Difficulty.EASY }
        val phyMed = phySec.questions.count { it.difficulty == Difficulty.MEDIUM }
        val phyHard = phySec.questions.count { it.difficulty == Difficulty.HARD }
        assertTrue("Physics should have between 5 and 9 Easy questions", phyEasy in 5..9)
        assertTrue("Physics should have between 10 and 15 Medium questions", phyMed in 10..15)
        assertTrue("Physics should have between 4 and 8 Hard questions", phyHard in 4..8)
    }

    @Test
    fun `generateStandardMockTest for NEET UG creates official 180-question 720-mark test`() {
        val questions = mutableListOf<Question>()
        for (i in 1..50) questions.add(createQuestion("q_bio_$i", "exam_neet_ug", "sub_neet_bio", "top_bio"))
        for (i in 51..100) questions.add(createQuestion("q_bio_$i", "exam_neet_ug", "sub_neet_bio", "top_bio"))
        for (i in 1..50) questions.add(createQuestion("q_chem_$i", "exam_neet_ug", "sub_jee_chem", "top_chem"))
        for (i in 1..50) questions.add(createQuestion("q_phy_$i", "exam_neet_ug", "sub_jee_phy", "top_phy"))

        val mockTest = mockTestStore.generateStandardMockTest("exam_neet_ug", questions)

        assertEquals("All-India NEET UG Full Mock Test #1", mockTest.title)
        assertEquals(3, mockTest.sections.size)
        assertEquals(200, mockTest.durationMinutes)
        assertEquals(720, mockTest.totalMarks)
        assertEquals(4, mockTest.positiveMarks)
        assertEquals(1, mockTest.negativeMarks)

        val bioSec = mockTest.sections.find { it.name == "Biology" }
        val chemSec = mockTest.sections.find { it.name == "Chemistry" }
        val phySec = mockTest.sections.find { it.name == "Physics" }

        assertNotNull(bioSec)
        assertNotNull(chemSec)
        assertNotNull(phySec)

        assertEquals(90, bioSec!!.questions.size)
        assertEquals(45, chemSec!!.questions.size)
        assertEquals(45, phySec!!.questions.size)
        assertEquals(180, mockTest.sections.sumOf { it.questions.size })
    }

    @Test
    fun `generateStandardMockTest for UPSC CSE creates official 100-question 200-mark test`() {
        val questions = mutableListOf<Question>()
        for (i in 1..55) questions.add(createQuestion("q_upsc_m_$i", "exam_upsc_cse", "sub_upsc_gs1", "top_upsc", Difficulty.MEDIUM))
        for (i in 1..35) questions.add(createQuestion("q_upsc_h_$i", "exam_upsc_cse", "sub_upsc_gs1", "top_upsc", Difficulty.HARD))
        for (i in 1..25) questions.add(createQuestion("q_upsc_vh_$i", "exam_upsc_cse", "sub_upsc_gs1", "top_upsc", Difficulty.VERY_HARD))

        val mockTest = mockTestStore.generateStandardMockTest("exam_upsc_cse", questions)

        assertEquals("UPSC CSE Prelims Full GS Mock #1", mockTest.title)
        assertEquals(1, mockTest.sections.size)
        assertEquals(120, mockTest.durationMinutes)
        assertEquals(200, mockTest.totalMarks)
        assertEquals(2, mockTest.positiveMarks)
        assertEquals(100, mockTest.sections.first().questions.size)
    }

    @Test
    fun `generateStandardMockTest for CAT creates official 66-question 198-mark test in VARC DILR QA order`() {
        val questions = mutableListOf<Question>()
        // 30 VARC
        for (i in 1..30) questions.add(createQuestion("q_cat_varc_$i", "exam_cat_mba", "sub_cat_qa", "top_cat_percentages", tags = listOf("VARC", "Reading Comprehension")))
        // 25 DILR
        for (i in 1..25) questions.add(createQuestion("q_cat_dilr_$i", "exam_cat_mba", "sub_cat_qa", "top_cat_percentages", tags = listOf("DILR", "Data Interpretation & VARC")))
        // 30 QA
        for (i in 1..30) questions.add(createQuestion("q_cat_qa_$i", "exam_cat_mba", "sub_cat_qa", "top_cat_tsd", tags = listOf("Quantitative Aptitude")))

        val mockTest = mockTestStore.generateStandardMockTest("exam_cat_mba", questions)

        assertEquals("CAT National Percentile Mock #1", mockTest.title)
        assertEquals(3, mockTest.sections.size)
        assertEquals(120, mockTest.durationMinutes)
        assertEquals(198, mockTest.totalMarks)
        assertEquals(3, mockTest.positiveMarks)
        assertEquals(1, mockTest.negativeMarks)

        assertEquals("VARC", mockTest.sections[0].name)
        assertEquals("DILR", mockTest.sections[1].name)
        assertEquals("Quantitative Aptitude", mockTest.sections[2].name)

        assertEquals(24, mockTest.sections[0].questions.size)
        assertEquals(20, mockTest.sections[1].questions.size)
        assertEquals(22, mockTest.sections[2].questions.size)
        assertEquals(66, mockTest.sections.sumOf { it.questions.size })
    }

    @Test
    fun `generateStandardMockTest for GATE CS creates official 65-question 100-mark test`() {
        val questions = mutableListOf<Question>()
        for (i in 1..25) questions.add(createQuestion("q_gate_ga_$i", "exam_gate_cs", "sub_gate_dsa", "top_gate_sorting", tags = listOf("General Aptitude & Mathematics")))
        for (i in 1..55) questions.add(createQuestion("q_gate_cs_$i", "exam_gate_cs", "sub_gate_dsa", "top_gate_dp", tags = listOf("Algorithms", "Operating Systems & Networks")))

        val mockTest = mockTestStore.generateStandardMockTest("exam_gate_cs", questions)

        assertEquals("GATE Computer Science All-India Mock #1", mockTest.title)
        assertEquals(2, mockTest.sections.size)
        assertEquals(180, mockTest.durationMinutes)
        assertEquals(100, mockTest.totalMarks)
        assertEquals(2, mockTest.positiveMarks)

        val gaSec = mockTest.sections.find { it.name.contains("General Aptitude") || it.name.contains("Mathematics") }
        val csSec = mockTest.sections.find { it.name.contains("Computer Science") || it.name.contains("Core") }

        assertNotNull(gaSec)
        assertNotNull(csSec)
        assertEquals(15, gaSec!!.questions.size)
        assertEquals(50, csSec!!.questions.size)
        assertEquals(65, mockTest.sections.sumOf { it.questions.size })
    }

    @Test
    fun `graceful fallback when questions are limited scales sections without crashing`() {
        val questions = mutableListOf<Question>()
        for (i in 1..5) questions.add(createQuestion("q_phy_$i", "exam_jee_main", "sub_jee_phy", "top_phy"))
        for (i in 1..5) questions.add(createQuestion("q_chem_$i", "exam_jee_main", "sub_jee_chem", "top_chem"))
        for (i in 1..5) questions.add(createQuestion("q_math_$i", "exam_jee_main", "sub_jee_math", "top_math"))

        val mockTest = mockTestStore.generateStandardMockTest("exam_jee_main", questions)
        assertEquals(3, mockTest.sections.size)
        assertEquals(15, mockTest.sections.sumOf { it.questions.size })
    }

    @Test
    fun `auto-submit evaluation generates valid result with full time spent`() {
        val questions = listOf(
            createQuestion("q1", "exam_jee_main", "sub_jee_phy", "top1"),
            createQuestion("q2", "exam_jee_main", "sub_jee_phy", "top1")
        )
        val mockTest = mockTestStore.generateStandardMockTest("exam_jee_main", questions)
        val userAnswers = mapOf("q1" to "q1_opt_1") // answered q1 correctly, skipped q2

        val totalDurationSeconds = 180 * 60L
        val result = mockTestStore.evaluateTest(mockTest, userAnswers, totalDurationSeconds)

        assertNotNull(result.attemptId)
        assertEquals(totalDurationSeconds, result.timeSpentSeconds)
        assertEquals(1, result.correctAnswers)
        assertEquals(0, result.incorrectAnswers)
        assertEquals(1, result.attemptedQuestions)
        assertEquals(result.totalQuestions - 1, result.unattemptedQuestions)
        assertEquals(4, result.totalScore)
        assertEquals(100f, result.overallAccuracy, 0.01f)
    }
}

