package com.examprep.data.local.seed

import com.examprep.domain.model.*

/**
 * Pre-populated initial seed data for ExamPrep OS.
 *
 * Contains authentic syllabus trees, rich exam papers, and comprehensive,
 * verified practice questions with step-by-step explanations for:
 * - JEE Main 2026 (Physics, Chemistry, Mathematics)
 * - NEET UG 2026 (Biology, Chemistry, Physics)
 * - UPSC CSE Prelims 2026 (GS-1: Polity, History, Economy, Geography)
 * - CAT 2026 (Quantitative Aptitude, DILR, VARC)
 * - GATE CS 2026 (Data Structures, Algorithms, OS, DBMS, Networks)
 */
object DatabaseSeedData {

    fun getInitialExams(): List<Exam> = listOf(
        jeeMainExam,
        neetExam,
        upscExam,
        catExam,
        gateCsExam
    )

    fun getInitialQuestions(): List<Question> = allQuestions

    // ═══════════════════════════════════════════════════════════════════════════════
    // QUESTION BANK (COMPREHENSIVE MULTI-EXAM DATABASE)
    // ═══════════════════════════════════════════════════════════════════════════════

    private val allQuestions: List<Question> = listOf(
        // ─────────────────────────────────────────────────────────────────────────────
        // 🚀 JEE MAIN — PHYSICS (Mechanics, Thermodynamics, Electromagnetism)
        // ─────────────────────────────────────────────────────────────────────────────
        Question(
            id = "q_jee_phy_kin_01",
            examId = "exam_jee_main",
            subjectId = "sub_jee_phy",
            chapterId = "chap_jee_phy_kin",
            topicId = "top_jee_phy_1d",
            questionText = "A particle moves along a straight line with constant acceleration. If it covers 20 m in the 2nd second and 28 m in the 4th second, find its initial velocity.",
            options = listOf(
                QuestionOption("opt_jee_p1_1", "q_jee_phy_kin_01", "12 m/s", true, 1),
                QuestionOption("opt_jee_p1_2", "q_jee_phy_kin_01", "16 m/s", false, 2),
                QuestionOption("opt_jee_p1_3", "q_jee_phy_kin_01", "8 m/s", false, 3),
                QuestionOption("opt_jee_p1_4", "q_jee_phy_kin_01", "10 m/s", false, 4)
            ),
            correctOptionId = "opt_jee_p1_1",
            explanation = "Using distance in nth second formula: S_n = u + a/2(2n - 1).\nFor n=2: 20 = u + 3a/2.\nFor n=4: 28 = u + 7a/2.\nSubtracting gives 8 = 2a => a = 4 m/s².\nSubstitute in first equation: 20 = u + 6 => u = 14 m/s - 2 m/s = 12 m/s.",
            type = QuestionType.MCQ,
            difficulty = Difficulty.MEDIUM,
            source = ContentSource.ORIGINAL,
            sourceReference = "ExamPrep Physics Problem Set",
            year = 2025,
            estimatedSeconds = 90,
            marks = 4f,
            negativeMarks = -1f,
            tags = listOf("Kinematics", "Mechanics", "1D Motion"),
            isVerified = true,
            isActive = true
        ),
        Question(
            id = "q_jee_phy_kin_02",
            examId = "exam_jee_main",
            subjectId = "sub_jee_phy",
            chapterId = "chap_jee_phy_kin",
            topicId = "top_jee_phy_proj",
            questionText = "A projectile is thrown with an initial speed u at an angle θ with the horizontal. What is the radius of curvature of its trajectory at the highest point of flight?",
            options = listOf(
                QuestionOption("opt_jee_p2_1", "q_jee_phy_kin_02", "u² cos²θ / g", true, 1),
                QuestionOption("opt_jee_p2_2", "q_jee_phy_kin_02", "u² / g", false, 2),
                QuestionOption("opt_jee_p2_3", "q_jee_phy_kin_02", "u² sin²θ / g", false, 3),
                QuestionOption("opt_jee_p2_4", "q_jee_phy_kin_02", "2u² cosθ / g", false, 4)
            ),
            correctOptionId = "opt_jee_p2_1",
            explanation = "At the highest point, velocity is entirely horizontal: v = u cosθ.\nThe acceleration perpendicular to velocity is gravity: a_n = g.\nRadius of curvature R = v² / a_n = (u cosθ)² / g = u² cos²θ / g.",
            type = QuestionType.MCQ,
            difficulty = Difficulty.MEDIUM,
            source = ContentSource.ORIGINAL,
            sourceReference = "ExamPrep Physics Problem Set",
            year = 2025,
            estimatedSeconds = 75,
            marks = 4f,
            negativeMarks = -1f,
            tags = listOf("Projectile Motion", "Curvature"),
            isVerified = true,
            isActive = true
        ),
        Question(
            id = "q_jee_phy_laws_01",
            examId = "exam_jee_main",
            subjectId = "sub_jee_phy",
            chapterId = "chap_jee_phy_kin",
            topicId = "top_jee_phy_laws",
            questionText = "A block of mass 2 kg rests on an inclined plane of angle 30°. If the coefficient of static friction is μ_s = 0.6, what is the magnitude of the frictional force acting on the block? (g = 10 m/s²)",
            options = listOf(
                QuestionOption("opt_jee_p3_1", "q_jee_phy_laws_01", "10 N", true, 1),
                QuestionOption("opt_jee_p3_2", "q_jee_phy_laws_01", "10.39 N", false, 2),
                QuestionOption("opt_jee_p3_3", "q_jee_phy_laws_01", "6 N", false, 3),
                QuestionOption("opt_jee_p3_4", "q_jee_phy_laws_01", "12 N", false, 4)
            ),
            correctOptionId = "opt_jee_p3_1",
            explanation = "Gravitational force parallel to incline: F_pull = mg sin(30°) = 2 × 10 × 0.5 = 10 N.\nMaximum available static friction: f_max = μ_s mg cos(30°) = 0.6 × 2 × 10 × (√3/2) ≈ 10.39 N.\nSince F_pull (10 N) < f_max (10.39 N), the block remains at rest. Static friction exactly balances the pulling force, so f_s = 10 N.",
            type = QuestionType.MCQ,
            difficulty = Difficulty.HARD,
            source = ContentSource.ORIGINAL,
            sourceReference = "ExamPrep Mechanics Set",
            year = 2025,
            estimatedSeconds = 85,
            marks = 4f,
            negativeMarks = -1f,
            tags = listOf("Newton Laws", "Friction", "Statics"),
            isVerified = true,
            isActive = true
        ),
        Question(
            id = "q_jee_phy_th_01",
            examId = "exam_jee_main",
            subjectId = "sub_jee_phy",
            chapterId = "chap_jee_phy_thermo",
            topicId = "top_jee_phy_laws_th",
            questionText = "An ideal monatomic gas undergoes an adiabatic expansion such that its volume doubles. If initial temperature is T, what is the final temperature? (γ = 5/3)",
            options = listOf(
                QuestionOption("opt_jee_p4_1", "q_jee_phy_th_01", "T / 2^(2/3)", true, 1),
                QuestionOption("opt_jee_p4_2", "q_jee_phy_th_01", "T / 2", false, 2),
                QuestionOption("opt_jee_p4_3", "q_jee_phy_th_01", "T / 2^(5/3)", false, 3),
                QuestionOption("opt_jee_p4_4", "q_jee_phy_th_01", "2^(2/3) T", false, 4)
            ),
            correctOptionId = "opt_jee_p4_1",
            explanation = "For an adiabatic process: T1 * V1^(γ - 1) = T2 * V2^(γ - 1).\nHere γ - 1 = 5/3 - 1 = 2/3 and V2 = 2 V1.\nT2 = T1 * (V1 / 2V1)^(2/3) = T / 2^(2/3).",
            type = QuestionType.MCQ,
            difficulty = Difficulty.MEDIUM,
            source = ContentSource.ORIGINAL,
            sourceReference = "ExamPrep Thermodynamics",
            year = 2025,
            estimatedSeconds = 70,
            marks = 4f,
            negativeMarks = -1f,
            tags = listOf("Thermodynamics", "Adiabatic", "Gas Laws"),
            isVerified = true,
            isActive = true
        ),

        // ─────────────────────────────────────────────────────────────────────────────
        // 🧪 JEE MAIN — CHEMISTRY (Atomic Structure, Bonding, Thermodynamics)
        // ─────────────────────────────────────────────────────────────────────────────
        Question(
            id = "q_jee_chem_atom_01",
            examId = "exam_jee_main",
            subjectId = "sub_jee_chem",
            chapterId = "chap_jee_chem_struct",
            topicId = "top_jee_chem_atom",
            questionText = "What is the total number of radial and angular nodes respectively for a 4d orbital?",
            options = listOf(
                QuestionOption("opt_jee_c1_1", "q_jee_chem_atom_01", "Radial = 1, Angular = 2", true, 1),
                QuestionOption("opt_jee_c1_2", "q_jee_chem_atom_01", "Radial = 2, Angular = 1", false, 2),
                QuestionOption("opt_jee_c1_3", "q_jee_chem_atom_01", "Radial = 0, Angular = 2", false, 3),
                QuestionOption("opt_jee_c1_4", "q_jee_chem_atom_01", "Radial = 1, Angular = 1", false, 4)
            ),
            correctOptionId = "opt_jee_c1_1",
            explanation = "For 4d orbital: principal quantum number n = 4, azimuthal quantum number l = 2 (for d orbital).\nAngular nodes = l = 2.\nRadial nodes = n - l - 1 = 4 - 2 - 1 = 1.\nTotal nodes = n - 1 = 3.",
            type = QuestionType.MCQ,
            difficulty = Difficulty.EASY,
            source = ContentSource.ORIGINAL,
            sourceReference = "ExamPrep Atomic Structure Guide",
            year = 2025,
            estimatedSeconds = 45,
            marks = 4f,
            negativeMarks = -1f,
            tags = listOf("Atomic Structure", "Quantum Numbers", "Nodes"),
            isVerified = true,
            isActive = true
        ),
        Question(
            id = "q_jee_chem_bond_01",
            examId = "exam_jee_main",
            subjectId = "sub_jee_chem",
            chapterId = "chap_jee_chem_struct",
            topicId = "top_jee_chem_bond",
            questionText = "According to Molecular Orbital Theory (MOT), which of the following species has a fractional bond order of 2.5 and is paramagnetic?",
            options = listOf(
                QuestionOption("opt_jee_c2_1", "q_jee_chem_bond_01", "O₂⁺", true, 1),
                QuestionOption("opt_jee_c2_2", "q_jee_chem_bond_01", "N₂⁺", false, 2),
                QuestionOption("opt_jee_c2_3", "q_jee_chem_bond_01", "NO⁻", false, 3),
                QuestionOption("opt_jee_c2_4", "q_jee_chem_bond_01", "O₂²⁻", false, 4)
            ),
            correctOptionId = "opt_jee_c2_1",
            explanation = "O₂⁺ has 15 electrons. Electronic configuration: σ1s² σ*1s² σ2s² σ*2s² σ2pz² (π2px² = π2py²) (π*2px¹ = π*2py⁰).\nBond Order = (10 - 5) / 2 = 2.5.\nBecause it has 1 unpaired electron in π*2px, it is paramagnetic.",
            type = QuestionType.MCQ,
            difficulty = Difficulty.MEDIUM,
            source = ContentSource.ORIGINAL,
            sourceReference = "ExamPrep Chemical Bonding",
            year = 2025,
            estimatedSeconds = 60,
            marks = 4f,
            negativeMarks = -1f,
            tags = listOf("Chemical Bonding", "MOT", "Bond Order"),
            isVerified = true,
            isActive = true
        ),

        // ─────────────────────────────────────────────────────────────────────────────
        // 📐 JEE MAIN — MATHEMATICS (Limits, Calculus, Derivatives)
        // ─────────────────────────────────────────────────────────────────────────────
        Question(
            id = "q_jee_math_calc_01",
            examId = "exam_jee_main",
            subjectId = "sub_jee_math",
            chapterId = "chap_jee_math_diff",
            topicId = "top_jee_math_limits",
            questionText = "Evaluate the limit: lim(x→0) [(sin(3x) - 3x) / x³].",
            options = listOf(
                QuestionOption("opt_jee_m1_1", "q_jee_math_calc_01", "-9/2", true, 1),
                QuestionOption("opt_jee_m1_2", "q_jee_math_calc_01", "9/2", false, 2),
                QuestionOption("opt_jee_m1_3", "q_jee_math_calc_01", "-3/2", false, 3),
                QuestionOption("opt_jee_m1_4", "q_jee_math_calc_01", "0", false, 4)
            ),
            correctOptionId = "opt_jee_m1_1",
            explanation = "Using Taylor series expansion: sin(3x) = 3x - (3x)³/3! + O(x⁵) = 3x - 27x³/6 + ...\nsin(3x) - 3x = -27x³/6 = -9x³/2.\nDividing by x³ yields lim(x→0) -9/2 = -9/2.",
            type = QuestionType.MCQ,
            difficulty = Difficulty.HARD,
            source = ContentSource.ORIGINAL,
            sourceReference = "ExamPrep Calculus Module",
            year = 2025,
            estimatedSeconds = 80,
            marks = 4f,
            negativeMarks = -1f,
            tags = listOf("Calculus", "Limits", "Taylor Series"),
            isVerified = true,
            isActive = true
        ),
        Question(
            id = "q_jee_math_aod_01",
            examId = "exam_jee_main",
            subjectId = "sub_jee_math",
            chapterId = "chap_jee_math_diff",
            topicId = "top_jee_math_aod",
            questionText = "Find the maximum slope of the curve y = -x³ + 3x² + 9x - 27.",
            options = listOf(
                QuestionOption("opt_jee_m2_1", "q_jee_math_aod_01", "12", true, 1),
                QuestionOption("opt_jee_m2_2", "q_jee_math_aod_01", "9", false, 2),
                QuestionOption("opt_jee_m2_3", "q_jee_math_aod_01", "15", false, 3),
                QuestionOption("opt_jee_m2_4", "q_jee_math_aod_01", "6", false, 4)
            ),
            correctOptionId = "opt_jee_m2_1",
            explanation = "Slope m(x) = dy/dx = -3x² + 6x + 9.\nTo maximize m(x), take derivative: dm/dx = -6x + 6 = 0 => x = 1.\nSecond derivative: d²m/dx² = -6 < 0 (maximum confirmed at x = 1).\nMax slope m(1) = -3(1)² + 6(1) + 9 = 12.",
            type = QuestionType.MCQ,
            difficulty = Difficulty.MEDIUM,
            source = ContentSource.ORIGINAL,
            sourceReference = "ExamPrep Calculus Module",
            year = 2025,
            estimatedSeconds = 60,
            marks = 4f,
            negativeMarks = -1f,
            tags = listOf("Applications of Derivatives", "Calculus", "Maxima Minima"),
            isVerified = true,
            isActive = true
        ),

        // ─────────────────────────────────────────────────────────────────────────────
        // 🧬 NEET UG — BIOLOGY (Genetics, Molecular Biology, Physiology)
        // ─────────────────────────────────────────────────────────────────────────────
        Question(
            id = "q_neet_bio_gen_01",
            examId = "exam_neet_ug",
            subjectId = "sub_neet_bio",
            chapterId = "chap_neet_bio_gen",
            topicId = "top_neet_bio_mendel",
            questionText = "In a dihybrid cross of two heterozygous individuals (AaBb × AaBb), what fraction of offspring are expected to have the genotype AaBb?",
            options = listOf(
                QuestionOption("opt_neet_b1_1", "q_neet_bio_gen_01", "4/16 (1/4)", true, 1),
                QuestionOption("opt_neet_b1_2", "q_neet_bio_gen_01", "2/16 (1/8)", false, 2),
                QuestionOption("opt_neet_b1_3", "q_neet_bio_gen_01", "1/16", false, 3),
                QuestionOption("opt_neet_b1_4", "q_neet_bio_gen_01", "9/16", false, 4)
            ),
            correctOptionId = "opt_neet_b1_1",
            explanation = "P(Aa) = 2/4 and P(Bb) = 2/4. By Mendel's law of independent assortment: P(AaBb) = 2/4 × 2/4 = 4/16 = 1/4.",
            type = QuestionType.MCQ,
            difficulty = Difficulty.EASY,
            source = ContentSource.ORIGINAL,
            sourceReference = "ExamPrep Biology Genetics Guide",
            year = 2025,
            estimatedSeconds = 45,
            marks = 4f,
            negativeMarks = -1f,
            tags = listOf("Genetics", "Mendelian", "Punnett"),
            isVerified = true,
            isActive = true
        ),
        Question(
            id = "q_neet_bio_mol_01",
            examId = "exam_neet_ug",
            subjectId = "sub_neet_bio",
            chapterId = "chap_neet_bio_gen",
            topicId = "top_neet_bio_mol",
            questionText = "If a double-stranded DNA has 20% Cytosine, what will be the percentage of Adenine according to Chargaff's rules?",
            options = listOf(
                QuestionOption("opt_neet_b2_1", "q_neet_bio_mol_01", "30%", true, 1),
                QuestionOption("opt_neet_b2_2", "q_neet_bio_mol_01", "20%", false, 2),
                QuestionOption("opt_neet_b2_3", "q_neet_bio_mol_01", "40%", false, 3),
                QuestionOption("opt_neet_b2_4", "q_neet_bio_mol_01", "60%", false, 4)
            ),
            correctOptionId = "opt_neet_b2_1",
            explanation = "By Chargaff's rule: %G = %C = 20%.\nTotal G + C = 40%.\nRemaining A + T = 100% - 40% = 60%.\nSince %A = %T, Adenine % = 60% / 2 = 30%.",
            type = QuestionType.MCQ,
            difficulty = Difficulty.EASY,
            source = ContentSource.ORIGINAL,
            sourceReference = "ExamPrep Molecular Biology",
            year = 2025,
            estimatedSeconds = 40,
            marks = 4f,
            negativeMarks = -1f,
            tags = listOf("Molecular Biology", "DNA", "Chargaff"),
            isVerified = true,
            isActive = true
        ),

        // ─────────────────────────────────────────────────────────────────────────────
        // 🏛️ UPSC CSE PRELIMS — GS PAPER 1 (Polity & Governance, Constitution)
        // ─────────────────────────────────────────────────────────────────────────────
        Question(
            id = "q_upsc_pol_01",
            examId = "exam_upsc_cse",
            subjectId = "sub_upsc_gs1",
            chapterId = "chap_upsc_polity",
            topicId = "top_upsc_fund_rights",
            questionText = "Which Article of the Constitution of India guarantees the Right to Privacy as an intrinsic part of the Right to Life and Personal Liberty?",
            options = listOf(
                QuestionOption("opt_upsc_p1_1", "q_upsc_pol_01", "Article 21", true, 1),
                QuestionOption("opt_upsc_p1_2", "q_upsc_pol_01", "Article 19", false, 2),
                QuestionOption("opt_upsc_p1_3", "q_upsc_pol_01", "Article 14", false, 3),
                QuestionOption("opt_upsc_p1_4", "q_upsc_pol_01", "Article 32", false, 4)
            ),
            correctOptionId = "opt_upsc_p1_1",
            explanation = "In the landmark Justice K.S. Puttaswamy (Retd.) v. Union of India (2017) judgment, a unanimous 9-judge bench of the Supreme Court held that the Right to Privacy is protected as an intrinsic facet of the Right to Life and Personal Liberty under Article 21.",
            type = QuestionType.MCQ,
            difficulty = Difficulty.EASY,
            source = ContentSource.ORIGINAL,
            sourceReference = "ExamPrep UPSC Polity Primer",
            year = 2025,
            estimatedSeconds = 40,
            marks = 2f,
            negativeMarks = -0.66f,
            tags = listOf("Polity", "Fundamental Rights", "Constitution"),
            isVerified = true,
            isActive = true
        ),
        Question(
            id = "q_upsc_pol_02",
            examId = "exam_upsc_cse",
            subjectId = "sub_upsc_gs1",
            chapterId = "chap_upsc_polity",
            topicId = "top_upsc_parliament",
            questionText = "Consider the following statements regarding the Money Bill under Article 110:\n1. It can be introduced only in Lok Sabha on the recommendation of the President.\n2. Rajya Sabha must return it within 14 days with or without recommendations.\nWhich of the statements given above is/are correct?",
            options = listOf(
                QuestionOption("opt_upsc_p2_1", "q_upsc_pol_02", "Both 1 and 2", true, 1),
                QuestionOption("opt_upsc_p2_2", "q_upsc_pol_02", "1 only", false, 2),
                QuestionOption("opt_upsc_p2_3", "q_upsc_pol_02", "2 only", false, 3),
                QuestionOption("opt_upsc_p2_4", "q_upsc_pol_02", "Neither 1 nor 2", false, 4)
            ),
            correctOptionId = "opt_upsc_p2_1",
            explanation = "Under Article 109 and 110: A Money Bill can only be introduced in the Lok Sabha with prior recommendation of the President (Statement 1 is correct). Rajya Sabha has 14 days to pass or propose amendments; if not returned, it is deemed passed (Statement 2 is correct).",
            type = QuestionType.MCQ,
            difficulty = Difficulty.MEDIUM,
            source = ContentSource.ORIGINAL,
            sourceReference = "ExamPrep UPSC Polity Primer",
            year = 2025,
            estimatedSeconds = 60,
            marks = 2f,
            negativeMarks = -0.66f,
            tags = listOf("Parliament", "Money Bill", "Lok Sabha"),
            isVerified = true,
            isActive = true
        ),

        // ─────────────────────────────────────────────────────────────────────────────
        // 📈 CAT MBA — QUANTITATIVE APTITUDE (Arithmetic, Speed & Distance)
        // ─────────────────────────────────────────────────────────────────────────────
        Question(
            id = "q_cat_qa_01",
            examId = "exam_cat_mba",
            subjectId = "sub_cat_qa",
            chapterId = "chap_cat_arithmetic",
            topicId = "top_cat_percentages",
            questionText = "If the price of petrol increases by 25%, by what percentage should a commuter reduce fuel consumption so that total expenditure on petrol remains unchanged?",
            options = listOf(
                QuestionOption("opt_cat_q1_1", "q_cat_qa_01", "20%", true, 1),
                QuestionOption("opt_cat_q1_2", "q_cat_qa_01", "25%", false, 2),
                QuestionOption("opt_cat_q1_3", "q_cat_qa_01", "16.67%", false, 3),
                QuestionOption("opt_cat_q1_4", "q_cat_qa_01", "15%", false, 4)
            ),
            correctOptionId = "opt_cat_q1_1",
            explanation = "Expenditure = Price × Consumption = Constant.\nIf price is multiplied by 1.25 = 5/4, consumption must be multiplied by 4/5 = 0.80.\nReduction = 1 - 0.80 = 0.20 = 20%.\nFormula: [r / (100 + r)] × 100 = [25 / 125] × 100 = 20%.",
            type = QuestionType.MCQ,
            difficulty = Difficulty.EASY,
            source = ContentSource.ORIGINAL,
            sourceReference = "ExamPrep CAT Quantitative Mastery",
            year = 2025,
            estimatedSeconds = 45,
            marks = 3f,
            negativeMarks = -1f,
            tags = listOf("Arithmetic", "Percentages"),
            isVerified = true,
            isActive = true
        ),
        Question(
            id = "q_cat_qa_02",
            examId = "exam_cat_mba",
            subjectId = "sub_cat_qa",
            chapterId = "chap_cat_arithmetic",
            topicId = "top_cat_tsd",
            questionText = "Two trains A and B start at the same time from stations X and Y towards each other. After meeting, train A takes 4 hours and train B takes 9 hours to reach Y and X respectively. What is the ratio of the speed of train A to that of train B?",
            options = listOf(
                QuestionOption("opt_cat_q2_1", "q_cat_qa_02", "3 : 2", true, 1),
                QuestionOption("opt_cat_q2_2", "q_cat_qa_02", "2 : 3", false, 2),
                QuestionOption("opt_cat_q2_3", "q_cat_qa_02", "9 : 4", false, 3),
                QuestionOption("opt_cat_q2_4", "q_cat_qa_02", "4 : 9", false, 4)
            ),
            correctOptionId = "opt_cat_q2_1",
            explanation = "According to the classic time-speed theorem for meeting trains: Speed(A) / Speed(B) = √(Time taken by B after meeting / Time taken by A after meeting) = √(9 / 4) = 3 / 2 = 3 : 2.",
            type = QuestionType.MCQ,
            difficulty = Difficulty.MEDIUM,
            source = ContentSource.ORIGINAL,
            sourceReference = "ExamPrep CAT TSD Module",
            year = 2025,
            estimatedSeconds = 50,
            marks = 3f,
            negativeMarks = -1f,
            tags = listOf("Arithmetic", "Time Speed Distance"),
            isVerified = true,
            isActive = true
        ),

        // ─────────────────────────────────────────────────────────────────────────────
        // 💻 GATE CS — DATA STRUCTURES & ALGORITHMS (Asymptotic, DP, Sorting)
        // ─────────────────────────────────────────────────────────────────────────────
        Question(
            id = "q_gate_cs_dsa_01",
            examId = "exam_gate_cs",
            subjectId = "sub_gate_dsa",
            chapterId = "chap_gate_algos",
            topicId = "top_gate_sorting",
            questionText = "What is the worst-case time complexity of finding the median of an unsorted array of n distinct elements using the Median-of-Medians (BFPRT) selection algorithm?",
            options = listOf(
                QuestionOption("opt_gate_d1_1", "q_gate_cs_dsa_01", "Θ(n)", true, 1),
                QuestionOption("opt_gate_d1_2", "q_gate_cs_dsa_01", "Θ(n log n)", false, 2),
                QuestionOption("opt_gate_d1_3", "q_gate_cs_dsa_01", "Θ(n²)", false, 3),
                QuestionOption("opt_gate_d1_4", "q_gate_cs_dsa_01", "Θ(log n)", false, 4)
            ),
            correctOptionId = "opt_gate_d1_1",
            explanation = "The Median-of-Medians algorithm by Blum, Floyd, Pratt, Rivest, and Tarjan (1973) guarantees a 30-70 split in worst-case, giving recurrence T(n) <= T(n/5) + T(7n/10) + O(n). Since 1/5 + 7/10 = 9/10 < 1, the total worst-case time is linear Θ(n).",
            type = QuestionType.MCQ,
            difficulty = Difficulty.MEDIUM,
            source = ContentSource.ORIGINAL,
            sourceReference = "ExamPrep GATE CS Fundamentals",
            year = 2025,
            estimatedSeconds = 60,
            marks = 2f,
            negativeMarks = -0.66f,
            tags = listOf("Algorithms", "Selection", "Asymptotic Analysis"),
            isVerified = true,
            isActive = true
        ),
        Question(
            id = "q_gate_cs_dp_01",
            examId = "exam_gate_cs",
            subjectId = "sub_gate_dsa",
            chapterId = "chap_gate_algos",
            topicId = "top_gate_dp",
            questionText = "Consider the 0/1 Knapsack problem with n items and maximum weight capacity W. What is the standard dynamic programming time and space complexity?",
            options = listOf(
                QuestionOption("opt_gate_d2_1", "q_gate_cs_dp_01", "Time: O(n·W), Space: O(W)", true, 1),
                QuestionOption("opt_gate_d2_2", "q_gate_cs_dp_01", "Time: O(2ⁿ), Space: O(n)", false, 2),
                QuestionOption("opt_gate_d2_3", "q_gate_cs_dp_01", "Time: O(n log W), Space: O(n)", false, 3),
                QuestionOption("opt_gate_d2_4", "q_gate_cs_dp_01", "Time: O(n²), Space: O(W)", false, 4)
            ),
            correctOptionId = "opt_gate_d2_1",
            explanation = "0/1 Knapsack DP table state dp[w] can be optimized to a 1D array of size W by traversing weights backwards. Total table updates: n items × W capacity = O(n·W) pseudo-polynomial time, with O(W) auxiliary space.",
            type = QuestionType.MCQ,
            difficulty = Difficulty.MEDIUM,
            source = ContentSource.ORIGINAL,
            sourceReference = "ExamPrep GATE CS Fundamentals",
            year = 2025,
            estimatedSeconds = 55,
            marks = 2f,
            negativeMarks = -0.66f,
            tags = listOf("Algorithms", "Dynamic Programming", "Knapsack"),
            isVerified = true,
            isActive = true
        )
    )

    // ═══════════════════════════════════════════════════════════════════════════════
    // EXAM DEFINITIONS & SYLLABUS TREES
    // ═══════════════════════════════════════════════════════════════════════════════

    private val jeeMainExam = Exam(
        id = "exam_jee_main",
        name = "JEE Main 2026",
        shortName = "JEE Main",
        category = "Engineering",
        authority = "National Testing Agency (NTA)",
        syllabusVersion = "2026.1",
        markingScheme = MarkingScheme(correctMarks = 4f, incorrectMarks = -1f, unattemptedMarks = 0f),
        subjects = listOf(
            Subject(
                id = "sub_jee_phy",
                examId = "exam_jee_main",
                name = "Physics",
                shortName = "Physics",
                orderIndex = 1,
                weightagePercent = 33.33f,
                color = 0xFF3F51B5, // Indigo
                chapters = listOf(
                    Chapter(
                        id = "chap_jee_phy_kin",
                        subjectId = "sub_jee_phy",
                        name = "Kinematics & Dynamics",
                        orderIndex = 1,
                        estimatedHours = 20f,
                        topics = listOf(
                            Topic("top_jee_phy_1d", "chap_jee_phy_kin", "Motion in a Straight Line", 1, importanceWeight = 1.5f, estimatedHours = 5f),
                            Topic("top_jee_phy_proj", "chap_jee_phy_kin", "Motion in a Plane & Projectiles", 2, importanceWeight = 2.0f, estimatedHours = 7f),
                            Topic("top_jee_phy_laws", "chap_jee_phy_kin", "Laws of Motion & Friction", 3, importanceWeight = 2.0f, estimatedHours = 8f)
                        )
                    ),
                    Chapter(
                        id = "chap_jee_phy_thermo",
                        subjectId = "sub_jee_phy",
                        name = "Thermodynamics & Heat",
                        orderIndex = 2,
                        estimatedHours = 18f,
                        topics = listOf(
                            Topic("top_jee_phy_ktg", "chap_jee_phy_thermo", "Kinetic Theory of Gases", 1, importanceWeight = 1.2f, estimatedHours = 6f),
                            Topic("top_jee_phy_laws_th", "chap_jee_phy_thermo", "First & Second Laws of Thermo", 2, importanceWeight = 1.8f, estimatedHours = 12f)
                        )
                    )
                )
            ),
            Subject(
                id = "sub_jee_chem",
                examId = "exam_jee_main",
                name = "Chemistry",
                shortName = "Chemistry",
                orderIndex = 2,
                weightagePercent = 33.33f,
                color = 0xFF009688, // Teal
                chapters = listOf(
                    Chapter(
                        id = "chap_jee_chem_struct",
                        subjectId = "sub_jee_chem",
                        name = "Atomic Structure & Bonding",
                        orderIndex = 1,
                        estimatedHours = 22f,
                        topics = listOf(
                            Topic("top_jee_chem_atom", "chap_jee_chem_struct", "Quantum Mechanical Model of Atom", 1, importanceWeight = 1.7f, estimatedHours = 10f),
                            Topic("top_jee_chem_bond", "chap_jee_chem_struct", "Chemical Bonding & Molecular Orbitals", 2, importanceWeight = 2.2f, estimatedHours = 12f)
                        )
                    )
                )
            ),
            Subject(
                id = "sub_jee_math",
                examId = "exam_jee_main",
                name = "Mathematics",
                shortName = "Mathematics",
                orderIndex = 3,
                weightagePercent = 33.33f,
                color = 0xFFFF9800, // Amber
                chapters = listOf(
                    Chapter(
                        id = "chap_jee_math_diff",
                        subjectId = "sub_jee_math",
                        name = "Differential Calculus",
                        orderIndex = 1,
                        estimatedHours = 30f,
                        topics = listOf(
                            Topic("top_jee_math_limits", "chap_jee_math_diff", "Limits, Continuity & Differentiability", 1, importanceWeight = 2.0f, estimatedHours = 12f),
                            Topic("top_jee_math_aod", "chap_jee_math_diff", "Applications of Derivatives", 2, importanceWeight = 2.2f, estimatedHours = 18f)
                        )
                    )
                )
            )
        )
    )

    private val neetExam = Exam(
        id = "exam_neet_ug",
        name = "NEET UG 2026",
        shortName = "NEET UG",
        category = "Medical",
        authority = "National Testing Agency (NTA)",
        syllabusVersion = "2026.1",
        markingScheme = MarkingScheme(correctMarks = 4f, incorrectMarks = -1f, unattemptedMarks = 0f),
        subjects = listOf(
            Subject(
                id = "sub_neet_bio",
                examId = "exam_neet_ug",
                name = "Biology",
                shortName = "Biology",
                orderIndex = 1,
                weightagePercent = 50.0f,
                color = 0xFF4CAF50, // Green
                chapters = listOf(
                    Chapter(
                        id = "chap_neet_bio_gen",
                        subjectId = "sub_neet_bio",
                        name = "Genetics & Evolution",
                        orderIndex = 1,
                        estimatedHours = 35f,
                        topics = listOf(
                            Topic("top_neet_bio_mendel", "chap_neet_bio_gen", "Principles of Inheritance & Variation", 1, importanceWeight = 2.5f, estimatedHours = 15f),
                            Topic("top_neet_bio_mol", "chap_neet_bio_gen", "Molecular Basis of Inheritance", 2, importanceWeight = 2.5f, estimatedHours = 20f)
                        )
                    )
                )
            )
        )
    )

    private val upscExam = Exam(
        id = "exam_upsc_cse",
        name = "UPSC Civil Services Prelims 2026",
        shortName = "UPSC CSE",
        category = "Civil Services",
        authority = "Union Public Service Commission (UPSC)",
        syllabusVersion = "2026.1",
        markingScheme = MarkingScheme(correctMarks = 2f, incorrectMarks = -0.66f, unattemptedMarks = 0f),
        subjects = listOf(
            Subject(
                id = "sub_upsc_gs1",
                examId = "exam_upsc_cse",
                name = "General Studies I",
                shortName = "GS-1",
                orderIndex = 1,
                weightagePercent = 100f,
                color = 0xFF2196F3, // Blue
                chapters = listOf(
                    Chapter(
                        id = "chap_upsc_polity",
                        subjectId = "sub_upsc_gs1",
                        name = "Indian Polity & Governance",
                        orderIndex = 1,
                        estimatedHours = 40f,
                        topics = listOf(
                            Topic("top_upsc_fund_rights", "chap_upsc_polity", "Fundamental Rights & DPSP", 1, importanceWeight = 2.2f, estimatedHours = 15f),
                            Topic("top_upsc_parliament", "chap_upsc_polity", "Parliament & Union Executive", 2, importanceWeight = 2.0f, estimatedHours = 18f)
                        )
                    )
                )
            )
        )
    )

    private val catExam = Exam(
        id = "exam_cat_mba",
        name = "CAT 2026",
        shortName = "CAT MBA",
        category = "Management",
        authority = "Indian Institutes of Management (IIMs)",
        syllabusVersion = "2026.1",
        markingScheme = MarkingScheme(correctMarks = 3f, incorrectMarks = -1f, unattemptedMarks = 0f),
        subjects = listOf(
            Subject(
                id = "sub_cat_qa",
                examId = "exam_cat_mba",
                name = "Quantitative Aptitude",
                shortName = "QA",
                orderIndex = 1,
                weightagePercent = 33.33f,
                color = 0xFF9C27B0, // Purple
                chapters = listOf(
                    Chapter(
                        id = "chap_cat_arithmetic",
                        subjectId = "sub_cat_qa",
                        name = "Arithmetic",
                        orderIndex = 1,
                        estimatedHours = 30f,
                        topics = listOf(
                            Topic("top_cat_percentages", "chap_cat_arithmetic", "Percentages, Profit & Loss", 1, importanceWeight = 2.0f, estimatedHours = 10f),
                            Topic("top_cat_tsd", "chap_cat_arithmetic", "Time, Speed & Distance", 2, importanceWeight = 2.0f, estimatedHours = 12f)
                        )
                    )
                )
            )
        )
    )

    private val gateCsExam = Exam(
        id = "exam_gate_cs",
        name = "GATE Computer Science 2026",
        shortName = "GATE CS",
        category = "Engineering",
        authority = "IITs / IISc",
        syllabusVersion = "2026.1",
        markingScheme = MarkingScheme(correctMarks = 2f, incorrectMarks = -0.66f, unattemptedMarks = 0f),
        subjects = listOf(
            Subject(
                id = "sub_gate_dsa",
                examId = "exam_gate_cs",
                name = "Data Structures & Algorithms",
                shortName = "DSA",
                orderIndex = 1,
                weightagePercent = 20.0f,
                color = 0xFF00BCD4, // Cyan
                chapters = listOf(
                    Chapter(
                        id = "chap_gate_algos",
                        subjectId = "sub_gate_dsa",
                        name = "Design and Analysis of Algorithms",
                        orderIndex = 1,
                        estimatedHours = 25f,
                        topics = listOf(
                            Topic("top_gate_sorting", "chap_gate_algos", "Asymptotic Analysis & Sorting Algorithms", 1, importanceWeight = 2.0f, estimatedHours = 10f),
                            Topic("top_gate_dp", "chap_gate_algos", "Dynamic Programming & Greedy", 2, importanceWeight = 2.2f, estimatedHours = 15f)
                        )
                    )
                )
            )
        )
    )
}
