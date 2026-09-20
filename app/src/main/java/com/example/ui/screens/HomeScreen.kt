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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Book
import com.example.ui.theme.AmberStreak
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onOpenBook: (String) -> Unit,
    onNavigateToLibrary: (String) -> Unit,
    onNavigateToStudy: () -> Unit,
    onNavigateToCacheSettings: () -> Unit
) {
    val recentlyRead by viewModel.recentlyReadBooks.collectAsState()
    val allBooks by viewModel.allBooks.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val cacheSize by viewModel.cacheSizeBytes.collectAsState()
    val totalMinutes by viewModel.totalMinutesStudied.collectAsState()

    val continueBook = recentlyRead.firstOrNull() ?: allBooks.firstOrNull()

    val subjects = listOf(
        SubjectItem("Physics", "পদার্থবিজ্ঞান", "⚛️", Color(0xFF0284C7)),
        SubjectItem("Chemistry", "রসায়ন", "🧪", Color(0xFF0D9488)),
        SubjectItem("Biology", "জীববিজ্ঞান", "🧬", Color(0xFF16A34A)),
        SubjectItem("Higher Mathematics", "উচ্চতর গণিত", "📐", Color(0xFFE11D48)),
        SubjectItem("ICT", "তথ্যপ্রযুক্তি", "💻", Color(0xFF7C3AED)),
        SubjectItem("Bangla", "বাংলা", "📚", Color(0xFFD97706)),
        SubjectItem("English", "English", "📖", Color(0xFF2563EB)),
        SubjectItem("Exam Preparation", "পরীক্ষা প্রস্তুতি", "🎯", Color(0xFFDC2626))
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_column"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Top Student Greeting & Streak Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                EmeraldPrimary.copy(alpha = 0.15f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "চর্চা রিডার",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = EmeraldPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldPrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "ক্লাউড এডুকেশন",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "স্বাগতম, ${userProfile?.name ?: "শিক্ষার্থী"} 👋",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Streak Pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = AmberStreak.copy(alpha = 0.15f),
                        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp),
                        modifier = Modifier.clickable { onNavigateToStudy() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Streak",
                                tint = AmberStreak,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${userProfile?.streakDays ?: 7} দিন",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = AmberStreak
                            )
                        }
                    }
                }
            }
        }

        // Search bar quick jump
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clickable { onNavigateToLibrary("All") },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "বই, বিষয় বা লেখকের নাম খুঁজুন...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Today's Study Progress Strip
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .clickable { onNavigateToStudy() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "আজকের পড়ার অগ্রগতি",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "$totalMinutes / ${userProfile?.dailyGoalMinutes ?: 45} মিনিট",
                            style = MaterialTheme.typography.labelMedium,
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    val progress = ((totalMinutes.toFloat()) / (userProfile?.dailyGoalMinutes ?: 45).toFloat()).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = EmeraldPrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }

        // Continue Reading Hero Card
        if (continueBook != null) {
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                    Text(
                        text = "চলমান পাঠ (Continue Reading)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenBook(continueBook.id) }
                            .testTag("continue_reading_card"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = continueBook.coverUrl,
                                contentDescription = continueBook.title,
                                modifier = Modifier
                                    .size(width = 75.dp, height = 105.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldPrimary.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = continueBook.subject,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = EmeraldPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = continueBook.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "পৃষ্ঠা ${continueBook.lastReadPage} / ${continueBook.pageCount}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    LinearProgressIndicator(
                                        progress = { continueBook.progressPercent.coerceIn(0.05f, 1f) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = EmeraldPrimary,
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Surface(
                                        shape = CircleShape,
                                        color = EmeraldPrimary,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Read",
                                            tint = Color.White,
                                            modifier = Modifier
                                                .padding(6.dp)
                                                .fillMaxSize()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Minimal Phone Storage & Cloud Status Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = EmeraldDark.copy(alpha = 0.08f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = "Cloud Safe",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "জিরো ফোন স্টোরেজ প্রযুক্তি",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                            Text(
                                text = "ফোনে ক্যাশ: ${viewModel.formatBytes(cacheSize)} • মূল বই ক্লাউডে",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(
                        onClick = onNavigateToCacheSettings,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CleaningServices,
                            contentDescription = "Clean Cache",
                            tint = EmeraldPrimary
                        )
                    }
                }
            }
        }

        // Academic Subjects Grid Carousel
        item {
            Column(modifier = Modifier.padding(top = 12.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "বিষয়ভিত্তিক বই (Academic Subjects)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "সব দেখুন",
                        style = MaterialTheme.typography.labelMedium,
                        color = EmeraldPrimary,
                        modifier = Modifier.clickable { onNavigateToLibrary("All") }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(subjects) { subj ->
                        SubjectCard(
                            subject = subj,
                            onClick = { onNavigateToLibrary(subj.key) }
                        )
                    }
                }
            }
        }

        // All Books List
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ক্লাউড লাইব্রেরির বইসমূহ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${allBooks.size} টি বই",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        items(allBooks) { book ->
            BookHomeRowItem(
                book = book,
                onClick = { onOpenBook(book.id) },
                onToggleFavorite = { viewModel.toggleFavorite(book.id) }
            )
        }
    }
}

data class SubjectItem(
    val key: String,
    val banglaName: String,
    val iconEmoji: String,
    val color: Color
)

@Composable
fun SubjectCard(subject: SubjectItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(130.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = subject.color.copy(alpha = 0.12f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = subject.iconEmoji,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subject.banglaName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = subject.color,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subject.key,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun BookHomeRowItem(
    book: Book,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = book.coverUrl,
                contentDescription = book.title,
                modifier = Modifier
                    .size(width = 56.dp, height = 76.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = book.subject,
                        style = MaterialTheme.typography.labelSmall,
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    if (book.isCached) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = EmeraldPrimary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "⚡ ক্যাশড",
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
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
                                text = "☁️ ক্লাউড",
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${book.pageCount} পৃষ্ঠা • প্রগ্রেস: ${(book.progressPercent * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (book.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (book.isFavorite) Color(0xFFF43F5E) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
