package com.examprep.android.feature.flashcards

import com.examprep.domain.model.Flashcard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlashcardStore @Inject constructor() {

    private val _flashcards = MutableStateFlow<List<Flashcard>>(defaultFlashcards())
    val flashcards: StateFlow<List<Flashcard>> = _flashcards.asStateFlow()

    fun toggleMastered(cardId: String) {
        _flashcards.value = _flashcards.value.map {
            if (it.id == cardId) it.copy(isMastered = !it.isMastered, reviewCount = it.reviewCount + 1) else it
        }
    }

    fun toggleBookmark(cardId: String) {
        _flashcards.value = _flashcards.value.map {
            if (it.id == cardId) it.copy(isBookmarked = !it.isBookmarked) else it
        }
    }

    companion object {
        private var instance: FlashcardStore? = null
        fun get(): FlashcardStore {
            return instance ?: FlashcardStore().also { instance = it }
        }

        private fun defaultFlashcards(): List<Flashcard> = listOf(
            Flashcard(
                id = "fc_phy_1",
                subject = "Physics",
                topic = "Thermodynamics",
                title = "Carnot Engine Efficiency",
                frontPrompt = "What is the theoretical maximum efficiency of a Carnot Engine operating between reservoirs at temperatures T_hot and T_cold?",
                backAnswer = "Efficiency η = 1 - (T_cold / T_hot). Temperatures must always be in Kelvin (K). No real heat engine operating between two temperatures can be more efficient than a Carnot engine.",
                keyFormula = "η = 1 - (T_C / T_H) = (W / Q_H)",
                mnemonicTip = "Remember: Cold over Hot, Kelvin or you're shot!",
                importanceRating = 5
            ),
            Flashcard(
                id = "fc_phy_2",
                subject = "Physics",
                topic = "Modern Physics",
                title = "De Broglie Wavelength",
                frontPrompt = "What is the de Broglie wavelength associated with a particle having mass m and kinetic energy K?",
                backAnswer = "Since p = √(2mK), the de Broglie wavelength λ = h / p = h / √(2mK). For an electron accelerated through potential difference V, λ ≈ 12.27 / √V Å.",
                keyFormula = "λ = h / p = h / √(2mK) = 1.227 / √V nm",
                mnemonicTip = "λ = h / √(2m qV)",
                importanceRating = 5
            ),
            Flashcard(
                id = "fc_phy_3",
                subject = "Physics",
                topic = "Optics",
                title = "Lens Maker's Formula",
                frontPrompt = "State the Lens Maker's formula for a thin lens of refractive index n_2 placed in a medium of refractive index n_1.",
                backAnswer = "1/f = ((n_2 / n_1) - 1) * (1/R_1 - 1/R_2). Apply Cartesian sign conventions for radii of curvature R_1 and R_2.",
                keyFormula = "1/f = (μ_rel - 1) * (1/R_1 - 1/R_2)",
                mnemonicTip = "Convention: Light from left, R measured from optical centre.",
                importanceRating = 4
            ),
            Flashcard(
                id = "fc_chem_1",
                subject = "Chemistry",
                topic = "Electrochemistry",
                title = "Nernst Equation",
                frontPrompt = "What is the Nernst Equation for electrode potential at 298 K?",
                backAnswer = "E_cell = E°_cell - (0.0591 / n) * log_10(Q), where n is number of moles of electrons transferred in balanced redox reaction and Q is reaction quotient [Products]^p / [Reactants]^r.",
                keyFormula = "E = E° - (0.0591 / n) log Q",
                mnemonicTip = "OIL RIG: Oxidation Is Loss, Reduction Is Gain.",
                importanceRating = 5
            ),
            Flashcard(
                id = "fc_chem_2",
                subject = "Chemistry",
                topic = "Chemical Kinetics",
                title = "Arrhenius Equation",
                frontPrompt = "How does temperature affect reaction rate constant k according to Arrhenius?",
                backAnswer = "k = A * e^(-E_a / (R T)). In linear logarithmic form: ln(k_2 / k_1) = (E_a / R) * [1/T_1 - 1/T_2]. Slope of ln(k) vs 1/T is -E_a/R.",
                keyFormula = "ln(k) = ln(A) - (E_a / RT)",
                mnemonicTip = "A plot of ln k vs 1/T has negative slope.",
                importanceRating = 5
            ),
            Flashcard(
                id = "fc_chem_3",
                subject = "Chemistry",
                topic = "Organic Chemistry",
                title = "Markovnikov's Rule",
                frontPrompt = "State Markovnikov's Rule for electrophilic addition of HX to an asymmetrical alkene.",
                backAnswer = "The electrophile (H⁺) attaches to the carbon of the double bond that already has the greater number of hydrogen atoms, yielding the more stable carbocation intermediate.",
                keyFormula = "R-CH=CH_2 + H-Br → R-CH(Br)-CH_3",
                mnemonicTip = "'The rich get richer': Carbon with more hydrogens gets more hydrogens.",
                importanceRating = 4
            ),
            Flashcard(
                id = "fc_math_1",
                subject = "Mathematics",
                topic = "Calculus",
                title = "Integration by Parts (ILATE Rule)",
                frontPrompt = "What is the formula for integration by parts, and what order determines the first function u(x)?",
                backAnswer = "∫ u v dx = u ∫ v dx - ∫ [u' * (∫ v dx)] dx. The choice of first function u follows ILATE order: Inverse trig, Logarithmic, Algebraic, Trigonometric, Exponential.",
                keyFormula = "∫ u v dx = u ∫ v dx - ∫ (du/dx * ∫ v dx) dx",
                mnemonicTip = "ILATE: Inverse, Log, Algebra, Trig, Exponential.",
                importanceRating = 5
            ),
            Flashcard(
                id = "fc_math_2",
                subject = "Mathematics",
                topic = "Probability",
                title = "Bayes' Theorem",
                frontPrompt = "State Bayes' Theorem for finding posterior probability P(A_i | B).",
                backAnswer = "P(A_i | B) = [P(A_i) * P(B | A_i)] / [Σ P(A_j) * P(B | A_j)]. Relates conditional probability to prior probabilities.",
                keyFormula = "P(A|B) = [P(B|A) * P(A)] / P(B)",
                mnemonicTip = "Posterior = (Likelihood × Prior) / Total Probability.",
                importanceRating = 5
            ),
            Flashcard(
                id = "fc_math_3",
                subject = "Mathematics",
                topic = "Coordinate Geometry",
                title = "Distance from Point to Line",
                frontPrompt = "What is the perpendicular distance d from point (x_0, y_0) to the line Ax + By + C = 0?",
                backAnswer = "d = |A x_0 + B y_0 + C| / √(A² + B²).",
                keyFormula = "d = |Ax_0 + By_0 + C| / √(A² + B²)",
                mnemonicTip = "Substitute point into equation, take absolute value, divide by root of squared coefficients.",
                importanceRating = 4
            )
        )
    }
}
