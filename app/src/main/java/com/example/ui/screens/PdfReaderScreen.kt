package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Book
import com.example.data.model.Bookmark
import com.example.pdf.PdfRendererHelper
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ReaderBgDark
import com.example.ui.theme.ReaderBgLight
import com.example.ui.theme.ReaderBgSepia
import com.example.ui.theme.ReaderTextDark
import com.example.ui.theme.ReaderTextLight
import com.example.ui.theme.ReaderTextSepia
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

enum class ReaderColorMode {
    LIGHT, SEPIA, NIGHT
}

@Composable
fun PdfReaderScreen(
    bookId: String,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var book by remember { mutableStateOf<Book?>(null) }
    var pdfHelper by remember { mutableStateOf<PdfRendererHelper?>(null) }
    var totalPages by remember { mutableIntStateOf(1) }
    var currentPage by remember { mutableIntStateOf(1) }
    var currentBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var streamProgress by remember { mutableFloatStateOf(0f) }
    var isFullScreen by remember { mutableStateOf(false) }
    var colorMode by remember { mutableStateOf(ReaderColorMode.LIGHT) }
    var showJumpDialog by remember { mutableStateOf(false) }
    var showNoteDialog by remember { mutableStateOf(false) }
    var showThumbnailStrip by remember { mutableStateOf(false) }
    var isBookmarked by remember { mutableStateOf(false) }

    // Zoom & Pan state
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // Load book & initialize renderer
    LaunchedEffect(bookId) {
        isLoading = true
        val loadedBook = viewModel.repository.getBook(bookId)
        book = loadedBook

        if (loadedBook != null) {
            currentPage = loadedBook.lastReadPage.coerceAtLeast(1)
            val file: File = viewModel.repository.openAndCacheBook(loadedBook) { progress ->
                streamProgress = progress
            }
            val helper = PdfRendererHelper(file)
            pdfHelper = helper
            totalPages = helper.pageCount.coerceAtLeast(1)
            if (currentPage > totalPages) currentPage = 1

            // Check bookmark
            isBookmarked = viewModel.repository.isPageBookmarked(bookId, currentPage)

            // Render initial page
            currentBitmap = helper.renderPageBitmap(currentPage - 1)
            isLoading = false
            viewModel.refreshCacheSize()
        }
    }

    // Clean up renderer on exit
    DisposableEffect(Unit) {
        onDispose {
            pdfHelper?.close()
        }
    }

    // Function to navigate page
    fun goToPage(newPage: Int) {
        val target = newPage.coerceIn(1, totalPages)
        if (target == currentPage && currentBitmap != null) return

        currentPage = target
        scale = 1f
        offsetX = 0f
        offsetY = 0f

        coroutineScope.launch {
            pdfHelper?.let { helper ->
                currentBitmap = helper.renderPageBitmap(currentPage - 1)
            }
            isBookmarked = viewModel.repository.isPageBookmarked(bookId, currentPage)
            viewModel.repository.saveReadingProgress(bookId, currentPage, totalPages)
        }
    }

    // Reader background and text styling according to mode
    val readerBg = when (colorMode) {
        ReaderColorMode.LIGHT -> ReaderBgLight
        ReaderColorMode.SEPIA -> ReaderBgSepia
        ReaderColorMode.NIGHT -> ReaderBgDark
    }

    val readerTextColor = when (colorMode) {
        ReaderColorMode.LIGHT -> ReaderTextLight
        ReaderColorMode.SEPIA -> ReaderTextSepia
        ReaderColorMode.NIGHT -> ReaderTextDark
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(readerBg)
            .testTag("pdf_reader_screen")
    ) {
        if (isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = EmeraldPrimary, strokeWidth = 3.dp)
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "ক্লাউড থেকে পৃষ্ঠা প্রস্তুত হচ্ছে...",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = readerTextColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "অন-ডিমান্ড স্ট্রিমিং ও স্মার্ট ক্যাশিং সক্রিয়",
                    style = MaterialTheme.typography.bodySmall,
                    color = readerTextColor.copy(alpha = 0.7f)
                )
                if (streamProgress in 0.01f..0.99f) {
                    Spacer(modifier = Modifier.height(14.dp))
                    LinearProgressIndicator(
                        progress = { streamProgress },
                        modifier = Modifier
                            .width(200.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = EmeraldPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${(streamProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = readerTextColor
                    )
                }
            }
        } else {
            // PDF Page Canvas with Zoom & Pan & Double Tap
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 4f)
                            if (scale > 1f) {
                                offsetX += pan.x
                                offsetY += pan.y
                            } else {
                                offsetX = 0f
                                offsetY = 0f
                            }
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                if (scale > 1.2f) {
                                    scale = 1f
                                    offsetX = 0f
                                    offsetY = 0f
                                } else {
                                    scale = 2.2f
                                }
                            },
                            onTap = {
                                // Toggle UI visibility
                                isFullScreen = !isFullScreen
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                currentBitmap?.let { bmp ->
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "PDF Page $currentPage",
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offsetX,
                                translationY = offsetY
                            ),
                        contentScale = ContentScale.Fit,
                        colorFilter = if (colorMode == ReaderColorMode.NIGHT) {
                            // Inverted color matrix for night mode comfort
                            ColorFilter.colorMatrix(
                                androidx.compose.ui.graphics.ColorMatrix(
                                    floatArrayOf(
                                        -1f, 0f, 0f, 0f, 255f,
                                        0f, -1f, 0f, 0f, 255f,
                                        0f, 0f, -1f, 0f, 255f,
                                        0f, 0f, 0f, 1f, 0f
                                    )
                                )
                            )
                        } else if (colorMode == ReaderColorMode.SEPIA) {
                            ColorFilter.tint(Color(0xFF6B4C1B), androidx.compose.ui.graphics.BlendMode.ColorBurn)
                        } else null
                    )
                }

                // Invisible Navigation zones on sides for easy page flipping when zoomed out
                if (scale == 1f) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .clickable {
                                    if (currentPage > 1) goToPage(currentPage - 1)
                                }
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .clickable {
                                    if (currentPage < totalPages) goToPage(currentPage + 1)
                                }
                        )
                    }
                }
            }

            // Top Reader Navigation Bar (Animated)
            AnimatedVisibility(
                visible = !isFullScreen,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = book?.title ?: "বই",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "পৃষ্ঠা $currentPage / $totalPages  •  ${((currentPage.toFloat() / totalPages) * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { showJumpDialog = true }
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Bookmark toggle
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    if (isBookmarked) {
                                        viewModel.repository.removeBookmarkForPage(bookId, currentPage)
                                        isBookmarked = false
                                    } else {
                                        viewModel.addBookmark(
                                            bookId = bookId,
                                            bookTitle = book?.title ?: "বই",
                                            page = currentPage,
                                            title = "পৃষ্ঠা $currentPage এর বুকমার্ক"
                                        )
                                        isBookmarked = true
                                    }
                                }
                            }) {
                                Icon(
                                    imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                    contentDescription = "Bookmark",
                                    tint = if (isBookmarked) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Add Note
                            IconButton(onClick = { showNoteDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.EditNote,
                                    contentDescription = "Add Note",
                                    tint = EmeraldPrimary
                                )
                            }

                            // Color Mode Toggle
                            IconButton(onClick = {
                                colorMode = when (colorMode) {
                                    ReaderColorMode.LIGHT -> ReaderColorMode.SEPIA
                                    ReaderColorMode.SEPIA -> ReaderColorMode.NIGHT
                                    ReaderColorMode.NIGHT -> ReaderColorMode.LIGHT
                                }
                            }) {
                                Icon(
                                    imageVector = when (colorMode) {
                                        ReaderColorMode.LIGHT -> Icons.Default.LightMode
                                        ReaderColorMode.SEPIA -> Icons.Default.FormatColorFill
                                        ReaderColorMode.NIGHT -> Icons.Default.DarkMode
                                    },
                                    contentDescription = "Reading Mode",
                                    tint = when (colorMode) {
                                        ReaderColorMode.LIGHT -> EmeraldPrimary
                                        ReaderColorMode.SEPIA -> Color(0xFFD97706)
                                        ReaderColorMode.NIGHT -> Color(0xFF38BDF8)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Reader Control Bar (Animated)
            AnimatedVisibility(
                visible = !isFullScreen,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        // Slider to quickly jump pages
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "1",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Slider(
                                value = currentPage.toFloat(),
                                onValueChange = { target ->
                                    goToPage(target.toInt())
                                },
                                valueRange = 1f..totalPages.toFloat(),
                                steps = if (totalPages > 2) totalPages - 2 else 0,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp),
                                colors = SliderDefaults.colors(
                                    thumbColor = EmeraldPrimary,
                                    activeTrackColor = EmeraldPrimary
                                )
                            )
                            Text(
                                text = "$totalPages",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Bottom Actions Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Previous Page Button
                            OutlinedButton(
                                onClick = { if (currentPage > 1) goToPage(currentPage - 1) },
                                enabled = currentPage > 1,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NavigateBefore,
                                    contentDescription = "Prev"
                                )
                                Text("পূর্ববর্তী", fontSize = 12.sp)
                            }

                            // Jump Dialog Trigger
                            Button(
                                onClick = { showJumpDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text(
                                    text = "পৃষ্ঠা $currentPage / $totalPages",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Next Page Button
                            OutlinedButton(
                                onClick = { if (currentPage < totalPages) goToPage(currentPage + 1) },
                                enabled = currentPage < totalPages,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("পরবর্তী", fontSize = 12.sp)
                                Icon(
                                    imageVector = Icons.Default.NavigateNext,
                                    contentDescription = "Next"
                                )
                            }
                        }
                    }
                }
            }

            // Small Floating Fullscreen toggle button when in fullscreen
            if (isFullScreen) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    shadowElevation = 3.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .clickable { isFullScreen = false }
                ) {
                    Icon(
                        imageVector = Icons.Default.FullscreenExit,
                        contentDescription = "Exit Fullscreen",
                        modifier = Modifier.padding(8.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }

    // Jump to Page Dialog
    if (showJumpDialog) {
        var inputPage by remember { mutableStateOf(currentPage.toString()) }
        AlertDialog(
            onDismissRequest = { showJumpDialog = false },
            title = { Text("পৃষ্ঠা নম্বরে যান", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "মোট পৃষ্ঠা: $totalPages। আপনার কাঙ্ক্ষিত পৃষ্ঠা নম্বরটি লিখুন:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = inputPage,
                        onValueChange = { inputPage = it.filter { c -> c.isDigit() } },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val num = inputPage.toIntOrNull()
                        if (num != null && num in 1..totalPages) {
                            goToPage(num)
                        }
                        showJumpDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("যান")
                }
            },
            dismissButton = {
                TextButton(onClick = { showJumpDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // Add Note Dialog
    if (showNoteDialog) {
        var noteText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showNoteDialog = false },
            title = {
                Text("পৃষ্ঠা $currentPage এর স্টাডি নোট", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "এই পৃষ্ঠার গুরুত্বপূর্ণ সূত্র, সংজ্ঞা বা প্রশ্নের নোট লিখুন:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        placeholder = { Text("নোট লিখুন...") },
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (noteText.isNotBlank()) {
                            viewModel.addNote(
                                bookId = bookId,
                                bookTitle = book?.title ?: "বই",
                                page = currentPage,
                                content = noteText.trim()
                            )
                        }
                        showNoteDialog = false
                    },
                    enabled = noteText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("সংরক্ষণ করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNoteDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}
