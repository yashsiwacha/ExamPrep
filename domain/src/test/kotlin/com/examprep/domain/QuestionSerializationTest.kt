package com.examprep.domain

import com.examprep.domain.model.Question
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class QuestionSerializationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun `jee_main_paper_1 json parses correctly into Question domain objects`() {
        val file = File("../data/src/main/assets/content/exam_jee_main/jee_main_paper_1.json")
        assertTrue("File must exist at ${file.canonicalPath}", file.exists())
        val questions: List<Question> = json.decodeFromString(file.readText())
        assertEquals(100, questions.size)

        val phy = questions.count { it.subjectId == "sub_jee_phy" }
        val chem = questions.count { it.subjectId == "sub_jee_chem" }
        val math = questions.count { it.subjectId == "sub_jee_math" }

        assertTrue(phy > 0)
        assertTrue(chem > 0)
        assertTrue(math > 0)
        assertTrue(questions.all { it.options.size == 4 })
        assertTrue(questions.all { it.correctOptionId.isNotBlank() })
    }
}
