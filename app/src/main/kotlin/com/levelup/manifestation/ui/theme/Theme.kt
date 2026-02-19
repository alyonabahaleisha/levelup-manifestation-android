package com.levelup.manifestation.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ── Tone themes (mirrors iOS ToneTheme) ──────────────────────────────────────

enum class ToneTheme(
    val displayName: String,
    val gradient: List<Color>,
    val accent: Color,
    val glowColor: Color
) {
    SoftFeminine(
        displayName = "Soft Feminine",
        gradient = listOf(Color(0xFF2E0E2E), Color(0xFF1A0A2E), Color(0xFF0D0520)),
        accent = Color(0xFFD4A8FF),
        glowColor = Color(0x40D4A8FF)
    ),
    DarkMystic(
        displayName = "Dark Mystic",
        gradient = listOf(Color(0xFF000814), Color(0xFF001233), Color(0xFF023E8A)),
        accent = Color(0xFF90E0EF),
        glowColor = Color(0x4090E0EF)
    ),
    GoldenAbundance(
        displayName = "Golden Abundance",
        gradient = listOf(Color(0xFF1A0A00), Color(0xFF2D1B00), Color(0xFF4A2C00)),
        accent = Color(0xFFFFD700),
        glowColor = Color(0x40FFD700)
    )
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
