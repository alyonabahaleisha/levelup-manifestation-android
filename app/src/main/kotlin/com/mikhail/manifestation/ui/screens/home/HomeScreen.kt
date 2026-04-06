package com.mikhail.manifestation.ui.screens.home

// iOS Migration: -> HomeView.swift — LazyColumn -> ScrollView+VStack, LazyRow -> ScrollView(.horizontal), HorizontalPager -> TabView(.page)

import android.content.res.Configuration
import android.graphics.drawable.BitmapDrawable
import androidx.palette.graphics.Palette
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.border
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.mikhail.manifestation.R
import com.mikhail.manifestation.Translations
import com.mikhail.manifestation.data.content.AffirmationContent
import com.mikhail.manifestation.data.content.MeditationContent
import com.mikhail.manifestation.data.content.ProgramContent
import com.mikhail.manifestation.data.model.Meditation
import com.mikhail.manifestation.ui.theme.AppTypography
import com.mikhail.manifestation.ui.theme.LifeArea
import com.mikhail.manifestation.ui.theme.LocalToneTheme
import com.mikhail.manifestation.ui.theme.Manrope
import com.mikhail.manifestation.ui.theme.PlayfairDisplay
import com.mikhail.manifestation.ui.theme.areaColor
import com.mikhail.manifestation.ui.viewmodel.ClubsUiState
import com.mikhail.manifestation.ui.viewmodel.ClubsViewModel
import com.mikhail.manifestation.ui.viewmodel.MeditationViewModel
import com.mikhail.manifestation.ui.viewmodel.SavedProgramsViewModel
import android.os.Build
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime

private val bgLight = Color(0xFF154C6C)
private val textPrimary = Color.White
private val textOnCard = Color(0xFF0A0A14)
private val textSecondary = Color(0xFFAAC8D8)

private val cardImages = listOf(
    R.drawable.card_bg_1, R.drawable.card_bg_2, R.drawable.card_bg_3,
    R.drawable.card_bg_4, R.drawable.card_bg_5, R.drawable.card_bg_6,
    R.drawable.card_bg_7, R.drawable.card_bg_8, R.drawable.card_bg_9,
    R.drawable.card_bg_10
)

private data class HomeMoodBubbleData(
    val translationKey: String,
    val color: Color
)

private val homeMoodBubbles = listOf(
    HomeMoodBubbleData("moodCalm", Color(0xFFA8D8EA)),
    HomeMoodBubbleData("moodEnergy", Color(0xFFE0C8A0)),
    HomeMoodBubbleData("moodLove", Color(0xFFD4A0BE)),
    HomeMoodBubbleData("moodRelease", Color(0xFF8EC8DC)),
    HomeMoodBubbleData("moodTransform", Color(0xFFB8A8D8)),
    HomeMoodBubbleData("moodProtection", Color(0xFFA0D4C4)),
    HomeMoodBubbleData("moodShowAll", Color(0xFFB0C8D8))
)

@Composable
fun HomeScreen(
    savedProgramsViewModel: SavedProgramsViewModel,
    meditationViewModel: MeditationViewModel,
    clubsViewModel: ClubsViewModel,
    onNavigateToAffirmations: (String) -> Unit,
    onNavigateToReprogram: () -> Unit,
    onNavigateToMeditations: () -> Unit,
    onExpandClubsMap: () -> Unit = {},
    onMeditationTapped: (Meditation) -> Unit = {}
) {
    val haptics = LocalHapticFeedback.current
    val saved by savedProgramsViewModel.saved.collectAsState()
    val clubsUiState by clubsViewModel.uiState.collectAsState()

    val randomAffirmation = remember { AffirmationContent.feed().randomOrNull() }
    val allMeds = remember { MeditationContent.allMeditations() }
    val popularIds = remember { Translations.popularMeditations().map { it.id }.toSet() }
    val popularMeditations = remember(popularIds) { allMeds.filter { it.id in popularIds } }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize().background(bgLight))

        LazyColumn(
            contentPadding = PaddingValues(bottom = 140.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // ── Compact header: portrait + school name + greeting ─────
            item {
                var appeared by remember { mutableStateOf(false) }
                val alpha by animateFloatAsState(if (appeared) 1f else 0f, tween(700), label = "heroAlpha")
                LaunchedEffect(Unit) { appeared = true }

                val timeGreeting = remember {
                    val hour = LocalTime.now().hour
                    when {
                        hour < 12 -> Translations.ui("greetingMorning")
                        hour < 18 -> Translations.ui("greetingDay")
                        else -> Translations.ui("greetingEvening")
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(start = 24.dp, end = 24.dp, top = 40.dp)
                        .graphicsLayer { this.alpha = alpha }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.mikhail_portrait),
                            contentDescription = "Михаил Агеев",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                "Школа Михаила Агеева",
                                style = AppTypography.bodySmall,
                                color = Color.White.copy(0.7f)
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                timeGreeting,
                                style = AppTypography.headingMedium.copy(fontFamily = PlayfairDisplay),
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // ── Affirmation hero card (single random) ─────────────────
            if (randomAffirmation != null) {
                item {
                    var appeared by remember { mutableStateOf(false) }
                    val cardAlpha by animateFloatAsState(if (appeared) 1f else 0f, tween(500), label = "affAlpha")
                    LaunchedEffect(Unit) { delay(150); appeared = true }

                    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
                    Column(
                        modifier = Modifier
                            .padding(top = 24.dp)
                            .graphicsLayer { this.alpha = cardAlpha }
                    ) {
                        Text(
                            Translations.ui("homeDailyAffirmation"),
                            style = AppTypography.headingSmall.copy(fontFamily = PlayfairDisplay),
                            color = textPrimary,
                            modifier = Modifier.padding(start = 24.dp, bottom = 16.dp)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .height(screenHeight * 0.44f)
                                .clip(RoundedCornerShape(28.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onNavigateToAffirmations(randomAffirmation.text)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            val context = LocalContext.current
                            val gifLoader = remember {
                                ImageLoader.Builder(context)
                                    .components {
                                        if (Build.VERSION.SDK_INT >= 28) add(ImageDecoderDecoder.Factory())
                                        else add(GifDecoder.Factory())
                                    }
                                    .build()
                            }
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(R.raw.bg_affirmation_card)
                                    .build(),
                                imageLoader = gifLoader,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.35f)))
                            Text(
                                randomAffirmation.text,
                                style = AppTypography.bodyMedium.copy(
                                    fontFamily = PlayfairDisplay,
                                    fontSize = 22.sp
                                ),
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                lineHeight = 26.sp,
                                modifier = Modifier.padding(horizontal = 28.dp, vertical = 24.dp)
                            )
                        }
                    }
                }
            }

            // ── Popular meditations ──────────────────────────────────────
            if (popularMeditations.isNotEmpty()) {
                item {
                    var appeared by remember { mutableStateOf(false) }
                    val alpha by animateFloatAsState(if (appeared) 1f else 0f, tween(400), label = "medAlpha")
                    LaunchedEffect(Unit) { delay(300); appeared = true }

                    Column(
                        modifier = Modifier
                            .padding(top = 32.dp)
                            .graphicsLayer { this.alpha = alpha }
                    ) {
                        Text(
                            Translations.ui("homeMeditations"),
                            style = AppTypography.headingSmall.copy(fontFamily = PlayfairDisplay),
                            color = textPrimary,
                            modifier = Modifier.padding(start = 24.dp, bottom = 16.dp)
                        )

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            itemsIndexed(popularMeditations) { index, meditation ->
                                PopularMeditationCard(
                                    meditation = meditation,
                                    index = index,
                                    onClick = {
                                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onMeditationTapped(meditation)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // ── Clubs map section ─────────────────────────────────────────
            item {
                var appeared by remember { mutableStateOf(false) }
                val alpha by animateFloatAsState(if (appeared) 1f else 0f, tween(400), label = "clubsAlpha")
                LaunchedEffect(Unit) { delay(400); appeared = true }

                Box(
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .graphicsLayer { this.alpha = alpha }
                ) {
                    when (val state = clubsUiState) {
                        is ClubsUiState.Loading -> {
                            ClubsMapSectionSkeleton()
                        }
                        is ClubsUiState.Success -> {
                            if (state.clubs.isEmpty()) {
                                ClubsMapSectionEmpty(
                                    masterTelegramUrl = state.masterTelegramUrl,
                                    onJoinTapped = {}
                                )
                            } else {
                                ClubsMapSection(
                                    clubs = state.clubs,
                                    masterTelegramUrl = state.masterTelegramUrl,
                                    onExpandMap = onExpandClubsMap,
                                    onJoinTapped = {}
                                )
                            }
                        }
                        is ClubsUiState.Error -> {
                            ClubsMapSectionError(
                                onRetry = { clubsViewModel.loadClubs() }
                            )
                        }
                    }
                }
            }

        }
    }
}

// ── Popular meditation card ─────────────────────────────────────────────────

@Composable
private fun PopularMeditationCard(
    meditation: Meditation,
    index: Int,
    onClick: () -> Unit
) {
    val coverUrl = remember(meditation) { MeditationContent.coverUrl(meditation) }
    val coverColorHex = remember(meditation) { MeditationContent.coverColor(meditation) }
    val ctx = LocalContext.current
    val dominantColor = remember(coverColorHex) {
        if (coverColorHex != null) {
            try { Color(android.graphics.Color.parseColor(coverColorHex)) }
            catch (_: Exception) { areaColor(meditation.area) }
        } else areaColor(meditation.area)
    }

    Box(
        modifier = Modifier
            .width(200.dp)
            .height(160.dp)
            .clip(RoundedCornerShape(22.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        if (!coverUrl.isNullOrEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(ctx).data(coverUrl)
                    .crossfade(true).build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Image(
                painter = painterResource(
                    com.mikhail.manifestation.ui.screens.meditations.meditationCardImage(meditation.id, index)
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
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
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text(
                meditation.title,
                style = AppTypography.headingSmall,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "${meditation.durationSeconds / 60} ${Translations.ui("minutesShort")}",
                style = AppTypography.caption,
                color = Color.White.copy(0.6f)
            )
        }
    }
}

// ── Mood bubble for home screen ─────────────────────────────────────────────

@Composable
private fun HomeMoodBubble(
    label: String,
    bubbleColor: Color,
    index: Int,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sizeDp = 130f
    var appeared by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        if (appeared) 1f else 0.3f,
        spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessLow),
        label = "homeBubbleScale"
    )
    val alphaAnim by animateFloatAsState(
        if (appeared) 1f else 0f,
        tween(400),
        label = "homeBubbleAlpha"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "homeFloat$index")

    val driftX by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400 + index * 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "homeDriftX$index"
    )
    val driftY by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800 + index * 500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "homeDriftY$index"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000 + index * 300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "homePulse$index"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200 + index * 400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "homeGlow$index"
    )

    LaunchedEffect(Unit) { delay(index * 80L); appeared = true }

    val glowSize = sizeDp * 1.25f

    Box(
        modifier = modifier.size(sizeDp.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer glow — overflows layout bounds
        Box(
            modifier = Modifier
                .size(glowSize.dp)
                .graphicsLayer {
                    scaleX = scale * pulse; scaleY = scale * pulse; this.alpha = alphaAnim * glowAlpha
                    translationX = driftX * 2f
                    translationY = driftY * 2f
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
                    scaleX = scale * pulse; scaleY = scale * pulse; this.alpha = alphaAnim
                    translationX = driftX * 2f
                    translationY = driftY * 2f
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
                fontSize = (sizeDp * 0.14f).sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = Color.White,
                maxLines = 2,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}
