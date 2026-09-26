package com.examprep.android.navigation

/**
 * Type-safe navigation route definitions for the ExamPrep OS.
 *
 * Navigation decisions:
 * - Sealed hierarchy for compile-time safety
 * - Route strings kept internal; external code uses Screen objects
 * - Argument encoding follows Navigation Compose conventions
 *
 * Navigation structure:
 * - Splash → Onboarding (first launch) OR Dashboard (returning user)
 * - Bottom Navigation: Home | Timeline | Practice | Progress | Settings
 * - Deep stacks within each tab are supported via nested graphs
 */
sealed class Screen(val route: String) {

    // ── Splash ──────────────────────────────────────────────────────────────
    data object Splash : Screen("splash")

    // ── Onboarding Flow ─────────────────────────────────────────────────────
    data object OnboardingGraph : Screen("onboarding_graph")
    data object Onboarding : Screen("onboarding")
    data object ExamSelection : Screen("exam_selection")
    data object ExamSetup : Screen("exam_setup/{examId}") {
        fun createRoute(examId: String) = "exam_setup/$examId"
    }
    data object DiagnosticAssessment : Screen("diagnostic_assessment")

    // ── Main App (Bottom Nav) ────────────────────────────────────────────────
    data object MainGraph : Screen("main_graph")

    // Home / Dashboard
    data object Dashboard : Screen("dashboard")

    // Timeline
    data object Timeline : Screen("timeline")

    // Practice (PYQ + Quiz)
    data object PracticeGraph : Screen("practice_graph")
    data object PracticeHome : Screen("practice_home")
    data object QuizSetup : Screen("quiz_setup")
    data object Quiz : Screen("quiz/{quizId}") {
        fun createRoute(quizId: String) = "quiz/$quizId"
    }
    data object QuizResult : Screen("quiz_result/{attemptId}") {
        fun createRoute(attemptId: String) = "quiz_result/$attemptId"
    }

    // Progress
    data object ProgressGraph : Screen("progress_graph")
    data object Performance : Screen("performance")
    data object Syllabus : Screen("syllabus")
    data object Revision : Screen("revision")

    // Settings
    data object Settings : Screen("settings")

    // ── Contextual Screens ───────────────────────────────────────────────────
    data object FocusSession : Screen("focus_session/{taskId}") {
        fun createRoute(taskId: String) = "focus_session/$taskId"
    }
    data object TopicDetail : Screen("topic_detail/{topicId}") {
        fun createRoute(topicId: String) = "topic_detail/$topicId"
    }
    data object MockTest : Screen("mock_test/{mockTestId}") {
        fun createRoute(mockTestId: String) = "mock_test/$mockTestId"
    }
    data object MockTestResult : Screen("mock_test_result/{mockTestAttemptId}") {
        fun createRoute(mockTestAttemptId: String) = "mock_test_result/$mockTestAttemptId"
    }
    data object MistakeVault : Screen("mistake_vault")
    data object Flashcards : Screen("flashcards")
    data object Roadmap : Screen("study_roadmap")
    data object Leaderboard : Screen("leaderboard")
    data object Auth : Screen("auth")
    data object Profile : Screen("profile")
    data object AIRankAccelerator : Screen("ai_rank_accelerator")
}

/**
 * Bottom navigation items — the primary app navigation destinations.
 *
 * Decision: 5 tabs chosen over 4 after analysis.
 * "Practice" is a first-class destination (not nested under Progress)
 * because it's a primary daily action for exam prep students.
 */
enum class TopLevelDestination(
    val screen: Screen,
    val label: String,
    val contentDescription: String
) {
    HOME(Screen.Dashboard, "Home", "Navigate to home dashboard"),
    TIMELINE(Screen.Timeline, "Timeline", "Navigate to study timeline"),
    PRACTICE(Screen.PracticeHome, "Practice", "Navigate to practice"),
    PROGRESS(Screen.Performance, "Progress", "Navigate to performance"),
    SETTINGS(Screen.Settings, "Settings", "Navigate to settings"),
}
