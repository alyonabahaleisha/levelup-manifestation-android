package com.mikhail.manifestation.ui.screens.affirmations

import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import com.mikhail.manifestation.data.content.AffirmationContent
import com.mikhail.manifestation.data.model.Affirmation
import com.mikhail.manifestation.ui.theme.AffirmationTheme
import com.mikhail.manifestation.ui.theme.PlayfairDisplay
import com.mikhail.manifestation.ui.viewmodel.ThemeViewModel
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun AffirmationsScreen(
    themeViewModel: ThemeViewModel,
    startText: String? = null,
    deepLinkText: String? = null,
    onDeepLinkConsumed: () -> Unit = {}
) {
    val haptics = LocalHapticFeedback.current
    val affirmationTheme by themeViewModel.affirmationTheme.collectAsState()

    val affirmations = remember { AffirmationContent.feed() }
    val startPage = remember(startText) {
        if (startText != null) affirmations.indexOfFirst { it.text == startText }.coerceAtLeast(0)
        else 0
    }
    val pagerState = rememberPagerState(
        initialPage = startPage,
        pageCount = { affirmations.size.coerceAtLeast(1) }
    )

    var showThemePicker by remember { mutableStateOf(false) }
    var shareAffirmation by remember { mutableStateOf<Affirmation?>(null) }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect {
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    LaunchedEffect(deepLinkText) {
        if (deepLinkText != null) {
            val idx = affirmations.indexOfFirst { it.text == deepLinkText }
            if (idx >= 0) pagerState.animateScrollToPage(idx)
            onDeepLinkConsumed()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(themeBrush(affirmationTheme))
    ) {
        if (affirmations.isNotEmpty()) {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val affirmation = affirmations[page]
                AffirmationCard(
                    affirmation = affirmation,
                    theme = affirmationTheme,
                    onShare = { shareAffirmation = affirmation }
                )
            }
        }

        // Theme picker button
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 140.dp)
        ) {
            IconButton(
                onClick = { showThemePicker = true },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(affirmationTheme.textColor.copy(alpha = 0.15f))
            ) {
                Icon(
                    imageVector = Icons.Outlined.ColorLens,
                    contentDescription = "Темы",
                    tint = affirmationTheme.textColor.copy(alpha = 0.8f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }

    if (showThemePicker) {
        ThemePickerSheet(
            selectedThemeId = affirmationTheme.id,
            onSelect = { theme ->
                themeViewModel.setAffirmationTheme(theme)
                showThemePicker = false
            },
            onDismiss = { showThemePicker = false }
        )
    }

    shareAffirmation?.let { affirmation ->
        SharePreviewDialog(
            affirmation = affirmation,
            theme = affirmationTheme,
            onDismiss = { shareAffirmation = null }
        )
    }
}

@Composable
private fun AffirmationCard(
    affirmation: Affirmation,
    theme: AffirmationTheme,
    onShare: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    var liked by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f))

            val len = affirmation.text.length
            val fontSize = when {
                len < 60 -> 34.sp
                len < 120 -> 30.sp
                len < 200 -> 26.sp
                else -> 22.sp
            }
            val lineHeight = when {
                len < 60 -> 40.sp
                len < 120 -> 36.sp
                len < 200 -> 32.sp
                else -> 28.sp
            }

            Text(
                text = affirmation.text,
                fontFamily = PlayfairDisplay,
                fontSize = fontSize,
                color = theme.textColor,
                textAlign = TextAlign.Center,
                lineHeight = lineHeight
            )

            Spacer(Modifier.weight(1f))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 180.dp)
            ) {
                Icon(
                    Icons.Outlined.Share,
                    contentDescription = "Share",
                    tint = theme.textColor.copy(alpha = 0.6f),
                    modifier = Modifier
                        .size(30.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            onShare()
                        }
                )
                Spacer(Modifier.width(36.dp))
                Icon(
                    Icons.Outlined.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (liked) Color(0xFFE06080) else theme.textColor.copy(alpha = 0.6f),
                    modifier = Modifier
                        .size(30.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            liked = !liked
                        }
                )
            }
        }
    }
}

// MARK: - Share Preview Dialog

@Composable
private fun SharePreviewDialog(
    affirmation: Affirmation,
    theme: AffirmationTheme,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1C1816)),
            contentAlignment = Alignment.Center
        ) {
            // Card centered with capture layer
            Box(
                modifier = Modifier
                    .padding(horizontal = 40.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f / 1.5f)
                    .clip(RoundedCornerShape(24.dp))
                    .drawWithContent {
                        graphicsLayer.record {
                            this@drawWithContent.drawContent()
                        }
                        drawLayer(graphicsLayer)
                    }
            ) {
                ShareCardContent(text = affirmation.text, theme = theme)
            }

            // Close button overlay
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp, top = 48.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
            ) {
                Icon(
                    Icons.Outlined.Close,
                    contentDescription = "Close",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
            }

            // Action buttons at bottom
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 64.dp)
            ) {
                ActionButton(
                    icon = { Icon(Icons.Outlined.Share, contentDescription = null, modifier = Modifier.size(20.dp)) },
                    label = "Поделиться"
                ) {
                    scope.launch {
                        val bitmap = graphicsLayer.toImageBitmap()
                        val androidBitmap = Bitmap.createBitmap(
                            bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888
                        ).also { bmp ->
                            val pixels = IntArray(bitmap.width * bitmap.height)
                            bitmap.readPixels(pixels)
                            bmp.setPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
                        }
                        shareImage(context, androidBitmap)
                    }
                }
                ActionButton(
                    icon = { Icon(Icons.Outlined.FavoriteBorder, contentDescription = null, modifier = Modifier.size(20.dp)) },
                    label = "Сохранить"
                ) {
                    scope.launch {
                        val bitmap = graphicsLayer.toImageBitmap()
                        val androidBitmap = Bitmap.createBitmap(
                            bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888
                        ).also { bmp ->
                            val pixels = IntArray(bitmap.width * bitmap.height)
                            bitmap.readPixels(pixels)
                            bmp.setPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
                        }
                        saveBitmapToGallery(context, androidBitmap)
                    }
                }
                ActionButton(
                    icon = { Icon(Icons.Outlined.ContentCopy, contentDescription = null, modifier = Modifier.size(20.dp)) },
                    label = "Текст"
                ) {
                    clipboardManager.setText(AnnotatedString(affirmation.text))
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: @Composable () -> Unit,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f))
        ) {
            Box(modifier = Modifier.graphicsLayer { }) {
                icon()
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

// MARK: - Share Card Content (what gets rendered to image)

@Composable
private fun ShareCardContent(text: String, theme: AffirmationTheme) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(themeBrush(theme)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.weight(1f))

            val len = text.length
            val fontSize = when {
                len < 60 -> 34.sp
                len < 120 -> 30.sp
                len < 200 -> 26.sp
                else -> 22.sp
            }
            val lineHeight = when {
                len < 60 -> 40.sp
                len < 120 -> 36.sp
                len < 200 -> 32.sp
                else -> 28.sp
            }

            Text(
                text = text,
                fontFamily = PlayfairDisplay,
                fontSize = fontSize,
                color = theme.textColor,
                textAlign = TextAlign.Center,
                lineHeight = lineHeight,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.weight(1f))

            Text(
                text = "Прислано из приложения\nМихаила Агеева",
                fontSize = 10.sp,
                color = theme.textColor.copy(alpha = 0.35f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(theme.textColor.copy(alpha = 0.06f))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

// MARK: - Theme Picker Bottom Sheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemePickerSheet(
    selectedThemeId: String,
    onSelect: (AffirmationTheme) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF1C1C1E),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.3f))
            )
        }
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Темы",
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(AffirmationTheme.all) { theme ->
                    ThemeCard(
                        theme = theme,
                        isSelected = theme.id == selectedThemeId,
                        onClick = { onSelect(theme) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeCard(
    theme: AffirmationTheme,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(0.75f)
            .clip(RoundedCornerShape(16.dp))
            .background(themeBrush(theme))
            .then(
                if (isSelected) Modifier.background(Color.Transparent)
                else Modifier
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = theme.name,
            fontSize = 13.sp,
            color = theme.textColor
        )

        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Icon(
                    Icons.Outlined.Check,
                    contentDescription = "Selected",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(14.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }
}

// MARK: - Helpers

private fun themeBrush(theme: AffirmationTheme): Brush {
    return if (theme.colors.size > 1) {
        Brush.linearGradient(theme.colors)
    } else {
        Brush.linearGradient(listOf(theme.colors[0], theme.colors[0]))
    }
}

@Composable
private fun Modifier.background(brush: Brush): Modifier = this.background(brush)

private fun shareImage(context: android.content.Context, bitmap: Bitmap) {
    val file = File(context.cacheDir, "shared_images").also { it.mkdirs() }
    val imageFile = File(file, "affirmation_${System.currentTimeMillis()}.png")
    imageFile.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }

    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, null))
}

private fun saveBitmapToGallery(context: android.content.Context, bitmap: Bitmap) {
    val values = android.content.ContentValues().apply {
        put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, "affirmation_${System.currentTimeMillis()}.png")
        put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(android.provider.MediaStore.Images.Media.RELATIVE_PATH, "Pictures/LevelUp")
    }
    val uri = context.contentResolver.insert(
        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
    ) ?: return
    context.contentResolver.openOutputStream(uri)?.use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
}
