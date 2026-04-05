package com.mikhail.manifestation.ui.screens.meditations

// iOS Migration: -> MeditationPlayerView.swift — horizontal seek bar, glass circles, ExoPlayer -> AVPlayer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.Forward10
import androidx.compose.material.icons.outlined.Replay10
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.mikhail.manifestation.R
import com.mikhail.manifestation.Translations
import com.mikhail.manifestation.data.audio.PlaybackState
import com.mikhail.manifestation.data.model.Meditation
import com.mikhail.manifestation.ui.theme.AppTypography
import com.mikhail.manifestation.ui.theme.LocalToneTheme
import com.mikhail.manifestation.ui.theme.PlayfairDisplay
import com.mikhail.manifestation.ui.viewmodel.MeditationViewModel

@Composable
fun MeditationPlayerScreen(
    meditation: Meditation,
    viewModel: MeditationViewModel,
    backgroundImageRes: Int = R.drawable.bg_player,
    isGif: Boolean = false,
    coverUrl: String? = null,
    onBack: () -> Unit
) {
    val theme = LocalToneTheme.current
    val haptics = LocalHapticFeedback.current

    val playbackState by viewModel.playbackState.collectAsState()
    val currentPos by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val currentId by viewModel.currentMeditationId.collectAsState()

    val isThisMeditation = currentId == meditation.id
    val isPlaying = isThisMeditation && playbackState == PlaybackState.Playing
    val isActive = isThisMeditation &&
        (playbackState == PlaybackState.Playing || playbackState == PlaybackState.Paused || playbackState == PlaybackState.Buffering)

    val progress = if (isActive && duration > 0) currentPos.toFloat() / duration else 0f
    val displayPos = if (isActive) currentPos else 0L
    val displayDur = if (isActive && duration > 0) duration else meditation.durationSeconds * 1000L

    // Dragging state for the seek bar
    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableFloatStateOf(0f) }
    val effectiveProgress = if (isDragging) dragProgress else progress

    val accentColor = theme.accent

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background: prefer remote coverUrl, fall back to local drawable/GIF
        val context = LocalContext.current
        if (!coverUrl.isNullOrEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(context).data(coverUrl)
                    .crossfade(true).build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else if (isGif) {
            val gifLoader = remember {
                ImageLoader.Builder(context)
                    .components {
                        if (android.os.Build.VERSION.SDK_INT >= 28) add(ImageDecoderDecoder.Factory())
                        else add(GifDecoder.Factory())
                    }
                    .build()
            }
            AsyncImage(
                model = ImageRequest.Builder(context).data(backgroundImageRes).build(),
                imageLoader = gifLoader,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Image(
                painter = painterResource(backgroundImageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Gradient overlay matching iOS
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0f to Color.Black.copy(alpha = 0.5f),
                        0.25f to Color.Black.copy(alpha = 0.10f),
                        0.50f to Color.Black.copy(alpha = 0.10f),
                        0.70f to Color.Black.copy(alpha = 0.50f),
                        1f to Color.Black.copy(alpha = 0.80f)
                    )
                )
            )
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar — glass circle back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 64.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onBack
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.ChevronLeft,
                        contentDescription = "Back",
                        tint = Color.White.copy(0.85f)
                    )
                }
                Spacer(Modifier.weight(1f))
            }

            Spacer(Modifier.weight(1f))

            // Title
            Text(
                meditation.title,
                style = AppTypography.headingLarge.copy(fontFamily = PlayfairDisplay),
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(8.dp))

            // Area label
            Text(
                "${meditation.area.emoji}  ${Translations.lifeAreaLabel(meditation.area)}  ·  ${formatDurationFull(meditation.durationSeconds)}",
                style = AppTypography.bodyMedium,
                color = Color.White.copy(0.65f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            // Horizontal seek bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                // Track + thumb
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp), // touch target height
                    contentAlignment = Alignment.CenterStart
                ) {
                    // Background track
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                    )

                    // Progress fill
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(effectiveProgress.coerceIn(0f, 1f))
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        accentColor.copy(alpha = 0.8f),
                                        accentColor
                                    )
                                )
                            )
                    )

                    // Draggable thumb
                    val thumbSize = if (isDragging) 16.dp else 12.dp
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(isActive, duration) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        isDragging = true
                                        dragProgress = (offset.x / size.width.toFloat()).coerceIn(0f, 1f)
                                    },
                                    onDrag = { change, _ ->
                                        change.consume()
                                        dragProgress = (change.position.x / size.width.toFloat()).coerceIn(0f, 1f)
                                    },
                                    onDragEnd = {
                                        val seekPos = (dragProgress * duration).toLong().coerceIn(0, duration)
                                        viewModel.seekTo(seekPos)
                                        isDragging = false
                                    },
                                    onDragCancel = {
                                        isDragging = false
                                    }
                                )
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(thumbSize)
                                .align(Alignment.CenterStart)
                                .padding(start = 0.dp)
                                // offset thumb center to match progress fraction
                                .then(
                                    Modifier.fillMaxWidth(effectiveProgress.coerceIn(0f, 1f))
                                ),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(thumbSize)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Time labels
                val displayElapsed = if (isDragging) (dragProgress * displayDur).toLong() else displayPos
                val displayRemaining = displayDur - displayElapsed
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        formatMs(displayElapsed),
                        style = AppTypography.bodySmall,
                        color = Color.White.copy(0.65f)
                    )
                    Text(
                        "-${formatMs(displayRemaining)}",
                        style = AppTypography.bodySmall,
                        color = Color.White.copy(0.65f)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Playback controls — glass circles
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Rewind 10s
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            if (isActive) viewModel.seekTo((currentPos - 10_000).coerceAtLeast(0))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Replay10,
                        contentDescription = "Rewind 10s",
                        tint = Color.White.copy(0.85f),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(Modifier.width(32.dp))

                // Play/Pause — glass circle
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            if (isActive) {
                                viewModel.togglePlayPause()
                            } else {
                                viewModel.play(meditation)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isThisMeditation && playbackState == PlaybackState.Buffering) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(30.dp)
                        )
                    } else {
                        Icon(
                            if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(Modifier.width(32.dp))

                // Forward 10s
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            if (isActive) viewModel.seekTo((currentPos + 10_000).coerceAtMost(duration))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Forward10,
                        contentDescription = "Forward 10s",
                        tint = Color.White.copy(0.85f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}

private fun formatMs(ms: Long): String {
    val totalSec = (ms / 1000).coerceAtLeast(0)
    val min = totalSec / 60
    val sec = totalSec % 60
    return "%d:%02d".format(min, sec)
}

private fun formatDurationFull(seconds: Int): String {
    val min = seconds / 60
    return "$min ${Translations.ui("minutesShort")}"
}
