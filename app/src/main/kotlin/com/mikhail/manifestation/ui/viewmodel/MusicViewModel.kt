package com.mikhail.manifestation.ui.viewmodel

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
import com.mikhail.manifestation.data.content.MusicContent
import com.mikhail.manifestation.data.model.MusicTrack
import com.mikhail.manifestation.data.store.PrefsKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(
    private val audioPlayerManager: AudioPlayerManager,
    private val downloadManager: AudioDownloadManager,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    val playbackState: StateFlow<PlaybackState> = audioPlayerManager.playbackState
    val currentPosition: StateFlow<Long> = audioPlayerManager.currentPosition
    val duration: StateFlow<Long> = audioPlayerManager.duration
    val currentTrackId: StateFlow<String?> = audioPlayerManager.currentMeditationId
    val currentTitle: StateFlow<String?> = audioPlayerManager.currentTitle
    val contentType: StateFlow<ContentType?> = audioPlayerManager.contentType
    val currentCoverUrl: StateFlow<String?> = audioPlayerManager.currentCoverUrl

    val downloads: StateFlow<Map<String, DownloadState>> = downloadManager.downloads

    fun downloadState(trackId: String): DownloadState = downloadManager.downloadState(trackId)

    fun downloadTrack(track: MusicTrack) {
        val url = MusicContent.audioUrl(track)
        downloadManager.download(track.id, url)
    }

    fun allTracks(): List<MusicTrack> = MusicContent.allTracks()

    fun play(track: MusicTrack) {
        val url = MusicContent.audioUrl(track)
        audioPlayerManager.play(track.id, url, track.title, ContentType.Music)
        viewModelScope.launch {
            dataStore.edit { it[PrefsKeys.LAST_MUSIC_TRACK_ID] = track.id }
        }
    }

    fun togglePlayPause() = audioPlayerManager.togglePlayPause()

    fun seekTo(positionMs: Long) = audioPlayerManager.seekTo(positionMs)

    fun stop() = audioPlayerManager.stop()
}
