package com.levelup.manifestation.ui.screens.settings

import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.NightlightRound
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.levelup.manifestation.ui.screens.splash.ParticlesView
import com.levelup.manifestation.ui.theme.GlassCard
import com.levelup.manifestation.ui.theme.GlassChip
import com.levelup.manifestation.ui.theme.LocalToneTheme
import com.levelup.manifestation.ui.theme.ToneTheme
import com.levelup.manifestation.ui.viewmodel.NotificationViewModel
import com.levelup.manifestation.ui.viewmodel.ThemeViewModel

private val intervalOptions = listOf(15 to "15m", 30 to "30m", 60 to "1h", 120 to "2h", 180 to "3h")

@Composable
fun SettingsSheet(
    themeViewModel: ThemeViewModel,
    notifViewModel: NotificationViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    val theme = LocalToneTheme.current
    val notifSettings by notifViewModel.settings.collectAsState()
    val haptics = LocalHapticFeedback.current
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.linearGradient(theme.gradientColors))
    ) {
        ParticlesView(modifier = Modifier.height(600.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 48.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Settings", fontSize = 28.sp, fontWeight = FontWeight.Thin,
                    color = Color.White, letterSpacing = 3.sp, modifier = Modifier.weight(1f))
                GlassCard(cornerRadius = 14.dp, modifier = Modifier.size(44.dp)
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onDismiss)) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.Close, contentDescription = "Close", tint = Color.White.copy(0.7f))
                    }
                }
            }

            // Theme section
            SettingsSection(title = "THEME") {
                Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    ToneTheme.entries.forEachIndexed { index, tone ->
                        ToneRow(
                            tone = tone,
                            isSelected = theme == tone,
                            onTap = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                themeViewModel.setTone(tone)
                            }
                        )
                        if (index < ToneTheme.entries.size - 1) {
                            HorizontalDivider(color = Color.White.copy(0.08f), modifier = Modifier.padding(horizontal = 4.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Notifications section
            SettingsSection(title = "NOTIFICATIONS") {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Daily affirmations", fontSize = 16.sp, fontWeight = FontWeight.Light,
                            color = Color.White, modifier = Modifier.weight(1f))
                        Switch(
                            checked = notifSettings.isEnabled,
                            onCheckedChange = { enabled ->
                                if (enabled) notifViewModel.enable() else notifViewModel.disable()
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = theme.accent, checkedTrackColor = theme.accent.copy(0.3f))
                        )
                    }

                    AnimatedVisibility(
                        visible = notifSettings.isEnabled,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            HorizontalDivider(color = Color.White.copy(0.08f))

                            // Time window
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.LightMode, contentDescription = null,
                                    tint = theme.accent.copy(0.7f), modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                TimeButton(
                                    hour = notifSettings.startHour,
                                    minute = notifSettings.startMin,
                                    onTimePicked = { h, m -> notifViewModel.updateStartTime(h, m) },
                                    accentColor = theme.accent
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("to", fontSize = 13.sp, fontWeight = FontWeight.Light, color = Color.White.copy(0.35f))
                                Spacer(Modifier.width(8.dp))
                                TimeButton(
                                    hour = notifSettings.endHour,
                                    minute = notifSettings.endMin,
                                    onTimePicked = { h, m -> notifViewModel.updateEndTime(h, m) },
                                    accentColor = theme.accent
                                )
                                Spacer(Modifier.width(8.dp))
                                Icon(Icons.Outlined.NightlightRound, contentDescription = null,
                                    tint = theme.accent.copy(0.7f), modifier = Modifier.size(16.dp))
                            }

                            HorizontalDivider(color = Color.White.copy(0.08f))

                            // Interval chips
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Every", fontSize = 14.sp, fontWeight = FontWeight.Light,
                                    color = Color.White.copy(0.5f))
                                Spacer(Modifier.width(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    intervalOptions.forEach { (minutes, label) ->
                                        val isSelected = notifSettings.intervalMinutes == minutes
                                        GlassChip(isSelected = isSelected, accentColor = theme.accent,
                                            modifier = Modifier.clickable(
                                                interactionSource = remember { MutableInteractionSource() }, indication = null
                                            ) {
                                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                notifViewModel.updateInterval(minutes)
                                            }
                                        ) {
                                            Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium,
                                                color = if (isSelected) theme.accent else Color.White.copy(0.4f),
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(60.dp))
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
            letterSpacing = 2.sp, color = Color.White.copy(0.4f),
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 0.dp).padding(bottom = 12.dp)
        )
        GlassCard(cornerRadius = 22.dp, modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun ToneRow(tone: ToneTheme, isSelected: Boolean, onTap: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onTap),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Gradient circle preview
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(tone.gradientColors))
                .then(if (isSelected) Modifier.border(2.dp, tone.accent.copy(0.8f), CircleShape) else Modifier)
                .shadow(if (isSelected) 8.dp else 0.dp, CircleShape, ambientColor = tone.glowColor, spotColor = tone.glowColor)
        )
        Spacer(Modifier.width(14.dp))
        Text(tone.displayName, fontSize = 16.sp, fontWeight = FontWeight.Light,
            color = Color.White.copy(if (isSelected) 1f else 0.6f), modifier = Modifier.weight(1f))
        if (isSelected) {
            Icon(Icons.Outlined.Check, contentDescription = null, tint = tone.accent, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun TimeButton(hour: Int, minute: Int, onTimePicked: (Int, Int) -> Unit, accentColor: Color) {
    val context = LocalContext.current
    val label = "%02d:%02d".format(hour, minute)
    GlassChip(
        isSelected = false,
        modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
            TimePickerDialog(context, { _, h, m -> onTimePicked(h, m) }, hour, minute, true).show()
        }
    ) {
        Text(label, fontSize = 14.sp, color = accentColor, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
    }
}
