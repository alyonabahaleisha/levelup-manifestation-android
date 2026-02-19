package com.levelup.manifestation.ui.screens.splash

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.levelup.manifestation.ui.theme.ToneTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    val theme = ToneTheme.SoftFeminine
    var appeared by remember { mutableStateOf(false) }
    var logoVisible by remember { mutableStateOf(false) }
    var taglineVisible by remember { mutableStateOf(false) }

    // Symbol spring animation
    val symbolScale by animateFloatAsState(
        targetValue = if (appeared) 1f else 0.8f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMediumLow),
        label = "symbolScale"
    )
    val symbolAlpha by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(700),
        label = "symbolAlpha"
    )

    // Logo
    val logoAlpha by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0f,
        animationSpec = tween(700),
        label = "logoAlpha"
    )
    val logoScale by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0.8f,
        animationSpec = tween(700),
        label = "logoScale"
    )

    // Tagline
    val taglineAlpha by animateFloatAsState(
        targetValue = if (taglineVisible) 1f else 0f,
        animationSpec = tween(600),
        label = "taglineAlpha"
    )

    LaunchedEffect(Unit) {
        appeared = true
        delay(250)
        logoVisible = true
        delay(350)
        taglineVisible = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.linearGradient(theme.gradientColors)),
        contentAlignment = Alignment.Center
    ) {
        ParticlesView()

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.weight(1f))

            // ✦ Symbol
            Text(
                text = "✦",
                fontSize = 52.sp,
                fontWeight = FontWeight.ExtraLight,
                color = theme.accent,
                modifier = Modifier.graphicsLayer {
                    scaleX = symbolScale
                    scaleY = symbolScale
                    alpha = symbolAlpha
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // LevelUp
            Text(
                text = "LevelUp",
                fontSize = 44.sp,
                fontWeight = FontWeight.Thin,
                color = Color.White,
                letterSpacing = 10.sp,
                modifier = Modifier.graphicsLayer {
                    alpha = logoAlpha
                    scaleX = logoScale
                    scaleY = logoScale
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Tagline
            Text(
                text = "become who you are meant to be",
                fontSize = 13.sp,
                fontWeight = FontWeight.Light,
                color = Color.White.copy(alpha = 0.45f),
                letterSpacing = 2.sp,
                modifier = Modifier.graphicsLayer { alpha = taglineAlpha }
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
