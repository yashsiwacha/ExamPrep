package com.examprep.android.feature.rankaccelerator

import com.examprep.domain.model.AIRankAcceleratorReport
import com.examprep.domain.model.WeaknessSurgicalNode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIRankAcceleratorStore @Inject constructor() {

    private val _report = MutableStateFlow(defaultDiagnosticReport())
    val report: StateFlow<AIRankAcceleratorReport> = _report.asStateFlow()

    companion object {
        private var instance: AIRankAcceleratorStore? = null
        fun get(): AIRankAcceleratorStore {
            return instance ?: AIRankAcceleratorStore().also { instance = it }
        }

        private fun defaultDiagnosticReport(): AIRankAcceleratorReport = AIRankAcceleratorReport(
            studentName = "Yash Siwach",
            currentEstimatedPercentile = 97.06f,
            projectedPercentileAfterSurgery = 99.42f,
            predictedAirGain = 3850,
            totalMarksBleeding = 44,
            topVulnerabilities = listOf(
                WeaknessSurgicalNode(
                    subject = "Physics",
                    chapter = "Rotational Mechanics",
                    topicTrap = "Moment of Inertia about Non-Centroidal Axes & Rolling Constraint",
                    frequencyCount = 4,
                    marksBleeding = 16,
                    remedyStrategy = "Parallel Axis Theorem & strict energy conservation before torque balance",
                    surgicalActionQuizId = "surg_phy_rot"
                ),
                WeaknessSurgicalNode(
                    subject = "Chemistry",
                    chapter = "Thermodynamics",
                    topicTrap = "IUPAC Sign Convention for Work Done in Reversible vs Irreversible Expansion",
                    frequencyCount = 3,
                    marksBleeding = 12,
                    remedyStrategy = "Enforce w = -P_ext ΔV universally for gas expansions",
                    surgicalActionQuizId = "surg_chem_thermo"
                ),
                WeaknessSurgicalNode(
                    subject = "Mathematics",
                    chapter = "Calculus & Limits",
                    topicTrap = "L'Hôpital Indeterminate Form 0/0 Fallacy with Composite Exponentials",
                    frequencyCount = 4,
                    marksBleeding = 16,
                    remedyStrategy = "Taylor series polynomial expansion is 3x faster and avoids chain rule differentiation traps",
                    surgicalActionQuizId = "surg_math_calc"
                )
            ),
            surgicalDrillTitle = "AI Precision Surgery · 15-Minute Rapid Remediation"
        )
    }
}
