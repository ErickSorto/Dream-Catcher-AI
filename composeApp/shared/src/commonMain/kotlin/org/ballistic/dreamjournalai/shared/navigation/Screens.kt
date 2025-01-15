package org.ballistic.dreamjournalai.shared.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource
import dreamjournalai.composeapp.shared.generated.resources.Res
import dreamjournalai.composeapp.shared.generated.resources.dicetool
import dreamjournalai.composeapp.shared.generated.resources.dream_journal_reminder_tool
import dreamjournalai.composeapp.shared.generated.resources.dream_statistic_analyzer_tool
import dreamjournalai.composeapp.shared.generated.resources.dream_world_painter_tool
import dreamjournalai.composeapp.shared.generated.resources.mass_interpretation_tool
import dreamjournalai.composeapp.shared.generated.resources.reality_check_reminder_tool

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
    data object AnalyzeMultipleDreams : Route()

    @Serializable
    data object PaintDreamWorldDetails : Route()

    @Serializable
    data object DynamicLucidChecker : Route()

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

    @Serializable
    data object AboutMeScreen : Route()
}


sealed class ToolRoute(val image: DrawableResource){

    data class AnalyzeMultipleDreamsDetails(val imageID: DrawableResource) : ToolRoute(imageID)

    data class RandomDreamPicker(val imageID: DrawableResource) : ToolRoute(imageID)

    data class PaintDreamWorld(val imageID: DrawableResource) : ToolRoute(imageID)

    data class DreamJournalReminder(val imageID: DrawableResource) : ToolRoute(imageID)

    data class DynamicLucidChecker(val imageID: DrawableResource) : ToolRoute(imageID)

    data class Statistics(val imageID: DrawableResource) : ToolRoute(imageID)
}

enum class DrawerNavigation(val title: String?, val icon: ImageVector, val route: Route){
    DreamJournalScreen("My Dreams", Icons.Filled.Book, Route.DreamJournalScreen),
    StoreScreen("Store", Icons.Default.Shop, Route.StoreScreen),
    Favorites("Favorites", Icons.Default.Star, Route.Favorites),
    AccountSettings("Account Settings", Icons.Default.Settings, Route.AccountSettings),
    Statistics("Statistics", Icons.Default.BarChart, Route.Statistics),
    NotificationSettings("Notification Settings", Icons.Default.Notifications, Route.NotificationSettings),
    Nightmares("Nightmares", Icons.Default.ErrorOutline, Route.Nightmares),
    Symbol("Symbols", Icons.AutoMirrored.Filled.List, Route.Symbol),
    RateMyApp("Rate this App", Icons.Default.Favorite, Route.RateMyApp),
    DreamToolGraphScreen("Tools", Icons.Default.Build, Route.DreamToolGraphScreen),
}

enum class BottomNavigationRoutes(val title: String?, val icon: ImageVector, val route: Route){
    DreamJournalScreen("My Dreams", Icons.Filled.Book, Route.DreamJournalScreen),
    StoreScreen("Store", Icons.Default.Shop, Route.StoreScreen)
}

enum class DreamTools(val title: String, val description: String, val route: ToolRoute, val enabled: Boolean) {
    AnalyzeDreams("Interpret Multiple Dreams", "Interpret multiple dreams at once using AI", ToolRoute.AnalyzeMultipleDreamsDetails(Res.drawable.mass_interpretation_tool), true),
    RandomDreamPicker("Random Dream Picker",  "Pick a random dream from your dream journal", ToolRoute.RandomDreamPicker(Res.drawable.dicetool), true),
    AnalyzeStatistics("Analyze Statistics", "Analyze your dream statistics using AI", ToolRoute.Statistics(Res.drawable.dream_statistic_analyzer_tool), false), //Analyze Statistics
    DynamicLucidChecker("Dynamic Lucid Checker",  "Dynamic Lucid Reminder Task", ToolRoute.DynamicLucidChecker(Res.drawable.reality_check_reminder_tool), false), //Analyze Statistics
    DreamJournalReminder("Dream Journal Reminder",  "Set a reminder to write in your dream journal", ToolRoute.DreamJournalReminder(Res.drawable.dream_journal_reminder_tool), false), //Dream Journal Reminder
    DREAM_WORLD("Dream World", "Dream World Painter", ToolRoute.PaintDreamWorld(Res.drawable.dream_world_painter_tool), false), //Dream World Painter
}