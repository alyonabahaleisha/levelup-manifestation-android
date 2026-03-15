package com.levelup.manifestation.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FormatQuote
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.levelup.manifestation.Translations
import com.levelup.manifestation.ui.screens.affirmations.AffirmationsScreen
import com.levelup.manifestation.ui.screens.home.HomeScreen
import com.levelup.manifestation.ui.screens.meditations.MeditationsScreen
import com.levelup.manifestation.ui.screens.reprogram.ReprogramScreen
import com.levelup.manifestation.ui.screens.settings.SettingsSheet
import com.levelup.manifestation.ui.theme.AppTab
import com.levelup.manifestation.ui.theme.AppTypography
import com.levelup.manifestation.ui.theme.LocalToneTheme
import com.levelup.manifestation.ui.viewmodel.MeditationViewModel
import com.levelup.manifestation.ui.viewmodel.SavedProgramsViewModel
import com.levelup.manifestation.ui.viewmodel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTabScreen(
    themeViewModel: ThemeViewModel = hiltViewModel(),
    savedProgramsViewModel: SavedProgramsViewModel = hiltViewModel(),
    meditationViewModel: MeditationViewModel = hiltViewModel(),
    openAffirmations: Boolean = false,
    onAffirmationsOpened: () -> Unit = {},
    deepLinkAffirmation: String? = null,
    onAffirmationDeepLinked: () -> Unit = {}
) {
    val tone = themeViewModel.tone.collectAsState().value
    val theme = LocalToneTheme.current
    val systemUiController = rememberSystemUiController()
    val haptics = LocalHapticFeedback.current
    var selectedTab by remember { mutableStateOf<AppTab>(AppTab.Home) }
    var showSettings by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(openAffirmations) {
        if (openAffirmations) {
            selectedTab = AppTab.Affirmations
            onAffirmationsOpened()
        }
    }

    SideEffect {
        systemUiController.isStatusBarVisible = false
        systemUiController.isNavigationBarVisible = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Screen content
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier.fillMaxSize(),
            label = "tabContent"
        ) { tab ->
            when (tab) {
                is AppTab.Home -> HomeScreen(
                    savedProgramsViewModel = savedProgramsViewModel,
                    meditationViewModel = meditationViewModel,
                    onNavigateToAffirmations = { selectedTab = AppTab.Affirmations },
                    onNavigateToReprogram = { selectedTab = AppTab.Reprogram },
                    onNavigateToMeditations = { selectedTab = AppTab.Meditations }
                )
                is AppTab.Affirmations -> AffirmationsScreen(
                    themeViewModel = themeViewModel,
                    deepLinkText = deepLinkAffirmation,
                    onDeepLinkConsumed = onAffirmationDeepLinked
                )
                is AppTab.Reprogram -> ReprogramScreen(
                    savedProgramsViewModel = savedProgramsViewModel,
                    themeViewModel = themeViewModel
                )
                is AppTab.Meditations -> MeditationsScreen(
                    viewModel = meditationViewModel
                )
            }
        }

        // Bottom navigation bar
        NavigationBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .drawBehind {
                    drawLine(
                        color = Color.White.copy(alpha = 0.10f),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 1.dp.toPx()
                    )
                },
            containerColor = Color(0xCC000000),
            tonalElevation = 0.dp
        ) {
            val itemColors = NavigationBarItemDefaults.colors(
                selectedIconColor = theme.accent,
                selectedTextColor = theme.accent,
                indicatorColor = theme.accent.copy(alpha = 0.15f),
                unselectedIconColor = Color.White.copy(alpha = 0.40f),
                unselectedTextColor = Color.White.copy(alpha = 0.40f)
            )

            NavigationBarItem(
                selected = selectedTab is AppTab.Home,
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    selectedTab = AppTab.Home
                },
                icon = { Icon(Icons.Outlined.FavoriteBorder, contentDescription = null) },
                label = { Text(Translations.ui("homeTab"), style = AppTypography.tabLabel) },
                colors = itemColors
            )

            NavigationBarItem(
                selected = selectedTab is AppTab.Affirmations,
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    selectedTab = AppTab.Affirmations
                },
                icon = { Icon(Icons.Outlined.FormatQuote, contentDescription = null) },
                label = { Text(Translations.ui("affirmationsTab"), style = AppTypography.tabLabel) },
                colors = itemColors
            )

            NavigationBarItem(
                selected = selectedTab is AppTab.Reprogram,
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    selectedTab = AppTab.Reprogram
                },
                icon = { Icon(Icons.Outlined.Refresh, contentDescription = null) },
                label = { Text(Translations.ui("reprogramTab"), style = AppTypography.tabLabel) },
                colors = itemColors
            )

            NavigationBarItem(
                selected = selectedTab is AppTab.Meditations,
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    selectedTab = AppTab.Meditations
                },
                icon = { Icon(Icons.Outlined.Headphones, contentDescription = null) },
                label = { Text(Translations.ui("meditationsTab"), style = AppTypography.tabLabel) },
                colors = itemColors
            )

            NavigationBarItem(
                selected = false,
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    showSettings = true
                },
                icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                label = { Text(Translations.ui("settingsTitle"), style = AppTypography.tabLabel) },
                colors = itemColors
            )
        }

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
