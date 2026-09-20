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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Book
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.viewmodel.MainViewModel

@Composable
fun LibraryScreen(
    viewModel: MainViewModel,
    onOpenBook: (String) -> Unit
) {
    val books by viewModel.filteredBooks.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val activeFilter by viewModel.activeFilter.collectAsState()

    val subjectCategories = listOf(
        "All" to "সকল বিষয়",
        "Physics" to "পদার্থবিজ্ঞান",
        "Chemistry" to "রসায়ন",
        "Biology" to "জীববিজ্ঞান",
        "Higher Mathematics" to "উচ্চতর গণিত",
        "ICT" to "আইসিটি",
        "Bangla" to "বাংলা",
        "English" to "English",
        "Exam Preparation" to "ভর্তি ও পরীক্ষা"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("library_screen")
    ) {
        // Top Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 8.dp)
        ) {
            Text(
                text = "বইয়ের লাইব্রেরি (Cloud Library)",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "ক্লাউড থেকে দ্রুত যেকোনো বই পড়ুন এবং প্রগ্রেস সংরক্ষণ করুন",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Search input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_text_field"),
                placeholder = { Text("বই, অধ্যায় বা লেখকের নাম লিখুন...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = EmeraldPrimary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EmeraldPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Filter Tabs (All, Cached, Favorites)
            val tabs = listOf("All" to "সকল বই", "Cached" to "ক্যাশড (Offline)", "Favorites" to "প্রিয় তালিকা")
            TabRow(
                selectedTabIndex = tabs.indexOfFirst { it.first == activeFilter }.coerceAtLeast(0),
                containerColor = Color.Transparent,
                contentColor = EmeraldPrimary,
                indicator = { tabPositions ->
                    val index = tabs.indexOfFirst { it.first == activeFilter }.coerceAtLeast(0)
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                        color = EmeraldPrimary
                    )
                }
            ) {
                tabs.forEach { (key, label) ->
                    Tab(
                        selected = activeFilter == key,
                        onClick = { viewModel.onFilterSelected(key) },
                        text = {
                            Text(
                                text = label,
                                fontWeight = if (activeFilter == key) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
        }

        // Subject Horizontal Pills
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(subjectCategories) { (key, label) ->
                FilterChip(
                    selected = selectedSubject == key,
                    onClick = { viewModel.onSubjectSelected(key) },
                    label = { Text(text = label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EmeraldPrimary,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }

        // Book list
        if (books.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "কোনো বই পাওয়া যায়নি",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "ফিল্টার পরিবর্তন করুন অথবা নতুন বই অ্যাড করুন।",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(books, key = { it.id }) { book ->
                    LibraryBookCard(
                        book = book,
                        onOpen = { onOpenBook(book.id) },
                        onToggleFavorite = { viewModel.toggleFavorite(book.id) },
                        onClearCache = { viewModel.clearBookCache(book.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun LibraryBookCard(
    book: Book,
    onOpen: () -> Unit,
    onToggleFavorite: () -> Unit,
    onClearCache: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen)
            .testTag("book_card_${book.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                AsyncImage(
                    model = book.coverUrl,
                    contentDescription = book.title,
                    modifier = Modifier
                        .size(width = 75.dp, height = 105.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldPrimary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = book.subject,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (book.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (book.isFavorite) Color(0xFFF43F5E) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = book.author,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (book.isCached) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = EmeraldPrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "⚡ ডিভাইসে সংরক্ষিত (Cached)",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 10.sp,
                                    color = EmeraldPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = "☁️ ক্লাউড স্ট্রিম (Cloud Only)",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            // Description snippet
            Text(
                text = book.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))
            // Progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "পৃষ্ঠা ${book.lastReadPage} / ${book.pageCount}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${(book.progressPercent * 100).toInt()}% পড়া হয়েছে",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { book.progressPercent.coerceIn(0.02f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = EmeraldPrimary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (book.isCached) {
                    OutlinedButton(
                        onClick = onClearCache,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = "Clear Cache",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ক্যাশ মুছুন",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Button(
                    onClick = onOpen,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = if (book.progressPercent > 0f) "পড়া অব্যাহত রাখুন" else "পড়া শুরু করুন",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
