package com.mikhail.manifestation.ui.navigation

// iOS Migration: -> ContentView.swift — NavigationBar -> TabView, NavigationBarItem -> .tabItem { Label }, AnimatedContent -> .transition

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mikhail.manifestation.Translations
import com.mikhail.manifestation.data.audio.ContentType
import com.mikhail.manifestation.data.audio.PlaybackState
import com.mikhail.manifestation.ui.screens.affirmations.AffirmationsScreen
import com.mikhail.manifestation.ui.screens.home.ClubsMapScreen
import com.mikhail.manifestation.ui.screens.home.HomeScreen
import com.mikhail.manifestation.data.model.Meditation
import com.mikhail.manifestation.data.content.MeditationContent
import com.mikhail.manifestation.ui.screens.meditations.MeditationPlayerScreen
import com.mikhail.manifestation.ui.screens.meditations.MeditationsScreen
import com.mikhail.manifestation.ui.screens.meditations.meditationCardImage
import com.mikhail.manifestation.ui.screens.meditations.meditationHasGif
import com.mikhail.manifestation.ui.screens.music.MusicScreen
import com.mikhail.manifestation.ui.screens.reprogram.ReprogramScreen
import com.mikhail.manifestation.ui.screens.settings.SettingsSheet
import com.mikhail.manifestation.ui.theme.AppTab
import com.mikhail.manifestation.ui.theme.AppTypography
import com.mikhail.manifestation.ui.theme.LocalToneTheme
import com.mikhail.manifestation.ui.viewmodel.ClubsViewModel
import com.mikhail.manifestation.ui.viewmodel.MeditationViewModel
import com.mikhail.manifestation.ui.viewmodel.MusicViewModel
import com.mikhail.manifestation.ui.viewmodel.SavedProgramsViewModel
import com.mikhail.manifestation.ui.viewmodel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTabScreen(
    themeViewModel: ThemeViewModel = hiltViewModel(),
    savedProgramsViewModel: SavedProgramsViewModel = hiltViewModel(),
    meditationViewModel: MeditationViewModel = hiltViewModel(),
    musicViewModel: MusicViewModel = hiltViewModel(),
    clubsViewModel: ClubsViewModel = hiltViewModel(),
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
    var showAffirmations by remember { mutableStateOf(false) }
    var showClubsMap by remember { mutableStateOf(false) }
    var affirmationStartText by remember { mutableStateOf<String?>(null) }
    var homeMeditation by remember { mutableStateOf<Meditation?>(null) }
    var isMeditationPlayerOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val currentTitle by meditationViewModel.currentTitle.collectAsState()
    val contentType by meditationViewModel.contentType.collectAsState()
    val playbackState by meditationViewModel.playbackState.collectAsState()
    val currentPosition by meditationViewModel.currentPosition.collectAsState()
    val audioDuration by meditationViewModel.duration.collectAsState()
    val currentId by meditationViewModel.currentMeditationId.collectAsState()
    val currentCoverUrl by meditationViewModel.currentCoverUrl.collectAsState()

    LaunchedEffect(openAffirmations) {
        if (openAffirmations) {
            showAffirmations = true
            onAffirmationsOpened()
        }
    }

    SideEffect {
        systemUiController.isStatusBarVisible = false
        systemUiController.isNavigationBarVisible = !isMeditationPlayerOpen
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
                    clubsViewModel = clubsViewModel,
                    onNavigateToAffirmations = { text ->
                        affirmationStartText = text
                        selectedTab = AppTab.Affirmations
                    },
                    onNavigateToReprogram = { selectedTab = AppTab.Reprogram },
                    onNavigateToMeditations = { selectedTab = AppTab.Meditations },
                    onExpandClubsMap = { showClubsMap = true },
                    onMeditationTapped = { meditation ->
                        homeMeditation = meditation
                        isMeditationPlayerOpen = true
                    },
                    onOpenSettings = { showSettings = true }
                )
                is AppTab.Affirmations -> AffirmationsScreen(
                    themeViewModel = themeViewModel,
                    startText = affirmationStartText,
                    deepLinkText = deepLinkAffirmation,
                    onDeepLinkConsumed = onAffirmationDeepLinked
                )
                is AppTab.Reprogram -> ReprogramScreen(
                    savedProgramsViewModel = savedProgramsViewModel,
                    themeViewModel = themeViewModel
                )
                is AppTab.Meditations -> {
                    MeditationsScreen(
                        viewModel = meditationViewModel,
                        onPlayerVisibilityChanged = { isMeditationPlayerOpen = it }
                    )
                }
                is AppTab.Music -> MusicScreen(
                    viewModel = musicViewModel
                )
            }
        }

        // Bottom navigation bar + mini-player — hidden during meditation player
        val showMiniPlayer = currentTitle != null &&
            playbackState != PlaybackState.Idle &&
            homeMeditation == null &&
            !isMeditationPlayerOpen

        if (!isMeditationPlayerOpen) {
            Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                AnimatedVisibility(
                    visible = showMiniPlayer,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    MiniPlayerBar(
                        title = currentTitle ?: "",
                        contentType = contentType ?: ContentType.Meditation,
                        isPlaying = playbackState == PlaybackState.Playing,
                        isBuffering = playbackState == PlaybackState.Buffering,
                        progress = if (audioDuration > 0) (currentPosition.toFloat() / audioDuration.toFloat()).coerceIn(0f, 1f) else 0f,
                        coverUrl = currentCoverUrl,
                        onTogglePlayPause = { meditationViewModel.togglePlayPause() },
                        onDismiss = { meditationViewModel.stop() },
                        onClick = {
                            when (contentType) {
                                ContentType.Music -> selectedTab = AppTab.Music
                                else -> {
                                    val med = meditationViewModel.allMeditations().find { it.id == currentId }
                                    if (med != null) {
                                        homeMeditation = med
                                        isMeditationPlayerOpen = true
                                    }
                                }
                            }
                        }
                    )
                }

            // Glass tab bar matching iOS
            val tabCount = 5
            val tabs = remember {
                listOf(
                    AppTab.Home to "homeTab",
                    AppTab.Affirmations to "affirmationsTab",
                    AppTab.Meditations to "meditationsTab",
                    AppTab.Music to "musicTab",
                    AppTab.Reprogram to "reprogramTab"
                )
            }
            val selectedIndex = tabs.indexOfFirst { it.first == selectedTab }.coerceAtLeast(0)

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .border(
                        1.dp,
                        Color.White.copy(alpha = 0.15f),
                        RoundedCornerShape(28.dp)
                    )
                    .background(
                        Color(0xFF0D3347),
                        RoundedCornerShape(28.dp)
                    )
                    .clip(RoundedCornerShape(28.dp))
                    .padding(vertical = 6.dp)
            ) {
                val barInnerWidth = maxWidth
                val tabWidth = barInnerWidth / tabCount
                val indicatorPadding = 4.dp
                val indicatorWidth = tabWidth - indicatorPadding * 2

                // Animated sliding indicator
                val animatedOffset by animateDpAsState(
                    targetValue = tabWidth * selectedIndex + indicatorPadding,
                    animationSpec = spring(
                        dampingRatio = 0.75f,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    label = "tabIndicator"
                )

                Row(
                    modifier = Modifier.fillMaxWidth().drawBehind {
                        drawRoundRect(
                            color = Color.White.copy(alpha = 0.10f),
                            topLeft = Offset(animatedOffset.toPx(), 0f),
                            size = androidx.compose.ui.geometry.Size(indicatorWidth.toPx(), size.height),
                            cornerRadius = CornerRadius(22.dp.toPx(), 22.dp.toPx())
                        )
                    },
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tabs.forEachIndexed { index, (tab, labelKey) ->
                        val selected = selectedTab == tab
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .semantics {
                                    role = Role.Tab
                                    this.selected = selected
                                    contentDescription = Translations.ui(labelKey)
                                }
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    selectedTab = tab
                                }
                                .padding(vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val iconColor = if (selected) Color.White else Color.White.copy(alpha = 0.4f)
                            when (index) {
                                0 -> Icon(if (selected) Icons.Filled.LightMode else Icons.Outlined.LightMode, contentDescription = null, modifier = Modifier.size(26.dp), tint = iconColor)
                                1 -> Icon(if (selected) Icons.Filled.ChatBubble else Icons.Outlined.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(26.dp), tint = iconColor)
                                2 -> Icon(if (selected) Icons.Filled.Bedtime else Icons.Outlined.Bedtime, contentDescription = null, modifier = Modifier.size(26.dp), tint = iconColor)
                                3 -> Icon(if (selected) Icons.Filled.MusicNote else Icons.Outlined.MusicNote, contentDescription = null, modifier = Modifier.size(26.dp), tint = iconColor)
                                4 -> Icon(if (selected) Icons.Filled.Psychology else Icons.Outlined.Psychology, contentDescription = null, modifier = Modifier.size(26.dp), tint = iconColor)
                            }
                            Spacer(Modifier.height(3.dp))
                            Text(
                                Translations.ui(labelKey),
                                style = AppTypography.tabLabel,
                                color = if (selected) Color.White else Color.White.copy(alpha = 0.4f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
            }
        }

        // Meditation player overlay (from Home tap)
        val homeMediation = homeMeditation
        if (homeMediation != null) {
            MeditationPlayerScreen(
                meditation = homeMediation,
                viewModel = meditationViewModel,
                backgroundImageRes = meditationCardImage(homeMediation.id, 0),
                isGif = meditationHasGif(homeMediation.id),
                coverUrl = MeditationContent.coverUrl(homeMediation),
                onBack = {
                    homeMeditation = null
                    isMeditationPlayerOpen = false
                }
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

        // Affirmations bottom sheet
        if (showAffirmations) {
            ModalBottomSheet(
                onDismissRequest = { showAffirmations = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = Color.Transparent,
                dragHandle = null
            ) {
                AffirmationsScreen(
                    themeViewModel = themeViewModel,
                    startText = affirmationStartText,
                    deepLinkText = deepLinkAffirmation,
                    onDeepLinkConsumed = onAffirmationDeepLinked
                )
            }
        }

        // Full-screen clubs map overlay — slides up over everything including the nav bar
        AnimatedVisibility(
            visible = showClubsMap,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.fillMaxSize()
        ) {
            val clubsState by clubsViewModel.uiState.collectAsState()
            val clubs = when (val s = clubsState) {
                is com.mikhail.manifestation.ui.viewmodel.ClubsUiState.Success -> s.clubs
                else -> emptyList()
            }
            val masterUrl = when (val s = clubsState) {
                is com.mikhail.manifestation.ui.viewmodel.ClubsUiState.Success -> s.masterTelegramUrl
                else -> com.mikhail.manifestation.Translations.ui("clubs_master_telegram_url")
            }
            val isLoading = clubsState is com.mikhail.manifestation.ui.viewmodel.ClubsUiState.Loading

            ClubsMapScreen(
                clubs = clubs,
                masterTelegramUrl = masterUrl,
                isLoading = isLoading,
                onDismiss = { showClubsMap = false }
            )
        }
    }
}
