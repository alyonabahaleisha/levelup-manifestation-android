package com.levelup.manifestation.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.levelup.manifestation.R

// ── Font Families ────────────────────────────────────────────────────────────

val PlayfairDisplay = FontFamily(
    Font(R.font.playfair_display_regular, FontWeight.Normal),
    Font(R.font.playfair_display_medium, FontWeight.Medium),
    Font(R.font.playfair_display_semibold, FontWeight.SemiBold),
    Font(R.font.playfair_display_bold, FontWeight.Bold),
)

val Manrope = FontFamily(
    Font(R.font.manrope_light, FontWeight.Light),
    Font(R.font.manrope_regular, FontWeight.Normal),
    Font(R.font.manrope_medium, FontWeight.Medium),
    Font(R.font.manrope_semibold, FontWeight.SemiBold),
    Font(R.font.manrope_bold, FontWeight.Bold),
)

// ── Type Scale ───────────────────────────────────────────────────────────────
// PlayfairDisplay: titles, headings, display text
// Manrope: body, labels, buttons, UI controls

object AppTypography {
    // Display — large splash/hero text
    val displayLarge = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.Normal,
        fontSize = 52.sp,
    )
    val displayMedium = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
    )

    // Heading — screen titles, section headers
    val headingLarge = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
    )
    val headingMedium = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
    )
    val headingSmall = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
    )

    // Body — main content text
    val bodyLarge = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Light,
        fontSize = 16.sp,
    )
    val bodyMedium = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Light,
        fontSize = 14.sp,
    )
    val bodySmall = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Light,
        fontSize = 13.sp,
    )

    // Label — section labels, chips, buttons
    val labelLarge = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
    )
    val labelMedium = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
    )
    val labelSmall = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        letterSpacing = 2.sp,
    )

    // Caption — small text, counters, hints
    val caption = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
    )

    // Tab bar labels
    val tabLabel = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
    )
}
