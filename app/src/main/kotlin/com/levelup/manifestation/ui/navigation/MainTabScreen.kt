package com.levelup.manifestation.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.levelup.manifestation.ui.screens.affirmations.AffirmationsScreen
import com.levelup.manifestation.ui.screens.reprogram.ReprogramScreen
import com.levelup.manifestation.ui.screens.settings.SettingsSheet
import com.levelup.manifestation.ui.theme.AppTab
import com.levelup.manifestation.ui.theme.LocalToneTheme
import com.levelup.manifestation.ui.viewmodel.SavedProgramsViewModel
import com.levelup.manifestation.ui.viewmodel.ThemeViewModel
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FormatQuote
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTabScreen(
    themeViewModel: ThemeViewModel = hiltViewModel(),
    savedProgramsViewModel: SavedProgramsViewModel = hiltViewModel()
) {
    val tone = themeViewModel.tone.collectAsState().value
    val systemUiController = rememberSystemUiController()
    var selectedTab by remember { mutableStateOf<AppTab>(AppTab.Affirmations) }
    var showSettings by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    SideEffect {
        systemUiController.isStatusBarVisible = false
        systemUiController.isNavigationBarVisible = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Animated screen content
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier.fillMaxSize(),
            label = "tabContent"
        ) { tab ->
            when (tab) {
                is AppTab.Affirmations -> AffirmationsScreen(
                    savedProgramsViewModel = savedProgramsViewModel,
                    themeViewModel = themeViewModel
                )
                is AppTab.Reprogram -> ReprogramScreen(
                    savedProgramsViewModel = savedProgramsViewModel,
                    themeViewModel = themeViewModel
                )
            }
        }

        // Floating glass tab bar
        GlassTabBar(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            onSettings = { showSettings = true },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
        )

        // Settings bottom sheet
        if (showSettings) {
            ModalBottomSheet(
                onDismissRequest = { showSettings = false },
                sheetState = sheetState,
                containerColor = Color.Transparent,
                dragHandle = null
            ) {
                SettingsSheet(
                    themeViewModel = themeViewModel,
                    onDismiss = { showSettings = false }
                )
            }
        }
    }
}

@Composable
fun GlassTabBar(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalToneTheme.current
    val haptics = LocalHapticFeedback.current

    Row(
        modifier = modifier
            .shadow(24.dp, CircleShape, clip = false)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.25f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Affirmations tab
        TabButton(
            label = "Affirm",
            icon = { Icon(Icons.Outlined.FormatQuote, contentDescription = "Affirm", tint = if (selectedTab is AppTab.Affirmations) theme.accent else Color.White.copy(0.4f)) },
            isSelected = selectedTab is AppTab.Affirmations,
            accentColor = theme.accent,
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onTabSelected(AppTab.Affirmations)
            }
        )

        // Reprogram tab
        TabButton(
            label = "Reprogram",
            icon = { Icon(Icons.Outlined.Refresh, contentDescription = "Reprogram", tint = if (selectedTab is AppTab.Reprogram) theme.accent else Color.White.copy(0.4f)) },
            isSelected = selectedTab is AppTab.Reprogram,
            accentColor = theme.accent,
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onTabSelected(AppTab.Reprogram)
            }
        )

        // Divider
        Box(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .width(1.dp)
                .height(32.dp)
                .background(Color.White.copy(alpha = 0.12f))
        )

        // Settings button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .width(72.dp)
                .height(52.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onSettings
                )
        ) {
            Icon(
                Icons.Outlined.Settings,
                contentDescription = "Settings",
                tint = Color.White.copy(alpha = 0.4f)
            )
            Spacer(Modifier.height(4.dp))
            Text("Settings", fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f))
        }
    }
}

@Composable
private fun TabButton(
    label: String,
    icon: @Composable () -> Unit,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(),
        label = "tabScale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(88.dp)
            .height(52.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(CircleShape)
            .background(if (isSelected) accentColor.copy(alpha = 0.14f) else Color.Transparent)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            icon()
            Spacer(Modifier.height(4.dp))
            Text(
                label,
                fontSize = 10.sp,
                color = if (isSelected) accentColor else Color.White.copy(alpha = 0.4f)
            )
        }
    }
}
