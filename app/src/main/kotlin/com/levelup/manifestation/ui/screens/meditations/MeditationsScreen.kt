package com.levelup.manifestation.ui.screens.meditations

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.levelup.manifestation.Translations
import com.levelup.manifestation.data.audio.PlaybackState
import com.levelup.manifestation.data.model.Meditation
import com.levelup.manifestation.ui.components.FeatherBackground
import com.levelup.manifestation.ui.theme.AppTypography
import com.levelup.manifestation.ui.theme.GlassCard
import com.levelup.manifestation.ui.theme.GlassChip
import com.levelup.manifestation.ui.theme.LifeArea
import com.levelup.manifestation.ui.theme.LocalToneTheme
import com.levelup.manifestation.ui.theme.PlayfairDisplay
import com.levelup.manifestation.ui.viewmodel.MeditationViewModel
import kotlinx.coroutines.delay

@Composable
fun MeditationsScreen(viewModel: MeditationViewModel) {
    var selectedMeditation by remember { mutableStateOf<Meditation?>(null) }

    val currentMeditation = selectedMeditation
    if (currentMeditation != null) {
        MeditationPlayerScreen(
            meditation = currentMeditation,
            viewModel = viewModel,
            onBack = { selectedMeditation = null }
        )
    } else {
        MeditationBrowseContent(
            viewModel = viewModel,
            onSelectMeditation = { selectedMeditation = it }
        )
    }
}

@Composable
private fun MeditationBrowseContent(
    viewModel: MeditationViewModel,
    onSelectMeditation: (Meditation) -> Unit
) {
    val theme = LocalToneTheme.current
    val haptics = LocalHapticFeedback.current
    var selectedArea by remember { mutableStateOf<LifeArea?>(null) }

    val meditations = remember(selectedArea) {
        if (selectedArea == null) viewModel.allMeditations()
        else viewModel.meditationsForArea(selectedArea!!)
    }

    val playbackState by viewModel.playbackState.collectAsState()
    val currentId by viewModel.currentMeditationId.collectAsState()
    val currentPos by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize().background(Brush.linearGradient(theme.gradientColors)))
        FeatherBackground()

        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 80.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    Translations.ui("meditationsTitle"),
                    style = AppTypography.headingLarge,
                    color = Color.White
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    Translations.ui("meditationsSubtitle"),
                    style = AppTypography.bodyMedium,
                    color = Color.White.copy(0.55f)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Area filter chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    AreaFilterChip(
                        label = Translations.ui("allAreas"),
                        isSelected = selectedArea == null,
                        accentColor = theme.accent
                    ) {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        selectedArea = null
                    }
                }
                items(LifeArea.entries) { area ->
                    AreaFilterChip(
                        label = "${area.emoji} ${Translations.lifeAreaLabel(area)}",
                        isSelected = selectedArea == area,
                        accentColor = theme.accent
                    ) {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        selectedArea = area
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Meditation list
            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 160.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(meditations, key = { it.id }) { meditation ->
                    val isPlaying = currentId == meditation.id &&
                        (playbackState == PlaybackState.Playing || playbackState == PlaybackState.Buffering)
                    val isPaused = currentId == meditation.id && playbackState == PlaybackState.Paused
                    val isActive = isPlaying || isPaused

                    MeditationCard(
                        meditation = meditation,
                        isActive = isActive,
                        isPlaying = isPlaying,
                        progress = if (isActive && duration > 0) currentPos.toFloat() / duration else 0f,
                        currentPos = currentPos,
                        duration = duration,
                        accentColor = theme.accent,
                        index = meditations.indexOf(meditation),
                        onPlay = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            onSelectMeditation(meditation)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AreaFilterChip(
    label: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    GlassChip(
        isSelected = isSelected,
        accentColor = accentColor,
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    ) {
        Text(
            label,
            style = AppTypography.labelMedium,
            color = if (isSelected) accentColor else Color.White.copy(0.6f),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun MeditationCard(
    meditation: Meditation,
    isActive: Boolean,
    isPlaying: Boolean,
    progress: Float,
    currentPos: Long,
    duration: Long,
    accentColor: Color,
    index: Int,
    onPlay: () -> Unit
) {
    var appeared by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (appeared) 1f else 0.85f, spring(stiffness = Spring.StiffnessMediumLow), label = "medScale")
    val alpha by animateFloatAsState(if (appeared) 1f else 0f, tween(300), label = "medAlpha")
    val borderColor by animateColorAsState(
        if (isActive) accentColor.copy(0.45f) else Color.Transparent,
        tween(300),
        label = "medBorder"
    )

    LaunchedEffect(Unit) { delay(index * 60L); appeared = true }

    GlassCard(
        cornerRadius = 18.dp,
        borderWidth = if (isActive) 1.dp else 0.dp,
        borderColor = borderColor,
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = alpha }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onPlay
            )
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Play/pause button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isActive) accentColor.copy(0.2f) else Color.White.copy(0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = if (isActive) accentColor else Color.White.copy(0.7f),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        meditation.title,
                        style = AppTypography.bodyLarge,
                        color = if (isActive) Color.White else Color.White.copy(0.85f)
                    )
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            meditation.area.emoji,
                            style = AppTypography.caption
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            Translations.lifeAreaLabel(meditation.area),
                            style = AppTypography.caption,
                            color = Color.White.copy(0.4f)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            formatDuration(meditation.durationSeconds),
                            style = AppTypography.caption,
                            color = Color.White.copy(0.35f)
                        )
                    }
                }
            }

            // Progress bar when active
            if (isActive) {
                Spacer(Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(3.dp).clip(CircleShape),
                    color = accentColor,
                    trackColor = Color.White.copy(0.08f),
                    strokeCap = StrokeCap.Round,
                )
                Spacer(Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        formatMs(currentPos),
                        style = AppTypography.caption,
                        color = Color.White.copy(0.4f)
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        formatMs(duration),
                        style = AppTypography.caption,
                        color = Color.White.copy(0.4f)
                    )
                }
            }
        }
    }
}

private fun formatDuration(seconds: Int): String {
    val min = seconds / 60
    return "$min ${Translations.ui("minutesShort")}"
}

private fun formatMs(ms: Long): String {
    val totalSec = ms / 1000
    val min = totalSec / 60
    val sec = totalSec % 60
    return "%d:%02d".format(min, sec)
}
