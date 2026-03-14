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
                Color(0xFF0B0B0B),  // near-black base
                Color(0xFF0E1A24),  // dark navy
                Color(0xFF154C6C)   // teal header
            ),
            accent = Color(0xFF6A93AA),          // muted blue-gray
            glowColor = Color(0x406A93AA),       // glow variant
            surface = Color.White.copy(alpha = 0.07f),
            surfaceBorder = Color.White.copy(alpha = 0.12f)
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
    object Affirmations : AppTab()
    object Reprogram : AppTab()
    object Meditations : AppTab()
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
