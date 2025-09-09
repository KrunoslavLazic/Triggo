package com.klazic.triggo.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.klazic.triggo.ui.screens.quiz.QuizRoute
import com.klazic.triggo.ui.screens.result.ResultRoute
import com.klazic.triggo.ui.screens.main.MainRoute
import com.klazic.triggo.ui.screens.settings.SettingsRoute
import com.klazic.triggo.ui.screens.welcome.WelcomeRoute

object Route {
    const val WELCOME = "welcome"
    const val MAIN = "main"
    const val SETTINGS = "settings"

    const val QUIZ = "quiz/{categoryId}/{difficulty}"
    fun quiz(categoryId: String, difficulty: String) = "quiz/$categoryId/$difficulty"

    const val RESULT = "result/{categoryId}/{difficulty}/{correct}/{total}"
    fun result(categoryId: String, difficulty: String, correct: Int, total: Int) =
        "result/$categoryId/$difficulty/$correct/$total"
}

@Composable
fun TrigoNavHost() {
    val nav = rememberNavController()

    val slideSpec =
        remember { tween<IntOffset>(durationMillis = 220, easing = FastOutSlowInEasing) }

    NavHost(
        navController = nav, startDestination = Route.WELCOME,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = slideSpec
            )
        }, exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = slideSpec
            )
        }, popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = slideSpec
            )
        }, popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = slideSpec
            )
        }

    ) {
        composable(
            Route.WELCOME
        ) {
            WelcomeRoute {
                nav.navigate(Route.MAIN) {
                    popUpTo(Route.WELCOME) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
        composable(Route.MAIN) {
            MainRoute(
                onSettingsClick = {
                    nav.navigate(Route.SETTINGS) { launchSingleTop = true }
                },
                onCategoryClick = { id, diff -> nav.navigate(Route.quiz(id, diff.name)) })

        }

        composable(Route.SETTINGS) { SettingsRoute() }

        composable(
            route = Route.QUIZ,
            arguments = listOf(
                navArgument("categoryId"){type = NavType.StringType},
                navArgument("difficulty"){type = NavType.StringType}
            )
        ) { backStack ->
            val cat = requireNotNull(backStack.arguments?.getString("categoryId"))
            val diff = requireNotNull(backStack.arguments?.getString("difficulty"))
            QuizRoute(
                categoryId = cat,
                difficultyStr = diff,
                onFinish = { correct, total ->
                    nav.navigate(Route.result(cat, diff, correct, total)){
                        popUpTo(Route.QUIZ){inclusive = true}
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = Route.RESULT,
            arguments = listOf(
                navArgument("categoryId"){type = NavType.StringType},
                navArgument("difficulty"){type = NavType.StringType},
                navArgument("correct"){type = NavType.IntType},
                navArgument("total"){type = NavType.IntType},
            )
        ) { backStack ->
            val catId   = requireNotNull(backStack.arguments?.getString("categoryId"))
            val diffStr = requireNotNull(backStack.arguments?.getString("difficulty"))
            val correct = backStack.arguments?.getInt("correct") ?: 0
            val total   = backStack.arguments?.getInt("total") ?: 0

            ResultRoute(
                categoryId    = catId,
                difficultyStr = diffStr,
                correct       = correct,
                total         = total,
                onBackHome    = {
                    if (!nav.popBackStack()){
                        nav.navigate(Route.MAIN){
                            launchSingleTop = true
                            popUpTo(nav.graph.startDestinationId) { inclusive = true}
                        }
                    }
                },
                onRetry       = { c, d -> nav.navigate(Route.quiz(c, d)) }
            )
        }


    }
}