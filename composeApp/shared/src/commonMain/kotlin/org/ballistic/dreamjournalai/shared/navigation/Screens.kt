package org.ballistic.dreamjournalai.shared.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import dreamjournalai.composeapp.shared.generated.resources.Res
import dreamjournalai.composeapp.shared.generated.resources.dicetool
import dreamjournalai.composeapp.shared.generated.resources.dream_journal_reminder_tool
import dreamjournalai.composeapp.shared.generated.resources.dream_statistic_analyzer_tool
import dreamjournalai.composeapp.shared.generated.resources.dream_world_painter_tool
import dreamjournalai.composeapp.shared.generated.resources.mass_interpretation_tool
import dreamjournalai.composeapp.shared.generated.resources.reality_check_reminder_tool
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource

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


/**
 * A sealed class representing each "tool route"
 * in a type-safe, serializable manner for KMM & Compose Navigation.
 */
@Serializable
sealed class ToolRoute(
    val image: DreamDrawable
) {
    @Serializable
    data class AnalyzeMultipleDreamsDetails(
        val imageID: DreamDrawable
    ) : ToolRoute(imageID)

    @Serializable
    data class RandomDreamPicker(
        val imageID: DreamDrawable
    ) : ToolRoute(imageID)

    @Serializable
    data class PaintDreamWorld(
        val imageID: DreamDrawable
    ) : ToolRoute(imageID)

    @Serializable
    data class DreamJournalReminder(
        val imageID: DreamDrawable
    ) : ToolRoute(imageID)

    @Serializable
    data class DynamicLucidChecker(
        val imageID: DreamDrawable
    ) : ToolRoute(imageID)

    @Serializable
    data class Statistics(
        val imageID: DreamDrawable
    ) : ToolRoute(imageID)
}

enum class DreamTools(
    val title: String,
    val description: String,
    val route: ToolRoute,
    val enabled: Boolean
) {
    AnalyzeDreams(
        title = "Interpret Multiple Dreams",
        description = "Interpret multiple dreams at once using AI",
        route = ToolRoute.AnalyzeMultipleDreamsDetails(
            imageID = DreamDrawable.MASS_INTERPRETATION_TOOL
        ),
        enabled = true
    ),
    RandomDreamPicker(
        title = "Random Dream Picker",
        description = "Pick a random dream from your dream journal",
        route = ToolRoute.RandomDreamPicker(
            imageID = DreamDrawable.DICE_TOOL
        ),
        enabled = true
    ),
    AnalyzeStatistics(
        title = "Analyze Statistics",
        description = "Analyze your dream statistics using AI",
        route = ToolRoute.Statistics(
            imageID = DreamDrawable.DREAM_STATISTIC_ANALYZER_TOOL
        ),
        enabled = false
    ),
    DynamicLucidChecker(
        title = "Dynamic Lucid Checker",
        description = "Dynamic Lucid Reminder Task",
        route = ToolRoute.DynamicLucidChecker(
            imageID = DreamDrawable.REALITY_CHECK_REMINDER_TOOL
        ),
        enabled = false
    ),
    DreamJournalReminder(
        title = "Dream Journal Reminder",
        description = "Set a reminder to write in your dream journal",
        route = ToolRoute.DreamJournalReminder(
            imageID = DreamDrawable.DREAM_JOURNAL_REMINDER_TOOL
        ),
        enabled = false
    ),
    DREAM_WORLD(
        title = "Dream World",
        description = "Dream World Painter",
        route = ToolRoute.PaintDreamWorld(
            imageID = DreamDrawable.DREAM_WORLD_PAINTER_TOOL
        ),
        enabled = false
    );
}

@Serializable
enum class DreamDrawable {
    MASS_INTERPRETATION_TOOL,
    DICE_TOOL,
    DREAM_STATISTIC_ANALYZER_TOOL,
    REALITY_CHECK_REMINDER_TOOL,
    DREAM_JOURNAL_REMINDER_TOOL,
    DREAM_WORLD_PAINTER_TOOL
}

enum class DrawerNavigation(val title: String?, val icon: ImageVector, val route: Route){
    DreamJournalScreen("My Dreams", Icons.Filled.Book, Route.DreamJournalScreen),
    StoreScreen("Store", Icons.Default.Shop, Route.StoreScreen),
    Favorites("Favorites", Icons.Default.Star, Route.Favorites),
    AccountSettings("Account Settings", Icons.Default.Settings, Route.AccountSettings),
    Statistics("Statistics", Icons.Default.BarChart, Route.Statistics),
  //  NotificationSettings("Notification Settings", Icons.Default.Notifications, Route.NotificationSettings),
    Nightmares("Nightmares", Icons.Default.ErrorOutline, Route.Nightmares),
    Symbol("Symbols", Icons.AutoMirrored.Filled.List, Route.Symbol),
    RateMyApp("Rate this App", Icons.Default.Favorite, Route.RateMyApp),
    DreamToolGraphScreen("Tools", Icons.Default.Build, Route.DreamToolGraphScreen),
}

enum class BottomNavigationRoutes(val title: String?, val icon: ImageVector, val route: Route){
    DreamJournalScreen("My Dreams", Icons.Filled.Book, Route.DreamJournalScreen),
    StoreScreen("Store", Icons.Default.Shop, Route.StoreScreen)
}


fun DreamDrawable.toDrawableResource(): DrawableResource {
    return when (this) {
        DreamDrawable.MASS_INTERPRETATION_TOOL -> Res.drawable.mass_interpretation_tool
        DreamDrawable.DICE_TOOL                -> Res.drawable.dicetool
        DreamDrawable.DREAM_STATISTIC_ANALYZER_TOOL -> Res.drawable.dream_statistic_analyzer_tool
        DreamDrawable.REALITY_CHECK_REMINDER_TOOL    -> Res.drawable.reality_check_reminder_tool
        DreamDrawable.DREAM_JOURNAL_REMINDER_TOOL    -> Res.drawable.dream_journal_reminder_tool
        DreamDrawable.DREAM_WORLD_PAINTER_TOOL       -> Res.drawable.dream_world_painter_tool
    }
}




