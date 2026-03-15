package com.levelup.manifestation.ui.screens.home

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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.levelup.manifestation.R
import com.levelup.manifestation.Translations
import com.levelup.manifestation.data.content.AffirmationContent
import com.levelup.manifestation.data.content.ProgramContent
import com.levelup.manifestation.data.model.Meditation
import com.levelup.manifestation.ui.theme.AppTypography
import com.levelup.manifestation.ui.theme.LifeArea
import com.levelup.manifestation.ui.theme.LocalToneTheme
import com.levelup.manifestation.ui.theme.Manrope
import com.levelup.manifestation.ui.theme.PlayfairDisplay
import com.levelup.manifestation.ui.theme.areaColor
import com.levelup.manifestation.ui.viewmodel.MeditationViewModel
import com.levelup.manifestation.ui.viewmodel.SavedProgramsViewModel
import kotlinx.coroutines.delay
import java.time.LocalDate

private val bgLight = Color(0xFFF0E8F0)
private val textPrimary = Color(0xFF2A2A3A)
private val textSecondary = Color(0xFF5A5070)

private val cardImages = listOf(
    R.drawable.card_bg_1, R.drawable.card_bg_2, R.drawable.card_bg_3,
    R.drawable.card_bg_4, R.drawable.card_bg_5, R.drawable.card_bg_6,
    R.drawable.card_bg_7, R.drawable.card_bg_8, R.drawable.card_bg_9,
    R.drawable.card_bg_10
)

@Composable
fun HomeScreen(
    savedProgramsViewModel: SavedProgramsViewModel,
    meditationViewModel: MeditationViewModel,
    onNavigateToAffirmations: () -> Unit,
    onNavigateToReprogram: () -> Unit,
    onNavigateToMeditations: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val saved by savedProgramsViewModel.saved.collectAsState()

    val allMeditations = remember { meditationViewModel.allMeditations() }
    val totalPerArea = remember { LifeArea.entries.associateWith { ProgramContent.programs(it).size } }
    val topAffirmations = remember { AffirmationContent.feed().take(10) }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize().background(bgLight))

        LazyColumn(
            contentPadding = PaddingValues(bottom = 140.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Hero image + welcome
            item {
                var appeared by remember { mutableStateOf(false) }
                val alpha by animateFloatAsState(if (appeared) 1f else 0f, tween(700), label = "heroAlpha")
                LaunchedEffect(Unit) { appeared = true }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.bg_home),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .align(Alignment.BottomCenter)
                            .background(Brush.verticalGradient(listOf(Color.Transparent, bgLight)))
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
                            .graphicsLayer { this.alpha = alpha }
                    ) {
                        Text(
                            Translations.ui("homeGreeting"),
                            style = AppTypography.headingLarge.copy(fontFamily = PlayfairDisplay),
                            color = Color.White
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Школа Михаила Агеева",
                            style = AppTypography.bodySmall,
                            color = Color.White.copy(0.6f)
                        )
                    }
                }
            }

            // ── Affirmation pager ────────────────────────────────────────
            item {
                var appeared by remember { mutableStateOf(false) }
                val cardAlpha by animateFloatAsState(if (appeared) 1f else 0f, tween(500), label = "affAlpha")
                LaunchedEffect(Unit) { delay(150); appeared = true }

                Column(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .graphicsLayer { this.alpha = cardAlpha }
                ) {
                    Text(
                        "Провозглашение дня",
                        style = AppTypography.headingSmall.copy(fontFamily = PlayfairDisplay),
                        color = textPrimary,
                        modifier = Modifier.padding(start = 24.dp, bottom = 16.dp)
                    )

                    val affPagerState = rememberPagerState(pageCount = { topAffirmations.size })

                    HorizontalPager(
                        state = affPagerState,
                        contentPadding = PaddingValues(horizontal = 32.dp),
                        pageSpacing = 14.dp
                    ) { index ->
                        val affirmation = topAffirmations[index]
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                                .clip(RoundedCornerShape(28.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onNavigateToAffirmations()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(cardImages[index % cardImages.size]),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                Modifier.fillMaxSize().background(
                                    Color.White.copy(alpha = 0.55f)
                                )
                            )
                            Text(
                                affirmation.text,
                                style = AppTypography.bodyMedium.copy(
                                    fontFamily = PlayfairDisplay,
                                    fontSize = 16.sp
                                ),
                                color = textPrimary,
                                textAlign = TextAlign.Center,
                                lineHeight = 24.sp,
                                maxLines = 7,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)
                            )
                        }
                    }
                }
            }

            // ── Reprogram section ────────────────────────────────────────
            item {
                var appeared by remember { mutableStateOf(false) }
                val alpha by animateFloatAsState(if (appeared) 1f else 0f, tween(400), label = "repAlpha")
                LaunchedEffect(Unit) { delay(300); appeared = true }

                Column(
                    modifier = Modifier
                        .padding(top = 56.dp)
                        .graphicsLayer { this.alpha = alpha }
                ) {
                    Text(
                        "Программы для работы",
                        style = AppTypography.headingSmall.copy(fontFamily = PlayfairDisplay),
                        color = textPrimary,
                        modifier = Modifier.padding(start = 24.dp, bottom = 16.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(LifeArea.entries.toList()) { index, area ->
                            val savedCount = saved.count { it.area == area }
                            val totalCount = totalPerArea[area] ?: 0
                            ReprogramAreaCard(
                                area = area,
                                savedCount = savedCount,
                                totalCount = totalCount,
                                index = index,
                                onClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onNavigateToReprogram()
                                }
                            )
                        }
                    }
                }
            }

            // ── Meditations section ──────────────────────────────────────
            item {
                var appeared by remember { mutableStateOf(false) }
                val alpha by animateFloatAsState(if (appeared) 1f else 0f, tween(400), label = "medAlpha")
                LaunchedEffect(Unit) { delay(450); appeared = true }

                Column(
                    modifier = Modifier
                        .padding(top = 56.dp)
                        .graphicsLayer { this.alpha = alpha }
                ) {
                    Text(
                        "Медитации",
                        style = AppTypography.headingSmall.copy(fontFamily = PlayfairDisplay),
                        color = textPrimary,
                        modifier = Modifier.padding(start = 24.dp, bottom = 16.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(allMeditations) { index, meditation ->
                            MeditationPreviewCard(
                                meditation = meditation,
                                index = index,
                                onClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onNavigateToMeditations()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Reprogram card — colored background per area ─────────────────────────────

@Composable
private fun ReprogramAreaCard(
    area: LifeArea,
    savedCount: Int,
    totalCount: Int,
    index: Int,
    onClick: () -> Unit
) {
    var appeared by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (appeared) 1f else 0.85f, spring(stiffness = Spring.StiffnessMediumLow), label = "areaScale")
    val alpha by animateFloatAsState(if (appeared) 1f else 0f, tween(300), label = "areaAlpha")

    LaunchedEffect(Unit) { delay(index * 70L); appeared = true }

    val color = areaColor(area)

    Box(
        modifier = Modifier
            .width(120.dp)
            .height(140.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = alpha }
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        color.copy(alpha = 0.25f),
                        color.copy(alpha = 0.10f)
                    )
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp)
        ) {
            Spacer(Modifier.weight(1f))
            Text(
                Translations.lifeAreaLabel(area),
                style = AppTypography.labelLarge,
                color = textPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                if (savedCount > 0) "$savedCount / $totalCount" else "$totalCount программ",
                style = AppTypography.caption,
                color = textSecondary
            )
            Spacer(Modifier.weight(1f))
        }
    }
}

// ── Meditation card — with image background ──────────────────────────────────

@Composable
private fun MeditationPreviewCard(
    meditation: Meditation,
    index: Int,
    onClick: () -> Unit
) {
    var appeared by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (appeared) 1f else 0.85f, spring(stiffness = Spring.StiffnessMediumLow), label = "medScale")
    val alpha by animateFloatAsState(if (appeared) 1f else 0f, tween(300), label = "medAlpha")

    LaunchedEffect(Unit) { delay(index * 70L); appeared = true }

    Box(
        modifier = Modifier
            .width(200.dp)
            .height(160.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = alpha }
            .clip(RoundedCornerShape(22.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        // Image background from card images
        Image(
            painter = painterResource(cardImages[(index + 3) % cardImages.size]),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Gradient overlay for text
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.45f)
                    )
                )
            )
        )
        // Text at bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text(
                meditation.title,
                style = AppTypography.bodyMedium.copy(fontFamily = Manrope),
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
