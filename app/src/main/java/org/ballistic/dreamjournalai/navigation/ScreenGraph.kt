package org.ballistic.dreamjournalai.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.AddEditDreamScreen
import org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.viewmodel.AddEditDreamViewModel
import org.ballistic.dreamjournalai.dream_favorites.DreamFavoriteScreenViewModel
import org.ballistic.dreamjournalai.dream_favorites.presentation.DreamFavoriteScreen
import org.ballistic.dreamjournalai.dream_journal_list.dream_list_screen.DreamJournalListScreen
import org.ballistic.dreamjournalai.dream_journal_list.dream_list_screen.viewmodel.DreamJournalListViewModel
import org.ballistic.dreamjournalai.dream_nightmares.presentation.DreamNightmareScreen
import org.ballistic.dreamjournalai.dream_nightmares.presentation.DreamNightmareScreenViewModel
import org.ballistic.dreamjournalai.dream_notifications.presentation.DreamNotificationSettingScreen
import org.ballistic.dreamjournalai.dream_notifications.presentation.NotificationScreenViewModel
import org.ballistic.dreamjournalai.dream_statistics.presentation.DreamStatisticScreen
import org.ballistic.dreamjournalai.dream_statistics.presentation.viewmodel.DreamStatisticScreenViewModel
import org.ballistic.dreamjournalai.dream_store.presentation.store_screen.StoreScreen
import org.ballistic.dreamjournalai.dream_store.presentation.store_screen.StoreScreenViewModel
import org.ballistic.dreamjournalai.dream_symbols.presentation.SymbolScreen
import org.ballistic.dreamjournalai.dream_symbols.presentation.viewmodel.DictionaryScreenViewModel
import org.ballistic.dreamjournalai.feature_dream.presentation.account_settings.AccountSettingsScreen
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.MainScreenEvent
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.viewmodel.MainScreenViewModelState
import org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.viewmodel.LoginViewModel
import org.ballistic.dreamjournalai.user_authentication.presentation.signup_screen.viewmodel.SignupViewModel

@Composable
fun ScreenGraph(
    navController: NavHostController,
    mainScreenViewModelState: MainScreenViewModelState,
    bottomPaddingValue: Dp,
    onMainEvent: (MainScreenEvent) -> Unit = {},
    onNavigateToOnboardingScreen: () -> Unit = {},
) {

    NavHost(
        navController = navController,
        startDestination = Screens.DreamJournalScreen.route,
    ) {

        composable(route = Screens.DreamJournalScreen.route) {
            val dreamJournalListViewModel: DreamJournalListViewModel = hiltViewModel()
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
                onNavigateToDream = {
                    navController.popBackStack()
                    navController.navigate(it)
                }
            )
        }

        //store
        composable(route = Screens.StoreScreen.route) {
            val storeScreenViewModel: StoreScreenViewModel = hiltViewModel()
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

        composable(
            route = Screens.AddEditDreamScreen.route +
                    "?dreamId={dreamId}&dreamImageBackground={dreamImageBackground}",
            arguments = listOf(
                navArgument(
                    name = "dreamId"
                ) {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument(
                    name = "dreamImageBackground"
                ) {
                    type = NavType.IntType
                    defaultValue = -1
                },
            )
        ) { value ->
            val image = value.arguments?.getInt("dreamImageBackground") ?: -1
            val addEditDreamViewModel: AddEditDreamViewModel = hiltViewModel()
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
                onNavigateToDreamJournalScreen = {
                    navController.popBackStack()
                    navController.navigate(Screens.DreamJournalScreen.route)
                },
            )
        }

        composable(route = Screens.Favorites.route) {
            val dreamFavoriteScreenViewModel: DreamFavoriteScreenViewModel = hiltViewModel()
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
            val loginViewModel: LoginViewModel = hiltViewModel()
            val signupViewModel: SignupViewModel = hiltViewModel()

            AccountSettingsScreen(
                loginViewModel.state.collectAsStateWithLifecycle().value,
                signupViewModel.state.collectAsStateWithLifecycle().value,
                mainScreenViewModelState = mainScreenViewModelState,
                navigateToOnboardingScreen = onNavigateToOnboardingScreen,
                onLoginEvent = { loginViewModel.onEvent(it) },
                onSignupEvent = { signupViewModel.onEvent(it) },
                navigateToDreamJournalScreen = {
                    navController.popBackStack()
                    navController.navigate(Screens.DreamJournalScreen.route)
                }
            )
        }

//        composable(route = Screens.AboutMe.route) {
//            AboutMeScreen(
//                mainScreenViewModelState = mainScreenViewModelState,
//            )
//        }

        composable(route = Screens.DreamToolGraphScreen.route) {
            DreamToolsGraph(
                mainScreenViewModelState = mainScreenViewModelState,
                bottomPaddingValue = bottomPaddingValue,
                onMainEvent = onMainEvent,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Screens.DreamJournalScreen.route) {
                            saveState = true
                            inclusive = true
                        }
                    }
                },
            )
        }

        composable(route = Screens.Statistics.route) {
            val dreamStatisticScreenViewModel: DreamStatisticScreenViewModel = hiltViewModel()
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
            val dreamNotificationScreenViewModel: NotificationScreenViewModel = hiltViewModel()
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
            val dreamNightmareScreenViewModel: DreamNightmareScreenViewModel = hiltViewModel()
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
            val dictionaryScreenViewModel: DictionaryScreenViewModel = hiltViewModel()
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
