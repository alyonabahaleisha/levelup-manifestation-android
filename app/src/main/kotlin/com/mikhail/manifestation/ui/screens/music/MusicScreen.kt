package com.mikhail.manifestation.ui.screens.music

// iOS Migration: -> MusicView.swift

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mikhail.manifestation.Translations
import com.mikhail.manifestation.data.audio.PlaybackState
import com.mikhail.manifestation.data.model.MusicTrack
import com.mikhail.manifestation.ui.theme.AppTypography
import com.mikhail.manifestation.ui.viewmodel.MusicViewModel
import kotlinx.coroutines.delay

@Composable
fun MusicScreen(viewModel: MusicViewModel) {
    val haptics = LocalHapticFeedback.current
    val tracks = remember { viewModel.allTracks() }
    val playbackState by viewModel.playbackState.collectAsState()
    val currentId by viewModel.currentTrackId.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF154C6C))) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 80.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    Translations.ui("musicTitle"),
                    style = AppTypography.headingLarge,
                    color = Color.White
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    Translations.ui("musicSubtitle"),
                    style = AppTypography.bodyMedium,
                    color = Color.White.copy(0.55f)
                )
            }

            Spacer(Modifier.height(20.dp))

            // Track list
            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 160.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(tracks, key = { _, t -> t.id }) { index, track ->
                    val isPlaying = currentId == track.id &&
                        (playbackState == PlaybackState.Playing || playbackState == PlaybackState.Buffering)
                    val isPaused = currentId == track.id && playbackState == PlaybackState.Paused
                    val isActive = isPlaying || isPaused
                    val progress = if (isActive && duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f

                    MusicTrackCard(
                        track = track,
                        isActive = isActive,
                        isPlaying = isPlaying,
                        progress = progress,
                        index = index,
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            if (isActive) {
                                viewModel.togglePlayPause()
                            } else {
                                viewModel.downloadTrack(track)
                                viewModel.play(track)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MusicTrackCard(
    track: MusicTrack,
    isActive: Boolean,
    isPlaying: Boolean,
    progress: Float,
    index: Int,
    onClick: () -> Unit
) {
    var appeared by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(if (appeared) 1f else 0f, tween(400), label = "musicAlpha")

    LaunchedEffect(Unit) { delay(index * 80L); appeared = true }

    val cardColor = if (isActive) Color.White.copy(0.15f) else Color.White.copy(0.08f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { this.alpha = alpha }
            .clip(RoundedCornerShape(16.dp))
            .background(cardColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Play button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(if (isActive) 0.25f else 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(14.dp))

            // Track info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    track.title,
                    style = AppTypography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                )
                if (track.artist.isNotEmpty()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        track.artist,
                        style = AppTypography.bodyMedium,
                        color = Color.White.copy(0.6f),
                        maxLines = 1
                    )
                }
            }

            // Duration
            Text(
                formatMusicDuration(track.durationSeconds),
                style = AppTypography.caption,
                color = Color.White.copy(0.5f)
            )
        }

        // Progress bar when active
        if (isActive) {
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = Color.White.copy(0.6f),
                trackColor = Color.White.copy(0.1f)
            )
        }
    }
}

private fun formatMusicDuration(seconds: Int): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    return if (h > 0) "$h:${"%02d".format(m)}:00" else "$m:${"%02d".format(seconds % 60)}"
}
