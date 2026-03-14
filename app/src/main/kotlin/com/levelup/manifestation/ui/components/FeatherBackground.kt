package com.levelup.manifestation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import androidx.compose.runtime.withFrameNanos
import kotlinx.coroutines.isActive

private class Feather(
    var x: Float,        // 0..1 relative
    var y: Float,        // 0..1 relative
    var rotation: Float, // degrees
    val size: Float,     // dp multiplier (0.6..1.4)
    var alpha: Float,    // current opacity
    val maxAlpha: Float, // peak opacity (0.08..0.25)
    val driftX: Float,   // horizontal drift speed
    val driftY: Float,   // downward drift speed
    val rotSpeed: Float, // rotation speed deg/s
    val swayAmp: Float,  // horizontal sway amplitude
    val swayFreq: Float, // sway frequency
    var age: Float,      // seconds alive
    val lifespan: Float, // total lifespan seconds
    val phase: Float     // random phase for sway
)

private fun spawnFeather(rng: Random): Feather {
    return Feather(
        x = rng.nextFloat(),
        y = -0.05f - rng.nextFloat() * 0.15f, // start above screen
        rotation = rng.nextFloat() * 360f,
        size = 0.6f + rng.nextFloat() * 0.8f,
        alpha = 0f,
        maxAlpha = 0.08f + rng.nextFloat() * 0.17f,
        driftX = (rng.nextFloat() - 0.5f) * 0.012f,
        driftY = 0.015f + rng.nextFloat() * 0.025f,
        rotSpeed = (rng.nextFloat() - 0.5f) * 30f,
        swayAmp = 0.01f + rng.nextFloat() * 0.03f,
        swayFreq = 0.3f + rng.nextFloat() * 0.7f,
        age = 0f,
        lifespan = 12f + rng.nextFloat() * 18f,
        phase = rng.nextFloat() * 6.28f
    )
}

@Composable
fun FeatherBackground(
    modifier: Modifier = Modifier,
    featherCount: Int = 12,
    featherColor: Color = Color.White
) {
    val density = LocalDensity.current
    val feathersState = remember { mutableStateOf<List<Feather>>(emptyList()) }

    LaunchedEffect(Unit) {
        val rng = Random(System.nanoTime())
        // Seed initial feathers spread across the screen
        val initial = List(featherCount) {
            spawnFeather(rng).also { f ->
                f.y = rng.nextFloat() // spread vertically
                f.age = rng.nextFloat() * f.lifespan * 0.5f // stagger ages
                val lifeFrac = f.age / f.lifespan
                f.alpha = when {
                    lifeFrac < 0.15f -> f.maxAlpha * (lifeFrac / 0.15f)
                    lifeFrac > 0.8f -> f.maxAlpha * (1f - (lifeFrac - 0.8f) / 0.2f)
                    else -> f.maxAlpha
                }
            }
        }
        feathersState.value = initial

        var lastNanos = 0L
        while (isActive) {
            withFrameNanos { nanos ->
                if (lastNanos != 0L) {
                    val dt = (nanos - lastNanos) / 1_000_000_000f
                    val feathers = feathersState.value.toMutableList()

                    val iterator = feathers.listIterator()
                    while (iterator.hasNext()) {
                        val f = iterator.next()
                        f.age += dt

                        if (f.age >= f.lifespan || f.y > 1.15f) {
                            // Respawn
                            iterator.set(spawnFeather(rng))
                            continue
                        }

                        // Drift
                        val sway = sin(f.age * f.swayFreq * 6.28f + f.phase) * f.swayAmp
                        f.x += (f.driftX + sway) * dt
                        f.y += f.driftY * dt
                        f.rotation += f.rotSpeed * dt

                        // Wrap horizontal
                        if (f.x < -0.1f) f.x += 1.2f
                        if (f.x > 1.1f) f.x -= 1.2f

                        // Fade in/out lifecycle
                        val lifeFrac = f.age / f.lifespan
                        f.alpha = when {
                            lifeFrac < 0.15f -> f.maxAlpha * (lifeFrac / 0.15f)
                            lifeFrac > 0.8f -> f.maxAlpha * (1f - (lifeFrac - 0.8f) / 0.2f)
                            else -> f.maxAlpha
                        }.coerceIn(0f, 1f)
                    }

                    feathersState.value = ArrayList(feathers)
                }
                lastNanos = nanos
            }
        }
    }

    // Pre-compute feather path dimensions in dp
    val featherLengthPx = with(density) { 48.dp.toPx() }
    val featherWidthPx = with(density) { 12.dp.toPx() }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        feathersState.value.forEach { feather ->
            if (feather.alpha <= 0.001f) return@forEach

            val cx = feather.x * w
            val cy = feather.y * h
            val scaledLength = featherLengthPx * feather.size
            val scaledWidth = featherWidthPx * feather.size

            translate(left = cx, top = cy) {
                rotate(degrees = feather.rotation, pivot = Offset.Zero) {
                    drawFeatherShape(
                        length = scaledLength,
                        width = scaledWidth,
                        color = featherColor,
                        alpha = feather.alpha
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawFeatherShape(
    length: Float,
    width: Float,
    color: Color,
    alpha: Float
) {
    val halfLen = length / 2f
    val halfW = width / 2f

    // Central spine (rachis)
    val spinePath = Path().apply {
        moveTo(0f, -halfLen)
        lineTo(0f, halfLen)
    }
    drawPath(
        path = spinePath,
        color = color.copy(alpha = alpha * 0.6f),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 0.8f)
    )

    // Left vane — asymmetric, slightly narrower
    val leftVane = Path().apply {
        moveTo(0f, -halfLen)
        cubicTo(
            -halfW * 0.7f, -halfLen * 0.4f,
            -halfW, halfLen * 0.1f,
            0f, halfLen
        )
    }
    drawPath(
        path = leftVane,
        color = color.copy(alpha = alpha * 0.4f)
    )

    // Right vane — slightly wider
    val rightVane = Path().apply {
        moveTo(0f, -halfLen)
        cubicTo(
            halfW * 0.9f, -halfLen * 0.35f,
            halfW * 1.1f, halfLen * 0.15f,
            0f, halfLen
        )
    }
    drawPath(
        path = rightVane,
        color = color.copy(alpha = alpha * 0.35f)
    )
}
