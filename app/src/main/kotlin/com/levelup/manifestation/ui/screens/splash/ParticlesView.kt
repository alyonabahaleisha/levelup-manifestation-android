package com.levelup.manifestation.ui.screens.splash

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.random.Random

private data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var alpha: Float,
    var radius: Float,
    var life: Float,      // 0..1 progress
    var lifeSpeed: Float  // how fast life ticks
)

@Composable
fun ParticlesView(modifier: Modifier = Modifier) {
    val particles = remember { mutableStateOf<List<Particle>>(emptyList()) }
    var lastNanos = remember { 0L }

    LaunchedEffect(Unit) {
        val pool = mutableListOf<Particle>()
        while (true) {
            withFrameNanos { nanos ->
                val dt = if (lastNanos == 0L) 0f else (nanos - lastNanos) / 1_000_000_000f
                lastNanos = nanos

                // Spawn ~3 particles per frame
                repeat(3) {
                    pool.add(
                        Particle(
                            x = Random.nextFloat(),
                            y = Random.nextFloat() * 1.1f + 0.1f, // start slightly below visible
                            vx = (Random.nextFloat() - 0.5f) * 0.015f,
                            vy = -(Random.nextFloat() * 0.04f + 0.01f), // drift upward
                            alpha = 0f,
                            radius = Random.nextFloat() * 1.8f + 0.6f,
                            life = 0f,
                            lifeSpeed = Random.nextFloat() * 0.08f + 0.04f
                        )
                    )
                }

                // Update
                val iter = pool.iterator()
                while (iter.hasNext()) {
                    val p = iter.next()
                    p.life += lifeSpeed(p) * dt * 60f
                    p.x += p.vx * dt * 60f
                    p.y += p.vy * dt * 60f
                    // Fade in first 30%, fade out last 30%
                    p.alpha = when {
                        p.life < 0.3f -> p.life / 0.3f
                        p.life > 0.7f -> 1f - (p.life - 0.7f) / 0.3f
                        else -> 1f
                    } * 0.6f
                    if (p.life >= 1f) iter.remove()
                }

                // Keep reasonable cap
                while (pool.size > 250) pool.removeAt(0)

                particles.value = pool.toList()
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        for (p in particles.value) {
            drawCircle(
                color = Color.White.copy(alpha = p.alpha),
                radius = p.radius,
                center = androidx.compose.ui.geometry.Offset(p.x * w, p.y * h),
                blendMode = BlendMode.Plus
            )
        }
    }
}

private fun lifeSpeed(p: Particle) = p.lifeSpeed
