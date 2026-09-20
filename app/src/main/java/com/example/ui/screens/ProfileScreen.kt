package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.AmberStreak
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.viewmodel.MainViewModel

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    onNavigateToAdmin: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val cacheSize by viewModel.cacheSizeBytes.collectAsState()
    val cachedBooks by viewModel.cachedBooks.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showClearAllConfirm by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Column {
                Text(
                    text = "প্রোফাইল ও ডিভাইস মেমরি সেটিংস",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "ব্যক্তিগত তথ্য, অধ্যয়ন লক্ষ্য ও ক্লাউড ক্যাশ নিয়ন্ত্রণ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Student Profile Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = EmeraldPrimary,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = userProfile?.name ?: "Mohammad Robin",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = userProfile?.email ?: "mdrrrrobin1209@gmail.com",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldPrimary.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = userProfile?.academicTarget ?: "HSC Science & University Admission",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    fontSize = 11.sp,
                                    color = EmeraldPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        IconButton(onClick = { showEditProfileDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = EmeraldPrimary
                            )
                        }
                    }
                }
            }
        }

        // ZERO PHONE STORAGE / CACHE MANAGEMENT SECTION
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = EmeraldDark.copy(alpha = 0.15f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storage,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "ফোন মেমরি ও ক্যাশ নিয়ন্ত্রণ",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Zero Phone Storage Architecture",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldPrimary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = viewModel.formatBytes(cacheSize),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "আপনার ফোনের স্টোরেজ বাঁচাতে বইগুলো ক্লাউডে সংরক্ষিত থাকে এবং পড়ার সময় সাময়িকভাবে ক্যাশ হয়। যেকোনো সময় ক্যাশ মুছে দিলে ফোনের মেমরি ফাঁকা হবে, তবে ক্লাউডের বই এবং পড়ার হিসেব ঠিক থাকবে।",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { showClearAllConfirm = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.CleaningServices,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "সকল ক্যাশ মেমরি মুছুন (Clear All Cache)",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Cached Books List (if any)
        item {
            Text(
                text = "বর্তমানে ডিভাইসে ক্যাশ থাকা বই (${cachedBooks.size} টি)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        if (cachedBooks.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "ডিভাইসে কোনো অতিরিক্ত ক্যাশ নেই। ১০০% ক্লাউড মোডে চলছে।",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(cachedBooks, key = { it.id }) { book ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = book.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = "${book.subject} • পৃষ্ঠা ${book.lastReadPage}/${book.pageCount}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        OutlinedButton(
                            onClick = { viewModel.clearBookCache(book.id) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteOutline,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("মুছুন", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // App Settings & Admin
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Dark Mode Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DarkMode,
                                contentDescription = null,
                                tint = EmeraldPrimary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "ডার্ক মোড (Dark Theme)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Switch(
                            checked = userProfile?.isDarkMode == true,
                            onCheckedChange = { viewModel.setDarkMode(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    // Admin Dashboard Entry
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onNavigateToAdmin)
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = EmeraldPrimary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "ক্লাউড অ্যাডমিন ড্যাশবোর্ড",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "নতুন বই আপলোড, লিংক ও কনটেন্ট নিয়ন্ত্রণ",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldPrimary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "প্রবেশ",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                        }
                    }
                }
            }
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        var nameInput by remember { mutableStateOf(userProfile?.name ?: "") }
        var targetInput by remember { mutableStateOf(userProfile?.academicTarget ?: "") }
        var goalInput by remember { mutableStateOf((userProfile?.dailyGoalMinutes ?: 45).toString()) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("প্রোফাইল আপডেট করুন", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("আপনার নাম") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = targetInput,
                        onValueChange = { targetInput = it },
                        label = { Text("পরীক্ষার লক্ষ্য / ক্লাস") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = goalInput,
                        onValueChange = { goalInput = it.filter { c -> c.isDigit() } },
                        label = { Text("দৈনিক পড়ার লক্ষ্য (মিনিট)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val goal = goalInput.toIntOrNull() ?: 45
                        viewModel.updateProfile(nameInput, targetInput, goal)
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("সংরক্ষণ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // Clear All Cache Confirmation Dialog
    if (showClearAllConfirm) {
        AlertDialog(
            onDismissRequest = { showClearAllConfirm = false },
            title = { Text("সব ক্যাশ মুছে ফেলবেন?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "ক্যাশ মেমরি মুছে ফেললে আপনার ফোনের ফাঁকা জায়গা বাড়বে। ক্লাউডের বই বা আপনার দাগানো বুকমার্ক/নোটসে কোনো প্রভাব পড়বে না।",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllCache()
                        showClearAllConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48))
                ) {
                    Text("হ্যাঁ, ক্যাশ মুছুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllConfirm = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}
