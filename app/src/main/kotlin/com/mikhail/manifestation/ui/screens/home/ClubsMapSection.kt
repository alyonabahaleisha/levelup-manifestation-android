package com.mikhail.manifestation.ui.screens.home

// iOS Migration: -> ClubsMapSection.swift — GoogleMap -> Map (MapKit), LazyColumn item -> VStack section

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.mikhail.manifestation.R
import com.mikhail.manifestation.data.model.Club
import com.mikhail.manifestation.ui.theme.AppTypography
import com.mikhail.manifestation.ui.theme.PlayfairDisplay

// ── Palette (matches HomeScreen.kt) ────────────────────────────────────────
private val mapCardBg = Color(0xFF1A2D3D)
private val mapScrimEnd = Color(0xFF0A1A28)
private val accentTeal = Color(0xFF5BB8D4)

// Geographic center of Russia — fits Kaliningrad to Vladivostok at zoom ~3
private val russiaCenter = LatLng(62.0, 90.0)
private const val RUSSIA_ZOOM = 2.8f

// ── Public entry point ──────────────────────────────────────────────────────

@Composable
fun ClubsMapSection(
    clubs: List<Club>,
    masterTelegramUrl: String,
    onExpandMap: () -> Unit,
    onJoinTapped: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current

    Column(modifier = modifier.padding(horizontal = 24.dp)) {

        // Section title — same pattern as homeMeditations / homeDailyAffirmation
        Text(
            "Наши клубы",
            style = AppTypography.headingSmall.copy(fontFamily = PlayfairDisplay),
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Preview card — 288dp total (200dp map + 88dp info zone)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(288.dp)
                .clip(RoundedCornerShape(24.dp))
        ) {
            // ── Map layer (non-interactive) ──────────────────────────────
            MapPreviewLayer(clubs = clubs)

            // ── Gradient scrim over lower portion ────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.0f to Color.Transparent,
                                0.6f to Color.Transparent,
                                0.75f to mapScrimEnd.copy(alpha = 0.7f),
                                1.0f to mapScrimEnd
                            )
                        )
                    )
            )

            // ── Info zone (title + subtitle + CTA) ───────────────────────
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    "Клубы по всему миру",
                    style = AppTypography.bodyMedium.copy(
                        fontFamily = PlayfairDisplay,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "${clubs.size} ${clubsCityLabel(clubs.size)} · найди ближайший",
                    style = AppTypography.caption.copy(fontSize = 13.sp),
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            // ── Expand chevron (top-right) ────────────────────────────────
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(28.dp)
                    .background(Color.Black.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }

            // ── Touch overlay — sits on TOP of everything to catch taps ──
            // GoogleMap swallows all touch events even with gestures disabled.
            // This transparent Box is the last child so it's on top in Z-order.
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onExpandMap()
                    }
            )
        }
    }
}

// ── Non-interactive map snapshot ─────────────────────────────────────────────

@Composable
private fun MapPreviewLayer(clubs: List<Club>) {
    val context = LocalContext.current

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(russiaCenter, RUSSIA_ZOOM)
    }

    val mapStyle = remember {
        try {
            MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark)
        } catch (_: Exception) {
            null
        }
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(288.dp)
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { },
            properties = MapProperties(
                mapStyleOptions = mapStyle,
                isMyLocationEnabled = false,
                isTrafficEnabled = false,
                isIndoorEnabled = false
            ),
            uiSettings = MapUiSettings(
                scrollGesturesEnabled = false,
                zoomGesturesEnabled = false,
                tiltGesturesEnabled = false,
                rotationGesturesEnabled = false,
                zoomControlsEnabled = false,
                compassEnabled = false,
                myLocationButtonEnabled = false,
                mapToolbarEnabled = false,
                scrollGesturesEnabledDuringRotateOrZoom = false
            )
        ) {
            val tealIcon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(195f)
            clubs.forEach { club ->
                if (club.latitude != 0.0 || club.longitude != 0.0) {
                    Marker(
                        state = MarkerState(LatLng(club.latitude, club.longitude)),
                        title = club.city,
                        icon = tealIcon,
                        visible = true
                    )
                }
            }
        }

    }
}

// ── Loading skeleton for ClubsMapSection ─────────────────────────────────────

@Composable
fun ClubsMapSectionSkeleton(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "clubsShimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "clubsShimmerOffset"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF1A2D3D),
            Color(0xFF243D50),
            Color(0xFF1A2D3D)
        ),
        start = Offset(shimmerOffset * 1000f, 0f),
        end = Offset((shimmerOffset + 1f) * 1000f, 0f)
    )

    Column(modifier = modifier.padding(horizontal = 24.dp)) {
        // Section title skeleton
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(shimmerBrush)
        )
        Spacer(Modifier.height(16.dp))

        // Card shell
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(288.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(mapCardBg)
        ) {
            // Map shimmer area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(shimmerBrush)
            )

            // Info zone skeletons
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(18.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.4f))
                )
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .width(90.dp)
                        .height(13.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.3f))
                )
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                )
            }
        }
    }
}

// ── Empty / error states ──────────────────────────────────────────────────────

@Composable
fun ClubsMapSectionEmpty(
    masterTelegramUrl: String,
    onJoinTapped: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(modifier = modifier.padding(horizontal = 24.dp)) {
        Text(
            "Наши клубы",
            style = AppTypography.headingSmall.copy(fontFamily = PlayfairDisplay),
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(mapCardBg)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🌐", fontSize = 32.sp)
                Spacer(Modifier.height(12.dp))
                Text(
                    "Клубы пока недоступны",
                    style = AppTypography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Заходи позже — сообщество растёт",
                    style = AppTypography.caption,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun ClubsMapSectionError(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 24.dp)) {
        Text(
            "Наши клубы",
            style = AppTypography.headingSmall.copy(fontFamily = PlayfairDisplay),
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(mapCardBg)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Нет подключения к сети",
                    style = AppTypography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Проверь интернет и попробуй снова",
                    style = AppTypography.caption,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onRetry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentTeal,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        "Обновить",
                        style = AppTypography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

/** Russian plural for "город / города / городов" */
private fun clubsCityLabel(count: Int): String {
    val mod100 = count % 100
    val mod10 = count % 10
    return when {
        mod100 in 11..19 -> "городов"
        mod10 == 1 -> "город"
        mod10 in 2..4 -> "города"
        else -> "городов"
    }
}

