package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.example.data.model.Book
import com.example.ui.theme.AmberStreak
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val books by viewModel.allBooks.collectAsState()
    val cacheSize by viewModel.cacheSizeBytes.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showArchitectureGuide by remember { mutableStateOf(false) }

    // Form states
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf("Physics") }
    var description by remember { mutableStateOf("") }
    var pdfUrl by remember { mutableStateOf("") }
    var coverUrl by remember { mutableStateOf("") }
    var pageCountText by remember { mutableStateOf("20") }
    var isSubjectDropdownExpanded by remember { mutableStateOf(false) }

    val subjectList = listOf(
        "Physics",
        "Chemistry",
        "Biology",
        "Higher Mathematics",
        "ICT",
        "Bangla",
        "English",
        "Exam Preparation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_screen")
    ) {
        // Top App Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ক্লাউড অ্যাডমিন ড্যাশবোর্ড",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "বই যোগ, ক্লাউড স্টোরেজ ও মেটাডাটা কন্ট্রোল",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { showArchitectureGuide = true }) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = "Architecture Guide",
                        tint = EmeraldPrimary
                    )
                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Stats Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "মোট ক্লাউড বই: ${books.size} টি",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "ডিভাইস ক্যাশ: ${viewModel.formatBytes(cacheSize)} (জিরো স্টোরেজ মোড)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = { showAddDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("নতুন বই", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Architecture Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldDark.copy(alpha = 0.08f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Cloudflare R2 ও Firebase সংযুক্তির নিয়ম",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Cloudflare R2 বা Firebase Storage এ আপনার PDF আপলোড করে পাবলিক লিংক বা সাইনড URL সরাসরি নিচে যুক্ত করতে পারেন। কোনো সিক্রেট কি অ্যাপ্লিকেশনের ভেতরে রাখতে হয় না।",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Existing Books Management List
            item {
                Text(
                    text = "লাইব্রেরির বইসমূহ (${books.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(books, key = { it.id }) { b ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = EmeraldPrimary.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = b.subject,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontSize = 10.sp,
                                        color = EmeraldPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${b.pageCount} পৃষ্ঠা",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = b.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "লেখক: ${b.author}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                            Text(
                                text = "লিংক: ${b.pdfUrl}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        IconButton(onClick = { viewModel.deleteBook(b.id) }) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteOutline,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Book Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("নতুন বই যুক্ত করুন", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("বইয়ের নাম (Title)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = author,
                            onValueChange = { author = it },
                            label = { Text("লেখক / সংকলক (Author)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        ExposedDropdownMenuBox(
                            expanded = isSubjectDropdownExpanded,
                            onExpandedChange = { isSubjectDropdownExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedSubject,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("বিষয় (Subject)") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isSubjectDropdownExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = isSubjectDropdownExpanded,
                                onDismissRequest = { isSubjectDropdownExpanded = false }
                            ) {
                                subjectList.forEach { s ->
                                    DropdownMenuItem(
                                        text = { Text(s) },
                                        onClick = {
                                            selectedSubject = s
                                            isSubjectDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = pdfUrl,
                            onValueChange = { pdfUrl = it },
                            label = { Text("ক্লাউড PDF URL (R2 / Drive / Web)") },
                            placeholder = { Text("https://example.com/book.pdf") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = coverUrl,
                            onValueChange = { coverUrl = it },
                            label = { Text("কভার ছবির URL (ঐচ্ছিক)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = pageCountText,
                            onValueChange = { pageCountText = it.filter { c -> c.isDigit() } },
                            label = { Text("আনুমানিক পৃষ্ঠা সংখ্যা") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("সংক্ষিপ্ত বিবরণ") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.addNewBook(
                                title = title.trim(),
                                author = author.trim(),
                                subject = selectedSubject,
                                description = description.trim(),
                                pdfUrl = pdfUrl.trim(),
                                coverUrl = coverUrl.trim(),
                                pageCount = pageCountText.toIntOrNull() ?: 15
                            )
                            // Reset form
                            title = ""
                            author = ""
                            description = ""
                            pdfUrl = ""
                            coverUrl = ""
                            showAddDialog = false
                        }
                    },
                    enabled = title.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("প্রকাশ করুন (Add Book)")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // Architecture Guide Dialog (Simple Bangla instructions)
    if (showArchitectureGuide) {
        AlertDialog(
            onDismissRequest = { showArchitectureGuide = false },
            title = { Text("ক্লাউড আর্কিটেকচার গাইড", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "১. Cloudflare R2 (ফ্রি ১০ জিবি স্টোরেজ):",
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                    Text(
                        text = "Cloudflare R2 এ একটি বাকেট তৈরি করে আপনার পিডিএফ ফাইলগুলো আপলোড করুন। বাকেটের 'Public Access' চালু করলে বাকেটটির একটি URL পাবেন। সেই লিঙ্কটি এখানে 'ক্লাউড PDF URL' হিসেবে দিলেই কাজ করবে।",
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "২. অন-ডিমান্ড ক্যাশিং কিভাবে কাজ করে:",
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                    Text(
                        text = "অ্যাপটি সম্পূর্ণ ফাইল ফোনে স্থায়ীভাবে জমিয়ে রাখে না। পড়ার সময় ফাইলটি সাময়িক ক্যাশে এনে দ্রুত পেজ রেন্ডার করে। ব্যবহারকারী 'ক্যাশ মুছুন' দিলেই ফোন আবার ফাঁকা হয়ে যায়।",
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showArchitectureGuide = false },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("বুঝেছি")
                }
            }
        )
    }
}
