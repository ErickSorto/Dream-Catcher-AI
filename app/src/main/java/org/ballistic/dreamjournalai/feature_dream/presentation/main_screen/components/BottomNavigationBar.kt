package org.ballistic.dreamjournalai.feature_dream.presentation.main_screen


import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color


import androidx.compose.ui.res.colorResource

import androidx.compose.ui.text.font.FontWeight.Companion.Black
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.feature_dream.navigation.Screens

@Composable
fun BottomNavigation(navController: NavController) {
    val items = listOf(
        Screens.DreamListScreen,
        Screens.StoreSignInScreen
    )
    androidx.compose.material.BottomNavigation(
        backgroundColor = colorResource(id = R.color.RedPink),
        contentColor = Color.Black
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon((item.icon!!), contentDescription = item.title) },
                label = {
                    item.title?.let {
                        Text(text = it,
                            fontSize = 9.sp)
                    }
                },
                selectedContentColor = Color.Black,
                unselectedContentColor = Color.Black.copy(0.4f),
                alwaysShowLabel = true,
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {

                        navController.graph.startDestinationRoute?.let { screen_route ->
                            popUpTo(screen_route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}