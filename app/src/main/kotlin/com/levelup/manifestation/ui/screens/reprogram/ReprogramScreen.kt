package com.levelup.manifestation.ui.screens.reprogram

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.AutoAwesome
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.levelup.manifestation.data.content.ProgramContent
import com.levelup.manifestation.data.model.HiddenProgram
import com.levelup.manifestation.ui.components.StarSkyView
import com.levelup.manifestation.ui.theme.GlassCard
import com.levelup.manifestation.ui.theme.LifeArea
import com.levelup.manifestation.ui.theme.LocalToneTheme
import com.levelup.manifestation.ui.viewmodel.SavedProgramsViewModel
import com.levelup.manifestation.ui.viewmodel.ThemeViewModel
import kotlinx.coroutines.delay

private val availableAreas = listOf(
    LifeArea.Money, LifeArea.Relationships, LifeArea.SelfWorth,
    LifeArea.Fear, LifeArea.Body, LifeArea.Career
)

@Composable
fun ReprogramScreen(
    savedProgramsViewModel: SavedProgramsViewModel,
    themeViewModel: ThemeViewModel
) {
    val theme = LocalToneTheme.current
    var selectedArea by remember { mutableStateOf<LifeArea?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(theme.gradientColors)))
        StarSkyView()

        AnimatedContent(
            targetState = selectedArea,
            transitionSpec = {
                if (targetState != null) {
                    (slideInHorizontally { it } + fadeIn()) togetherWith (slideOutHorizontally { -it } + fadeOut())
                } else {
                    (slideInHorizontally { -it } + fadeIn()) togetherWith (slideOutHorizontally { it } + fadeOut())
                }
            },
            modifier = Modifier.fillMaxSize(),
            label = "reprogramContent"
        ) { area ->
            if (area == null) {
                AreaSelectionGrid(onAreaSelected = { selectedArea = it })
            } else {
                HiddenProgramsScreen(
                    area = area,
                    savedProgramsViewModel = savedProgramsViewModel,
                    onBack = { selectedArea = null }
                )
            }
        }
    }
}

// ── Area Selection Grid ────────────────────────────────────────────────────────

@Composable
private fun AreaSelectionGrid(onAreaSelected: (LifeArea) -> Unit) {
    val theme = LocalToneTheme.current
    val haptics = LocalHapticFeedback.current

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(top = 80.dp, bottom = 36.dp)
        ) {
            Text("Rewrite Your Programs", fontSize = 26.sp, fontWeight = FontWeight.Light, color = Color.White)
            Spacer(Modifier.height(8.dp))
            Text("What area of life feels blocked?", fontSize = 15.sp, color = Color.White.copy(0.55f))
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(availableAreas) { index, area ->
                AreaCard(area = area, index = index, glowColor = theme.glowColor) {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onAreaSelected(area)
                }
            }
        }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun AreaCard(area: LifeArea, index: Int, glowColor: Color, onClick: () -> Unit) {
    var appeared by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (appeared) 1f else 0.85f, spring(stiffness = Spring.StiffnessMediumLow), label = "areaScale")
    val alpha by animateFloatAsState(if (appeared) 1f else 0f, tween(300), label = "areaAlpha")

    LaunchedEffect(Unit) {
        delay(index * 60L)
        appeared = true
    }

    GlassCard(
        cornerRadius = 20.dp,
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = alpha }
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = 28.dp)
        ) {
            Text(area.emoji, fontSize = 36.sp)
            Spacer(Modifier.height(10.dp))
            Text(area.label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.White, textAlign = TextAlign.Center)
        }
    }
}

// ── Hidden Programs Screen ─────────────────────────────────────────────────────

@Composable
private fun HiddenProgramsScreen(
    area: LifeArea,
    savedProgramsViewModel: SavedProgramsViewModel,
    onBack: () -> Unit
) {
    val programs = remember(area) { ProgramContent.programs(area = area) }
    var selectedProgram by remember { mutableStateOf<HiddenProgram?>(null) }

    AnimatedContent(
        targetState = selectedProgram,
        transitionSpec = {
            if (targetState != null) {
                (slideInHorizontally { it } + fadeIn()) togetherWith (slideOutHorizontally { -it } + fadeOut())
            } else {
                (slideInHorizontally { -it } + fadeIn()) togetherWith (slideOutHorizontally { it } + fadeOut())
            }
        },
        modifier = Modifier.fillMaxSize(),
        label = "programContent"
    ) { program ->
        if (program == null) {
            ProgramListContent(programs = programs, area = area, onSelect = { selectedProgram = it }, onBack = onBack)
        } else {
            ProgramRewriteContent(program = program, savedProgramsViewModel = savedProgramsViewModel, onBack = { selectedProgram = null })
        }
    }
}

@Composable
private fun ProgramListContent(
    programs: List<HiddenProgram>,
    area: LifeArea,
    onSelect: (HiddenProgram) -> Unit,
    onBack: () -> Unit
) {
    val theme = LocalToneTheme.current
    val haptics = LocalHapticFeedback.current

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(top = 64.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlassCard(cornerRadius = 14.dp, modifier = Modifier.size(44.dp).clickable(
                interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onBack
            )) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.ChevronLeft, contentDescription = "Back", tint = Color.White.copy(0.7f))
                }
            }
            Spacer(Modifier.weight(1f))
            Text(area.emoji, fontSize = 22.sp)
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.width(44.dp))
        }

        Text(
            "These programs may be\nrunning in your subconscious",
            fontSize = 22.sp, fontWeight = FontWeight.Light, color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 0.dp).padding(bottom = 32.dp)
        )

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(programs) { program ->
                GlassCard(
                    cornerRadius = 18.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onSelect(program)
                        }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("\"${program.limiting}\"", fontSize = 16.sp, fontWeight = FontWeight.Light,
                            color = Color.White.copy(0.85f), modifier = Modifier.weight(1f))
                        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = Color.White.copy(0.35f))
                    }
                }
            }
            item { Spacer(Modifier.height(140.dp)) }
        }
    }
}

// ── Program Rewrite Content ────────────────────────────────────────────────────

@Composable
private fun ProgramRewriteContent(
    program: HiddenProgram,
    savedProgramsViewModel: SavedProgramsViewModel,
    onBack: () -> Unit
) {
    val theme = LocalToneTheme.current
    val saved by savedProgramsViewModel.saved.collectAsState()
    val isSaved = saved.any { it.text == program.rewrite }
    val haptics = LocalHapticFeedback.current

    var showRewrite by remember { mutableStateOf(false) }
    var pulseTriggered by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { delay(400); showRewrite = true }

    val oldAlpha by animateFloatAsState(if (showRewrite) 0.3f else 0.7f, tween(600), label = "oldAlpha")
    val arrowScale by animateFloatAsState(if (showRewrite) 1.2f else 0.8f, spring(stiffness = Spring.StiffnessMediumLow), label = "arrowScale")
    val newCardAlpha by animateFloatAsState(if (showRewrite) 1f else 0.4f, tween(500), label = "newAlpha")
    val newBorderColor by animateColorAsState(
        targetValue = if (showRewrite) theme.accent.copy(0.4f) else theme.accent.copy(0.1f),
        animationSpec = tween(600), label = "borderColor"
    )
    val newGlowRadius by animateDpAsState(if (showRewrite) 30.dp else 10.dp, tween(600), label = "glow")
    val pulseScale by animateFloatAsState(if (pulseTriggered) 1.12f else 1f, tween(700), label = "pulseScale")
    val pulseAlpha by animateFloatAsState(if (pulseTriggered) 0f else 0.8f, tween(700), label = "pulseAlpha")

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(horizontal = 24.dp).padding(top = 64.dp)) {
            GlassCard(cornerRadius = 14.dp, modifier = Modifier.size(44.dp).clickable(
                interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onBack
            )) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.ChevronLeft, contentDescription = "Back", tint = Color.White.copy(0.7f))
                }
            }
        }

        Spacer(Modifier.weight(1f))

        // Old program
        GlassCard(cornerRadius = 22.dp, modifier = Modifier.padding(horizontal = 24.dp)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp, vertical = 24.dp)
            ) {
                Text("OLD PROGRAM", fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp, color = Color.White.copy(0.35f))
                Spacer(Modifier.height(8.dp))
                Text("\"${program.limiting}\"", fontSize = 18.sp, fontWeight = FontWeight.Light,
                    color = Color.White.copy(oldAlpha),
                    textDecoration = if (showRewrite) TextDecoration.LineThrough else TextDecoration.None,
                    textAlign = TextAlign.Center)
            }
        }

        // Arrow
        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.ArrowDownward, contentDescription = null, tint = theme.accent.copy(0.7f),
                modifier = Modifier.graphicsLayer { scaleX = arrowScale; scaleY = arrowScale })
        }

        // New program
        GlassCard(
            cornerRadius = 22.dp,
            borderWidth = 1.5.dp,
            borderColor = newBorderColor,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .graphicsLayer { alpha = newCardAlpha }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp, vertical = 28.dp)
            ) {
                Text("YOUR NEW PROGRAM", fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp, color = theme.accent.copy(0.7f))
                Spacer(Modifier.height(8.dp))
                Text(program.rewrite, fontSize = 20.sp, fontWeight = FontWeight.Light,
                    color = Color.White, textAlign = TextAlign.Center, lineHeight = 28.sp)
            }
        }

        Spacer(Modifier.weight(1f))

        // Save button
        Box(modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 140.dp).fillMaxWidth()) {
            // Pulse ring
            if (pulseTriggered) {
                Box(modifier = Modifier.matchParentSize()
                    .border(2.dp, theme.accent, RoundedCornerShape(18.dp))
                    .graphicsLayer { scaleX = pulseScale; scaleY = pulseScale; alpha = pulseAlpha })
            }

            GlassCard(
                cornerRadius = 18.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (isSaved) Modifier.border(1.5.dp, theme.accent.copy(0.5f), RoundedCornerShape(18.dp)) else Modifier)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = !isSaved
                    ) {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        savedProgramsViewModel.save(program)
                        pulseTriggered = true
                    }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (isSaved) Icons.Outlined.CheckCircle else Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = if (isSaved) theme.accent else Color.White
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        if (isSaved) "Saved to Identity" else "Save to Identity",
                        fontSize = 16.sp, fontWeight = FontWeight.Medium,
                        color = if (isSaved) theme.accent else Color.White
                    )
                }
            }
        }
    }
}
