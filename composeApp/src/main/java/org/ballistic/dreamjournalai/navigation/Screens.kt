package org.ballistic.dreamjournalai.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
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

    @Serializable
    data object Favorites : Route()

    @Serializable
    data object AccountSettings : Route()

    @Serializable
    data object Tools : Route()

    @Serializable
    data object RandomDreamPicker : Route()

    @Serializable
    data object AnalyzeMultipleDreamsDetails : Route()

    @Serializable
    data object AnalyzeMultipleDreams : Route()

    @Serializable
    data object PaintDreamWorldDetails : Route()

    @Serializable
    data object PaintDreamWorld : Route()

    @Serializable
    data object Statistics : Route()

    @Serializable
    data object NotificationSettings : Route()

    @Serializable
    data object Nightmares : Route()

    @Serializable
    data object Symbol : Route()

    @Serializable
    data object DreamToolGraphScreen : Route()

    @Serializable
    data class FullScreenImageScreen(val imageID: String) : Route()

    @Serializable
    data object RateMyApp : Route()
}

enum class DrawerNavigation(val title: String?, val icon: ImageVector, val route: Route){
    DreamJournalScreen("My Dreams", Icons.Filled.Book, Route.DreamJournalScreen),
    AddEditDreamScreen("Add Dream", Icons.Default.Add, Route.AddEditDreamScreen("", -1)),
    StoreScreen("Store", Icons.Default.Shop, Route.StoreScreen),
    Favorites("Favorites", Icons.Default.Star, Route.Favorites),
    AccountSettings("Account Settings", Icons.Default.Settings, Route.AccountSettings),
    Tools("Tools", Icons.Default.Build, Route.Tools),
    Statistics("Statistics", Icons.Default.BarChart, Route.Statistics),
    NotificationSettings("Notification Settings", Icons.Default.Notifications, Route.NotificationSettings),
    Nightmares("Nightmares", Icons.Default.ErrorOutline, Route.Nightmares),
    Symbol("Symbols", Icons.AutoMirrored.Filled.List, Route.Symbol),
    RateMyApp("Rate this App", Icons.Default.Favorite, Route.RateMyApp),
    DreamToolGraphScreen("Tools", Icons.Default.Build, Route.DreamToolGraphScreen),
    //AboutMe("About Me", Icons.Default.Info, Route.MainScreen),
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