package org.ballistic.dreamjournalai.dream_main.presentation


import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.core.util.VibrationUtil.triggerVibration
import org.ballistic.dreamjournalai.dream_main.domain.MainScreenEvent
import org.ballistic.dreamjournalai.dream_main.presentation.components.BottomNavigation
import org.ballistic.dreamjournalai.dream_main.presentation.components.DrawerGroupHeading
import org.ballistic.dreamjournalai.dream_main.presentation.viewmodel.MainScreenViewModelState
import org.ballistic.dreamjournalai.navigation.DrawerNavigation
import org.ballistic.dreamjournalai.navigation.Route
import org.ballistic.dreamjournalai.navigation.ScreenGraph


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreenView(
    mainScreenViewModelState: MainScreenViewModelState,
    onMainEvent: (MainScreenEvent) -> Unit = {},
    onNavigateToOnboardingScreen: () -> Unit = {},
    onDataLoaded: () -> Unit
) {

    val postNotificationPermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
        } else {
            rememberPermissionState(permission = Manifest.permission.ACCESS_NOTIFICATION_POLICY)
        }
    LaunchedEffect(Unit) {
        if (!postNotificationPermission.status.isGranted) {
            postNotificationPermission.launchPermissionRequest()
        }
    }

    LaunchedEffect(Unit) {
        onDataLoaded()
    }
    val context = LocalContext.current
    val vibrator = context.getSystemService(Vibrator::class.java)

    LaunchedEffect(Unit) {
        onMainEvent(MainScreenEvent.GetAuthState)
        onMainEvent(MainScreenEvent.UserInteracted)
    }

    val navController = rememberNavController()
    val drawerGroups = listOf(
        DrawerGroup(
            title = "Pages",
            items = listOf(
                DrawerNavigation.DreamJournalScreen,
                DrawerNavigation.StoreScreen,
                DrawerNavigation.Favorites,
                DrawerNavigation.Nightmares,
                DrawerNavigation.DreamToolGraphScreen,
                DrawerNavigation.Statistics,
                DrawerNavigation.Symbol,
            )
        ),
        DrawerGroup(
            title = "Settings",
            items = listOf(
                DrawerNavigation.AccountSettings,
                DrawerNavigation.NotificationSettings,
            //    Screens.DreamSettings,
            )
        ),
        DrawerGroup(
            title = "Others",
            items = listOf(
                DrawerNavigation.RateMyApp,
              //  Screens.AboutMe
            )
        )
    )
    val selectedItem = remember { mutableStateOf(drawerGroups.first().items.first()) }

    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            val route = destination.route ?: return@OnDestinationChangedListener
            val matchedScreen = drawerGroups.flatMap { it.items }.firstOrNull { it ->
                it.route::class.qualifiedName == route
            }
            if (matchedScreen != null) {
                selectedItem.value = matchedScreen
            }
        }

        navController.addOnDestinationChangedListener(listener)
        onDispose { navController.removeOnDestinationChangedListener(listener) }
    }

    CoilImage(
        imageModel = {mainScreenViewModelState.backgroundResource},
        modifier = Modifier
            .fillMaxSize()
            .blur(15.dp),
        imageOptions = ImageOptions(
            contentScale = ContentScale.Crop
        )
    )

    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = mainScreenViewModelState.drawerMain,
        gesturesEnabled = mainScreenViewModelState.isDrawerEnabled,
        drawerContent = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ModalDrawerSheet {
                    Spacer(Modifier.height(12.dp))

                    drawerGroups.forEach { group ->
                        DrawerGroupHeading(title = group.title)

                        group.items.forEach { item ->
                            NavigationDrawerItem(
                                icon = {
                                    if (item == DrawerNavigation.RateMyApp) {
                                        AnimatedHeartIcon()
                                    } else {
                                        Icon(
                                            item.icon,
                                            contentDescription = null
                                        )
                                    }
                                },
                                label = { Text(item.title ?: "") },
                                selected = item == selectedItem.value,
                                onClick = {
                                    triggerVibration(vibrator)
                                    scope.launch {
                                        mainScreenViewModelState.drawerMain.close()
                                    }
                                    selectedItem.value = item

                                    if (item == DrawerNavigation.RateMyApp) {
                                        // Launch Play Store intent
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            data =
                                                Uri.parse("https://play.google.com/store/apps/details?id=org.ballistic.dreamjournalai")
                                            setPackage("com.android.vending")
                                        }
                                        context.startActivity(intent)
                                    } else {
                                        navController.navigate(item.route) {
                                            popUpTo(DrawerNavigation.DreamJournalScreen.route) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .padding(NavigationDrawerItemDefaults.ItemPadding)
                                    .fillMaxWidth()
                            )
                        }
                    }
                    Text(
                        text = "Version: 1.2.8",
                        color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                        modifier = Modifier
                            .padding(bottom = 16.dp, top = 8.dp)
                            .align(Alignment.CenterHorizontally),
                        fontSize = 12.sp
                    )
                }
            }
        },
        content = {
            Scaffold(
                snackbarHost = {
                    SnackbarHost(mainScreenViewModelState.scaffoldState.snackBarHostState.value)
                },
                bottomBar = {
                    AnimatedVisibility(
                        visible = mainScreenViewModelState.scaffoldState.bottomBarState,
                        enter = slideInVertically(initialOffsetY = { it + 100 }),
                        exit = slideOutVertically(targetOffsetY = { it + 100 })
                    ) {
                        BottomNavigation(
                            navController = navController,
                            modifier = Modifier.navigationBarsPadding(),
                            vibrator = vibrator,
                            isNavigationEnabled = mainScreenViewModelState.isBottomBarEnabledState
                        )
                        Box(
                            modifier = Modifier
                                .navigationBarsPadding()
                                .offset(y = (-24).dp)
                                .fillMaxWidth()
                        ) {
                            FloatingActionButton(
                                onClick = {
                                    if (mainScreenViewModelState.isBottomBarEnabledState) {
                                        triggerVibration(vibrator)
                                        navController.navigate(Route.AddEditDreamScreen(
                                            dreamID = "",
                                            backgroundID = -1
                                        )) {
                                            popUpTo(Route.DreamJournalScreen) {
                                                saveState = false
                                                inclusive = true
                                            }
                                        }
                                    }
                                },
                                containerColor = colorResource(id = R.color.Yellow),
                                elevation = FloatingActionButtonDefaults.elevation(3.dp, 4.dp),
                                shape = CircleShape,
                                modifier = Modifier
                                    .size(60.dp)
                                    .align(Alignment.Center)

                            ) {
                                Icon(
                                    Icons.Filled.Add,
                                    tint = Color.White,
                                    contentDescription = "Add dream",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                },
                containerColor = Color.Transparent,
            ) { innerPadding ->
                onMainEvent(MainScreenEvent.UpdatePaddingValues(innerPadding))

                AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
                    ScreenGraph(
                        navController = navController,
                        mainScreenViewModelState = mainScreenViewModelState,
                        bottomPaddingValue = mainScreenViewModelState.paddingValues.calculateBottomPadding(),
                        onMainEvent = { onMainEvent(it) },
                        onNavigateToOnboardingScreen = { onNavigateToOnboardingScreen() }
                    )
                }
            }
        }
    )
}

data class DrawerGroup(
    val title: String,
    val items: List<DrawerNavigation>
)

@Composable
fun AnimatedHeartIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val size by infiniteTransition.animateFloat(
        initialValue = 24f,
        targetValue = 28f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800),  // Slower animation
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Icon(
        imageVector = Icons.Default.Favorite,
        contentDescription = "Animated Heart",
        tint = Color.Red,
        modifier = Modifier.size(size.dp)
    )
}