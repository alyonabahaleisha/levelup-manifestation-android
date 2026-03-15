package com.levelup.manifestation.ui.screens.home

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.levelup.manifestation.Translations
import com.levelup.manifestation.data.content.AffirmationContent
import com.levelup.manifestation.data.content.MeditationContent
import com.levelup.manifestation.data.content.ProgramContent
import com.levelup.manifestation.data.model.Affirmation
import com.levelup.manifestation.data.model.Meditation
import com.levelup.manifestation.ui.components.FeatherBackground
import com.levelup.manifestation.ui.theme.AppTypography
import com.levelup.manifestation.ui.theme.GlassCard
import com.levelup.manifestation.ui.theme.LifeArea
import com.levelup.manifestation.ui.theme.LocalToneTheme
import com.levelup.manifestation.ui.theme.Manrope
import com.levelup.manifestation.ui.theme.PlayfairDisplay
import com.levelup.manifestation.ui.theme.areaColor
import com.levelup.manifestation.ui.viewmodel.MeditationViewModel
import com.levelup.manifestation.ui.viewmodel.SavedProgramsViewModel
import kotlinx.coroutines.delay
import java.time.LocalDate

@Composable
fun HomeScreen(
    savedProgramsViewModel: SavedProgramsViewModel,
    meditationViewModel: MeditationViewModel,
    onNavigateToAffirmations: () -> Unit,
    onNavigateToReprogram: () -> Unit,
    onNavigateToMeditations: () -> Unit
) {
    val theme = LocalToneTheme.current
    val haptics = LocalHapticFeedback.current
    val saved by savedProgramsViewModel.saved.collectAsState()

    val dayOfYear = remember { LocalDate.now().dayOfYear }
    val dailyAffirmation = remember(dayOfYear) {
        val feed = AffirmationContent.feed()
        if (feed.isNotEmpty()) feed[dayOfYear % feed.size] else null
    }

    val allMeditations = remember { meditationViewModel.allMeditations() }
    val totalPerArea = remember { LifeArea.entries.associateWith { ProgramContent.programs(it).size } }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize().background(Brush.linearGradient(theme.gradientColors)))
        FeatherBackground()

        LazyColumn(
            contentPadding = PaddingValues(bottom = 140.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Welcome greeting
            item {
                var appeared by remember { mutableStateOf(false) }
                val alpha by animateFloatAsState(if (appeared) 1f else 0f, tween(500), label = "greetAlpha")
                LaunchedEffect(Unit) { appeared = true }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 80.dp)
                        .graphicsLayer { this.alpha = alpha }
                ) {
                    Text(
                        Translations.ui("homeGreeting"),
                        style = AppTypography.headingLarge.copy(fontFamily = PlayfairDisplay),
                        color = Color.White
                    )
                }
            }

            // Daily affirmation section
            item {
                var appeared by remember { mutableStateOf(false) }
                val scale by animateFloatAsState(if (appeared) 1f else 0.9f, spring(stiffness = Spring.StiffnessMediumLow), label = "dailyScale")
                val alpha by animateFloatAsState(if (appeared) 1f else 0f, tween(400), label = "dailyAlpha")
                LaunchedEffect(Unit) { delay(100); appeared = true }

                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(top = 32.dp)
                        .graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = alpha }
                ) {
                    Text(
                        Translations.ui("homeDailyAffirmation"),
                        style = AppTypography.labelSmall,
                        color = Color.White.copy(0.4f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (dailyAffirmation != null) {
                        GlassCard(
                            cornerRadius = 28.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onNavigateToAffirmations()
                                }
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 32.dp, vertical = 36.dp)
                            ) {
                                Text(
                                    "✦",
                                    style = AppTypography.headingMedium,
                                    color = theme.accent
                                )
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    dailyAffirmation.text,
                                    style = AppTypography.headingSmall.copy(fontFamily = PlayfairDisplay),
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 28.sp
                                )
                            }
                        }
                    }
                }
            }

            // Reprogram section
            item {
                var appeared by remember { mutableStateOf(false) }
                val alpha by animateFloatAsState(if (appeared) 1f else 0f, tween(400), label = "repAlpha")
                LaunchedEffect(Unit) { delay(200); appeared = true }

                Column(
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .graphicsLayer { this.alpha = alpha }
                ) {
                    Text(
                        Translations.ui("homeReprogram"),
                        style = AppTypography.labelSmall,
                        color = Color.White.copy(0.4f),
                        modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 12.dp)
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
                                accentColor = theme.accent,
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

            // Meditations section
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
                        style = AppTypography.labelSmall,
                        color = Color.White.copy(0.4f),
                        modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 12.dp)
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

@Composable
private fun ReprogramAreaCard(
    area: LifeArea,
    savedCount: Int,
    totalCount: Int,
    accentColor: Color,
    index: Int,
    onClick: () -> Unit
) {
    var appeared by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (appeared) 1f else 0.85f, spring(stiffness = Spring.StiffnessMediumLow), label = "areaScale")
    val alpha by animateFloatAsState(if (appeared) 1f else 0f, tween(300), label = "areaAlpha")
    val progress = if (totalCount > 0) savedCount.toFloat() / totalCount else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = if (appeared) progress else 0f,
        animationSpec = tween(900),
        label = "progress"
    )

    LaunchedEffect(Unit) { delay(index * 60L); appeared = true }

    GlassCard(
        cornerRadius = 20.dp,
        modifier = Modifier
            .width(130.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = alpha }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 18.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(52.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 6.dp.toPx()
                    val inset = strokeWidth / 2f
                    val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                    val topLeft = Offset(inset, inset)
                    drawArc(
                        color = Color.White.copy(alpha = 0.10f),
                        startAngle = -90f, sweepAngle = 360f, useCenter = false,
                        style = Stroke(strokeWidth, cap = StrokeCap.Round),
                        topLeft = topLeft, size = arcSize
                    )
                    if (animatedProgress > 0f) {
                        drawArc(
                            color = areaColor(area),
                            startAngle = -90f, sweepAngle = 360f * animatedProgress, useCenter = false,
                            style = Stroke(strokeWidth, cap = StrokeCap.Round),
                            topLeft = topLeft, size = arcSize
                        )
                    }
                }
                Text(area.emoji, fontSize = 20.sp)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                Translations.lifeAreaLabel(area),
                style = AppTypography.caption,
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (savedCount > 0) {
                Spacer(Modifier.height(2.dp))
                Text(
                    "$savedCount / $totalCount",
                    style = AppTypography.caption.copy(fontSize = 10.sp),
                    color = accentColor.copy(0.7f)
                )
            }
        }
    }
}

@Composable
private fun MeditationPreviewCard(
    meditation: Meditation,
    index: Int,
    onClick: () -> Unit
) {
    var appeared by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (appeared) 1f else 0.85f, spring(stiffness = Spring.StiffnessMediumLow), label = "medScale")
    val alpha by animateFloatAsState(if (appeared) 1f else 0f, tween(300), label = "medAlpha")

    LaunchedEffect(Unit) { delay(index * 60L); appeared = true }

    GlassCard(
        cornerRadius = 20.dp,
        modifier = Modifier
            .width(180.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = alpha }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp)
        ) {
            Text(meditation.area.emoji, fontSize = 28.sp)
            Spacer(Modifier.height(10.dp))
            Text(
                meditation.title,
                style = AppTypography.bodyMedium.copy(fontFamily = Manrope),
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "${meditation.durationSeconds / 60} ${Translations.ui("minutesShort")}",
                style = AppTypography.caption,
                color = Color.White.copy(0.4f)
            )
        }
    }
}
