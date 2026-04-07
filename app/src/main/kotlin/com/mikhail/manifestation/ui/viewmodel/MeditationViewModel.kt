package com.mikhail.manifestation.ui.viewmodel

// iOS Migration: -> MeditationViewModel.swift — @HiltViewModel -> @Observable, StateFlow -> @Published, viewModelScope.launch -> Task

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikhail.manifestation.data.audio.AudioDownloadManager
import com.mikhail.manifestation.data.audio.AudioPlayerManager
import com.mikhail.manifestation.data.audio.ContentType
import com.mikhail.manifestation.data.audio.DownloadState
import com.mikhail.manifestation.data.audio.PlaybackState
import com.mikhail.manifestation.data.content.MeditationContent
import com.mikhail.manifestation.data.model.Meditation
import com.mikhail.manifestation.data.store.PrefsKeys
import com.mikhail.manifestation.ui.theme.LifeArea
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
    private val downloadManager: AudioDownloadManager,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    val playbackState: StateFlow<PlaybackState> = audioPlayerManager.playbackState
    val currentPosition: StateFlow<Long> = audioPlayerManager.currentPosition
    val duration: StateFlow<Long> = audioPlayerManager.duration
    val currentMeditationId: StateFlow<String?> = audioPlayerManager.currentMeditationId
    val currentTitle: StateFlow<String?> = audioPlayerManager.currentTitle
    val contentType: StateFlow<ContentType?> = audioPlayerManager.contentType
    val currentCoverUrl: StateFlow<String?> = audioPlayerManager.currentCoverUrl

    val downloads: StateFlow<Map<String, DownloadState>> = downloadManager.downloads

    fun downloadState(meditationId: String): DownloadState = downloadManager.downloadState(meditationId)

    fun isDownloaded(meditationId: String): Boolean = downloadManager.isDownloaded(meditationId)

    fun downloadMeditation(meditation: Meditation) {
        val url = MeditationContent.audioUrl(meditation)
        downloadManager.download(meditation.id, url)
    }

    fun meditationsForArea(area: LifeArea): List<Meditation> =
        MeditationContent.meditations(area)

    fun allMeditations(): List<Meditation> =
        MeditationContent.allMeditations()

    fun play(meditation: Meditation) {
        val url = MeditationContent.audioUrl(meditation)
        val coverUrl = MeditationContent.coverUrl(meditation)
        audioPlayerManager.play(meditation.id, url, meditation.title, ContentType.Meditation, coverUrl)
        viewModelScope.launch {
            dataStore.edit { it[PrefsKeys.LAST_MEDITATION_ID] = meditation.id }
        }
    }

    fun togglePlayPause() = audioPlayerManager.togglePlayPause()

    fun seekTo(positionMs: Long) = audioPlayerManager.seekTo(positionMs)

    fun stop() = audioPlayerManager.stop()
}
