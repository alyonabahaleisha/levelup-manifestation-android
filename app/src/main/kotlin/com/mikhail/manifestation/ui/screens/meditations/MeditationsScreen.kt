package com.mikhail.manifestation.ui.screens.meditations

// iOS Migration: -> MeditationsView.swift — color-tinted visual cards

import android.graphics.BitmapFactory
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
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
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
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

// ── Mood → LifeArea mapping ─────────────────────────────────────────────────

private enum class MeditationMood(
    val translationKey: String,
    val areas: List<LifeArea>,
    val color: Color
) {
    Calm("moodCalm", listOf(LifeArea.Calm, LifeArea.Body), Color(0xFFA8D8EA)),
    Energy("moodEnergy", listOf(LifeArea.Confidence, LifeArea.Career), Color(0xFFE0C8A0)),
    Love("moodLove", listOf(LifeArea.Love, LifeArea.FeminineEnergy), Color(0xFFD4A0BE)),
    Release("moodRelease", listOf(LifeArea.Fear, LifeArea.Relationships), Color(0xFF8EC8DC)),
    Transform("moodTransform", listOf(LifeArea.Money, LifeArea.Career), Color(0xFFB8A8D8)),
    Protection("moodProtection", listOf(LifeArea.SelfWorth, LifeArea.Body), Color(0xFFA0D4C4))
}

// Bubble layout: relative x, y position (0..1) and diameter in dp
private data class BubbleLayout(val relX: Float, val relY: Float, val size: Float)

private val moodBubbleLayouts = listOf(
    BubbleLayout(0.28f, 0.04f, 170f),  // Calm
    BubbleLayout(0.68f, 0.06f, 180f),  // Energy
    BubbleLayout(0.18f, 0.24f, 175f),  // Love
    BubbleLayout(0.58f, 0.28f, 165f),  // Release
    BubbleLayout(0.35f, 0.46f, 185f),  // Transform
    BubbleLayout(0.72f, 0.50f, 160f),  // Protection
)

private val showAllBubbleLayout = BubbleLayout(0.25f, 0.68f, 175f)

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
    onPlayerVisibilityChanged: (Boolean) -> Unit = {}
) {
    var selectedMeditation by remember { mutableStateOf(initialMeditation) }
    // Use remember without key so it captures the initial value and doesn't reset on recomposition
    val initialMood = remember {
        initialMoodKey?.let { key -> MeditationMood.entries.find { it.translationKey == key } }
    }

    val currentMeditation = selectedMeditation
    LaunchedEffect(currentMeditation) {
        onPlayerVisibilityChanged(currentMeditation != null)
    }
    if (currentMeditation != null) {
        MeditationPlayerScreen(
            meditation = currentMeditation,
            viewModel = viewModel,
            backgroundImageRes = meditationCardImage(currentMeditation.id, 0),
            isGif = meditationHasGif(currentMeditation.id),
            coverUrl = MeditationContent.coverUrl(currentMeditation),
            onBack = { selectedMeditation = null }
        )
    } else {
        MeditationListScreen(
            viewModel = viewModel,
            selectedMood = initialMood,
            onBack = {},
            onSelectMeditation = { selectedMeditation = it }
        )
    }
}

@Composable
private fun MeditationListScreen(
    viewModel: MeditationViewModel,
    onSelectMeditation: (Meditation) -> Unit
) {
    val haptics = LocalHapticFeedback.current
    var selectedMood by remember { mutableStateOf<MeditationMood?>(null) }
    var showList by remember { mutableStateOf(false) }

    if (showList) {
        MeditationListScreen(
            viewModel = viewModel,
            selectedMood = selectedMood,
            onBack = { showList = false },
            onSelectMeditation = onSelectMeditation
        )
    } else {
        BubbleDiscoveryScreen(
            onMoodSelected = { mood ->
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                selectedMood = mood
                showList = true
            },
            onShowAll = {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                selectedMood = null
                showList = true
            }
        )
    }
}

// ── Bubble Discovery Screen ──────────────────────────────────────────────────

@Composable
private fun BubbleDiscoveryScreen(
    onMoodSelected: (MeditationMood) -> Unit,
    onShowAll: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF154C6C))) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 60.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    Translations.ui("meditationsTitle"),
                    style = AppTypography.headingLarge,
                    color = Color.White
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    Translations.ui("moodQuestion"),
                    style = AppTypography.bodyMedium,
                    color = Color.White.copy(0.55f)
                )
            }

            // Bubbles area
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val w = maxWidth
                val h = maxHeight

                // Mood bubbles
                MeditationMood.entries.forEachIndexed { index, mood ->
                    val layout = moodBubbleLayouts[index]
                    val sizeDp = layout.size.dp

                    MoodCircleBubble(
                        label = Translations.ui(mood.translationKey),
                        bubbleColor = mood.color,
                        sizeDp = layout.size,
                        index = index,
                        onTap = { onMoodSelected(mood) },
                        modifier = Modifier.offset(
                            x = w * layout.relX - sizeDp / 2,
                            y = h * layout.relY - sizeDp / 2
                        )
                    )
                }

                // Show All bubble
                MoodCircleBubble(
                    label = Translations.ui("moodShowAll"),
                    bubbleColor = Color.White,
                    sizeDp = showAllBubbleLayout.size,
                    index = MeditationMood.entries.size,
                    onTap = onShowAll,
                    modifier = Modifier.offset(
                        x = w * showAllBubbleLayout.relX - showAllBubbleLayout.size.dp / 2,
                        y = h * showAllBubbleLayout.relY - showAllBubbleLayout.size.dp / 2
                    )
                )
            }
        }
    }
}

@Composable
private fun MoodCircleBubble(
    label: String,
    bubbleColor: Color,
    sizeDp: Float,
    index: Int,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    var appeared by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        if (appeared) 1f else 0.3f,
        spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessLow),
        label = "bubbleScale"
    )
    val alpha by animateFloatAsState(
        if (appeared) 1f else 0f,
        tween(400),
        label = "bubbleAlpha"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "float$index")

    val driftX by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400 + index * 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "driftX$index"
    )
    val driftY by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800 + index * 500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "driftY$index"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000 + index * 300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse$index"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200 + index * 400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow$index"
    )

    LaunchedEffect(Unit) { delay(index * 100L); appeared = true }

    val glowSize = sizeDp * 1.35f

    Box(
        modifier = modifier.size(glowSize.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer glow
        Box(
            modifier = Modifier
                .size(glowSize.dp)
                .graphicsLayer {
                    scaleX = scale * pulse; scaleY = scale * pulse; this.alpha = alpha * glowAlpha
                    translationX = driftX * 2.5f
                    translationY = driftY * 2.5f
                }
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            bubbleColor.copy(alpha = 0.35f),
                            Color.White.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )

        // Main bubble
        Box(
            modifier = Modifier
                .size(sizeDp.dp)
                .graphicsLayer {
                    scaleX = scale * pulse; scaleY = scale * pulse; this.alpha = alpha
                    translationX = driftX * 2.5f
                    translationY = driftY * 2.5f
                }
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.45f),
                            bubbleColor.copy(alpha = 0.2f),
                            Color.White.copy(alpha = 0.12f)
                        ),
                        center = androidx.compose.ui.geometry.Offset(
                            sizeDp * 0.35f,
                            sizeDp * 0.3f
                        )
                    )
                )
                .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onTap
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                label,
                fontSize = (sizeDp * 0.10f).sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 2,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
    }
}

// ── Meditation List Screen ──────────────────────────────────────────────────

@Composable
private fun MeditationListScreen(
    viewModel: MeditationViewModel,
    selectedMood: MeditationMood?,
    onBack: () -> Unit,
    onSelectMeditation: (Meditation) -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val meditations = remember(selectedMood) {
        val raw = if (selectedMood == null) viewModel.allMeditations()
                  else selectedMood.areas.flatMap { viewModel.meditationsForArea(it) }
        raw.distinctBy { it.fileName }
    }
    val playbackState by viewModel.playbackState.collectAsState()
    val currentId by viewModel.currentMeditationId.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF154C6C))) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            if (selectedMood != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 54.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
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
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Text(
                        Translations.ui(selectedMood.translationKey),
                        style = AppTypography.headingMedium,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(Modifier.size(44.dp))
                }
            } else {
                Text(
                    Translations.ui("meditationsTitle"),
                    style = AppTypography.headingLarge,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 60.dp)
                        .padding(bottom = 4.dp),
                    textAlign = TextAlign.Center
                )
            }

            // Count label
            Text(
                "${meditations.size} ${Translations.ui("meditationsCountLabel")}",
                style = AppTypography.bodyMedium,
                color = Color.White.copy(0.55f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                textAlign = TextAlign.Center
            )

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

        // Background image: prefer remote coverUrl, fall back to local drawable/GIF
        if (!coverUrl.isNullOrEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(context).data(coverUrl)
                    .crossfade(true).build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else if (meditationHasGif(meditation.id)) {
            val gifLoader = remember {
                ImageLoader.Builder(context)
                    .components {
                        if (android.os.Build.VERSION.SDK_INT >= 28) add(ImageDecoderDecoder.Factory())
                        else add(GifDecoder.Factory())
                    }
                    .build()
            }
            AsyncImage(
                model = ImageRequest.Builder(context).data(cardImageRes).build(),
                imageLoader = gifLoader,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Image(
                painter = painterResource(cardImageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
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

        // Top badges
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Area badge
            Text(
                Translations.lifeAreaLabel(meditation.area),
                style = AppTypography.caption,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier
                    .background(Color.Black.copy(0.4f), RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )

            // Duration badge
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

            Spacer(Modifier.width(12.dp))

            // Play button
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(if (isActive) 0.35f else 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
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
