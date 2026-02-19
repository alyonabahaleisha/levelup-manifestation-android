package com.levelup.manifestation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.levelup.manifestation.ui.navigation.MainTabScreen
import com.levelup.manifestation.ui.screens.splash.SplashScreen
import com.levelup.manifestation.ui.theme.LevelUpTheme
import com.levelup.manifestation.ui.viewmodel.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val tone by themeViewModel.tone.collectAsState()
            var showSplash by remember { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                delay(2400)
                showSplash = false
            }

            LevelUpTheme(tone = tone) {
                AnimatedVisibility(
                    visible = showSplash,
                    enter = fadeIn(),
                    exit = fadeOut(tween(500)),
                    modifier = Modifier.fillMaxSize()
                ) {
                    SplashScreen(modifier = Modifier.fillMaxSize())
                }
                AnimatedVisibility(
                    visible = !showSplash,
                    enter = fadeIn(tween(500)),
                    exit = fadeOut(),
                    modifier = Modifier.fillMaxSize()
                ) {
                    MainTabScreen(themeViewModel = themeViewModel)
                }
            }
        }
    }
}
