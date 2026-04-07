package com.mikhail.manifestation.data.audio

// iOS Migration: -> AVAudioSession category + MPNowPlayingInfoCenter + MPRemoteCommandCenter

import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * MediaSessionService that owns the MediaSession for lock-screen and notification
 * media controls.
 *
 * Media3 handles:
 *  - Posting the media notification (play/pause/seek) automatically.
 *  - Connecting the session to the lock screen / Now Playing controls.
 *  - Keeping the service alive as a foreground service while audio is playing.
 *
 * The service grabs the already-built ExoPlayer from the Hilt singleton
 * [AudioPlayerManager] so there is only ever one player instance in the app.
 */
@AndroidEntryPoint
class PlaybackService : MediaSessionService() {

    @Inject
    lateinit var audioPlayerManager: AudioPlayerManager

    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSession.Builder(this, audioPlayerManager.player).build()
    }

    /**
     * Called by Media3 whenever a controller (notification, lock screen, Bluetooth)
     * wants to connect.  Returning the session grants it access.
     */
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
