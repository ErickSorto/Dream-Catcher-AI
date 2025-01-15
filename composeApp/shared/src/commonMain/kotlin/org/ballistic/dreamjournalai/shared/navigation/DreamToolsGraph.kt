package org.ballistic.dreamjournalai.shared.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.ballistic.dreamjournalai.shared.dream_main.domain.MainScreenEvent
import org.ballistic.dreamjournalai.shared.dream_main.presentation.viewmodel.MainScreenViewModelState
import org.ballistic.dreamjournalai.shared.dream_tools.presentation.dream_tools_screen.DreamToolsScreen
import org.ballistic.dreamjournalai.shared.dream_tools.presentation.interpret_dreams_screen.InterpretDreamsDetailScreen
import org.ballistic.dreamjournalai.shared.dream_tools.presentation.interpret_dreams_screen.MassInterpretDreamToolScreen
import org.ballistic.dreamjournalai.shared.dream_tools.presentation.interpret_dreams_screen.viewmodel.InterpretDreamsViewModel
import org.ballistic.dreamjournalai.shared.dream_tools.presentation.paint_dreams_screen.PaintDreamWorldDetailScreen
import org.ballistic.dreamjournalai.shared.dream_tools.presentation.random_dream_screen.RandomDreamToolScreen
import org.ballistic.dreamjournalai.shared.dream_tools.presentation.random_dream_screen.RandomDreamToolScreenViewModel
import org.koin.compose.viewmodel.koinViewModel

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
            startDestination = Route.Tools,
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
                    onNavigate = {
                        navController.navigate(Route.AnalyzeMultipleDreams)
                    },
                    navigateUp = {
                        navController.navigateUp()
                    }
                )
            }

            composable<Route.AnalyzeMultipleDreams> {
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
                        navController.navigate(Route.Tools)
                    }
                )
            }

//            composable<ToolRoute.PaintDreamWorld> {
//                val imageID = it.toRoute<ToolRoute.PaintDreamWorld>().imageID
//                PaintDreamWorldDetailScreen(
//                    imageID = imageID,
//                    animatedVisibilityScope = this,
//                    bottomPadding = bottomPaddingValue,
//                    onNavigate = {
//                        navController.navigate(Route.PaintDreamWorld)
//                    },
//                    navigateUp = {
//                        navController.popBackStack()
//                        navController.navigate(Route.Tools)
//                    }
//                )
//            }

            composable<Route.PaintDreamWorld> {
                PaintDreamWorldDetailScreen(
                    imageID = 0,
                    animatedVisibilityScope = this,
                    bottomPadding = bottomPaddingValue,
                    onNavigate = {
                        navController.navigate(Route.PaintDreamWorld)
                    },
                    navigateUp = {
                        navController.popBackStack()
                        navController.navigate(Route.Tools)
                    }
                )
            }
        }
    }
}