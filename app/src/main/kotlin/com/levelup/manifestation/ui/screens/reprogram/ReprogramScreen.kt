package com.levelup.manifestation.ui.screens.reprogram

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Close
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.levelup.manifestation.ui.theme.AppTypography
import com.levelup.manifestation.ui.theme.Manrope
import com.levelup.manifestation.ui.theme.PlayfairDisplay
import com.levelup.manifestation.Translations
import com.levelup.manifestation.data.content.ProgramContent
import com.levelup.manifestation.data.model.Affirmation
import com.levelup.manifestation.data.model.HiddenProgram
import com.levelup.manifestation.ui.components.FeatherBackground
import com.levelup.manifestation.ui.theme.GlassCard
import com.levelup.manifestation.ui.theme.LifeArea
import com.levelup.manifestation.ui.theme.LocalToneTheme
import com.levelup.manifestation.ui.viewmodel.SavedProgramsViewModel
import com.levelup.manifestation.ui.viewmodel.ThemeViewModel
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import kotlinx.coroutines.delay

private val availableAreas = LifeArea.entries

private fun areaColor(area: LifeArea): Color = when (area) {
    LifeArea.Money          -> Color(0xFFFFD966)
    LifeArea.Confidence     -> Color(0xFFFFE566)
    LifeArea.Love           -> Color(0xFFFF7A9A)
    LifeArea.Calm           -> Color(0xFF7EC8E3)
    LifeArea.Career         -> Color(0xFFFFB347)
    LifeArea.FeminineEnergy -> Color(0xFFFFB3DE)
    LifeArea.Relationships  -> Color(0xFFFF9AAF)
    LifeArea.SelfWorth      -> Color(0xFFB39DFF)
    LifeArea.Fear           -> Color(0xFF80D4FF)
    LifeArea.Body           -> Color(0xFF7FFFD4)
}

// ── Reprogram Screen ───────────────────────────────────────────────────────────

@Composable
fun ReprogramScreen(
    savedProgramsViewModel: SavedProgramsViewModel,
    themeViewModel: ThemeViewModel
) {
    val theme = LocalToneTheme.current
    var selectedArea by remember { mutableStateOf<LifeArea?>(null) }
    var showSavedCards by remember { mutableStateOf(false) }
    val saved by savedProgramsViewModel.saved.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(theme.gradientColors)))
        FeatherBackground()

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
                AreaSelectionGrid(
                    savedProgramsViewModel = savedProgramsViewModel,
                    onAreaSelected = { selectedArea = it },
                    onOpenSavedCards = { if (saved.isNotEmpty()) showSavedCards = true }
                )
            } else {
                HiddenProgramsScreen(
                    area = area,
                    savedProgramsViewModel = savedProgramsViewModel,
                    onBack = { selectedArea = null }
                )
            }
        }

        if (showSavedCards && saved.isNotEmpty()) {
            SavedCardsOverlay(
                saved = saved,
                accentColor = theme.accent,
                onDismiss = { showSavedCards = false }
            )
        }
    }
}

// ── Area Selection Grid ────────────────────────────────────────────────────────

@Composable
private fun AreaSelectionGrid(
    savedProgramsViewModel: SavedProgramsViewModel,
    onAreaSelected: (LifeArea) -> Unit,
    onOpenSavedCards: () -> Unit
) {
    val theme = LocalToneTheme.current
    val haptics = LocalHapticFeedback.current
    val saved by savedProgramsViewModel.saved.collectAsState()
    val totalPerArea = remember { availableAreas.associateWith { ProgramContent.programs(it).size } }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item(span = { GridItemSpan(2) }) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(top = 80.dp, bottom = 8.dp)
            ) {
                Text(Translations.ui("reprogramTitle"), style = AppTypography.headingLarge, color = Color.White)
                Spacer(Modifier.height(8.dp))
                Text(Translations.ui("reprogramSubtitle"), style = AppTypography.bodyMedium, color = Color.White.copy(0.55f))
            }
        }

        item(span = { GridItemSpan(2) }) {
            SavedBeliefsRingWidget(
                saved = saved,
                accentColor = theme.accent,
                onTap = onOpenSavedCards
            )
        }

        itemsIndexed(availableAreas) { index, area ->
            val savedCount = saved.count { it.area == area }
            val totalCount = totalPerArea[area] ?: 0
            AreaCard(
                area = area,
                index = index,
                glowColor = theme.glowColor,
                accentColor = theme.accent,
                savedCount = savedCount,
                totalCount = totalCount
            ) {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onAreaSelected(area)
            }
        }
    }
}

// ── Saved Beliefs Ring Widget ──────────────────────────────────────────────────

@Composable
private fun SavedBeliefsRingWidget(
    saved: List<Affirmation>,
    accentColor: Color,
    onTap: () -> Unit
) {
    val allPrograms = remember {
        availableAreas.flatMap { area -> ProgramContent.programs(area).map { area to it } }
    }
    val savedTexts = remember(saved) { saved.map { it.text }.toSet() }
    val totalCount = allPrograms.size
    val haptics = LocalHapticFeedback.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .padding(vertical = 8.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (saved.isNotEmpty()) {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onTap()
                }
            }
    ) {
        // Scattered dots ring
        Canvas(modifier = Modifier.size(260.dp)) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val baseRingR = size.width * 0.44f

            allPrograms.forEachIndexed { i, (area, program) ->
                val rng = Random(i * 31337)
                val angle = (i.toFloat() / totalCount.toFloat() * 2.0 * Math.PI - Math.PI / 2.0).toFloat()
                val radiusJitter = (rng.nextFloat() - 0.5f) * 12.dp.toPx()
                val ringR = baseRingR + radiusJitter
                val x = cx + ringR * cos(angle)
                val y = cy + ringR * sin(angle)
                val isSaved = program.rewrite in savedTexts

                if (isSaved) {
                    val dotR = 4.dp.toPx() + rng.nextFloat() * 2.dp.toPx()
                    val alpha = 0.80f + rng.nextFloat() * 0.20f
                    drawCircle(
                        color = areaColor(area).copy(alpha = alpha),
                        radius = dotR,
                        center = Offset(x, y)
                    )
                } else {
                    val dotR = 2.dp.toPx() + rng.nextFloat() * 1.5.dp.toPx()
                    val alpha = 0.10f + rng.nextFloat() * 0.08f
                    drawCircle(
                        color = Color.White.copy(alpha = alpha),
                        radius = dotR,
                        center = Offset(x, y)
                    )
                }
            }
        }

        // Central glow circle
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(158.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.28f),
                            accentColor.copy(alpha = 0.10f),
                            Color.Transparent
                        )
                    )
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "${saved.size}",
                    style = AppTypography.displayLarge.copy(fontFamily = Manrope, fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    "из $totalCount",
                    style = AppTypography.caption.copy(letterSpacing = 0.5.sp),
                    color = Color.White.copy(0.45f),
                )
                if (saved.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "смотреть",
                        style = AppTypography.labelSmall.copy(letterSpacing = 1.5.sp),
                        color = accentColor.copy(0.75f),
                    )
                }
            }
        }
    }
}

// ── Saved Cards Overlay ────────────────────────────────────────────────────────

@Composable
private fun SavedCardsOverlay(
    saved: List<Affirmation>,
    accentColor: Color,
    onDismiss: () -> Unit
) {
    val pagerState = rememberPagerState { saved.size }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xF2050508)) // ~95% opaque near-black
    ) {
        // Pager sits below header — give it top padding so cards don't hide under header
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 120.dp, bottom = 100.dp),
            contentPadding = PaddingValues(horizontal = 28.dp),
            pageSpacing = 12.dp
        ) { page ->
            val affirmation = saved[page]
            val dotColor = areaColor(affirmation.area)

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                GlassCard(
                    cornerRadius = 28.dp,
                    borderWidth = 1.5.dp,
                    borderColor = dotColor.copy(alpha = 0.40f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp, vertical = 48.dp)
                    ) {
                        Text(affirmation.area.emoji, fontSize = 40.sp)
                        Spacer(Modifier.height(20.dp))
                        Text(
                            affirmation.text,
                            style = AppTypography.headingSmall.copy(fontFamily = PlayfairDisplay),
                            color = dotColor.copy(alpha = 0.95f),
                            textAlign = TextAlign.Center,
                            lineHeight = 30.sp
                        )
                        Spacer(Modifier.height(20.dp))
                        Text(
                            Translations.lifeAreaLabel(affirmation.area),
                            style = AppTypography.labelSmall,
                            color = Color.White.copy(0.35f),
                        )
                    }
                }
            }
        }

        // Page indicator dots — drawn after pager so they're on top
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 128.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(saved.size.coerceAtMost(15)) { index ->
                val isActive = index == pagerState.currentPage
                Box(
                    modifier = Modifier
                        .size(if (isActive) 7.dp else 5.dp)
                        .clip(CircleShape)
                        .background(if (isActive) accentColor else Color.White.copy(0.25f))
                )
            }
        }

        // Header drawn last = always on top, close button always tappable
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 64.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "✦  МОИ НОВЫЕ УБЕЖДЕНИЯ  ✦",
                style = AppTypography.labelSmall.copy(fontWeight = FontWeight.Medium),
                color = Color.White.copy(0.4f)
            )
            GlassCard(
                cornerRadius = 14.dp,
                modifier = Modifier
                    .size(42.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss
                    )
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = "Close",
                        tint = Color.White.copy(0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ── Area Card ─────────────────────────────────────────────────────────────────

@Composable
private fun AreaCard(
    area: LifeArea,
    index: Int,
    glowColor: Color,
    accentColor: Color,
    savedCount: Int,
    totalCount: Int,
    onClick: () -> Unit
) {
    var appeared by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (appeared) 1f else 0.85f, spring(stiffness = Spring.StiffnessMediumLow), label = "areaScale")
    val alpha by animateFloatAsState(if (appeared) 1f else 0f, tween(300), label = "areaAlpha")
    val targetProgress = if (totalCount > 0) savedCount.toFloat() / totalCount else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = if (appeared) targetProgress else 0f,
        animationSpec = tween(900),
        label = "circleProgress"
    )
    val isComplete = savedCount > 0 && savedCount >= totalCount

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
            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(72.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 11.dp.toPx()
                    val inset = strokeWidth / 2f
                    val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                    val topLeft = Offset(inset, inset)
                    drawArc(
                        color = Color.White.copy(alpha = 0.10f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(strokeWidth, cap = StrokeCap.Round),
                        topLeft = topLeft,
                        size = arcSize
                    )
                    if (animatedProgress > 0f) {
                        drawArc(
                            color = if (isComplete) accentColor else accentColor.copy(alpha = 0.9f),
                            startAngle = -90f,
                            sweepAngle = 360f * animatedProgress,
                            useCenter = false,
                            style = Stroke(strokeWidth, cap = StrokeCap.Round),
                            topLeft = topLeft,
                            size = arcSize
                        )
                    }
                }
                Text(area.emoji, fontSize = 26.sp)
            }
            Spacer(Modifier.height(10.dp))
            Text(Translations.lifeAreaLabel(area), style = AppTypography.labelLarge, color = Color.White, textAlign = TextAlign.Center)
            if (savedCount > 0) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "$savedCount / $totalCount",
                    style = AppTypography.labelSmall.copy(letterSpacing = 0.sp),
                    color = accentColor.copy(alpha = 0.8f),
                )
            }
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
    val saved by savedProgramsViewModel.saved.collectAsState()
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
            ProgramListContent(programs = programs, area = area, saved = saved, onSelect = { selectedProgram = it }, onBack = onBack)
        } else {
            ProgramRewriteContent(program = program, savedProgramsViewModel = savedProgramsViewModel, onBack = { selectedProgram = null })
        }
    }
}

@Composable
private fun ProgramListContent(
    programs: List<HiddenProgram>,
    area: LifeArea,
    saved: List<Affirmation>,
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
            Text(area.emoji, style = AppTypography.headingMedium)
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.width(44.dp))
        }

        Text(
            Translations.ui("hiddenProgramsSubtitle"),
            style = AppTypography.headingMedium, color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 0.dp).padding(bottom = 32.dp)
        )

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(programs) { program ->
                val isReprogrammed = saved.any { it.text == program.rewrite }
                GlassCard(
                    cornerRadius = 18.dp,
                    borderWidth = if (isReprogrammed) 1.dp else 0.dp,
                    borderColor = if (isReprogrammed) theme.accent.copy(0.45f) else Color.Transparent,
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
                        Text(
                            if (isReprogrammed) program.rewrite else program.limiting,
                            style = AppTypography.bodyLarge,
                            color = if (isReprogrammed) theme.accent.copy(0.9f) else Color.White.copy(0.85f),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            if (isReprogrammed) Icons.Outlined.CheckCircle else Icons.Outlined.ChevronRight,
                            contentDescription = null,
                            tint = if (isReprogrammed) theme.accent else Color.White.copy(0.35f)
                        )
                    }
                }
            }
            item { Spacer(Modifier.height(88.dp)) }
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
                Text(Translations.ui("oldProgram"), style = AppTypography.labelSmall,
                    color = Color.White.copy(0.35f))
                Spacer(Modifier.height(8.dp))
                Text(program.limiting, style = AppTypography.headingSmall.copy(fontFamily = PlayfairDisplay),
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
                Text(Translations.ui("yourNewProgram"), style = AppTypography.labelSmall,
                    color = theme.accent.copy(0.7f))
                Spacer(Modifier.height(8.dp))
                Text(program.rewrite, style = AppTypography.headingSmall.copy(fontFamily = PlayfairDisplay, fontSize = 20.sp),
                    color = Color.White, textAlign = TextAlign.Center, lineHeight = 28.sp)
            }
        }

        Spacer(Modifier.weight(1f))

        // Save button
        Box(modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 140.dp).fillMaxWidth()) {
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
                        if (isSaved) Translations.ui("savedToIdentity") else Translations.ui("saveToIdentity"),
                        style = AppTypography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        color = if (isSaved) theme.accent else Color.White
                    )
                }
            }
        }
    }
}
