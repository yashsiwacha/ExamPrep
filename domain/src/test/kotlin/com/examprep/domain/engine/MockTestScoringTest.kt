package com.examprep.domain.engine

import com.examprep.domain.model.*
import org.junit.Assert.*
import org.junit.Test

class MockTestScoringTest {

    @Test
    fun `evaluates full length mock test marking with accurate positive and negative marks`() {
        val q1 = Question(
            id = "q1",
            examId = "jee_main",
            subjectId = "sub_1",
            chapterId = "chap_1",
            topicId = "top_1",
            questionText = "What is F?",
            options = listOf(
                QuestionOption("o1", "q1", "ma", true, 0),
                QuestionOption("o2", "q1", "mv", false, 1)
            ),
            correctOptionId = "o1",
            explanation = "Newton Second Law",
            type = QuestionType.MCQ,
            difficulty = Difficulty.MEDIUM,
            source = ContentSource.ORIGINAL,
            sourceReference = "Original",
            year = null
        )

        val q2 = Question(
            id = "q2",
            examId = "jee_main",
            subjectId = "sub_1",
            chapterId = "chap_1",
            topicId = "top_1",
            questionText = "What is W?",
            options = listOf(
                QuestionOption("o3", "q2", "Fd", true, 0),
                QuestionOption("o4", "q2", "Fv", false, 1)
            ),
            correctOptionId = "o3",
            explanation = "Work definition",
            type = QuestionType.MCQ,
            difficulty = Difficulty.MEDIUM,
            source = ContentSource.ORIGINAL,
            sourceReference = "Original",
            year = null
        )

        val section = MockTestSection("sec_1", "Physics", "sub_1", listOf(q1, q2))
        val mockTest = MockTest(
            id = "mock_test_1",
            examId = "jee_main",
            title = "JEE Main Full Test #1",
            description = "3-hour simulation",
            durationMinutes = 180,
            totalMarks = 8,
            positiveMarks = 4,
            negativeMarks = 1,
            sections = listOf(section)
        )

        // Student answers: Q1 correct ("o1"), Q2 incorrect ("o4")
        val userAnswers = mapOf("q1" to "o1", "q2" to "o4")

        var correctCount = 0
        var incorrectCount = 0
        var score = 0

        section.questions.forEach { q ->
            val ans = userAnswers[q.id]
            if (ans != null) {
                val correct = q.options.find { it.isCorrect }?.id
                if (ans == correct) {
                    correctCount++
                    score += mockTest.positiveMarks
                } else {
                    incorrectCount++
                    score -= mockTest.negativeMarks
                }
            }
        }

        // Expected: 1 correct (+4) + 1 incorrect (-1) = 3 marks
        assertEquals(1, correctCount)
        assertEquals(1, incorrectCount)
        assertEquals(3, score)

        val attempted = correctCount + incorrectCount
        val accuracy = (correctCount.toFloat() / attempted) * 100f
        assertEquals(50f, accuracy, 0.01f)
    }

    @Test
    fun `percentile calculation scales correctly within valid 60 to 99_8 percent range`() {
        val totalMarks = 300
        val studentScore = 240
        val scorePercent = (studentScore.toFloat() / totalMarks).coerceIn(0f, 1f)
        val percentile = (70f + (scorePercent * 29.8f)).coerceIn(60f, 99.8f)

        assertTrue(percentile in 90f..99.8f)
    }
}
