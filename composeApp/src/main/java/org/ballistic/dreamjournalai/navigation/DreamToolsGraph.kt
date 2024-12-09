package org.ballistic.dreamjournalai.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import org.ballistic.dreamjournalai.dream_main.domain.MainScreenEvent
import org.ballistic.dreamjournalai.dream_main.presentation.viewmodel.MainScreenViewModelState
import org.ballistic.dreamjournalai.dream_tools.presentation.dream_tools_screen.DreamToolsScreen
import org.ballistic.dreamjournalai.dream_tools.presentation.interpret_dreams_screen.InterpretDreamsDetailScreen
import org.ballistic.dreamjournalai.dream_tools.presentation.interpret_dreams_screen.MassInterpretDreamToolScreen
import org.ballistic.dreamjournalai.dream_tools.presentation.interpret_dreams_screen.viewmodel.InterpretDreamsViewModel
import org.ballistic.dreamjournalai.dream_tools.presentation.paint_dreams_screen.PaintDreamWorldDetailScreen
import org.ballistic.dreamjournalai.dream_tools.presentation.random_dream_screen.RandomDreamToolScreen
import org.ballistic.dreamjournalai.dream_tools.presentation.random_dream_screen.RandomDreamToolScreenViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DreamToolsGraph(
    navController: NavHostController = rememberNavController(),
    mainScreenViewModelState: MainScreenViewModelState,
    bottomPaddingValue: Dp,
    onNavigate: (dreamID: String?, backgroundID: Int) -> Unit,
    onMainEvent: (MainScreenEvent) -> Unit
) {
    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = Screens.Tools.route,
        ) {
            composable<Route.Tools> {
                DreamToolsScreen(
                    mainScreenViewModelState = mainScreenViewModelState,
                    animatedVisibilityScope = this,
                    onNavigate = { route ->
                        navController.navigate(route)
                    },
                )
            }
            composable<ToolRoute.RandomDreamPicker> {
                val args = it.toRoute<ToolRoute.RandomDreamPicker>()
                val randomDreamToolScreenViewModel = koinViewModel<RandomDreamToolScreenViewModel>()
                val randomDreamToolScreenState =
                    randomDreamToolScreenViewModel.randomDreamToolScreenState
                        .collectAsStateWithLifecycle()

                RandomDreamToolScreen(
                    animatedVisibilityScope = this,
                    imageID = args.imageID,
                    bottomPadding = bottomPaddingValue,
                    randomDreamToolScreenState = randomDreamToolScreenState.value,
                    onEvent = {
                        randomDreamToolScreenViewModel.onEvent(it)
                    },
                    onNavigateToDream = { dreamID, backgroundID ->
                        onNavigate(dreamID, backgroundID)
                    },
                    navigateUp = {
                        navController.navigateUp()
                    }
                )
            }

            composable<ToolRoute.AnalyzeMultipleDreamsDetails> {
                val args = it.toRoute<ToolRoute.AnalyzeMultipleDreamsDetails>()
                InterpretDreamsDetailScreen(
                    imageID = args.imageID,
                    animatedVisibilityScope = this,
                    bottomPadding = bottomPaddingValue,
                    navigateTo = { route ->
                        navController.navigate(route)
                    },
                    navigateUp = {
                        navController.navigateUp()
                    }
                )
            }

            composable(
                route = Screens.AnalyzeMultipleDreams.route
            ) {
                val interpretDreamsViewModel = koinViewModel<InterpretDreamsViewModel>()
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

            composable(
                route = Screens.PaintDreamWorldDetails.route + "/{image}",
                arguments = listOf(
                    navArgument("image") {
                        type = NavType.IntType
                    }
                )
            ) { it ->
                val imageID = it.arguments?.getInt("image") ?: -1
                PaintDreamWorldDetailScreen(
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
                route = Screens.PaintDreamWorld.route
            ) {
                PaintDreamWorldDetailScreen(
                    imageID = 0,
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
        }
    }
}