package org.ballistic.dreamjournalai.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import org.ballistic.dreamjournalai.dream_account.AccountSettingsScreen
import org.ballistic.dreamjournalai.dream_add_edit.presentation.AddEditDreamScreen
import org.ballistic.dreamjournalai.dream_add_edit.presentation.viewmodel.AddEditDreamViewModel
import org.ballistic.dreamjournalai.dream_authentication.presentation.signup_screen.viewmodel.LoginViewModel
import org.ballistic.dreamjournalai.dream_authentication.presentation.signup_screen.viewmodel.SignupViewModel
import org.ballistic.dreamjournalai.dream_favorites.presentation.DreamFavoriteScreen
import org.ballistic.dreamjournalai.dream_favorites.presentation.viewmodel.DreamFavoriteScreenViewModel
import org.ballistic.dreamjournalai.dream_fullscreen.FullScreenImageScreen
import org.ballistic.dreamjournalai.dream_fullscreen.FullScreenViewModel
import org.ballistic.dreamjournalai.dream_journal_list.presentation.DreamJournalListScreen
import org.ballistic.dreamjournalai.dream_journal_list.presentation.viewmodel.DreamJournalListViewModel
import org.ballistic.dreamjournalai.dream_main.domain.MainScreenEvent
import org.ballistic.dreamjournalai.dream_main.presentation.viewmodel.MainScreenViewModelState
import org.ballistic.dreamjournalai.dream_nightmares.presentation.DreamNightmareScreen
import org.ballistic.dreamjournalai.dream_nightmares.presentation.viewmodel.DreamNightmareScreenViewModel
import org.ballistic.dreamjournalai.dream_notifications.presentation.DreamNotificationSettingScreen
import org.ballistic.dreamjournalai.dream_notifications.presentation.viewmodel.NotificationScreenViewModel
import org.ballistic.dreamjournalai.dream_statistics.presentation.DreamStatisticScreen
import org.ballistic.dreamjournalai.dream_statistics.presentation.viewmodel.DreamStatisticScreenViewModel
import org.ballistic.dreamjournalai.dream_store.presentation.store_screen.StoreScreen
import org.ballistic.dreamjournalai.dream_store.presentation.store_screen.viewmodel.StoreScreenViewModel
import org.ballistic.dreamjournalai.dream_symbols.presentation.SymbolScreen
import org.ballistic.dreamjournalai.dream_symbols.presentation.viewmodel.DictionaryScreenViewModel
import org.koin.androidx.compose.koinViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ScreenGraph(
    navController: NavHostController,
    mainScreenViewModelState: MainScreenViewModelState,
    bottomPaddingValue: Dp,
    onMainEvent: (MainScreenEvent) -> Unit = {},
    onNavigateToOnboardingScreen: () -> Unit = {},
) {

    SharedTransitionLayout{
        NavHost(
            navController = navController,
            startDestination = Route.DreamJournalScreen,
            modifier = Modifier.fillMaxSize()
        ) {
            composable<Route.DreamJournalScreen> {
                val dreamJournalListViewModel = koinViewModel<DreamJournalListViewModel>()
                val searchTextFieldState =
                    dreamJournalListViewModel.searchTextFieldState.collectAsStateWithLifecycle().value
                val dreamJournalListState =
                    dreamJournalListViewModel.dreamJournalListState.collectAsStateWithLifecycle().value
                DreamJournalListScreen(
                    mainScreenViewModelState = mainScreenViewModelState,
                    searchTextFieldState = searchTextFieldState,
                    dreamJournalListState = dreamJournalListState,
                    bottomPaddingValue = bottomPaddingValue,
                    onMainEvent = { onMainEvent(it) },
                    onDreamListEvent = { dreamJournalListViewModel.onEvent(it) },
                    onNavigateToDream = { dreamID, backgroundID ->
                        navController.popBackStack()
                        navController.navigate(
                            Route.AddEditDreamScreen(
                                dreamID = dreamID,
                                backgroundID = backgroundID
                            )
                        )
                    }
                )
            }

            //store
            composable<Route.StoreScreen> {
                val storeScreenViewModel = koinViewModel<StoreScreenViewModel>()
                val storeScreenViewModelState = storeScreenViewModel.storeScreenViewModelState
                    .collectAsStateWithLifecycle().value
                StoreScreen(
                    storeScreenViewModelState = storeScreenViewModelState,
                    bottomPaddingValue = bottomPaddingValue,
                    onMainEvent = { onMainEvent(it) },
                    onStoreEvent = { storeScreenViewModel.onEvent(it) },
                    navigateToAccountScreen = {
                        navController.popBackStack()
                        navController.navigate(Screens.AccountSettings.route)
                    }
                )
            }

            composable<Route.AddEditDreamScreen> {
                val args = it.toRoute<Route.AddEditDreamScreen>()
                val image = args.backgroundID

                val addEditDreamViewModel = koinViewModel<AddEditDreamViewModel>()
                val addEditDreamState =
                    addEditDreamViewModel.addEditDreamState.collectAsStateWithLifecycle().value
                val dreamTitle =
                    addEditDreamViewModel.titleTextFieldState.collectAsStateWithLifecycle().value
                val dreamContent =
                    addEditDreamViewModel.contentTextFieldState.collectAsStateWithLifecycle().value

                AddEditDreamScreen(
                    dreamImage = image,
                    dreamTitleState = dreamTitle,
                    dreamContentState = dreamContent,
                    addEditDreamState = addEditDreamState,
                    onMainEvent = { onMainEvent(it) },
                    onAddEditDreamEvent = { addEditDreamViewModel.onEvent(it) },
                    animateVisibilityScope = this,
                    onNavigateToDreamJournalScreen = {
                        navController.popBackStack()
                        navController.navigate(Route.DreamJournalScreen)
                    },
                    onImageClick = { imageID ->
                        val encodedURL = URLEncoder.encode(imageID, StandardCharsets.UTF_8.toString())
                        navController.navigate(
                            Screens.FullScreenImageScreen.route + "/$encodedURL"
                        )
                    }
                )
            }

            composable(
                route = Screens.FullScreenImageScreen.route + "/{imageID}",
                arguments = listOf(
                    navArgument("imageID") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val fullScreenViewModel = koinViewModel<FullScreenViewModel>()
                val encodedImageID = backStackEntry.arguments?.getString("imageID") ?: ""
                val encodedURL = URLEncoder.encode(encodedImageID, StandardCharsets.UTF_8.toString())
                FullScreenImageScreen(
                    imageID = encodedURL,
                    animatedVisibilityScope = this,
                    onBackPress = {
                        navController.navigateUp()
                    },
                    onFullScreenEvent = {
                        fullScreenViewModel.onEvent(it)
                    },
                    onMainEvent = { onMainEvent(it) }
                )
            }

            composable(route = Screens.Favorites.route) {
                val dreamFavoriteScreenViewModel = koinViewModel<DreamFavoriteScreenViewModel>()
                val dreamFavoriteScreenState = dreamFavoriteScreenViewModel.dreamFavoriteScreenState
                    .collectAsStateWithLifecycle()
                DreamFavoriteScreen(
                    dreamFavoriteScreenState = dreamFavoriteScreenState.value,
                    mainScreenViewModelState = mainScreenViewModelState,
                    bottomPaddingValue = bottomPaddingValue,
                    navController = navController,
                    onEvent = { dreamFavoriteScreenViewModel.onEvent(it) },
                )
            }

            composable(route = Screens.AccountSettings.route) {
                val loginViewModel = koinViewModel<LoginViewModel>()
                val signupViewModel = koinViewModel<SignupViewModel>()

                val loginViewModelState = loginViewModel.state.collectAsStateWithLifecycle().value
                val signupViewModelState = signupViewModel.state.collectAsStateWithLifecycle().value

                AccountSettingsScreen(
                    loginViewModelState = loginViewModelState,
                    signupViewModelState = signupViewModelState,
                    mainScreenViewModelState = mainScreenViewModelState,
                    navigateToOnboardingScreen = onNavigateToOnboardingScreen,
                    onLoginEvent = { loginViewModel.onEvent(it) },
                    onSignupEvent = { signupViewModel.onEvent(it) },
                    navigateToDreamJournalScreen = {
                        navController.navigate(Route.DreamJournalScreen) {
                            // Pop up to the root of the navigation graph, so the back stack is cleared
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                            // Avoid multiple copies of the same destination when reselecting the same item
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(route = Screens.DreamToolGraphScreen.route) {
                DreamToolsGraph(
                    mainScreenViewModelState = mainScreenViewModelState,
                    bottomPaddingValue = bottomPaddingValue,
                    onMainEvent = onMainEvent,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Route.DreamJournalScreen) {
                                saveState = true
                                inclusive = true
                            }
                        }
                    },
                )
            }

            composable(route = Screens.Statistics.route) {
                val dreamStatisticScreenViewModel = koinViewModel<DreamStatisticScreenViewModel>()
                val dreamStatisticScreenState = dreamStatisticScreenViewModel.dreamStatisticScreen
                    .collectAsStateWithLifecycle()

                DreamStatisticScreen(
                    dreamStatisticScreenState = dreamStatisticScreenState.value,
                    mainScreenViewModelState = mainScreenViewModelState,
                    bottomPaddingValue = bottomPaddingValue,
                    onEvent = {
                        dreamStatisticScreenViewModel.onEvent(it)
                    }
                )
            }

            composable(route = Screens.NotificationSettings.route) {
                val dreamNotificationScreenViewModel = koinViewModel<NotificationScreenViewModel>()
                val dreamNotificationScreenState =
                    dreamNotificationScreenViewModel.notificationScreenState
                        .collectAsStateWithLifecycle()

                DreamNotificationSettingScreen(
                    mainScreenViewModelState = mainScreenViewModelState,
                    notificationScreenState = dreamNotificationScreenState.value,
                    bottomPaddingValue = bottomPaddingValue,
                ) {
                    dreamNotificationScreenViewModel.onEvent(it)
                }
            }

            composable(route = Screens.Nightmares.route) {
                val dreamNightmareScreenViewModel = koinViewModel<DreamNightmareScreenViewModel>()
                val dreamNightmareScreenState =
                    dreamNightmareScreenViewModel.dreamNightmareScreenState
                        .collectAsStateWithLifecycle()
                DreamNightmareScreen(
                    dreamNightmareScreenState = dreamNightmareScreenState.value,
                    mainScreenViewModelState = mainScreenViewModelState,
                    bottomPaddingValue = bottomPaddingValue,
                    navController = navController,
                    onEvent = { dreamNightmareScreenViewModel.onEvent(it) },
                )
            }

            composable(route = Screens.Symbol.route) {
                val dictionaryScreenViewModel = koinViewModel<DictionaryScreenViewModel>()
                val dictionaryScreenState = dictionaryScreenViewModel.symbolScreenState
                    .collectAsStateWithLifecycle()
                val searchTextFieldState = dictionaryScreenViewModel.searchTextFieldState
                    .collectAsStateWithLifecycle()
                SymbolScreen(
                    symbolScreenState = dictionaryScreenState.value,
                    mainScreenViewModelState = mainScreenViewModelState,
                    searchTextFieldState = searchTextFieldState.value,
                    bottomPaddingValue = bottomPaddingValue,
                    onMainEvent = { onMainEvent(it) },
                    onEvent = { dictionaryScreenViewModel.onEvent(it) },
                )
            }

//        composable(route = Screens.DreamSettings.route) {
//            FeatureComingSoonScreen(
//                onNavigateToAboutMeScreen = {
//                    navController.popBackStack()
//                    navController.navigate(Screens.AboutMe.route)
//                }
//            )
//        }
        }
    }

}
