package com.levelup.manifestation.ui.theme

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background

// ── GlassCard ─────────────────────────────────────────────────────────────────

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    borderWidth: Dp = 1.dp,
    borderColor: Color = Color.Transparent,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .clip(shape)
            .background(Color.White.copy(alpha = 0.07f))
            .border(borderWidth, borderColor, shape),
        content = content
    )
}

// ── GlassChip ─────────────────────────────────────────────────────────────────

@Composable
fun GlassChip(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    accentColor: Color = Color.White,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(
                if (isSelected) accentColor.copy(alpha = 0.18f)
                else Color.White.copy(alpha = 0.06f)
            )
            .border(
                1.dp,
                if (isSelected) accentColor.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.14f),
                CircleShape
            ),
        content = content
    )
}

// ── Pressable modifier ────────────────────────────────────────────────────────

@Composable
fun Modifier.pressable(scale: Float = 0.96f): Modifier {
    var pressed by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(
        targetValue = if (pressed) scale else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "pressScale"
    )
    return this
        .graphicsLayer {
            scaleX = animatedScale
            scaleY = animatedScale
        }
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    pressed = true
                    tryAwaitRelease()
                    pressed = false
                }
            )
        }
}
