package com.levelup.manifestation.ui.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.levelup.manifestation.data.audio.AudioPlayerManager
import com.levelup.manifestation.data.audio.PlaybackState
import com.levelup.manifestation.data.content.MeditationContent
import com.levelup.manifestation.data.model.Meditation
import com.levelup.manifestation.data.store.PrefsKeys
import com.levelup.manifestation.ui.theme.LifeArea
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeditationViewModel @Inject constructor(
    private val audioPlayerManager: AudioPlayerManager,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    val playbackState: StateFlow<PlaybackState> = audioPlayerManager.playbackState
    val currentPosition: StateFlow<Long> = audioPlayerManager.currentPosition
    val duration: StateFlow<Long> = audioPlayerManager.duration
    val currentMeditationId: StateFlow<String?> = audioPlayerManager.currentMeditationId

    fun meditationsForArea(area: LifeArea): List<Meditation> =
        MeditationContent.meditations(area)

    fun allMeditations(): List<Meditation> =
        MeditationContent.allMeditations()

    fun play(meditation: Meditation) {
        val url = MeditationContent.audioUrl(meditation)
        audioPlayerManager.play(meditation.id, url)
        viewModelScope.launch {
            dataStore.edit { it[PrefsKeys.LAST_MEDITATION_ID] = meditation.id }
        }
    }

    fun togglePlayPause() = audioPlayerManager.togglePlayPause()

    fun seekTo(positionMs: Long) = audioPlayerManager.seekTo(positionMs)

    fun stop() = audioPlayerManager.stop()
}
