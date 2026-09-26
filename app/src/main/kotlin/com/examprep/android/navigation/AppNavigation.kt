package com.examprep.android.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.examprep.android.feature.auth.AuthScreen
import com.examprep.android.feature.auth.ProfileScreen
import com.examprep.android.feature.dashboard.DashboardScreen
import com.examprep.android.feature.flashcards.FlashcardsScreen
import com.examprep.android.feature.leaderboard.LeaderboardScreen
import com.examprep.android.feature.mistakes.MistakeVaultScreen
import com.examprep.android.feature.mocktest.MockTestResultScreen
import com.examprep.android.feature.mocktest.MockTestScreen
import com.examprep.android.feature.onboarding.DiagnosticScreen
import com.examprep.android.feature.onboarding.ExamSelectionScreen
import com.examprep.android.feature.onboarding.ExamSetupScreen
import com.examprep.android.feature.onboarding.OnboardingScreen
import com.examprep.android.feature.performance.PerformanceScreen
import com.examprep.android.feature.quiz.QuizResultScreen
import com.examprep.android.feature.quiz.QuizScreen
import com.examprep.android.feature.rankaccelerator.AIRankAcceleratorScreen
import com.examprep.android.feature.revision.RevisionScreen
import com.examprep.android.feature.roadmap.RoadmapScreen
import com.examprep.android.feature.settings.SettingsScreen
import com.examprep.android.feature.study.FocusSessionScreen
import com.examprep.android.feature.syllabus.SyllabusScreen
import com.examprep.android.feature.timeline.TimelineScreen

private const val NAV_ANIM_DURATION = 300

/**
 * Root navigation host for the ExamPrep OS application.
 *
 * Navigation decisions:
 * - Splash screen determines initial destination (onboarding vs main)
 * - Onboarding is a nested graph — once completed, popped off back-stack
 * - Main app uses bottom nav with 5 top-level destinations
 * - Feature screens are pushed on top of bottom nav tabs
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        enterTransition = {
            fadeIn(tween(NAV_ANIM_DURATION)) + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start, tween(NAV_ANIM_DURATION)
            )
        },
        exitTransition = {
            fadeOut(tween(NAV_ANIM_DURATION)) + slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Start, tween(NAV_ANIM_DURATION)
            )
        },
        popEnterTransition = {
            fadeIn(tween(NAV_ANIM_DURATION)) + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.End, tween(NAV_ANIM_DURATION)
            )
        },
        popExitTransition = {
            fadeOut(tween(NAV_ANIM_DURATION)) + slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End, tween(NAV_ANIM_DURATION)
            )
        }
    ) {
        // ── Splash ───────────────────────────────────────────────────────────
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.OnboardingGraph.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.MainGraph.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Onboarding Graph ─────────────────────────────────────────────────
        navigation(
            startDestination = Screen.Onboarding.route,
            route = Screen.OnboardingGraph.route
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onNavigateToExamSelection = {
                        navController.navigate(Screen.ExamSelection.route)
                    }
                )
            }
            composable(Screen.ExamSelection.route) {
                ExamSelectionScreen(
                    onExamSelected = { examId ->
                        navController.navigate(Screen.ExamSetup.createRoute(examId))
                    }
                )
            }
            composable(
                route = Screen.ExamSetup.route,
                arguments = listOf(navArgument("examId") { type = NavType.StringType })
            ) { backStackEntry ->
                ExamSetupScreen(
                    examId = backStackEntry.arguments?.getString("examId") ?: "",
                    onSetupComplete = {
                        navController.navigate(Screen.DiagnosticAssessment.route)
                    }
                )
            }
            composable(Screen.DiagnosticAssessment.route) {
                DiagnosticScreen(
                    onAssessmentComplete = {
                        navController.navigate(Screen.MainGraph.route) {
                            popUpTo(Screen.OnboardingGraph.route) { inclusive = true }
                        }
                    },
                    onSkip = {
                        navController.navigate(Screen.MainGraph.route) {
                            popUpTo(Screen.OnboardingGraph.route) { inclusive = true }
                        }
                    }
                )
            }
        }

        // ── Main App Graph (Bottom Nav) ──────────────────────────────────────
        composable(Screen.MainGraph.route) {
            MainAppScreen(rootNavController = navController)
        }

        // ── Contextual Screens (above bottom nav) ────────────────────────────
        composable(
            route = Screen.FocusSession.route,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            FocusSessionScreen(
                taskId = backStackEntry.arguments?.getString("taskId") ?: "",
                onSessionComplete = {
                    navController.navigate(Screen.MainGraph.route) {
                        popUpTo(Screen.MainGraph.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateBack = {
                    if (!navController.popBackStack()) {
                        navController.navigate(Screen.MainGraph.route) {
                            popUpTo(Screen.MainGraph.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
        composable(
            route = Screen.Quiz.route,
            arguments = listOf(navArgument("quizId") { type = NavType.StringType })
        ) { backStackEntry ->
            QuizScreen(
                quizId = backStackEntry.arguments?.getString("quizId") ?: "",
                onQuizComplete = { attemptId ->
                    navController.navigate(Screen.QuizResult.createRoute(attemptId)) {
                        popUpTo(Screen.Quiz.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    if (!navController.popBackStack()) {
                        navController.navigate(Screen.MainGraph.route) {
                            popUpTo(Screen.MainGraph.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
        composable(
            route = Screen.QuizResult.route,
            arguments = listOf(navArgument("attemptId") { type = NavType.StringType })
        ) { backStackEntry ->
            QuizResultScreen(
                attemptId = backStackEntry.arguments?.getString("attemptId") ?: "",
                onNavigateHome = {
                    navController.navigate(Screen.MainGraph.route) {
                        popUpTo(Screen.MainGraph.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateBack = {
                    navController.navigate(Screen.MainGraph.route) {
                        popUpTo(Screen.MainGraph.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(
            route = Screen.MockTest.route,
            arguments = listOf(navArgument("mockTestId") { type = NavType.StringType })
        ) { backStackEntry ->
            MockTestScreen(
                onTestSubmitted = { attemptId ->
                    navController.navigate(Screen.MockTestResult.createRoute(attemptId)) {
                        popUpTo(Screen.MockTest.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    if (!navController.popBackStack()) {
                        navController.navigate(Screen.MainGraph.route) {
                            popUpTo(Screen.MainGraph.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
        composable(
            route = Screen.MockTestResult.route,
            arguments = listOf(navArgument("mockTestAttemptId") { type = NavType.StringType })
        ) { backStackEntry ->
            MockTestResultScreen(
                attemptId = backStackEntry.arguments?.getString("mockTestAttemptId") ?: "",
                onNavigateHome = {
                    navController.navigate(Screen.MainGraph.route) {
                        popUpTo(Screen.MainGraph.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToNewTest = {
                    navController.navigate(Screen.MockTest.createRoute("mock_jee_all_india_1")) {
                        popUpTo(Screen.MockTestResult.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.MistakeVault.route) {
            MistakeVaultScreen(
                onNavigateBack = {
                    if (!navController.popBackStack()) {
                        navController.navigate(Screen.MainGraph.route) {
                            popUpTo(Screen.MainGraph.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
        composable(Screen.Flashcards.route) {
            FlashcardsScreen(
                onNavigateBack = {
                    if (!navController.popBackStack()) {
                        navController.navigate(Screen.MainGraph.route) {
                            popUpTo(Screen.MainGraph.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
        composable(Screen.Roadmap.route) {
            RoadmapScreen(
                onNavigateBack = {
                    if (!navController.popBackStack()) {
                        navController.navigate(Screen.MainGraph.route) {
                            popUpTo(Screen.MainGraph.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
                onNavigateToRoute = { route ->
                    when (route) {
                        "mistake_vault" -> navController.navigate(Screen.MistakeVault.route)
                        "flashcards" -> navController.navigate(Screen.Flashcards.route)
                        "practice_home" -> navController.navigate(Screen.MainGraph.route)
                        else -> {
                            if (route.startsWith("mock_test")) {
                                navController.navigate(Screen.MockTest.createRoute("mock_jee_all_india_1"))
                            } else {
                                navController.navigate(route)
                            }
                        }
                    }
                }
            )
        }
        composable(Screen.Leaderboard.route) {
            LeaderboardScreen(
                onNavigateBack = {
                    if (!navController.popBackStack()) {
                        navController.navigate(Screen.MainGraph.route) {
                            popUpTo(Screen.MainGraph.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
        composable(Screen.Auth.route) {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Screen.MainGraph.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                },
                onBack = {
                    if (!navController.popBackStack()) {
                        navController.navigate(Screen.MainGraph.route) {
                            popUpTo(Screen.MainGraph.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = {
                    if (!navController.popBackStack()) {
                        navController.navigate(Screen.MainGraph.route) {
                            popUpTo(Screen.MainGraph.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
                onNavigateToAuth = {
                    navController.navigate(Screen.Auth.route)
                },
                onNavigateToHeroFeature = {
                    navController.navigate(Screen.AIRankAccelerator.route)
                }
            )
        }
        composable(Screen.AIRankAccelerator.route) {
            AIRankAcceleratorScreen(
                onNavigateBack = {
                    if (!navController.popBackStack()) {
                        navController.navigate(Screen.MainGraph.route) {
                            popUpTo(Screen.MainGraph.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
                onStartSurgicalDrill = { drillId ->
                    navController.navigate(Screen.Quiz.createRoute("surg_drill_rot_1"))
                },
                onUpgradeToPro = {
                    // Refreshes in-place with Pro state unlocked
                }
            )
        }
    }
}

/**
 * Main app shell with bottom navigation.
 * Hosts the 5 primary destinations as nested navigation.
 */
@Composable
private fun MainAppScreen(rootNavController: NavHostController) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                TopLevelDestination.entries.forEach { destination ->
                    val selected = currentDestination?.hierarchy?.any {
                        it.route == destination.screen.route
                    } == true
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = when (destination) {
                                    TopLevelDestination.HOME -> Icons.Default.Home
                                    TopLevelDestination.TIMELINE -> Icons.Default.Timeline
                                    TopLevelDestination.PRACTICE -> Icons.Default.Quiz
                                    TopLevelDestination.PROGRESS -> Icons.Default.BarChart
                                    TopLevelDestination.SETTINGS -> Icons.Default.Settings
                                },
                                contentDescription = destination.contentDescription
                            )
                        },
                        label = { Text(destination.label) },
                        selected = selected,
                        onClick = {
                            bottomNavController.navigate(destination.screen.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onStartFocusSession = { taskId ->
                        rootNavController.navigate(Screen.FocusSession.createRoute(taskId))
                    },
                    onNavigateToTimeline = {
                        bottomNavController.navigate(Screen.Timeline.route)
                    },
                    onNavigateToPractice = {
                        rootNavController.navigate(Screen.Quiz.createRoute("quick_quiz"))
                    },
                    onNavigateToRevision = {
                        bottomNavController.navigate(Screen.Revision.route)
                    },
                    onNavigateToSyllabus = {
                        bottomNavController.navigate(Screen.PracticeHome.route)
                    },
                    onNavigateToPerformance = {
                        bottomNavController.navigate(Screen.Performance.route) {
                            popUpTo(bottomNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onStartMockTest = {
                        rootNavController.navigate(Screen.MockTest.createRoute("mock_jee_all_india_1"))
                    },
                    onNavigateToMistakeVault = {
                        rootNavController.navigate(Screen.MistakeVault.route)
                    },
                    onNavigateToFlashcards = {
                        rootNavController.navigate(Screen.Flashcards.route)
                    },
                    onNavigateToRoadmap = {
                        rootNavController.navigate(Screen.Roadmap.route)
                    },
                    onNavigateToLeaderboard = {
                        rootNavController.navigate(Screen.Leaderboard.route)
                    },
                    onNavigateToProfile = {
                        rootNavController.navigate(Screen.Profile.route)
                    },
                    onNavigateToHeroFeature = {
                        rootNavController.navigate(Screen.AIRankAccelerator.route)
                    }
                )
            }
            composable(Screen.Timeline.route) {
                TimelineScreen(
                    onStartTask = { taskId ->
                        rootNavController.navigate(Screen.FocusSession.createRoute(taskId))
                    }
                )
            }
            composable(Screen.PracticeHome.route) {
                SyllabusScreen(
                    onStartQuiz = { quizId ->
                        rootNavController.navigate(Screen.Quiz.createRoute(quizId))
                    }
                )
            }
            composable(Screen.Performance.route) {
                PerformanceScreen(
                    onNavigateBack = {
                        bottomNavController.navigate(Screen.Dashboard.route) {
                            popUpTo(bottomNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(Screen.Revision.route) {
                RevisionScreen(
                    onStartRevision = { taskId ->
                        rootNavController.navigate(Screen.FocusSession.createRoute(taskId))
                    }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}

/**
 * Splash screen — determines routing on launch.
 * Placeholder: will check onboarding completion from DataStore.
 */
@Composable
private fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToDashboard: () -> Unit
) {
    com.examprep.android.feature.onboarding.SplashScreen(
        onNavigateToOnboarding = onNavigateToOnboarding,
        onNavigateToDashboard = onNavigateToDashboard
    )
}
