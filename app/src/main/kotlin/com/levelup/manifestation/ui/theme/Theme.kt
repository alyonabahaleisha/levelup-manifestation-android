package com.levelup.manifestation.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

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

// ── Tone themes (mirrors iOS ToneTheme — exact RGB values) ───────────────────

enum class ToneTheme(
    val displayName: String,
    val gradientColors: List<Color>,
    val accent: Color,
    val glowColor: Color
) {
    SoftFeminine(
        displayName = "Soft Feminine",
        // iOS: (0.14, 0.05, 0.14), (0.20, 0.07, 0.18), (0.09, 0.03, 0.09)
        gradientColors = listOf(
            Color(0xFF240D24),
            Color(0xFF33122E),
            Color(0xFF170817)
        ),
        // iOS accent: (0.92, 0.68, 0.75)
        accent = Color(0xFFEBADBF),
        glowColor = Color(0x40EBADBF)
    ),
    CeoPowerful(
        displayName = "CEO Powerful",
        // iOS: (0.06, 0.05, 0.04), (0.12, 0.09, 0.02), (0.04, 0.03, 0.02)
        gradientColors = listOf(
            Color(0xFF0F0D0A),
            Color(0xFF1F1705),
            Color(0xFF0A0805)
        ),
        // iOS accent: (0.84, 0.70, 0.38)
        accent = Color(0xFFD6B361),
        glowColor = Color(0x40D6B361)
    ),
    CalmSpiritual(
        displayName = "Calm Spiritual",
        // iOS: (0.05, 0.04, 0.18), (0.08, 0.04, 0.22), (0.03, 0.04, 0.12)
        gradientColors = listOf(
            Color(0xFF0D0A2E),
            Color(0xFF140A38),
            Color(0xFF080A1F)
        ),
        // iOS accent: (0.62, 0.56, 0.96)
        accent = Color(0xFF9E8FF5),
        glowColor = Color(0x409E8FF5)
    )
}

// ── App Tab ───────────────────────────────────────────────────────────────────

sealed class AppTab {
    object Affirmations : AppTab()
    object Reprogram : AppTab()
}

// ── Composition locals ────────────────────────────────────────────────────────

val LocalToneTheme = staticCompositionLocalOf { ToneTheme.SoftFeminine }

@Composable
fun LevelUpTheme(
    tone: ToneTheme = ToneTheme.SoftFeminine,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalToneTheme provides tone) {
        content()
    }
}
