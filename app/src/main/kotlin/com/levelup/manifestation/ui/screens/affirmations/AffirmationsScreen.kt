package com.levelup.manifestation.ui.screens.affirmations

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.levelup.manifestation.ui.theme.AppTypography
import com.levelup.manifestation.ui.theme.Manrope
import com.levelup.manifestation.ui.theme.PlayfairDisplay
import androidx.datastore.preferences.core.edit
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import com.levelup.manifestation.Translations
import com.levelup.manifestation.data.content.AffirmationContent
import com.levelup.manifestation.data.model.Affirmation
import com.levelup.manifestation.data.store.PrefsKeys
import com.levelup.manifestation.data.store.dataStore
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.levelup.manifestation.R
import com.levelup.manifestation.ui.theme.GlassCard
import com.levelup.manifestation.ui.theme.GlassChip
import com.levelup.manifestation.ui.theme.LifeArea
import com.levelup.manifestation.ui.theme.LocalToneTheme
import com.levelup.manifestation.ui.viewmodel.NotificationViewModel
import com.levelup.manifestation.ui.viewmodel.SavedProgramsViewModel
import com.levelup.manifestation.ui.viewmodel.ThemeViewModel

// Feed item types
private sealed class FeedItem {
    data class AffirmationItem(val affirmation: Affirmation) : FeedItem()
    object NotifPromoItem : FeedItem()
}

@Composable
fun AffirmationsScreen(
    themeViewModel: ThemeViewModel,
    notifViewModel: NotificationViewModel = hiltViewModel(),
    deepLinkText: String? = null,
    onDeepLinkConsumed: () -> Unit = {}
) {
    val theme = LocalToneTheme.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val notifSettings by notifViewModel.settings.collectAsState()
    var selectedArea by remember { mutableStateOf<LifeArea?>(null) }

    // Scroll hint — shown only on first ever visit
    val scrollHintShown by remember {
        context.dataStore.data.map { it[PrefsKeys.SCROLL_HINT_SHOWN] ?: false }
    }.collectAsState(initial = true) // default true avoids flash; real value loads instantly
    var showScrollHint by remember { mutableStateOf(false) }

    LaunchedEffect(scrollHintShown) {
        if (!scrollHintShown) {
            showScrollHint = true
            delay(3500)
            showScrollHint = false
            scope.launch { context.dataStore.edit { it[PrefsKeys.SCROLL_HINT_SHOWN] = true } }
        }
    }

    val affirmations = remember(selectedArea) {
        buildFeed(selectedArea)
    }

    val feedItems = remember(affirmations, notifSettings.isEnabled) {
        buildFeedWithPromo(affirmations, showPromo = !notifSettings.isEnabled)
    }

    val pagerState = rememberPagerState(pageCount = { feedItems.size.coerceAtLeast(1) })
    val haptics = LocalHapticFeedback.current

    // Permission launcher for the promo card "Enable" button
    val notifPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) notifViewModel.enable() }

    fun onEnableNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            notifViewModel.enable()
        }
    }

    // Deep link: store pending text locally so we can scroll after feed rebuilds
    var pendingDeepLink by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(deepLinkText) {
        if (deepLinkText != null) {
            pendingDeepLink = deepLinkText
            selectedArea = null
            onDeepLinkConsumed()
        }
    }

    val currentFeedItems by rememberUpdatedState(feedItems)
    LaunchedEffect(pendingDeepLink, feedItems) {
        val text = pendingDeepLink ?: return@LaunchedEffect
        val idx = currentFeedItems.indexOfFirst {
            it is FeedItem.AffirmationItem && it.affirmation.text == text
        }
        if (idx >= 0) {
            pagerState.animateScrollToPage(idx)
            pendingDeepLink = null
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect {
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            // Dismiss hint on first manual scroll
            if (showScrollHint) {
                showScrollHint = false
                scope.launch { context.dataStore.edit { it[PrefsKeys.SCROLL_HINT_SHOWN] = true } }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (feedItems.isEmpty()) {
            Image(
                painter = painterResource(R.drawable.bg_affirmations),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (val item = feedItems[page]) {
                    is FeedItem.AffirmationItem -> AffirmationCard(affirmation = item.affirmation)
                    FeedItem.NotifPromoItem -> NotificationPromoCard(
                        onEnableClick = { onEnableNotifications() }
                    )
                }
            }
        }

        // First-time scroll hint
        if (showScrollHint) {
            ScrollHint(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 120.dp))
        }

    }
}

private fun buildFeed(area: LifeArea?): List<Affirmation> =
    AffirmationContent.feed(if (area == null) emptyList() else listOf(area))

// Insert a promo card after every 2 affirmations (so it's every 3rd card)
private fun buildFeedWithPromo(
    affirmations: List<Affirmation>,
    showPromo: Boolean
): List<FeedItem> {
    if (!showPromo || affirmations.isEmpty()) {
        return affirmations.map { FeedItem.AffirmationItem(it) }
    }
    val result = mutableListOf<FeedItem>()
    affirmations.forEachIndexed { index, affirmation ->
        result.add(FeedItem.AffirmationItem(affirmation))
        if ((index + 1) % 2 == 0) result.add(FeedItem.NotifPromoItem)
    }
    return result
}

@Composable
private fun NotificationPromoCard(onEnableClick: () -> Unit) {
    val theme = LocalToneTheme.current
    var appeared by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessMediumLow),
        label = "promoAlpha"
    )
    val offset by animateFloatAsState(
        targetValue = if (appeared) 0f else 24f,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessMediumLow),
        label = "promoOffset"
    )

    LaunchedEffect(Unit) {
        appeared = false
        appeared = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_affirmations),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 88.dp),
            contentAlignment = Alignment.Center
        ) {
            GlassCard(
                cornerRadius = 28.dp,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .graphicsLayer { translationY = offset; this.alpha = alpha }
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        text = Translations.ui("notifPromoTitle"),
                        style = AppTypography.headingMedium,
                        color = Color(0xFF2A2A3A),
                        textAlign = TextAlign.Center,
                        lineHeight = 30.sp
                    )
                    Text(
                        text = Translations.ui("notifPromoBody"),
                        style = AppTypography.bodyMedium,
                        color = Color(0xFF5A5070),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    GlassChip(
                        isSelected = true,
                        accentColor = theme.accent,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onEnableClick
                        )
                    ) {
                        Text(
                            text = Translations.ui("notifPromoButton"),
                            style = AppTypography.labelLarge,
                            color = theme.accent,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }
    }
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
        Image(
            painter = painterResource(R.drawable.bg_affirmations),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 88.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val len = affirmation.text.length
                val fontSize = when {
                    len < 60  -> 32.sp
                    len < 120 -> 26.sp
                    len < 200 -> 21.sp
                    else      -> 17.sp
                }
                val lineHeight = when {
                    len < 60  -> 42.sp
                    len < 120 -> 36.sp
                    len < 200 -> 30.sp
                    else      -> 26.sp
                }

                GlassCard(
                    cornerRadius = 28.dp,
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .graphicsLayer {
                            translationY = cardOffset
                            alpha = cardAlpha
                        }
                ) {
                    Text(
                        text = affirmation.text,
                        fontFamily = PlayfairDisplay,
                        fontSize = fontSize,
                        color = Color(0xFF2A2A3A),
                        textAlign = TextAlign.Center,
                        lineHeight = lineHeight,
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 40.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ScrollHint(modifier: Modifier = Modifier) {
    val theme = LocalToneTheme.current
    val offsetAnim = remember { Animatable(0f) }
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alphaAnim.animateTo(1f, tween(400))
        offsetAnim.animateTo(
            targetValue = -12f,
            animationSpec = infiniteRepeatable(
                animation = tween(600),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Column(
        modifier = modifier.graphicsLayer { alpha = alphaAnim.value },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "↑",
            style = AppTypography.headingLarge,
            color = theme.accent.copy(alpha = 0.8f),
            modifier = Modifier.graphicsLayer { translationY = offsetAnim.value }
        )
        Text(
            text = Translations.ui("scrollHint"),
            style = AppTypography.caption.copy(letterSpacing = 1.sp),
            color = Color.White.copy(alpha = 0.5f),
        )
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
            style = AppTypography.labelMedium,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}
