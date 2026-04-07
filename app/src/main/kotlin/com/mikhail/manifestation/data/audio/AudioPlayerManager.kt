package com.mikhail.manifestation.data.audio

// iOS Migration: -> AudioPlayerManager.swift — ExoPlayer -> AVPlayer, StateFlow -> @Published, @Singleton -> actor or class with static shared

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

enum class PlaybackState { Idle, Buffering, Playing, Paused }

enum class ContentType { Meditation, Music }

@Singleton
class AudioPlayerManager @Inject constructor(
    @ApplicationContext private val context: Context,
    val downloadManager: AudioDownloadManager
) {
    // Internal visibility allows PlaybackService (same module) to attach a MediaSession
    // without leaking the player to the broader app layer.
    internal val player: ExoPlayer = ExoPlayer.Builder(context).build()
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _playbackState = MutableStateFlow(PlaybackState.Idle)
    val playbackState: StateFlow<PlaybackState> = _playbackState

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration

    private val _currentMeditationId = MutableStateFlow<String?>(null)
    val currentMeditationId: StateFlow<String?> = _currentMeditationId

    private val _currentTitle = MutableStateFlow<String?>(null)
    val currentTitle: StateFlow<String?> = _currentTitle

    private val _contentType = MutableStateFlow<ContentType?>(null)
    val contentType: StateFlow<ContentType?> = _contentType

    private val _currentCoverUrl = MutableStateFlow<String?>(null)
    val currentCoverUrl: StateFlow<String?> = _currentCoverUrl

    private var positionJob: Job? = null

    init {
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                _playbackState.value = when (state) {
                    Player.STATE_BUFFERING -> PlaybackState.Buffering
                    Player.STATE_READY -> if (player.playWhenReady) PlaybackState.Playing else PlaybackState.Paused
                    Player.STATE_ENDED -> { stopPositionUpdates(); PlaybackState.Idle }
                    else -> PlaybackState.Idle
                }
            }
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playbackState.value = if (isPlaying) PlaybackState.Playing else {
                    if (player.playbackState == Player.STATE_READY) PlaybackState.Paused else _playbackState.value
                }
                if (isPlaying) startPositionUpdates() else stopPositionUpdates()
            }
        })
    }

    fun play(meditationId: String, url: String, title: String? = null, contentType: ContentType? = null, coverUrl: String? = null) {
        _currentMeditationId.value = meditationId
        _currentTitle.value = title
        _contentType.value = contentType
        _currentCoverUrl.value = coverUrl

        // Prefer locally cached file; fall back to remote URL
        val playbackUrl = downloadManager.getLocalUrl(meditationId) ?: url
        val mediaItem = buildMediaItem(playbackUrl, title, coverUrl)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true
        _duration.value = 0L
        _currentPosition.value = 0L

        // Cache in the background if not already downloaded
        downloadManager.download(meditationId, url)
    }

    /**
     * Builds a MediaItem with embedded MediaMetadata so that Media3's MediaSession
     * can propagate title and artwork to the lock screen / notification without any
     * additional wiring in PlaybackService.
     */
    private fun buildMediaItem(url: String, title: String?, coverUrl: String?): MediaItem {
        val metadata = MediaMetadata.Builder()
            .setTitle(title)
            .setArtworkUri(coverUrl?.let { Uri.parse(it) })
            .build()
        return MediaItem.Builder()
            .setUri(url)
            .setMediaMetadata(metadata)
            .build()
    }

    fun togglePlayPause() {
        if (player.isPlaying) player.pause() else player.play()
    }

    fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
        _currentPosition.value = positionMs
    }

    fun stop() {
        player.stop()
        _currentMeditationId.value = null
        _currentTitle.value = null
        _contentType.value = null
        _currentCoverUrl.value = null
        _playbackState.value = PlaybackState.Idle
        _currentPosition.value = 0L
        _duration.value = 0L
        stopPositionUpdates()
    }

    private fun startPositionUpdates() {
        stopPositionUpdates()
        positionJob = scope.launch {
            while (isActive) {
                _currentPosition.value = player.currentPosition
                _duration.value = player.duration.coerceAtLeast(0L)
                delay(200L)
            }
        }
    }

    private fun stopPositionUpdates() {
        positionJob?.cancel()
        positionJob = null
    }
}
