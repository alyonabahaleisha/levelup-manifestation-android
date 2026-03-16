package com.levelup.manifestation.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

// ── Life Areas (mirrors iOS LifeArea enum) ────────────────────────────────────

enum class LifeArea(val label: String, val emoji: String) {
    Money("Money", "💰"),
    Confidence("Confidence", "✨"),
    Love("Love", "🌹"),
    Calm("Calm", "🌊"),
    Career("Career", "🚀"),
    FeminineEnergy("Feminine Energy", "🌸"),
    Relationships("Relationships", "💞"),
    SelfWorth("Self-Worth", "👑"),
    Fear("Fear", "🦋"),
    Body("Body", "🌿")
}

// ── App Color Scheme — aligned with mikhail-ageev.ru ─────────────────────────

data class ToneTheme(
    val displayName: String,
    val gradientColors: List<Color>,
    val accent: Color,
    val glowColor: Color,
    val surface: Color,
    val surfaceBorder: Color
) {
    companion object {
        val Default = ToneTheme(
            displayName = "Default",
            gradientColors = listOf(
                Color(0xFFE8D5E0),  // soft rose
                Color(0xFFD4C0E0),  // lavender
                Color(0xFFCBB8D8)   // deeper lavender
            ),
            accent = Color(0xFFB88AAE),          // warm mauve-rose
            glowColor = Color(0x40B88AAE),       // rose glow
            surface = Color.White.copy(alpha = 0.55f),
            surfaceBorder = Color.White.copy(alpha = 0.70f)
        )

        // Keep for backward compatibility with DataStore
        val SoftFeminine = Default
        val CeoPowerful = Default
        val CalmSpiritual = Default

        val entries = listOf(Default)
    }
}

// ── App Tab ───────────────────────────────────────────────────────────────────

sealed class AppTab {
    object Home : AppTab()
    object Affirmations : AppTab()
    object Reprogram : AppTab()
    object Meditations : AppTab()
}

// ── Life Area Colors ─────────────────────────────────────────────────────────

fun areaColor(area: LifeArea): Color = when (area) {
    LifeArea.Money          -> Color(0xFFFFD966)
    LifeArea.Confidence     -> Color(0xFFFFE566)
    LifeArea.Love           -> Color(0xFFFF7A9A)
    LifeArea.Calm           -> Color(0xFF7EC8E3)
    LifeArea.Career         -> Color(0xFFFFB347)
    LifeArea.FeminineEnergy -> Color(0xFFFFB3DE)
    LifeArea.Relationships  -> Color(0xFFFF9AAF)
    LifeArea.SelfWorth      -> Color(0xFFB39DFF)
    LifeArea.Fear           -> Color(0xFF80D4FF)
    LifeArea.Body           -> Color(0xFF7FFFD4)
}

// ── Theme Mode ───────────────────────────────────────────────────────────────

enum class ThemeMode { Teal, Ethereal }

// ── Composition locals ────────────────────────────────────────────────────────

val LocalToneTheme = staticCompositionLocalOf { ToneTheme.Default }
val LocalThemeMode = staticCompositionLocalOf { ThemeMode.Teal }

@Composable
fun LevelUpTheme(
    tone: ToneTheme = ToneTheme.Default,
    themeMode: ThemeMode = ThemeMode.Teal,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalToneTheme provides tone,
        LocalThemeMode provides themeMode
    ) {
        content()
    }
}
