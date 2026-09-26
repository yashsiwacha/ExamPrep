package com.examprep.domain.engine

import com.examprep.domain.model.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.UUID

class StudyPlanningEngineTest {

    private lateinit var engine: StudyPlanningEngine

    @Before
    fun setUp() {
        engine = StudyPlanningEngineImpl()
    }

    private fun createSampleExam(): Exam {
        val topic1 = Topic(id = "top_1", chapterId = "chap_1", name = "Kinematics", orderIndex = 1, importanceWeight = 2.0f)
        val topic2 = Topic(id = "top_2", chapterId = "chap_1", name = "Laws of Motion", orderIndex = 2, importanceWeight = 1.5f)
        val topic3 = Topic(id = "top_3", chapterId = "chap_2", name = "Thermodynamics", orderIndex = 1, importanceWeight = 1.8f)

        val chapter1 = Chapter(id = "chap_1", subjectId = "sub_1", name = "Mechanics", orderIndex = 1, topics = listOf(topic1, topic2))
        val chapter2 = Chapter(id = "chap_2", subjectId = "sub_1", name = "Heat & Thermo", orderIndex = 2, topics = listOf(topic3))

        val subject1 = Subject(id = "sub_1", examId = "exam_jee", name = "Physics", shortName = "PHY", orderIndex = 1, chapters = listOf(chapter1, chapter2))

        return Exam(
            id = "exam_jee",
            name = "JEE Main 2026",
            shortName = "JEE",
            category = "Engineering",
            authority = "NTA",
            syllabusVersion = "2026.1",
            subjects = listOf(subject1),
            markingScheme = MarkingScheme(correctMarks = 4f, incorrectMarks = -1f)
        )
    }

    @Test
    fun `generatePlan creates valid phases and tasks`() = runTest {
        val profile = UserProfile(
            id = "user_1",
            examDate = System.currentTimeMillis() + (120L * 24 * 60 * 60 * 1000),
            dailyStudyHours = 4f
        )
        val exam = createSampleExam()

        val plan = engine.generatePlan(profile, exam, emptyList())

        assertNotNull(plan)
        assertEquals("user_1", plan.userId)
        assertEquals("exam_jee", plan.examId)
        assertTrue(plan.phases.isNotEmpty())

        val phaseTypes = plan.phases.map { it.phaseType }
        assertTrue(phaseTypes.contains(StudyPhaseType.FOUNDATION))
        assertTrue(phaseTypes.contains(StudyPhaseType.CORE_LEARNING))
        assertTrue(phaseTypes.contains(StudyPhaseType.PRACTICE))
        assertTrue(phaseTypes.contains(StudyPhaseType.REVISION))

        val allTasks = plan.phases.flatMap { it.tasks }
        assertTrue(allTasks.isNotEmpty())
        assertTrue(allTasks.all { it.estimatedMinutes > 0 })
    }

    @Test
    fun `rebalancePlan shifts pending tasks when days are missed`() = runTest {
        val profile = UserProfile(
            id = "user_1",
            examDate = System.currentTimeMillis() + (100L * 24 * 60 * 60 * 1000),
            dailyStudyHours = 4f
        )
        val exam = createSampleExam()
        val originalPlan = engine.generatePlan(profile, exam, emptyList())

        val rebalanced = engine.rebalancePlan(originalPlan, missedDays = 5)

        assertEquals(-5, rebalanced.daysAheadOrBehind)
        assertEquals(PlanStatus.ON_TRACK, rebalanced.status)
    }
}
