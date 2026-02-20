package com.levelup.manifestation.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.sin
import kotlin.random.Random

private data class Star(
    var x: Float,
    var y: Float,
    val radius: Float,
    val brightness: Float,
    val phase: Float, // Random phase offset for twinkling
    val twinkleSpeed: Float, // Speed of twinkling
    val vx: Float, // Horizontal velocity
    val vy: Float  // Vertical velocity
)

@Composable
fun StarSkyView(
    modifier: Modifier = Modifier,
    starCount: Int = 150,
    starColor: Color = Color.White
) {
    val density = LocalDensity.current
    // Use a mutable state for the list to trigger recomposition on updates
    val starsState = remember { mutableStateOf<List<Star>>(emptyList()) }

    LaunchedEffect(Unit) {
        // Generate stars once with random velocities
        val initialStars = List(starCount) {
            Star(
                x = Random.nextFloat(), // 0..1 relative position
                y = Random.nextFloat(), // 0..1 relative position
                radius = Random.nextFloat() * 1.5f + 0.5f,
                brightness = Random.nextFloat() * 0.5f + 0.5f,
                phase = Random.nextFloat() * 6.28f,
                twinkleSpeed = Random.nextFloat() * 1.5f + 0.5f,
                // Slow drift: -0.01 to +0.01 per second approximately (adjusted in frame loop)
                vx = (Random.nextFloat() - 0.5f) * 0.005f, 
                vy = (Random.nextFloat() - 0.5f) * 0.005f
            )
        }
        starsState.value = initialStars

        var lastNanos = 0L
        while (true) {
            withFrameNanos { nanos ->
                if (lastNanos != 0L) {
                    val dt = (nanos - lastNanos) / 1_000_000_000f
                    // Update positions
                    val currentStars = starsState.value
                    // We modify the existing objects if possible or create copies. 
                    // Since Star is a data class, we can just update var properties if we kept them var. 
                    // Mutable list approach is better for performance than creating new list every frame.
                    // But Compose state needs a new object reference to trigger. 
                    // Let's iterate and update.
                    
                    for (star in currentStars) {
                        star.x += star.vx * dt * 10f // Scale up for visibility
                        star.y += star.vy * dt * 10f

                        // Wrap around
                        if (star.x < 0f) star.x += 1f
                        if (star.x > 1f) star.x -= 1f
                        if (star.y < 0f) star.y += 1f
                        if (star.y > 1f) star.y -= 1f
                    }
                    // Trigger recomposition by setting value (even if same list instance, 
                    // but compose checks equality. If we mutate elements, we might need a copy or forcing update).
                    // To be safe and correct in Compose, we should emit a new list or use a mutable state list.
                    // But creating a new list of 150 items every frame is also fine for this scale.
                    // Let's stick to updating vars and expecting redraw? 
                    // Compose might not redraw if the list reference hasn't changed. 
                    // Let's clone the list to be safe.
                    starsState.value = ArrayList(currentStars)
                }
                lastNanos = nanos
            }
        }
    }

    // Animation driver for twinkling effect
    val infiniteTransition = rememberInfiniteTransition(label = "StarTwinkle")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val stars = starsState.value

        stars.forEach { star ->
            val twinkle = (sin(time * star.twinkleSpeed + star.phase) + 1f) / 2f
            val currentAlpha = (0.3f + 0.7f * twinkle) * star.brightness

            drawCircle(
                color = starColor.copy(alpha = currentAlpha.coerceIn(0f, 1f)),
                radius = with(density) { star.radius.dp.toPx() },
                center = Offset(star.x * width, star.y * height)
            )
        }
    }
}
