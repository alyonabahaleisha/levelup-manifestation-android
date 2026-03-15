package com.levelup.manifestation.ui.screens.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.levelup.manifestation.R
import com.levelup.manifestation.Translations
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.levelup.manifestation.ui.theme.AppTypography
import com.levelup.manifestation.ui.theme.PlayfairDisplay
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    var logoVisible by remember { mutableStateOf(false) }
    var taglineVisible by remember { mutableStateOf(false) }

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
        delay(250)
        logoVisible = true
        delay(350)
        taglineVisible = true
    }

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.bg_home),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.height(20.dp))

            // App name
            Text(
                text = stringResource(R.string.app_name),
                style = AppTypography.headingLarge.copy(
                    fontFamily = PlayfairDisplay,
                    letterSpacing = 3.sp,
                ),
                color = Color.White,
                modifier = Modifier.graphicsLayer {
                    alpha = logoAlpha
                    scaleX = logoScale
                    scaleY = logoScale
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Tagline
            Text(
                text = Translations.ui("splashTagline"),
                style = AppTypography.labelSmall,
                color = Color.White.copy(alpha = 0.45f),
                modifier = Modifier.graphicsLayer { alpha = taglineAlpha }
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
