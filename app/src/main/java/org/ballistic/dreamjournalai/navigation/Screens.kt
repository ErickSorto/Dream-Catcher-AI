package org.ballistic.dreamjournalai.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screens(
    val route: String,
    val title: String? = null,
    val icon: ImageVector? = null
) {
    data object DreamJournalScreen : Screens(
        route = "dreams_screen",
        title = "My Dreams",
        icon = Icons.Filled.Book,
    )

    data object AddEditDreamScreen : Screens(
        route = "add_edit_dream_screen",
        title = "AddEdit",
        icon = Icons.Default.Add
    )

    data object StoreScreen : Screens(
        route = "dream_store",
        title = "Store",
        icon = Icons.Filled.Shop // Assuming filled version exists
        // Assuming outlined version exists
    )

    data object Favorites : Screens(
        route = "favorites",
        title = "Favorites",
        icon = Icons.Default.Favorite
    )

    data object AccountSettings : Screens(
        route = "account_settings",
        title = "Account Settings",
        icon = Icons.Default.Settings
    )

    data object AboutMe : Screens(
        route = "about_me",
        title = "About Me",
        icon = Icons.Default.Info
    )

    data object Tools : Screens(
        route = "tools",
        title = "Tools",
        icon = Icons.Default.Build
    )

    data object RandomDreamPicker : Screens(
        route = "random_dream_picker",
        title = "Random Dream Picker",
        icon = Icons.Default.Casino
    )

    data object AnalyzeMultipleDreamsDetails : Screens(
        route = "analyze_multiple_dream_details",
        title = "Analyze Multiple Dreams Details",
        icon = Icons.Default.Analytics
    )

    data object AnalyzeMultipleDreams : Screens(
        route = "analyze_multiple_dream",
        title = "Analyze Multiple Dreams",
        icon = Icons.Default.Analytics
    )

    data object Statistics : Screens(
        route = "statistics",
        title = "Statistics",
        icon = Icons.Default.BarChart
    )

    data object NotificationSettings : Screens(
        route = "notification_settings",
        title = "Notification Settings",
        icon = Icons.Default.Notifications
    )

    data object Nightmares : Screens(
        route = "nightmares",
        title = "Nightmares",
        icon = Icons.Default.ErrorOutline
    )

    data object Symbol : Screens(
        route = "symbol",
        title = "Symbols",
        icon = Icons.AutoMirrored.Filled.List
    )

    data object DreamSettings : Screens(
        "dream_settings",
        "Dream Settings",
        Icons.Default.Bedtime
    )

    data object DreamToolGraphScreen : Screens(
        "dream_tool_graph_screen",
        "Tools",
        Icons.Default.Build
    )

    data object OnboardingScreen : Screens(route = "welcome_screen", title = "Welcome", icon = null)
    data object MainScreen : Screens(route = "main_screen", title = "Main", icon = null)
}