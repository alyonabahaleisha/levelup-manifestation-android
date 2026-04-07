package com.mikhail.manifestation.ui.screens.affirmations

// iOS Migration: -> AffirmationsView.swift — VerticalPager -> TabView(.vertical), animateColorAsState -> withAnimation, Crossfade -> .transition

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikhail.manifestation.data.content.AffirmationContent
import com.mikhail.manifestation.data.model.Affirmation
import com.mikhail.manifestation.ui.theme.AppTypography
import com.mikhail.manifestation.ui.theme.PlayfairDisplay
import com.mikhail.manifestation.ui.viewmodel.ThemeViewModel

private val cardBlue = Color(0xFF154C6C)

@Composable
fun AffirmationsScreen(
    themeViewModel: ThemeViewModel,
    startText: String? = null,
    deepLinkText: String? = null,
    onDeepLinkConsumed: () -> Unit = {}
) {
    val haptics = LocalHapticFeedback.current
    val context = LocalContext.current

    val affirmations = remember { AffirmationContent.feed() }
    val startPage = remember(startText) {
        if (startText != null) affirmations.indexOfFirst { it.text == startText }.coerceAtLeast(0)
        else 0
    }
    val pagerState = rememberPagerState(
        initialPage = startPage,
        pageCount = { affirmations.size.coerceAtLeast(1) }
    )

    val bgColor = cardBlue

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect {
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    // Deep link handling
    LaunchedEffect(deepLinkText) {
        if (deepLinkText != null) {
            val idx = affirmations.indexOfFirst { it.text == deepLinkText }
            if (idx >= 0) pagerState.animateScrollToPage(idx)
            onDeepLinkConsumed()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(bgColor)) {
        if (affirmations.isNotEmpty()) {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val affirmation = affirmations[page]
                AffirmationCard(
                    affirmation = affirmation,
                    onShare = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, affirmation.text)
                        }
                        context.startActivity(Intent.createChooser(intent, null))
                    }
                )
            }
        }
    }
}

@Composable
private fun AffirmationCard(
    affirmation: Affirmation,
    onShare: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    var liked by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 80.dp, bottom = 120.dp),
            contentAlignment = Alignment.Center
        ) {
            // Card — solid blue
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(480.dp)
                    .clip(RoundedCornerShape(36.dp))
                    .background(cardBlue.copy(alpha = 0.85f))
            ) {
                // Text + action icons inside card
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 28.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.weight(1f))

                    val len = affirmation.text.length
                    val fontSize = when {
                        len < 60 -> 26.sp
                        len < 120 -> 22.sp
                        len < 200 -> 18.sp
                        else -> 16.sp
                    }
                    val lineHeight = when {
                        len < 60 -> 36.sp
                        len < 120 -> 32.sp
                        len < 200 -> 26.sp
                        else -> 24.sp
                    }

                    Text(
                        text = affirmation.text,
                        fontFamily = PlayfairDisplay,
                        fontSize = fontSize,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = lineHeight
                    )

                    Spacer(Modifier.weight(1f))

                    // Like & Share — inside card, bottom
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 20.dp)
                    ) {
                        Icon(
                            Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (liked) Color(0xFFE06080) else Color.White.copy(0.5f),
                            modifier = Modifier
                                .size(28.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    liked = !liked
                                }
                        )
                        Spacer(Modifier.width(32.dp))
                        Icon(
                            Icons.Outlined.Share,
                            contentDescription = "Share",
                            tint = Color.White.copy(0.5f),
                            modifier = Modifier
                                .size(28.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onShare()
                                }
                        )
                    }
                }
            }
        }
    }
}
