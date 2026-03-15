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
                Color(0xFF08090C),  // deep near-black
                Color(0xFF0D1520),  // midnight navy
                Color(0xFF111D2B)   // dark navy top
            ),
            accent = Color(0xFFC9A96E),          // champagne gold
            glowColor = Color(0x40C9A96E),       // gold glow
            surface = Color.White.copy(alpha = 0.06f),
            surfaceBorder = Color(0x30C9A96E)    // subtle gold border
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

// ── Composition locals ────────────────────────────────────────────────────────

val LocalToneTheme = staticCompositionLocalOf { ToneTheme.Default }

@Composable
fun LevelUpTheme(
    tone: ToneTheme = ToneTheme.Default,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalToneTheme provides tone) {
        content()
    }
}
