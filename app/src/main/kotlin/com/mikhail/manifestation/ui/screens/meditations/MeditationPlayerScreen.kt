package com.mikhail.manifestation.ui.screens.meditations

// iOS Migration: -> MeditationPlayerView.swift — horizontal seek bar, glass circles, ExoPlayer -> AVPlayer

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mikhail.manifestation.R
import com.mikhail.manifestation.Translations
import com.mikhail.manifestation.data.audio.DownloadState
import com.mikhail.manifestation.data.audio.DownloadStatus
import com.mikhail.manifestation.data.audio.PlaybackState
import com.mikhail.manifestation.data.model.Meditation
import com.mikhail.manifestation.ui.theme.AppTypography
import com.mikhail.manifestation.ui.theme.LocalToneTheme
import com.mikhail.manifestation.ui.theme.PlayfairDisplay
import com.mikhail.manifestation.ui.theme.areaColor
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
    val downloads by viewModel.downloads.collectAsState()
    val dlState = downloads[meditation.id]
        ?: if (viewModel.isDownloaded(meditation.id)) DownloadState(DownloadStatus.Downloaded, 1f)
        else DownloadState()

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

    // Bottom sheet state
    val screenHeightDp = LocalConfiguration.current.screenHeightDp
    val sheetMaxHeight = with(LocalDensity.current) { (screenHeightDp * 0.6f).dp }
    val sheetPeekHeight = 120.dp
    var sheetExpanded by remember { mutableStateOf(false) }
    val sheetHeight by animateDpAsState(
        targetValue = if (sheetExpanded) sheetMaxHeight else sheetPeekHeight,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
        label = "sheetHeight"
    )

    val hasDescription = meditation.description.isNotEmpty()

    Box(modifier = Modifier.fillMaxSize()) {

        // Layer 0: Background image
        val context = LocalContext.current
        if (!coverUrl.isNullOrEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(context).data(coverUrl)
                    .crossfade(true)
                    .memoryCacheKey(coverUrl)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(Modifier.fillMaxSize().background(areaColor(meditation.area)))
        }

        // Layer 1: Gradient overlay
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

        // Layer 2: Fixed controls centered on screen
        // No extra bottom offset needed — sheet starts collapsed at 80dp peek height
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 120.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                // Title
                Text(
                    text = meditation.title,
                    fontFamily = PlayfairDisplay,
                    fontSize = 24.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                )

                Spacer(Modifier.height(8.dp))

                // Duration label
                Text(
                    text = formatDurationFull(meditation.durationSeconds),
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.65f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                // Playback buttons row
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f))
                            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                if (isActive) viewModel.seekTo((currentPos - 10_000).coerceAtLeast(0))
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Replay10, contentDescription = "Rewind 10s", tint = Color.White.copy(0.85f), modifier = Modifier.size(24.dp))
                    }

                    Spacer(Modifier.width(32.dp))

                    // Play/Pause with download ring
                    val dlAnimatedProgress by animateFloatAsState(
                        targetValue = dlState.progress.coerceIn(0f, 1f),
                        animationSpec = tween(200),
                        label = "dlRing"
                    )
                    Box(modifier = Modifier.size(88.dp), contentAlignment = Alignment.Center) {
                        if (dlState.status == DownloadStatus.Downloading) {
                            Canvas(modifier = Modifier.size(88.dp)) {
                                val strokeWidth = 3.dp.toPx()
                                val inset = strokeWidth / 2f
                                val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                                val topLeft = Offset(inset, inset)
                                drawArc(color = Color.White.copy(alpha = 0.15f), startAngle = -90f, sweepAngle = 360f, useCenter = false, topLeft = topLeft, size = arcSize, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
                                drawArc(color = Color(0xFFB88AAE).copy(alpha = 0.90f), startAngle = -90f, sweepAngle = if (dlState.progress < 0f) 270f else dlAnimatedProgress * 360f, useCenter = false, topLeft = topLeft, size = arcSize, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
                            }
                        } else if (dlState.status == DownloadStatus.Downloaded) {
                            Canvas(modifier = Modifier.size(88.dp)) {
                                val strokeWidth = 2.dp.toPx()
                                val inset = strokeWidth / 2f
                                val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                                drawArc(color = Color(0xFFB88AAE).copy(alpha = 0.30f), startAngle = -90f, sweepAngle = 360f, useCenter = false, topLeft = Offset(inset, inset), size = arcSize, style = Stroke(width = strokeWidth))
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.25f))
                                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    if (isActive) viewModel.togglePlayPause() else viewModel.play(meditation)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isThisMeditation && playbackState == PlaybackState.Buffering) {
                                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(30.dp))
                            } else {
                                Icon(if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                            }
                        }
                    }

                    Spacer(Modifier.width(32.dp))

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f))
                            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                if (isActive) viewModel.seekTo((currentPos + 10_000).coerceAtMost(duration))
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Forward10, contentDescription = "Forward 10s", tint = Color.White.copy(0.85f), modifier = Modifier.size(24.dp))
                    }
                }

                Spacer(Modifier.height(32.dp))

                // Seek bar section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(effectiveProgress.coerceIn(0f, 1f))
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(accentColor.copy(alpha = 0.8f), accentColor)
                                    )
                                )
                        )
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
                                        onDragCancel = { isDragging = false }
                                    )
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(effectiveProgress.coerceIn(0.001f, 1f))
                                    .height(thumbSize)
                                    .align(Alignment.CenterStart),
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

                    val displayElapsed = if (isDragging) (dragProgress * displayDur).toLong() else displayPos
                    val displayRemaining = displayDur - displayElapsed
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(formatMs(displayElapsed), style = AppTypography.bodySmall, color = Color.White.copy(0.65f))
                        Text("-${formatMs(displayRemaining)}", style = AppTypography.bodySmall, color = Color.White.copy(0.65f))
                    }
                }
            }
        }

        // Layer 3: Draggable bottom sheet (only if description exists)
        if (hasDescription) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(sheetHeight)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color(0xEB101820))
                    .pointerInput(Unit) {
                        var totalDrag = 0f
                        detectVerticalDragGestures(
                            onDragStart = { totalDrag = 0f },
                            onVerticalDrag = { _, dragAmount -> totalDrag += dragAmount },
                            onDragEnd = {
                                if (totalDrag < -50) sheetExpanded = true
                                else if (totalDrag > 50) sheetExpanded = false
                            }
                        )
                    }
            ) {
                // Drag handle — tappable to toggle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { sheetExpanded = !sheetExpanded }
                        .padding(top = 12.dp, bottom = 12.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.White.copy(alpha = 0.30f))
                    )
                }

                // "Описание" label (always visible in collapsed state)
                Text(
                    text = "Описание",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.50f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                // Full description content (visible only when expanded)
                if (sheetExpanded) {
                    Spacer(Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .height(1.dp)
                            .background(Color.White.copy(alpha = 0.12f))
                    )

                    Spacer(Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .navigationBarsPadding()
                            .padding(horizontal = 24.dp)
                    ) {
                        Text(
                            text = meditation.description,
                            fontSize = 15.sp,
                            lineHeight = 24.sp,
                            color = Color.White.copy(alpha = 0.75f),
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Layer 4: Back button — fixed top-left, uses statusBarsPadding
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 20.dp, top = 48.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.ChevronLeft, contentDescription = "Back", tint = Color.White.copy(0.85f))
            }
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
