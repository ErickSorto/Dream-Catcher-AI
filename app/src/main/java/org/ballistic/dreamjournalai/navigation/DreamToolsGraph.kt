package org.ballistic.dreamjournalai.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.ballistic.dreamjournalai.dream_tools.presentation.DreamToolsScreen
import org.ballistic.dreamjournalai.dream_tools.presentation.interpret_dreams_screen.InterpretDreamsDetailScreen
import org.ballistic.dreamjournalai.dream_tools.presentation.interpret_dreams_screen.InterpretDreamsViewModel
import org.ballistic.dreamjournalai.dream_tools.presentation.interpret_dreams_screen.MassInterpretDreamToolScreen
import org.ballistic.dreamjournalai.dream_tools.presentation.random_dream_screen.RandomDreamToolScreen
import org.ballistic.dreamjournalai.dream_tools.presentation.random_dream_screen.RandomDreamToolScreenViewModel
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.MainScreenEvent
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.viewmodel.MainScreenViewModelState

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DreamToolsGraph(
    navController: NavHostController = rememberNavController(),
    mainScreenViewModelState: MainScreenViewModelState,
    bottomPaddingValue: Dp,
    onNavigate: (String) -> Unit,
    onMainEvent: (MainScreenEvent) -> Unit
) {
    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = Screens.Tools.route,
        ) {
            composable(route = Screens.Tools.route) {
                DreamToolsScreen(
                    mainScreenViewModelState = mainScreenViewModelState,
                    animatedVisibilityScope = this,
                    onNavigate = { image, route->
                        navController.navigate("$route/$image")
                    },
                )
            }
            composable(route = Screens.RandomDreamPicker.route + "/{image}",
                arguments = listOf(
                    navArgument("image") {
                        type = NavType.IntType
                    }
                )
            )
            {
                val randomDreamToolScreenViewModel: RandomDreamToolScreenViewModel = hiltViewModel()
                val randomDreamToolScreenState =
                    randomDreamToolScreenViewModel.randomDreamToolScreenState
                        .collectAsStateWithLifecycle()
                val imageID = it.arguments?.getInt("image") ?: -1
                RandomDreamToolScreen(
                    animatedVisibilityScope = this,
                    imageID = imageID,
                    bottomPadding = bottomPaddingValue,
                    randomDreamToolScreenState = randomDreamToolScreenState.value,
                    onEvent = {
                        randomDreamToolScreenViewModel.onEvent(it)
                    },
                    navigateTo = { route ->
                        navController.popBackStack()
                        onNavigate(route)
                    },
                    navigateUp = {
                        navController.navigate(Screens.Tools.route)
                    }
                )
            }

            composable(
                route = Screens.AnalyzeMultipleDreamsDetails.route + "/{image}",
                arguments = listOf(
                    navArgument("image") {
                        type = NavType.IntType
                    },
                )
            ) { it ->
                val imageID = it.arguments?.getInt("image") ?: -1
                InterpretDreamsDetailScreen(
                    imageID = imageID,
                    animatedVisibilityScope = this,
                    bottomPadding = bottomPaddingValue,
                    navigateTo = { route ->
                        navController.navigate(route)
                    },
                    navigateUp = {
                        navController.popBackStack()
                        navController.navigate(Screens.Tools.route)
                    }
                )
            }

            composable(
                route = Screens.AnalyzeMultipleDreams.route
            ) {
                val interpretDreamsViewModel: InterpretDreamsViewModel = hiltViewModel()
                val interpretDreamsScreenState = interpretDreamsViewModel.interpretDreamsScreenState
                    .collectAsStateWithLifecycle()
                MassInterpretDreamToolScreen(
                    interpretDreamsScreenState = interpretDreamsScreenState.value,
                    onEvent = { interpretDreamsViewModel.onEvent(it) },
                    bottomPaddingValue = bottomPaddingValue,
                    onMainScreenEvent = { onMainEvent(it) },
                    navigateUp = {
                        navController.popBackStack()
                        navController.navigate(Screens.Tools.route)
                    }
                )
            }
        }
    }
}