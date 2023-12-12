package org.ballistic.dreamjournalai.feature_dream.presentation.main_screen


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components.TransparentHintTextField
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.components.BottomNavigation
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.components.DrawerGroupHeading
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.viewmodel.MainScreenViewModelState
import org.ballistic.dreamjournalai.navigation.ScreenGraph
import org.ballistic.dreamjournalai.navigation.Screens
import org.ballistic.dreamjournalai.store_billing.presentation.store_screen.StoreEvent

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenView(
    mainScreenViewModelState: MainScreenViewModelState,
    onMainEvent : (MainScreenEvent) -> Unit = {},
    onStoreEvent: (StoreEvent) -> Unit = {},
    onNavigateToOnboardingScreen : () -> Unit = {},
    onDataLoaded: () -> Unit
) {

    LaunchedEffect(key1 = Unit) {
        delay(1500)
        onDataLoaded()
    }

    LaunchedEffect(Unit){
        onMainEvent(MainScreenEvent.GetAuthState)
        onMainEvent(MainScreenEvent.UserInteracted)
    }

    val navController = rememberNavController()
    val drawerGroups = listOf(
        DrawerGroup(
            title = "Pages",
            items = listOf(
                Screens.DreamJournalScreen,
                Screens.StoreScreen,
                Screens.Favorites,
                Screens.Nightmares,
                Screens.Tools,
                Screens.Statistics,
                Screens.Dictionary,
            )
        ),
        DrawerGroup(
            title = "Settings",
            items = listOf(
                Screens.AccountSettings,
                Screens.NotificationSettings,
                Screens.DreamSettings,
            )
        ),
        DrawerGroup(
            title = "Others",
            items = listOf(
                Screens.AboutMe,
            )
        )
    )
    val selectedItem = remember { mutableStateOf(drawerGroups.first().items.first()) }


    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            val route = destination.route ?: return@OnDestinationChangedListener
            val matchedScreen = drawerGroups.flatMap { it.items }.firstOrNull { it.route == route }
            if (matchedScreen != null) {
                selectedItem.value = matchedScreen
            }
        }

        navController.addOnDestinationChangedListener(listener)
        onDispose { navController.removeOnDestinationChangedListener(listener) }
    }

    val searchedText = mainScreenViewModelState.searchedText.collectAsStateWithLifecycle()

    Image(
        painter = rememberAsyncImagePainter(model = mainScreenViewModelState.backgroundResource),
        modifier = Modifier.fillMaxSize(),
        contentDescription = "Lighthouse",
        contentScale = ContentScale.Crop
    )

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
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
                                icon = { Icon(item.icon ?: Icons.AutoMirrored.Filled.Help, contentDescription = null) },
                                label = { Text(item.title ?: "") },
                                selected = item == selectedItem.value,
                                onClick = {
                                    scope.launch { drawerState.close() }
                                    selectedItem.value = item
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                        }
                    }
                    Text(
                        text = "Version: 1.1.1",
                        color = Color.White,
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
                modifier = if (!mainScreenViewModelState.scaffoldState.bottomBarState) {
                    Modifier
                        .padding(bottom = 0.dp)
                } else {
                    Modifier
                        .navigationBarsPadding()
                },
                snackbarHost = {
                    SnackbarHost(mainScreenViewModelState.scaffoldState.snackBarHostState.value)
                },
                topBar = {
                    AnimatedVisibility(
                        visible = mainScreenViewModelState.scaffoldState.topBarState,
                        enter = slideInVertically(initialOffsetY = { -it }),
                        exit = fadeOut()
                    ) {
                        CenterAlignedTopAppBar(
                            title = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                ) {
                                    if (!mainScreenViewModelState.scaffoldState.isUserSearching) {
                                        Text(
                                            text = "Dream Journal AI",
                                            color = Color.Black,
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                                .padding(start = 16.dp)
                                        )
                                    }
                                    AnimatedVisibility(
                                        visible = mainScreenViewModelState.scaffoldState.isUserSearching,
                                        //slide from left to right
                                        enter = slideInHorizontally(
                                            initialOffsetX = { it },
                                            animationSpec = tween(500)
                                        ),
                                        exit = slideOutHorizontally(
                                            targetOffsetX = { -it - 400 },
                                            animationSpec = tween(500)
                                        ),
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .padding(start = 16.dp, end = 16.dp)

                                    ) {
                                        TransparentHintTextField(
                                            text = searchedText.value,
                                            hint = "Search dream...",
                                            onValueChange = {
                                                onMainEvent(MainScreenEvent.SearchDreams(it))
                                            },
                                            onFocusChange = {
                                            },
                                            isHintVisible = searchedText.value.isBlank(),
                                            singleLine = true,
                                            textStyle = MaterialTheme.typography.headlineSmall,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(
                                                    color = colorResource(id = R.color.white).copy(
                                                        alpha = 0.2f
                                                    )
                                                )
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                                .fillMaxWidth()
                                                .padding(4.dp)
                                        )
                                    }
                                }
                            },
                            navigationIcon = {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.Black)
                                }
                            },
                            actions = {
                                if (!mainScreenViewModelState.scaffoldState.isUserSearching){
                                    IconButton(
                                        onClick = {
                                            onMainEvent(MainScreenEvent.SetSearchingState(!mainScreenViewModelState.scaffoldState.isUserSearching))
                                        },
                                    ) {
                                        Icon(
                                            Icons.Filled.Search,
                                            contentDescription = "Search",
                                            tint = Color.Black
                                        )
                                    }
                                } else {
                                    IconButton(
                                        onClick = {
                                            onMainEvent(MainScreenEvent.SetSearchingState(!mainScreenViewModelState.scaffoldState.isUserSearching))
                                            onMainEvent(MainScreenEvent.SearchDreams(""))
                                        },
                                    ) {
                                        Icon(
                                            Icons.Filled.Close,
                                            contentDescription = "Close",
                                            tint = Color.Black
                                        )
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                //animate content color
                                containerColor = colorResource(id = R.color.white).copy(alpha = 0.4f),
                                navigationIconContentColor = Color.Black,
                                titleContentColor = Color.Black,
                                actionIconContentColor = Color.Black
                            ),
                        )
                    }
                },

                bottomBar = {

                    AnimatedVisibility(
                        visible = mainScreenViewModelState.scaffoldState.bottomBarState,
                        enter = slideInVertically(initialOffsetY = { it }),
                        exit = slideOutVertically(targetOffsetY = { it })
                    )
                    {
                        BottomNavigation(navController = navController)
                        Box(
                            modifier = Modifier
                                .offset(y = 4.dp)
                                .fillMaxWidth()
                        ) {
                            FloatingActionButton(
                                onClick = {
                                    navController.navigate(Screens.AddEditDreamScreen.route)
                                },
                                containerColor = colorResource(id = R.color.Yellow),
                                elevation = FloatingActionButtonDefaults.elevation(3.dp, 4.dp),
                                shape = CircleShape,
                                modifier = Modifier
                                    .size(64.dp)
                                    .align(Alignment.Center)

                            ) {
                                Icon(Icons.Filled.Add, contentDescription = "Add dream")
                            }
                        }
                    }
                },
                containerColor = Color.Transparent,

                ) { innerPadding ->
                val layoutDirection = LocalLayoutDirection.current

                val newPadding = remember(innerPadding) {
                    PaddingValues(
                        top = innerPadding.calculateTopPadding(),
                        bottom = 56.dp, // Set the bottom padding to 96.dp
                        start = innerPadding.calculateStartPadding(layoutDirection),
                        end = innerPadding.calculateEndPadding(layoutDirection)
                    )
                }

                AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
                    ScreenGraph(
                        navController = navController,
                        mainScreenViewModelState = mainScreenViewModelState,
                        innerPadding = newPadding,
                        onMainEvent = { onMainEvent(it) },
                        onStoreEvent = { onStoreEvent(it) },
                    ) { onNavigateToOnboardingScreen() }
                }
            }
        }
    )
}

data class DrawerGroup(
    val title: String,
    val items: List<Screens>
)
