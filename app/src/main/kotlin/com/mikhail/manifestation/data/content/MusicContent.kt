package com.mikhail.manifestation.data.content

import com.mikhail.manifestation.Translations
import com.mikhail.manifestation.data.model.MusicTrack

object MusicContent {

    fun allTracks(): List<MusicTrack> =
        Translations.musicData().map { raw ->
            MusicTrack(
                id = raw.id,
                title = raw.title,
                artist = raw.artist,
                fileName = raw.fileName,
                durationSeconds = raw.durationSeconds
            )
        }

    private const val RELEASE_BASE =
        "https://github.com/alyonabahaleisha/levelup-manifestation-android/releases/download/v1.0.0-music"

    fun audioUrl(track: MusicTrack): String {
        // Use Firestore audioUrl if available, fall back to GitHub Releases
        val raw = Translations.musicData().find { it.id == track.id }
        if (raw != null && raw.audioUrl.isNotEmpty()) return raw.audioUrl
        return "$RELEASE_BASE/${track.fileName}"
    }
}
