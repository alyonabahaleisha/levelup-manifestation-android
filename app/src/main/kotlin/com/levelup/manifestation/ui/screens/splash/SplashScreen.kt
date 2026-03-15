package com.levelup.manifestation.ui.screens.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.levelup.manifestation.R
import com.levelup.manifestation.Translations
import com.levelup.manifestation.ui.theme.AppTypography
import com.levelup.manifestation.ui.theme.PlayfairDisplay
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    var logoVisible by remember { mutableStateOf(false) }
    var nameVisible by remember { mutableStateOf(false) }
    var taglineVisible by remember { mutableStateOf(false) }

    val logoAlpha by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0f,
        animationSpec = tween(800),
        label = "logoAlpha"
    )
    val logoScale by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0.85f,
        animationSpec = tween(800),
        label = "logoScale"
    )
    val nameAlpha by animateFloatAsState(
        targetValue = if (nameVisible) 1f else 0f,
        animationSpec = tween(700),
        label = "nameAlpha"
    )
    val taglineAlpha by animateFloatAsState(
        targetValue = if (taglineVisible) 1f else 0f,
        animationSpec = tween(600),
        label = "taglineAlpha"
    )

    LaunchedEffect(Unit) {
        delay(200)
        logoVisible = true
        delay(400)
        nameVisible = true
        delay(300)
        taglineVisible = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF154C6C)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Logo
            Image(
                painter = painterResource(R.drawable.logo_ageev),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .graphicsLayer {
                        alpha = logoAlpha
                        scaleX = logoScale
                        scaleY = logoScale
                    }
            )

            Spacer(Modifier.height(28.dp))

            // App name
            Text(
                text = stringResource(R.string.app_name),
                style = AppTypography.headingLarge.copy(
                    fontFamily = PlayfairDisplay,
                    letterSpacing = 1.sp
                ),
                color = Color.White,
                modifier = Modifier.graphicsLayer { alpha = nameAlpha }
            )

            Spacer(Modifier.height(12.dp))

            // Tagline
            Text(
                text = Translations.ui("splashTagline"),
                style = AppTypography.bodySmall,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.graphicsLayer { alpha = taglineAlpha }
            )
        }
    }
}
