package com.levelup.manifestation.ui.screens.affirmations

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.levelup.manifestation.data.content.AffirmationContent
import com.levelup.manifestation.data.model.Affirmation
import com.levelup.manifestation.ui.components.StarSkyView
import com.levelup.manifestation.ui.theme.GlassCard
import com.levelup.manifestation.ui.theme.GlassChip
import com.levelup.manifestation.ui.theme.LifeArea
import com.levelup.manifestation.ui.theme.LocalToneTheme
import com.levelup.manifestation.ui.viewmodel.SavedProgramsViewModel
import com.levelup.manifestation.ui.viewmodel.ThemeViewModel

@Composable
fun AffirmationsScreen(
    savedProgramsViewModel: SavedProgramsViewModel,
    themeViewModel: ThemeViewModel
) {
    val theme = LocalToneTheme.current
    val saved by savedProgramsViewModel.saved.collectAsState()
    var selectedArea by remember { mutableStateOf<LifeArea?>(null) }

    val affirmations = remember(selectedArea, saved) {
        buildFeed(selectedArea, saved)
    }

    val pagerState = rememberPagerState(pageCount = { affirmations.size.coerceAtLeast(1) })
    val haptics = LocalHapticFeedback.current

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect {
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (affirmations.isEmpty()) {
            Box(Modifier.fillMaxSize().background(Brush.linearGradient(theme.gradientColors))) {
                StarSkyView()
            }
        } else {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                AffirmationCard(affirmation = affirmations[page])
            }
        }

        // Filter bar
        LazyRow(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 60.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    label = "✦  All",
                    isSelected = selectedArea == null,
                    accentColor = theme.accent,
                    onClick = { selectedArea = null }
                )
            }
            items(LifeArea.entries) { area ->
                FilterChip(
                    label = "${area.emoji}  ${area.label}",
                    isSelected = selectedArea == area,
                    accentColor = theme.accent,
                    onClick = { selectedArea = if (selectedArea == area) null else area }
                )
            }
        }
    }
}

private fun buildFeed(area: LifeArea?, saved: List<Affirmation>): List<Affirmation> {
    val personal = if (area == null) saved else saved.filter { it.area == area }
    val regular = AffirmationContent.feed(if (area == null) emptyList() else listOf(area))
    return personal + regular
}

@Composable
fun AffirmationCard(affirmation: Affirmation) {
    val theme = LocalToneTheme.current
    var appeared by remember { mutableStateOf(false) }

    val cardOffset by animateFloatAsState(
        targetValue = if (appeared) 0f else 24f,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessMediumLow),
        label = "cardOffset"
    )
    val cardAlpha by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessMediumLow),
        label = "cardAlpha"
    )
    val chipOffset by animateFloatAsState(
        targetValue = if (appeared) 0f else 16f,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessMediumLow),
        label = "chipOffset"
    )

    LaunchedEffect(affirmation.id) {
        appeared = false
        appeared = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(theme.gradientColors))
        )
        StarSkyView()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 140.dp),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.layout.Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Area chip
                GlassChip(
                    isSelected = affirmation.isPersonal,
                    accentColor = theme.accent,
                    modifier = Modifier
                        .padding(bottom = 28.dp)
                        .graphicsLayer {
                            translationY = chipOffset
                            alpha = cardAlpha
                        }
                ) {
                    Text(
                        text = if (affirmation.isPersonal) "✦  Your Identity"
                        else "${affirmation.area.emoji}  ${affirmation.area.label}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (affirmation.isPersonal) theme.accent else Color.White.copy(0.75f),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }

                // Affirmation card
                val glowColor = if (affirmation.isPersonal) theme.accent.copy(alpha = 0.4f) else theme.glowColor
                val glowRadius = if (affirmation.isPersonal) 40.dp else 30.dp

                GlassCard(
                    cornerRadius = 28.dp,
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .shadow(glowRadius, androidx.compose.foundation.shape.RoundedCornerShape(28.dp), clip = false, ambientColor = glowColor, spotColor = glowColor)
                        .graphicsLayer {
                            translationY = cardOffset
                            alpha = cardAlpha
                        }
                ) {
                    Text(
                        text = affirmation.text,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Light,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 42.sp,
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 40.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    GlassChip(
        isSelected = isSelected,
        accentColor = accentColor,
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        }
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}
