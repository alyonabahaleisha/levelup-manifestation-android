package com.levelup.manifestation.data.content

import com.levelup.manifestation.BuildConfig
import com.levelup.manifestation.Translations
import com.levelup.manifestation.data.model.Meditation
import com.levelup.manifestation.ui.theme.LifeArea

object MeditationContent {

    fun meditations(area: LifeArea): List<Meditation> =
        Translations.meditationData(area).map { raw ->
            Meditation(
                id = raw.id,
                title = raw.title,
                area = area,
                fileName = raw.fileName,
                durationSeconds = raw.durationSeconds
            )
        }

    fun allMeditations(): List<Meditation> =
        LifeArea.entries.flatMap { meditations(it) }

    private const val RELEASE_BASE =
        "https://github.com/alyonabahaleisha/levelup-manifestation-android/releases/download/v1.0.0-meditations"

    fun audioUrl(meditation: Meditation): String =
        "$RELEASE_BASE/${meditation.fileName}"
}
