package com.mikhail.manifestation.ui.screens.home

// iOS Migration: -> ClubsMapScreen.swift — GoogleMap -> MKMapView (UIViewRepresentable),
// Clustering composable -> clusteringIdentifier on MKAnnotationView,
// ModalBottomSheet -> .sheet(presentationDetents:[.height(220)])

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.rememberCameraPositionState
import com.mikhail.manifestation.R
import com.mikhail.manifestation.data.model.Club
import com.mikhail.manifestation.ui.theme.AppTypography
import com.mikhail.manifestation.ui.theme.PlayfairDisplay
import kotlinx.coroutines.launch

// ── Palette ──────────────────────────────────────────────────────────────────
private val screenBg = Color(0xFF0D1E2A)
private val topBarBg = Color(0xE60D1E2A)   // 90% opacity
private val bottomBarBg = Color(0xF20D1E2A) // 95% opacity
private val sheetBg = Color(0xFF1A2D3D)
private val accentTeal = Color(0xFF5BB8D4)
private val accentTealDark = Color(0xFF3A8FA8)
private val telegramBlue = Color(0xFF2AABEE)
private val topBarBorder = Color.White.copy(alpha = 0.08f)
private val bottomBarBorder = Color.White.copy(alpha = 0.08f)

// Geographic center of Russia — fallback when no clubs are loaded yet
private val russiaCenter = LatLng(62.0, 90.0)
private const val RUSSIA_ZOOM = 3.0f

// ── Public entry point ────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubsMapScreen(
    clubs: List<Club>,
    masterTelegramUrl: String,
    isLoading: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Intercept system back gesture
    BackHandler(onBack = onDismiss)

    // Build cluster items once clubs are available
    val clusterItems = remember(clubs) {
        clubs
            .filter { it.latitude != 0.0 || it.longitude != 0.0 }
            .map { ClubClusterItem(it) }
    }

    // Camera — starts at Russia center, then animates to fit all pins once items arrive
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(russiaCenter, RUSSIA_ZOOM)
    }

    // Auto-fit bounds when clubs first load
    LaunchedEffect(clusterItems) {
        if (clusterItems.size < 2) return@LaunchedEffect
        val boundsBuilder = LatLngBounds.Builder()
        clusterItems.forEach { boundsBuilder.include(it.position) }
        val bounds = boundsBuilder.build()
        // 80dp padding ensures pins aren't clipped at edges
        cameraPositionState.animate(
            CameraUpdateFactory.newLatLngBounds(bounds, 80)
        )
    }

    val mapStyle = remember {
        try {
            MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark)
        } catch (_: Exception) {
            null
        }
    }

    // Bottom sheet state for selected pin
    var selectedClub by remember { mutableStateOf<Club?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBg)
    ) {

        // ── Interactive map — full bleed ─────────────────────────────────────
        ClubsInteractiveMap(
            clusterItems = clusterItems,
            cameraPositionState = cameraPositionState,
            mapStyle = mapStyle,
            onPinTapped = { item -> selectedClub = item.club },
            onClusterTapped = { cluster ->
                scope.launch {
                    val boundsBuilder = LatLngBounds.Builder()
                    cluster.items.forEach { boundsBuilder.include(it.position) }
                    val bounds = boundsBuilder.build()
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngBounds(bounds, 120)
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // ── Loading overlay ──────────────────────────────────────────────────
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White.copy(alpha = 0.7f),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Загружаем клубы...",
                        style = AppTypography.caption.copy(fontSize = 13.sp),
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
            }
        }

        // ── Empty state overlay (no clubs after load) ────────────────────────
        if (!isLoading && clubs.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(280.dp)
                        .background(
                            Color.White.copy(alpha = 0.10f),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Клубы пока не загружены. Попробуй вернуться позже.",
                        style = AppTypography.bodySmall.copy(fontSize = 14.sp),
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        // ── Top bar ──────────────────────────────────────────────────────────
        TopBar(
            onBack = onDismiss,
            onZoomIn = { scope.launch { cameraPositionState.animate(CameraUpdateFactory.zoomIn()) } },
            onZoomOut = { scope.launch { cameraPositionState.animate(CameraUpdateFactory.zoomOut()) } },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
        )

        // ── Club detail bottom sheet ─────────────────────────────────────────
        val club = selectedClub
        if (club != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedClub = null },
                sheetState = sheetState,
                containerColor = sheetBg,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                ClubDetailSheet(
                    club = club,
                    onClose = { selectedClub = null },
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
        }
    }
}

// ── Interactive map with clustering ──────────────────────────────────────────

@Composable
private fun ClubsInteractiveMap(
    clusterItems: List<ClubClusterItem>,
    cameraPositionState: CameraPositionState,
    mapStyle: MapStyleOptions?,
    onPinTapped: (ClubClusterItem) -> Unit,
    onClusterTapped: (Cluster<ClubClusterItem>) -> Unit,
    modifier: Modifier = Modifier
) {
    GoogleMap(
        modifier = modifier.semantics {
            contentDescription = "Интерактивная карта клубов. На карте отображено ${clusterItems.size} городов."
        },
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            mapStyleOptions = mapStyle,
            isMyLocationEnabled = false,
            isTrafficEnabled = false,
            isIndoorEnabled = false,
            minZoomPreference = 3f,
            maxZoomPreference = 12f
        ),
        uiSettings = MapUiSettings(
            scrollGesturesEnabled = true,
            zoomGesturesEnabled = true,
            tiltGesturesEnabled = false,
            rotationGesturesEnabled = false,
            zoomControlsEnabled = false,   // We provide our own zoom controls in the top bar
            compassEnabled = false,
            myLocationButtonEnabled = false,
            mapToolbarEnabled = false
        )
    ) {
        val tealHue = 195f // HSV hue for teal/cyan
        clusterItems.forEach { item ->
            com.google.maps.android.compose.Marker(
                state = com.google.maps.android.compose.MarkerState(
                    position = item.position
                ),
                title = item.title,
                snippet = item.snippet,
                icon = com.google.android.gms.maps.model.BitmapDescriptorFactory
                    .defaultMarker(tealHue),
                onClick = {
                    onPinTapped(item)
                    true
                }
            )
        }
    }
}

// ── Custom pin — teal circle with glow ───────────────────────────────────────

@Composable
private fun PinMarker(contentDesc: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(32.dp)
            .semantics { contentDescription = contentDesc }
    ) {
        // Outer glow
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            accentTeal.copy(alpha = 0.30f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )
        // Inner circle
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(accentTeal, CircleShape)
        )
    }
}

// ── Custom cluster marker — teal circle with count ────────────────────────────

@Composable
private fun ClusterMarker(count: Int) {
    // Size scales with cluster population per UX doc §6
    val sizeDp = when {
        count <= 5 -> 32.dp
        count <= 15 -> 40.dp
        else -> 48.dp
    }
    val glowDp = sizeDp * 1.6f

    val contentDesc = "Группа клубов: $count городов. Нажмите, чтобы приблизить."

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(glowDp)
            .semantics { contentDescription = contentDesc }
    ) {
        // Outer glow
        Box(
            modifier = Modifier
                .size(glowDp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            accentTeal.copy(alpha = 0.30f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )
        // Cluster circle
        Box(
            modifier = Modifier
                .size(sizeDp)
                .background(accentTeal, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count.toString(),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────

@Composable
private fun TopBar(
    onBack: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(topBarBg)
            .drawBehind {
                drawLine(
                    color = topBarBorder,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(horizontal = 8.dp)
    ) {
        // Back button — left side
        IconButton(
            onClick = onBack,
            modifier = Modifier.semantics {
                contentDescription = "Вернуться на главный экран"
            }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    "Назад",
                    style = AppTypography.bodySmall.copy(fontWeight = FontWeight.Normal),
                    color = Color.White
                )
            }
        }

        // Title — centered
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Наши клубы",
                style = AppTypography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp
                ),
                color = Color.White
            )
        }

        // Zoom controls — right side
        Row {
            ZoomButton(label = "+", contentDesc = "Увеличить масштаб", onClick = onZoomIn)
            Spacer(Modifier.width(8.dp))
            ZoomButton(label = "–", contentDesc = "Уменьшить масштаб", onClick = onZoomOut)
        }
        Spacer(Modifier.width(8.dp))
    }
}

@Composable
private fun ZoomButton(
    label: String,
    contentDesc: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(Color.White.copy(alpha = 0.12f), CircleShape)
            .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(40.dp)
                .semantics { contentDescription = contentDesc }
        ) {
            Text(
                text = label,
                fontSize = 20.sp,
                fontWeight = FontWeight.Light,
                color = Color.White
            )
        }
    }
}

// ── Bottom CTA bar ────────────────────────────────────────────────────────────

@Composable
private fun BottomCtaBar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(bottomBarBg)
            .drawBehind {
                drawLine(
                    color = bottomBarBorder,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 24.dp, vertical = 14.dp)
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            // Gradient background drawn inside the button
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(accentTeal, accentTealDark)
                        ),
                        RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Присоединиться к сообществу",
                    style = AppTypography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    ),
                    color = Color.White
                )
            }
        }
    }
}

// ── Club detail bottom sheet content ─────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClubDetailSheet(
    club: Club,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp)
    ) {
        // City name + close button row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = club.city,
                style = AppTypography.bodyMedium.copy(
                    fontFamily = PlayfairDisplay,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onClose,
                modifier = Modifier.semantics {
                    contentDescription = "Закрыть информацию о клубе"
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Leader label
        Text(
            "Руководитель клуба",
            style = AppTypography.caption.copy(fontSize = 12.sp),
            color = Color.White.copy(alpha = 0.5f)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            club.leaderName,
            style = AppTypography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            ),
            color = Color.White
        )

        Spacer(Modifier.height(20.dp))

        // Telegram button
        Button(
            onClick = {
                val url = club.telegramUrl.ifBlank { return@Button }
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .semantics {
                    contentDescription =
                        "Открыть клуб ${club.city} в Telegram. Откроет внешнее приложение."
                },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = telegramBlue,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            Text(
                "Открыть в Telegram",
                style = AppTypography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            )
        }
    }
}
