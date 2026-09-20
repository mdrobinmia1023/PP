package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Bookmark
import com.example.data.model.Note
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BookmarksAndNotesScreen(
    viewModel: MainViewModel,
    onOpenBookAtPage: (String, Int) -> Unit
) {
    val bookmarks by viewModel.allBookmarks.collectAsState()
    val notes by viewModel.allNotes.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("bookmarks_notes_screen")
    ) {
        // Top Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(top = 16.dp, start = 20.dp, end = 20.dp)
        ) {
            Text(
                text = "সংরক্ষিত স্টাডি ম্যাটেরিয়াল",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "আপনার চিহ্নিত গুরুত্বপূর্ণ পৃষ্ঠা ও লেখার নোটসমূহ",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = EmeraldPrimary,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = EmeraldPrimary
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("বুকমার্ক (${bookmarks.size})", fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("নোটস (${notes.size})", fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }
        }

        // Tab Contents
        if (selectedTab == 0) {
            // Bookmarks List
            if (bookmarks.isEmpty()) {
                EmptyMaterialState(
                    icon = Icons.Default.Bookmark,
                    title = "কোনো বুকমার্ক পাওয়া যায়নি",
                    subtitle = "বই পড়ার সময় উপরের বুকমার্ক আইকনটিতে ট্যাপ করে গুরুত্বপূর্ণ পৃষ্ঠা চিহ্নিত করুন।"
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(bookmarks, key = { it.id }) { b ->
                        BookmarkItemCard(
                            bookmark = b,
                            formattedDate = dateFormat.format(Date(b.timestamp)),
                            onOpen = { onOpenBookAtPage(b.bookId, b.pageNumber) },
                            onDelete = { viewModel.removeBookmark(b.id) }
                        )
                    }
                }
            }
        } else {
            // Notes List
            if (notes.isEmpty()) {
                EmptyMaterialState(
                    icon = Icons.Default.EditNote,
                    title = "কোনো নোট লেখা নেই",
                    subtitle = "রিডারে পড়ার সময় যেকোনো পৃষ্ঠায় ফর্মুলা বা প্রশ্ন লিখে সেভ করে রাখুন।"
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notes, key = { it.id }) { n ->
                        NoteItemCard(
                            note = n,
                            formattedDate = dateFormat.format(Date(n.timestamp)),
                            onOpen = { onOpenBookAtPage(n.bookId, n.pageNumber) },
                            onDelete = { viewModel.deleteNote(n.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BookmarkItemCard(
    bookmark: Bookmark,
    formattedDate: String,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = EmeraldPrimary.copy(alpha = 0.12f),
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = bookmark.bookTitle,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = bookmark.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "পৃষ্ঠা ${bookmark.pageNumber}",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formattedDate,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Outlined.DeleteOutline,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun NoteItemCard(
    note: Note,
    formattedDate: String,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = EmeraldPrimary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "পৃষ্ঠা ${note.pageNumber}",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = note.bookTitle,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = formattedDate,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EmptyMaterialState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
