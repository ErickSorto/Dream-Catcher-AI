package org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.components

import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.core.util.VibrationUtils.triggerVibration
import org.ballistic.dreamjournalai.navigation.Screens


@Composable
fun BottomNavigation(
    navController: NavController,
    isNavigationEnabled: Boolean,
    vibrator: Vibrator,
    modifier: Modifier
) {
    val items = listOf(
        Screens.DreamJournalScreen,
        Screens.StoreScreen
    )

    NavigationBar(
        containerColor = colorResource(id = R.color.sky_blue),
        contentColor = Color.Black,
        modifier = modifier
            .clip(
                RoundedCornerShape(
                    topStart = 8.dp,
                    topEnd = 8.dp
                )
            )
            .height(60.dp)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val infiniteTransition = rememberInfiniteTransition(label = "")
        items.forEach { item ->
            val isSelected = currentRoute == item.route

            // Setup for on-click and selection-based scaling
            val targetScale = if (isSelected) 1.25f else 1.1f
            val scale = remember { androidx.compose.animation.core.Animatable(targetScale) }

            // Continuous floating effect setup
            val floatingScale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.05f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ), label = ""
            )
            val rotation by infiniteTransition.animateFloat(
                initialValue = -2f,
                targetValue = 2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 3000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ), label = ""
            )

            val iconColor by animateColorAsState(
                targetValue = if (isSelected) colorResource(id = R.color.dark_purple) else Color.LightGray,
                animationSpec = tween(durationMillis = 500), label = ""
            )

            // Apply immediate scale change on selection
            LaunchedEffect(isSelected) {
                scale.animateTo(
                    targetValue = targetScale,
                    animationSpec = tween(durationMillis = 400)
                )
            }

            NavigationBarItem(
                icon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            item.icon!!,
                            contentDescription = item.title,
                            modifier = Modifier
                                .graphicsLayer {
                                    // Combine both scaling effects for a dynamic, floating appearance
                                    val finalScale =
                                        if (isSelected) floatingScale * scale.value else scale.value
                                    scaleX = finalScale
                                    scaleY = finalScale
                                    rotationZ = if (isSelected) rotation else 0f
                                }
                                .size(24.dp), // Example icon size, adjust as needed
                            tint = iconColor
                        )

                        AnimatedVisibility(
                            visible = isSelected,
                            modifier = Modifier.padding(start = 4.dp),
                        ) {
                            Text(
                                text = item.title!!,
                                color = colorResource(id = R.color.white),
                                style = typography.titleSmall,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                },
                selected = isSelected,
                onClick = {
                    triggerVibration(vibrator)
                    if (currentRoute != item.route) {
                        navController.popBackStack()
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    unselectedIconColor = Color.Black.copy(alpha = 0.5f),
                    selectedIconColor = Color.Black,
                    indicatorColor = colorResource(id = R.color.light_black).copy(alpha = 0.2f),
                ),
                alwaysShowLabel = true,
                enabled = isNavigationEnabled
            )
        }
    }
}
