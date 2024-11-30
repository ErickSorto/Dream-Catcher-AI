package org.ballistic.dreamjournalai.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.ballistic.dreamjournalai.dream_authentication.presentation.signup_screen.viewmodel.LoginViewModel
import org.ballistic.dreamjournalai.dream_authentication.presentation.signup_screen.viewmodel.SignupViewModel
import org.ballistic.dreamjournalai.dream_main.presentation.MainScreenView
import org.ballistic.dreamjournalai.dream_main.presentation.viewmodel.MainScreenViewModel
import org.ballistic.dreamjournalai.dream_onboarding.presentation.OnboardingScreen
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainGraph(
    navController: NavHostController,
    onDataLoaded: () -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = Screens.OnboardingScreen.route,
    ) {
        composable(route = Screens.OnboardingScreen.route) {
            val loginViewModel = koinViewModel<LoginViewModel>()
            val signupViewModel = koinViewModel<SignupViewModel>()
            val loginViewModelState = loginViewModel.state.collectAsStateWithLifecycle().value
            val signupViewModelState = signupViewModel.state.collectAsStateWithLifecycle().value

            OnboardingScreen(
                navigateToDreamJournalScreen = {
                    navController.popBackStack()
                    navController.navigate(Screens.MainScreen.route)
                },
                onDataLoaded = {
                    onDataLoaded()
                },
                loginViewModelState = loginViewModelState,
                signupViewModelState = signupViewModelState,
                onLoginEvent = {
                    loginViewModel.onEvent(it)
                },
                onSignupEvent = {
                    signupViewModel.onEvent(it)
                },
            )
        }

        composable(route = Screens.MainScreen.route) {
            val mainScreenViewModel = koinViewModel<MainScreenViewModel>()
            val mainScreenViewModelState = mainScreenViewModel.mainScreenViewModelState.collectAsStateWithLifecycle().value

            MainScreenView(
                mainScreenViewModelState = mainScreenViewModelState,
                onDataLoaded = {
                    onDataLoaded()
                },
                onMainEvent = {
                    mainScreenViewModel.onEvent(it)
                },
                onNavigateToOnboardingScreen = {
                    navController.popBackStack()
                    navController.navigate(Screens.OnboardingScreen.route)
                },
            )
        }
    }
}
