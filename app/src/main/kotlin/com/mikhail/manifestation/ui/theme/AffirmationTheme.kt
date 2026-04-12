package com.mikhail.manifestation.ui.theme

import androidx.compose.ui.graphics.Color

data class AffirmationTheme(
    val id: String,
    val name: String,
    val colors: List<Color>,
    val textColor: Color
) {
    companion object {
        val all = listOf(
            AffirmationTheme(
                id = "cream", name = "Крем",
                colors = listOf(Color(0xFFE8DDD3)),
                textColor = Color(0xFF3C2A21)
            ),
            AffirmationTheme(
                id = "teal", name = "Океан",
                colors = listOf(Color(0xFF154C6C)),
                textColor = Color.White
            ),
            AffirmationTheme(
                id = "lavender", name = "Лаванда",
                colors = listOf(Color(0xFFE6E0F3), Color(0xFFC9B8E8)),
                textColor = Color(0xFF4A3560)
            ),
            AffirmationTheme(
                id = "sunset", name = "Закат",
                colors = listOf(Color(0xFFFF8C69), Color(0xFFFF6B95)),
                textColor = Color.White
            ),
            AffirmationTheme(
                id = "forest", name = "Лес",
                colors = listOf(Color(0xFF2D5A3D), Color(0xFF1B3A2A)),
                textColor = Color(0xFFE8F5E9)
            ),
            AffirmationTheme(
                id = "rose", name = "Роза",
                colors = listOf(Color(0xFFF5D0D0), Color(0xFFEBB5B5)),
                textColor = Color(0xFF5A2D3D)
            ),
            AffirmationTheme(
                id = "midnight", name = "Полночь",
                colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E)),
                textColor = Color(0xFFE8E8FF)
            ),
            AffirmationTheme(
                id = "golden", name = "Золото",
                colors = listOf(Color(0xFFF4E4BA), Color(0xFFE8D5A0)),
                textColor = Color(0xFF5C4A28)
            ),
            AffirmationTheme(
                id = "sky", name = "Небо",
                colors = listOf(Color(0xFFA7D8F0), Color(0xFFC5E8F7)),
                textColor = Color(0xFF1A3A4A)
            ),
            AffirmationTheme(
                id = "charcoal", name = "Уголь",
                colors = listOf(Color(0xFF2C2C2C), Color(0xFF3D3D3D)),
                textColor = Color(0xFFF2EDE8)
            ),
            AffirmationTheme(
                id = "peach", name = "Персик",
                colors = listOf(Color(0xFFFFDAB9), Color(0xFFFFE4C4)),
                textColor = Color(0xFF5A3E2B)
            ),
            AffirmationTheme(
                id = "aurora", name = "Аврора",
                colors = listOf(Color(0xFF1B4332), Color(0xFF2D6A4F), Color(0xFF52B788)),
                textColor = Color(0xFFEDFAF0)
            ),
        )

        fun forId(id: String): AffirmationTheme =
            all.firstOrNull { it.id == id } ?: all[0]
    }
}
