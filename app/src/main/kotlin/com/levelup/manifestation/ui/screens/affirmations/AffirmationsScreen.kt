package com.levelup.manifestation.ui.screens.affirmations

import android.content.Intent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.levelup.manifestation.R
import com.levelup.manifestation.data.content.AffirmationContent
import com.levelup.manifestation.data.model.Affirmation
import com.levelup.manifestation.ui.theme.AppTypography
import com.levelup.manifestation.ui.theme.LifeArea
import com.levelup.manifestation.ui.theme.PlayfairDisplay
import com.levelup.manifestation.ui.viewmodel.ThemeViewModel

// Card background images and their dominant colors for screen background
private data class CardStyle(val imageRes: Int, val bgColor: Color)

private val cardStyles = listOf(
    CardStyle(R.drawable.card_bg_1, Color(0xFFF0E8E0)),  // soft peach
    CardStyle(R.drawable.card_bg_2, Color(0xFFEDE6E8)),  // blush pink
    CardStyle(R.drawable.card_bg_3, Color(0xFFE8E0F0)),  // lavender
    CardStyle(R.drawable.card_bg_4, Color(0xFFE8E0D8)),  // warm cream
    CardStyle(R.drawable.card_bg_5, Color(0xFFECE4E0)),  // rose cream
    CardStyle(R.drawable.card_bg_6, Color(0xFFF0E8EC)),  // soft pink
    CardStyle(R.drawable.card_bg_7, Color(0xFFE8E0E8)),  // light mauve
    CardStyle(R.drawable.card_bg_8, Color(0xFFE8E0F0)),  // pale lavender
    CardStyle(R.drawable.card_bg_9, Color(0xFFF0ECE8)),  // warm white
    CardStyle(R.drawable.card_bg_10, Color(0xFFECE4E8)), // soft rose
)

@Composable
fun AffirmationsScreen(
    themeViewModel: ThemeViewModel,
    deepLinkText: String? = null,
    onDeepLinkConsumed: () -> Unit = {}
) {
    val haptics = LocalHapticFeedback.current
    val context = LocalContext.current

    val affirmations = remember { AffirmationContent.feed() }
    val pagerState = rememberPagerState(pageCount = { affirmations.size.coerceAtLeast(1) })

    // Adaptive background color based on current card
    val currentStyle = remember(pagerState.currentPage) {
        if (affirmations.isNotEmpty()) cardStyles[pagerState.currentPage % cardStyles.size]
        else cardStyles[0]
    }
    val bgColor by animateColorAsState(
        targetValue = currentStyle.bgColor,
        animationSpec = tween(600),
        label = "bgColor"
    )

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

    Box(modifier = Modifier.fillMaxSize()) {
        // Fixed background — crossfades between card images
        Crossfade(
            targetState = currentStyle.imageRes,
            animationSpec = tween(800),
            label = "bgCrossfade"
        ) { imageRes ->
            Box(Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = 0.5f }
                )
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(bgColor.copy(alpha = 0.45f))
                )
            }
        }

        if (affirmations.isNotEmpty()) {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val affirmation = affirmations[page]
                val style = cardStyles[page % cardStyles.size]
                AffirmationCard(
                    affirmation = affirmation,
                    style = style,
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
    style: CardStyle,
    onShare: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    var liked by remember { mutableStateOf(false) }

    var appeared by remember { mutableStateOf(false) }
    val cardScale by animateFloatAsState(
        if (appeared) 1f else 0.92f,
        spring(stiffness = Spring.StiffnessMediumLow),
        label = "cardScale"
    )
    val cardAlpha by animateFloatAsState(
        if (appeared) 1f else 0f,
        tween(400),
        label = "cardAlpha"
    )

    LaunchedEffect(affirmation.id) {
        appeared = false
        appeared = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 80.dp, bottom = 120.dp),
            contentAlignment = Alignment.Center
        ) {
            // Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        scaleX = cardScale; scaleY = cardScale; alpha = cardAlpha
                    }
                    .clip(RoundedCornerShape(36.dp))
            ) {
                Image(
                    painter = painterResource(style.imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(480.dp)
                )

                // Radial frosted overlay — strong center, clear edges
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(480.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.65f),
                                    Color.White.copy(alpha = 0.50f),
                                    Color.White.copy(alpha = 0.15f),
                                    Color.Transparent
                                ),
                                radius = 600f
                            )
                        )
                )

                // Text + action icons inside card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(480.dp)
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
                        color = Color(0xFF2A2A3A),
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
                            tint = if (liked) Color(0xFFE06080) else Color(0xFF2A2A3A).copy(0.5f),
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
                            tint = Color(0xFF2A2A3A).copy(0.5f),
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
