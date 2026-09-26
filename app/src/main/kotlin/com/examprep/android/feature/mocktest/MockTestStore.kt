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

    fun generateStandardMockTest(examId: String, allQuestions: List<Question>): MockTest {
        val physicsQuestions = allQuestions.filter { it.subjectId == "subj_jee_phy" || it.topicId.contains("phy") }.ifEmpty { allQuestions.take(5) }
        val chemistryQuestions = allQuestions.filter { it.subjectId == "subj_jee_chem" || it.topicId.contains("chem") }.ifEmpty { allQuestions.drop(1).take(5) }
        val mathsQuestions = allQuestions.filter { it.subjectId == "subj_jee_math" || it.topicId.contains("math") }.ifEmpty { allQuestions.takeLast(5) }

        val sections = listOf(
            MockTestSection(
                id = "sec_phy",
                name = "Physics",
                subjectId = "subj_jee_phy",
                questions = physicsQuestions.ifEmpty { allQuestions }
            ),
            MockTestSection(
                id = "sec_chem",
                name = "Chemistry",
                subjectId = "subj_jee_chem",
                questions = chemistryQuestions.ifEmpty { allQuestions }
            ),
            MockTestSection(
                id = "sec_math",
                name = "Mathematics",
                subjectId = "subj_jee_math",
                questions = mathsQuestions.ifEmpty { allQuestions }
            )
        )

        return MockTest(
            id = "mock_jee_all_india_1",
            examId = examId,
            title = "All-India JEE Main Full Mock Test #1",
            description = "Complete 3-Hour Simulation · Physics, Chemistry & Mathematics · Marking Scheme (+4 / -1)",
            durationMinutes = 180,
            totalMarks = sections.sumOf { it.questions.size } * 4,
            positiveMarks = 4,
            negativeMarks = 1,
            sections = sections
        )
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

        val result = MockTestResult(
            attemptId = attemptId,
            mockTestId = mockTest.id,
            title = mockTest.title,
            examName = "JEE Main 2026",
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
