package org.ballistic.dreamjournalai.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.ballistic.dreamjournalai.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.open_sans_regular)),
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.open_sans_regular)),
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily(Font(R.font.open_sans_regular)),
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily(Font(R.font.open_sans_bold)),
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.15.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.open_sans_regular)),
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.15.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.open_sans_regular)),
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.15.sp
    ),

    )