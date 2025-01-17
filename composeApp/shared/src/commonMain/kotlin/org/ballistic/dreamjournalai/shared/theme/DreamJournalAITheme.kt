package org.ballistic.dreamjournalai.shared.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import org.ballistic.dreamjournalai.shared.ui.theme.Pink40
import org.ballistic.dreamjournalai.shared.ui.theme.Pink80
import org.ballistic.dreamjournalai.shared.ui.theme.Purple40
import org.ballistic.dreamjournalai.shared.ui.theme.Purple80
import org.ballistic.dreamjournalai.shared.ui.theme.PurpleGrey40
import org.ballistic.dreamjournalai.shared.ui.theme.PurpleGrey80

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

/**
 * Original XML Colors converted to Kotlin Color objects.
 * Each color has a descriptive name, matching the original XML color names.
 */
object OriginalXmlColors {
    val RedOrange = Color(0xFFDF2A55)     // #DF2A55
    val Violet = Color(0xFF3700B3)        // #FF3700B3 (leading "FF" in ARGB omitted in XML)
    val SkyBlue = Color(0xFF1D8A8A)       // #1D8A8A
    val Yellow = Color(0xFFF1B763)        // #F1B763
    val Purple = Color(0xFF8755CE)        // #8755CE
    val Green = Color(0xFF46A34B)         // #46A34B
    val LighterYellow = Color(0xFFEDB561) // #EDB561
    val Black = Color(0xFF000000)         // #000000
    val LightBlack = Color(0xFF202020)    // #202020
    val White = Color(0xFFEFEEE9)         // #EFEEE9
    val BrighterWhite = Color(0xFFFFFFFF) // #FFFFFF
    val DarkBlue = Color(0xFF22252A)      // #22252A
    val DarkPurple = Color(0xFF4A4261)    // #4A4261
}

/**
 * Data class that represents your entire color scheme.
 * This allows easy swapping of color palettes if you decide to support
 * different themes (e.g., Light vs. Dark, or brand colors vs. custom colors).
 */
data class OriginalXmlAppColors(
    val redOrange: Color,
    val violet: Color,
    val skyBlue: Color,
    val yellow: Color,
    val purple: Color,
    val green: Color,
    val lighterYellow: Color,
    val black: Color,
    val lightBlack: Color,
    val white: Color,
    val brighterWhite: Color,
    val darkBlue: Color,
    val darkPurple: Color
)

/**
 * CompositionLocal to provide access to [OriginalXmlAppColors] throughout
 * your Composable hierarchy. You can override these values in different themes.
 */
val LocalOriginalXmlAppColors = staticCompositionLocalOf {
    OriginalXmlAppColors(
        redOrange = OriginalXmlColors.RedOrange,
        violet = OriginalXmlColors.Violet,
        skyBlue = OriginalXmlColors.SkyBlue,
        yellow = OriginalXmlColors.Yellow,
        purple = OriginalXmlColors.Purple,
        green = OriginalXmlColors.Green,
        lighterYellow = OriginalXmlColors.LighterYellow,
        black = OriginalXmlColors.Black,
        lightBlack = OriginalXmlColors.LightBlack,
        white = OriginalXmlColors.White,
        brighterWhite = OriginalXmlColors.BrighterWhite,
        darkBlue = OriginalXmlColors.DarkBlue,
        darkPurple = OriginalXmlColors.DarkPurple
    )
}

@Composable
fun DreamJournalAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // For KMM, simply pick Dark or Light color scheme.
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // No references to `LocalView` or `SideEffect` for system UI.
    // That is platform-specific, so we remove it in the KMM-friendly version.

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}