package com.mikhail.manifestation.data.content

import com.mikhail.manifestation.Translations
import com.mikhail.manifestation.data.model.Meditation
import com.mikhail.manifestation.ui.theme.LifeArea

object MeditationContent {

    fun meditations(area: LifeArea): List<Meditation> =
        Translations.meditationData(area).map { raw ->
            Meditation(
                id = raw.id,
                title = raw.title,
                description = raw.description,
                area = area,
                fileName = raw.fileName,
                durationSeconds = raw.durationSeconds
            )
        }

    fun allMeditations(): List<Meditation> =
        LifeArea.entries.flatMap { meditations(it) }

    fun popularMeditations(): List<Meditation> =
        Translations.popularMeditations().map { raw ->
            val area = LifeArea.entries.find { a ->
                Translations.meditationData(a).any { it.id == raw.id }
            } ?: LifeArea.Money
            Meditation(id = raw.id, title = raw.title, description = raw.description, area = area, fileName = raw.fileName, durationSeconds = raw.durationSeconds)
        }

    private const val RELEASE_BASE =
        "https://github.com/alyonabahaleisha/levelup-manifestation-android/releases/download/v1.0.0-meditations"

    fun audioUrl(meditation: Meditation): String {
        // Use Firestore audioUrl if available, fall back to GitHub Releases
        val raw = Translations.meditationData(meditation.area).find { it.id == meditation.id }
        if (raw != null && raw.audioUrl.isNotEmpty()) return raw.audioUrl
        return "$RELEASE_BASE/${meditation.fileName}"
    }

    fun coverUrl(meditation: Meditation): String? {
        val raw = Translations.meditationData(meditation.area).find { it.id == meditation.id }
        return raw?.coverUrl?.takeIf { it.isNotEmpty() }
    }

    fun coverColor(meditation: Meditation): String? {
        val raw = Translations.meditationData(meditation.area).find { it.id == meditation.id }
        return raw?.coverColor?.takeIf { it.isNotEmpty() }
    }
}
