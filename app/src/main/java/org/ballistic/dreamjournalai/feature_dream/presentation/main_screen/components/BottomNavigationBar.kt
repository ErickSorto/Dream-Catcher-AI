package org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.navigation.Screens

@Composable
fun BottomNavigation(
    navController: NavController,
    modifier: Modifier
    ) {
    val items = listOf(
        Screens.DreamJournalScreen,
        Screens.StoreScreen
    )
    Box(modifier = modifier.height(96.dp).fillMaxWidth()){ //This box fixes padding issue, adding extra space to the height to account for floating action button
        androidx.compose.material.BottomNavigation(
            backgroundColor = colorResource(id = R.color.sky_blue),
            contentColor = Color.Black,
            modifier = Modifier.align(alignment = Alignment.BottomCenter)
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
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    }
}