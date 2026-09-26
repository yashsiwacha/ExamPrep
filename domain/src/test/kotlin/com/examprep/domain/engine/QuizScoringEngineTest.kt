package com.examprep.domain.engine

import com.examprep.domain.model.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class QuizScoringEngineTest {

    private lateinit var engine: QuizScoringEngine

    @Before
    fun setUp() {
        engine = QuizScoringEngineImpl()
    }

    @Test
    fun `scoreAttempt calculates JEE Main marking (+4 and -1) correctly`() {
        val quiz = Quiz(
            id = "quiz_1",
            mode = QuizMode.QUICK_QUIZ,
            examId = "exam_jee",
            topicId = "top_1",
            subjectId = "sub_1",
            questionCount = 3,
            durationMinutes = 10,
            markingScheme = MarkingScheme(correctMarks = 4f, incorrectMarks = -1f, unattemptedMarks = 0f)
        )

        val attempts = listOf(
            QuestionAttempt(id = "1", quizAttemptId = "att_1", questionId = "q1", selectedOptionId = "opt1", isCorrect = true, isSkipped = false, timeTakenMillis = 30000L, marksEarned = 4f),
            QuestionAttempt(id = "2", quizAttemptId = "att_1", questionId = "q2", selectedOptionId = "opt2", isCorrect = false, isSkipped = false, timeTakenMillis = 40000L, marksEarned = -1f),
            QuestionAttempt(id = "3", quizAttemptId = "att_1", questionId = "q3", selectedOptionId = null, isCorrect = false, isSkipped = true, timeTakenMillis = 10000L, marksEarned = 0f)
        )

        val quizAttempt = QuizAttempt(
            id = "att_1",
            quizId = "quiz_1",
            userId = "user_1",
            startTimeEpoch = System.currentTimeMillis() - 80000L,
            endTimeEpoch = System.currentTimeMillis(),
            questionAttempts = attempts,
            isCompleted = true
        )

        val scored = engine.scoreAttempt(quizAttempt, quiz)

        // 1 correct (+4), 1 incorrect (-1), 1 skipped (0) -> total score = 3
        assertEquals(3f, scored.totalScore, 0.01f)
        assertEquals(12f, scored.maxScore, 0.01f)
        assertEquals(1, scored.correctCount)
        assertEquals(1, scored.incorrectCount)
        assertEquals(1, scored.skippedCount)
        assertEquals(50f, scored.accuracy, 0.01f) // 1 correct out of 2 attempted = 50%
    }
}
