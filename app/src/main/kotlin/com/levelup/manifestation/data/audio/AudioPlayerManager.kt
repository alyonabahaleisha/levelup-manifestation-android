package com.levelup.manifestation.data.audio

import android.content.Context
import androidx.media3.common.MediaItem
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

@Singleton
class AudioPlayerManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val player: ExoPlayer = ExoPlayer.Builder(context).build()
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _playbackState = MutableStateFlow(PlaybackState.Idle)
    val playbackState: StateFlow<PlaybackState> = _playbackState

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration

    private val _currentMeditationId = MutableStateFlow<String?>(null)
    val currentMeditationId: StateFlow<String?> = _currentMeditationId

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

    fun play(meditationId: String, url: String) {
        _currentMeditationId.value = meditationId
        player.setMediaItem(MediaItem.fromUri(url))
        player.prepare()
        player.playWhenReady = true
        _duration.value = 0L
        _currentPosition.value = 0L
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
