package com.mikhail.manifestation.ui.screens.meditations

// iOS Migration: -> MeditationsView.swift — color-tinted visual cards

import android.graphics.BitmapFactory
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mikhail.manifestation.R
import com.mikhail.manifestation.Translations
import com.mikhail.manifestation.data.audio.PlaybackState
import com.mikhail.manifestation.data.content.MeditationContent
import com.mikhail.manifestation.data.model.Meditation
import com.mikhail.manifestation.ui.theme.AppTypography
import com.mikhail.manifestation.ui.theme.LifeArea
import com.mikhail.manifestation.ui.theme.areaColor
import com.mikhail.manifestation.ui.viewmodel.MeditationViewModel
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private val cardImages = listOf(
    R.drawable.card_bg_1, R.drawable.card_bg_2, R.drawable.card_bg_3,
    R.drawable.card_bg_4, R.drawable.card_bg_5, R.drawable.card_bg_6,
    R.drawable.card_bg_7, R.drawable.card_bg_8, R.drawable.card_bg_9,
    R.drawable.card_bg_10
)

sealed class MedImage {
    data class Drawable(val res: Int) : MedImage()
    data class Gif(val res: Int) : MedImage()
}

private val meditationImageOverrides = mapOf(
    "abundance" to MedImage.Drawable(R.drawable.med_abundance),
    "divine_dna" to MedImage.Gif(R.raw.med_divine_dna),
    "karmic_release" to MedImage.Drawable(R.drawable.med_karmic_release),
    "harmonize_life" to MedImage.Drawable(R.drawable.med_harmonize_life),
    "create_reality" to MedImage.Drawable(R.drawable.med_create_reality),
    "angel_activation" to MedImage.Drawable(R.drawable.med_angel_activation),
    "subtle_bodies" to MedImage.Drawable(R.drawable.med_subtle_bodies),
    "cleansing" to MedImage.Drawable(R.drawable.med_cleansing)
)

fun meditationCardImage(id: String, index: Int): Int =
    when (val override = meditationImageOverrides[id]) {
        is MedImage.Drawable -> override.res
        is MedImage.Gif -> override.res
        null -> cardImages[index % cardImages.size]
    }

fun meditationHasGif(id: String): Boolean =
    meditationImageOverrides[id] is MedImage.Gif

// ── Main Screen ─────────────────────────────────────────────────────────────

@Composable
fun MeditationsScreen(
    viewModel: MeditationViewModel,
    initialMeditation: Meditation? = null,
    initialMoodKey: String? = null,
    onPlayerVisibilityChanged: (Boolean) -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    var selectedMeditation by remember { mutableStateOf(initialMeditation) }
    val openedFromHome = remember { initialMeditation != null }

    val currentMeditation = selectedMeditation
    LaunchedEffect(currentMeditation) {
        onPlayerVisibilityChanged(currentMeditation != null)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (currentMeditation == null || !openedFromHome) {
            MeditationListScreen(
                viewModel = viewModel,
                onSelectMeditation = { selectedMeditation = it }
            )
        }

        if (currentMeditation != null) {
            MeditationPlayerScreen(
                meditation = currentMeditation,
                viewModel = viewModel,
                backgroundImageRes = meditationCardImage(currentMeditation.id, 0),
                isGif = meditationHasGif(currentMeditation.id),
                coverUrl = MeditationContent.coverUrl(currentMeditation),
                onBack = {
                    selectedMeditation = null
                    if (openedFromHome) onNavigateToHome()
                }
            )
        }
    }
}

// ── Meditation List Screen ──────────────────────────────────────────────────

@Composable
private fun MeditationListScreen(
    viewModel: MeditationViewModel,
    onSelectMeditation: (Meditation) -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val meditations = remember { viewModel.allMeditations().distinctBy { it.fileName } }
    val playbackState by viewModel.playbackState.collectAsState()
    val currentId by viewModel.currentMeditationId.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF154C6C))) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 40.dp)
                    .padding(bottom = 4.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    Translations.ui("meditationsTitle"),
                    style = AppTypography.headingLarge,
                    color = Color.White
                )

                // Count label
                Text(
                    "${meditations.size} ${Translations.ui("meditationsCountLabel")}",
                    style = AppTypography.bodyMedium,
                    color = Color.White.copy(0.55f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Meditation list
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 20.dp, end = 20.dp,
                    top = 14.dp, bottom = 160.dp
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                itemsIndexed(meditations, key = { _, m -> m.id }) { index, meditation ->
                    val isPlaying = currentId == meditation.id &&
                        (playbackState == PlaybackState.Playing || playbackState == PlaybackState.Buffering)
                    val isPaused = currentId == meditation.id && playbackState == PlaybackState.Paused
                    val isActive = isPlaying || isPaused

                    MeditationVisualCard(
                        meditation = meditation,
                        isActive = isActive,
                        isPlaying = isPlaying,
                        cardImageRes = meditationCardImage(meditation.id, index),
                        coverUrl = MeditationContent.coverUrl(meditation),
                        areaColor = areaColor(meditation.area),
                        index = index,
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

// ── Visual Card ─────────────────────────────────────────────────────────────

@Composable
private fun MeditationVisualCard(
    meditation: Meditation,
    isActive: Boolean,
    isPlaying: Boolean,
    cardImageRes: Int,
    coverUrl: String? = null,
    areaColor: Color,
    index: Int,
    onPlay: () -> Unit
) {
    var appeared by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (appeared) 1f else 0.85f, spring(stiffness = Spring.StiffnessMediumLow), label = "medScale")
    val alpha by animateFloatAsState(if (appeared) 1f else 0f, tween(300), label = "medAlpha")
    val borderColor by animateColorAsState(
        if (isActive) areaColor.copy(0.6f) else Color.Transparent,
        tween(300),
        label = "medBorder"
    )

    LaunchedEffect(Unit) { delay(index * 60L); appeared = true }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = alpha }
            .clip(RoundedCornerShape(22.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onPlay
            )
    ) {
        // Use coverColor from Firestore, fall back to local palette extraction
        val context = LocalContext.current
        val coverColorHex = remember(meditation) { MeditationContent.coverColor(meditation) }
        val fallbackColor = Color(0xFF0E3448)
        var dominantColor by remember { mutableStateOf(fallbackColor) }
        LaunchedEffect(coverColorHex, cardImageRes) {
            if (coverColorHex != null) {
                try { dominantColor = Color(android.graphics.Color.parseColor(coverColorHex)) }
                catch (_: Exception) {}
            } else {
                withContext(Dispatchers.Default) {
                    try {
                        val bitmap = BitmapFactory.decodeResource(context.resources, cardImageRes)
                        if (bitmap != null) {
                            val palette = Palette.from(bitmap).generate()
                            val rgb = palette.getDominantSwatch()?.rgb ?: palette.getMutedSwatch()?.rgb
                            if (rgb != null) {
                                dominantColor = androidx.compose.ui.graphics.lerp(Color(rgb), Color.Black, 0.45f)
                            }
                            bitmap.recycle()
                        }
                    } catch (_: Exception) {}
                }
            }
        }

        // Background image: remote coverUrl with color placeholder
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
            Box(Modifier.fillMaxSize().background(areaColor))
        }

        // Color gradient overlay — intense bottom third
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0f to Color.Transparent,
                        0.55f to Color.Transparent,
                        0.7f to dominantColor.copy(alpha = 0.7f),
                        1f to dominantColor.copy(alpha = 0.95f)
                    )
                )
            )
        )

        // Duration badge
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                formatDuration(meditation.durationSeconds),
                style = AppTypography.caption,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier
                    .background(Color.Black.copy(0.4f), RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        // Bottom content
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    meditation.title,
                    style = AppTypography.headingMedium,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (meditation.description.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        meditation.description,
                        style = AppTypography.bodyMedium,
                        color = Color.White.copy(0.8f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

        }

        // Active border overlay
        if (isActive) {
            Box(
                Modifier
                    .fillMaxSize()
                    .border(1.5.dp, borderColor, RoundedCornerShape(22.dp))
            )
        }
    }
}

private fun formatDuration(seconds: Int): String {
    val min = seconds / 60
    return "$min ${Translations.ui("minutesShort")}"
}
