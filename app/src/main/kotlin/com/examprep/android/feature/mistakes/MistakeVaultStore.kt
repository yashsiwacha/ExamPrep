package com.examprep.android.feature.mistakes

import com.examprep.domain.model.MistakeItem
import com.examprep.domain.model.MistakeMasteryStatus
import com.examprep.domain.model.MistakeReason
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

import kotlinx.coroutines.flow.update

@Singleton
class MistakeVaultStore @Inject constructor() {

    private val _mistakes = MutableStateFlow<List<MistakeItem>>(defaultMistakes())
    val mistakes: StateFlow<List<MistakeItem>> = _mistakes.asStateFlow()

    @Synchronized
    fun recordMistake(
        questionId: String,
        questionText: String,
        options: List<String>,
        correctAnswer: String,
        studentAnswer: String?,
        explanation: String?,
        subject: String,
        chapter: String = "Core Concepts",
        reason: MistakeReason = MistakeReason.CONCEPTUAL
    ) {
        _mistakes.update { oldList ->
            val current = oldList.toMutableList()
            val existingIndex = current.indexOfFirst { it.questionId == questionId }
            if (existingIndex >= 0) {
                val existing = current[existingIndex]
                current[existingIndex] = existing.copy(
                    attemptCount = existing.attemptCount + 1,
                    consecutiveCorrect = 0,
                    masteryStatus = MistakeMasteryStatus.ACTIVE,
                    studentAnswer = studentAnswer,
                    reason = reason,
                    recordedAt = System.currentTimeMillis()
                )
            } else {
                current.add(
                    0,
                    MistakeItem(
                        id = "mistake_${System.nanoTime()}",
                        questionId = questionId,
                        questionText = questionText,
                        options = options,
                        correctAnswer = correctAnswer,
                        studentAnswer = studentAnswer,
                        explanation = explanation,
                        subject = subject,
                        chapterName = chapter,
                        reason = reason,
                        masteryStatus = MistakeMasteryStatus.ACTIVE
                    )
                )
            }
            current
        }
    }

    @Synchronized
    fun markPracticed(mistakeId: String, isCorrect: Boolean) {
        _mistakes.update { oldList ->
            oldList.map { item ->
                if (item.id == mistakeId) {
                    if (isCorrect) {
                        val newConsecutive = item.consecutiveCorrect + 1
                        item.copy(
                            consecutiveCorrect = newConsecutive,
                            masteryStatus = if (newConsecutive >= 2) MistakeMasteryStatus.MASTERED else MistakeMasteryStatus.REVIEWING
                        )
                    } else {
                        item.copy(
                            attemptCount = item.attemptCount + 1,
                            consecutiveCorrect = 0,
                            masteryStatus = MistakeMasteryStatus.ACTIVE
                        )
                    }
                } else item
            }
        }
    }

    @Synchronized
    fun updateReasonTag(mistakeId: String, reason: MistakeReason) {
        _mistakes.update { oldList ->
            oldList.map {
                if (it.id == mistakeId) it.copy(reason = reason) else it
            }
        }
    }

    @Synchronized
    fun removeMistake(mistakeId: String) {
        _mistakes.update { oldList ->
            oldList.filter { it.id != mistakeId }
        }
    }

    companion object {
        private var instance: MistakeVaultStore? = null
        fun get(): MistakeVaultStore {
            return instance ?: MistakeVaultStore().also { instance = it }
        }

        private fun defaultMistakes(): List<MistakeItem> = listOf(
            MistakeItem(
                id = "m_phy_1",
                questionId = "q_phy_rot_1",
                questionText = "A uniform solid cylinder of mass M and radius R rolls without slipping down an inclined plane of angle θ. What is its linear acceleration?",
                options = listOf("g sin θ", "2/3 g sin θ", "1/2 g sin θ", "3/4 g sin θ"),
                correctAnswer = "2/3 g sin θ",
                studentAnswer = "1/2 g sin θ",
                explanation = "For a solid cylinder, moment of inertia I = 1/2 M R². Linear acceleration a = (g sin θ) / (1 + I / (M R²)) = (g sin θ) / (1 + 1/2) = 2/3 g sin θ.",
                subject = "Physics",
                chapterName = "Rotational Mechanics",
                reason = MistakeReason.FORMULA_CONFUSION,
                masteryStatus = MistakeMasteryStatus.ACTIVE,
                attemptCount = 2,
                consecutiveCorrect = 0
            ),
            MistakeItem(
                id = "m_chem_1",
                questionId = "q_chem_thermo_1",
                questionText = "For an ideal gas undergoing isothermal reversible expansion, which of the following is true?",
                options = listOf("ΔU = 0 and q = -w", "ΔU > 0 and q = 0", "ΔH ≠ 0 and w = 0", "ΔS_univ = 0 and ΔU ≠ 0"),
                correctAnswer = "ΔU = 0 and q = -w",
                studentAnswer = "ΔS_univ = 0 and ΔU ≠ 0",
                explanation = "In an isothermal process for an ideal gas, temperature is constant, so internal energy ΔU = n C_v ΔT = 0. By First Law, ΔU = q + w = 0, thus q = -w.",
                subject = "Chemistry",
                chapterName = "Thermodynamics",
                reason = MistakeReason.CONCEPTUAL,
                masteryStatus = MistakeMasteryStatus.REVIEWING,
                attemptCount = 1,
                consecutiveCorrect = 1
            ),
            MistakeItem(
                id = "m_math_1",
                questionId = "q_math_calc_1",
                questionText = "Evaluate the limit: lim (x -> 0) [sin(x) - x] / x³",
                options = listOf("-1/6", "1/6", "0", "-1/3"),
                correctAnswer = "-1/6",
                studentAnswer = "1/6",
                explanation = "Using Taylor series expansion: sin(x) = x - x³/6 + O(x⁵). So (sin x - x)/x³ = (-x³/6)/x³ = -1/6.",
                subject = "Mathematics",
                chapterName = "Limits & Derivatives",
                reason = MistakeReason.CALCULATION,
                masteryStatus = MistakeMasteryStatus.ACTIVE,
                attemptCount = 1,
                consecutiveCorrect = 0
            ),
            MistakeItem(
                id = "m_phy_2",
                questionId = "q_phy_optics_1",
                questionText = "In Young's Double Slit Experiment, if the distance between the slits is halved and the screen distance is doubled, the fringe width becomes:",
                options = listOf("4 times", "2 times", "Halved", "Unchanged"),
                correctAnswer = "4 times",
                studentAnswer = "2 times",
                explanation = "Fringe width β = λ D / d. If D becomes 2D and d becomes d/2, new fringe width β' = λ (2D) / (d/2) = 4 (λ D / d) = 4β.",
                subject = "Physics",
                chapterName = "Wave Optics",
                reason = MistakeReason.MISREAD_QUESTION,
                masteryStatus = MistakeMasteryStatus.MASTERED,
                attemptCount = 3,
                consecutiveCorrect = 2
            )
        )
    }
}
