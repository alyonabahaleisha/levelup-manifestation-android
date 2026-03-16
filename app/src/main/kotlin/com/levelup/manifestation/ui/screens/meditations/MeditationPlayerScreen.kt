package com.levelup.manifestation.ui.screens.meditations

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.Forward10
import androidx.compose.material.icons.outlined.Replay10
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.levelup.manifestation.R
import com.levelup.manifestation.Translations
import com.levelup.manifestation.data.audio.PlaybackState
import com.levelup.manifestation.data.model.Meditation
import com.levelup.manifestation.ui.theme.AppTypography
import com.levelup.manifestation.ui.theme.GlassCard
import com.levelup.manifestation.ui.theme.LocalToneTheme
import com.levelup.manifestation.ui.theme.PlayfairDisplay
import com.levelup.manifestation.ui.viewmodel.MeditationViewModel
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MeditationPlayerScreen(
    meditation: Meditation,
    viewModel: MeditationViewModel,
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

    // Dragging state for the arc
    var isDragging by remember { mutableFloatStateOf(0f) } // 0 = not dragging
    var dragProgress by remember { mutableFloatStateOf(0f) }
    val effectiveProgress = if (isDragging > 0f) dragProgress else progress

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background: teal or ethereal image based on theme mode
        val themeMode = com.levelup.manifestation.ui.theme.LocalThemeMode.current
        if (themeMode == com.levelup.manifestation.ui.theme.ThemeMode.Ethereal) {
            Image(
                painter = painterResource(R.drawable.bg_player),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(Modifier.fillMaxSize().background(Color(0xFF154C6C)))
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar — back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 64.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassCard(
                    cornerRadius = 14.dp,
                    modifier = Modifier
                        .size(44.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onBack
                        )
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.ChevronLeft, contentDescription = "Back", tint = Color.White.copy(0.7f))
                    }
                }
                Spacer(Modifier.weight(1f))
            }

            Spacer(Modifier.weight(0.8f))

            // Circular progress arc — draggable
            val startAngle = 150f
            val sweepAngle = 240f

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(280.dp)
                    .pointerInput(isActive, duration) {
                        if (!isActive) return@pointerInput
                        detectDragGestures(
                            onDragStart = { offset ->
                                isDragging = 1f
                                dragProgress = angleToProgress(offset, size.width.toFloat(), size.height.toFloat(), startAngle, sweepAngle)
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                dragProgress = angleToProgress(change.position, size.width.toFloat(), size.height.toFloat(), startAngle, sweepAngle)
                            },
                            onDragEnd = {
                                val seekPos = (dragProgress * duration).toLong().coerceIn(0, duration)
                                viewModel.seekTo(seekPos)
                                isDragging = 0f
                            },
                            onDragCancel = {
                                isDragging = 0f
                            }
                        )
                    }
            ) {
                CircularProgressArc(
                    progress = effectiveProgress,
                    accentColor = theme.accent,
                    modifier = Modifier.fillMaxSize()
                )

                // Center content: Ageev portrait for teal, nothing for ethereal
                if (themeMode == com.levelup.manifestation.ui.theme.ThemeMode.Teal) {
                    Image(
                        painter = painterResource(R.drawable.mikhail_portrait),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(180.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                    )
                }

                // Elapsed time — left side
                val displayElapsed = if (isDragging > 0f) (dragProgress * displayDur).toLong() else displayPos
                Text(
                    formatMs(displayElapsed),
                    style = AppTypography.bodySmall,
                    color = Color.White.copy(0.6f),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                )

                // Remaining time — right side
                val displayRemaining = if (isDragging > 0f) displayDur - (dragProgress * displayDur).toLong() else displayDur - displayPos
                Text(
                    "- ${formatMs(displayRemaining)}",
                    style = AppTypography.bodySmall,
                    color = Color.White.copy(0.6f),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                )
            }

            Spacer(Modifier.height(40.dp))

            // Title
            Text(
                meditation.title,
                style = AppTypography.headingLarge.copy(fontFamily = PlayfairDisplay),
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(12.dp))

            // Area label
            Text(
                "${meditation.area.emoji}  ${Translations.lifeAreaLabel(meditation.area)}  ·  ${formatDurationFull(meditation.durationSeconds)}",
                style = AppTypography.bodyMedium,
                color = Color.White.copy(0.5f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.weight(1f))

            // Playback controls
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Rewind 10s
                GlassCard(
                    cornerRadius = 28.dp,
                    modifier = Modifier
                        .size(56.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            if (isActive) viewModel.seekTo((currentPos - 10_000).coerceAtLeast(0))
                        }
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.Replay10, contentDescription = "Rewind 10s",
                            tint = Color.White.copy(0.7f), modifier = Modifier.size(28.dp))
                    }
                }

                Spacer(Modifier.width(32.dp))

                // Play/Pause — large dark button
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2A2A3A))
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
                    Icon(
                        if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(Modifier.width(32.dp))

                // Forward 10s
                GlassCard(
                    cornerRadius = 28.dp,
                    modifier = Modifier
                        .size(56.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            if (isActive) viewModel.seekTo((currentPos + 10_000).coerceAtMost(duration))
                        }
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.Forward10, contentDescription = "Forward 10s",
                            tint = Color.White.copy(0.7f), modifier = Modifier.size(28.dp))
                    }
                }
            }

            Spacer(Modifier.height(140.dp))
        }
    }
}

/** Convert a touch position within the arc box to a 0..1 progress value. */
private fun angleToProgress(position: Offset, width: Float, height: Float, startAngle: Float, sweepAngle: Float): Float {
    val cx = width / 2f
    val cy = height / 2f
    val dx = position.x - cx
    val dy = position.y - cy
    // atan2 returns angle in radians, convert to degrees (0° = right, clockwise)
    var angleDeg = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
    if (angleDeg < 0) angleDeg += 360f
    // Map from absolute angle to progress within the arc
    var relative = angleDeg - startAngle
    if (relative < 0) relative += 360f
    return (relative / sweepAngle).coerceIn(0f, 1f)
}

@Composable
private fun CircularProgressArc(
    progress: Float,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val strokeWidthPx = with(density) { 6.dp.toPx() }
    val dotRadiusPx = with(density) { 9.dp.toPx() }

    Canvas(modifier = modifier) {
        val strokeWidth = strokeWidthPx
        val inset = dotRadiusPx + strokeWidth
        val arcSize = Size(size.width - inset * 2, size.height - inset * 2)
        val topLeft = Offset(inset, inset)

        val startAngle = 150f
        val sweepAngle = 240f

        drawArc(
            color = Color.White.copy(alpha = 0.10f),
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(strokeWidth, cap = StrokeCap.Round),
            topLeft = topLeft,
            size = arcSize
        )

        if (progress > 0f) {
            drawArc(
                color = accentColor,
                startAngle = startAngle,
                sweepAngle = sweepAngle * progress,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round),
                topLeft = topLeft,
                size = arcSize
            )
        }

        val cx = size.width / 2f
        val cy = size.height / 2f
        val radius = (size.width - inset * 2) / 2f
        val dotAngle = Math.toRadians((startAngle + sweepAngle * progress).toDouble())
        val dotX = cx + radius * cos(dotAngle).toFloat()
        val dotY = cy + radius * sin(dotAngle).toFloat()

        drawCircle(
            color = accentColor.copy(alpha = 0.3f),
            radius = dotRadiusPx * 1.5f,
            center = Offset(dotX, dotY)
        )
        drawCircle(
            color = Color.White,
            radius = dotRadiusPx,
            center = Offset(dotX, dotY)
        )
    }
}

private fun formatMs(ms: Long): String {
    val totalSec = (ms / 1000).coerceAtLeast(0)
    val min = totalSec / 60
    val sec = totalSec % 60
    return "%02d:%02d".format(min, sec)
}

private fun formatDurationFull(seconds: Int): String {
    val min = seconds / 60
    return "$min ${Translations.ui("minutesShort")}"
}
