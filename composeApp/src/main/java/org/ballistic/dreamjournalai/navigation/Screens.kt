package org.ballistic.dreamjournalai.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable




sealed class Route{

    @Serializable
    data object DreamJournalScreen : Route()

    @Serializable
    data class AddEditDreamScreen(val dreamID: String?, val backgroundID: Int) : Route()

    @Serializable
    data object MainScreen : Route()

    @Serializable
    data object OnboardingScreen : Route()

    @Serializable
    data object StoreScreen : Route()
}

enum class DrawerNavigation(title: String?, icon: ImageVector, route: Route){
    DreamJournalScreen("My Dreams", Icons.Filled.Book, Route.DreamJournalScreen),
    AddEditDreamScreen("Add Dream", Icons.Default.Add, Route.AddEditDreamScreen("", -1)),
    MainScreen(null, Icons.Default.Home, Route.MainScreen),
    OnboardingScreen(null, Icons.Default.Info, Route.OnboardingScreen),
    StoreScreen("Store", Icons.Default.Shop, Route.StoreScreen)
}

enum class BottomNavigationRoutes(val title: String?, val icon: ImageVector, val route: Route){
    DreamJournalScreen("My Dreams", Icons.Filled.Book, Route.DreamJournalScreen),
    StoreScreen("Store", Icons.Default.Shop, Route.StoreScreen)
}


sealed class Screens(
    val route: String,
    val title: String? = null,
    val icon: ImageVector? = null
) {
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
        icon = Icons.Default.Star
    )

    data object AccountSettings : Screens(
        route = "account_settings",
        title = "Account Settings",
        icon = Icons.Default.Settings
    )

//    data object AboutMe : Screens(
//        route = "about_me",
//        title = "About Me",
//        icon = Icons.Default.Info
//    )

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

    data object PaintDreamWorldDetails : Screens(
        route = "paint_dream_world_details",
        title = "Paint Dream World Details",
        icon = Icons.Default.Brush
    )

    data object PaintDreamWorld : Screens(
        route = "paint_dream_world",
        title = "Paint Dream World",
        icon = Icons.Default.Brush
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

//    data object DreamSettings : Screens(
//        "dream_settings",
//        "Dream Settings",
//        Icons.Default.Bedtime
//    )

    data object DreamToolGraphScreen : Screens(
        "dream_tool_graph_screen",
        "Tools",
        Icons.Default.Build
    )

    data object RateMyApp : Screens(
        "rate_my_app",
        "Rate this App",
        Icons.Default.Favorite
    )

    data object OnboardingScreen : Screens(route = "welcome_screen", title = "Welcome", icon = null)
    data object MainScreen : Screens(route = "main_screen", title = "Main", icon = null)
    data object FullScreenImageScreen :
        Screens(route = "full_screen_image", title = "Full Screen Image", icon = null)
}