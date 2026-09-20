package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberStreak
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay

@Composable
fun StudyScreen(
    viewModel: MainViewModel,
    onNavigateToLibrary: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val totalMinutes by viewModel.totalMinutesStudied.collectAsState()
    val totalPages by viewModel.totalPagesRead.collectAsState()

    var isTimerRunning by remember { mutableStateOf(false) }
    var timerSeconds by remember { mutableIntStateOf(0) }
    var selectedTimerSubject by remember { mutableStateOf("Physics") }

    // Live Study Timer loop
    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning) {
            delay(1000L)
            timerSeconds++
        }
    }

    val goalMinutes = userProfile?.dailyGoalMinutes ?: 45
    val progress = (totalMinutes.toFloat() / goalMinutes.toFloat()).coerceIn(0f, 1f)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("study_screen"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Column {
                Text(
                    text = "স্টাডি ট্র্যাকার ও চর্চা রুটিন",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "দৈনিক পড়াশোনার ধারাবাহিকতা ও সময় নির্ধারণ করুন",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Streak & Daily Target Hero
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = AmberStreak.copy(alpha = 0.15f),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = "Streak",
                                    tint = AmberStreak,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "${userProfile?.streakDays ?: 7} দিনের স্টাডি স্ট্রিক 🔥",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberStreak
                                )
                                Text(
                                    text = "ধারাবাহিক চর্চায় সাফল্যের সম্ভাবনা বাড়ে",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Daily Target Circular / Linear Stat
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$totalMinutes মিনিট",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = EmeraldPrimary
                            )
                            Text(
                                text = "আজকের পাঠ সময়",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(64.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxSize(),
                                color = EmeraldPrimary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                strokeWidth = 6.dp
                            )
                            Text(
                                text = "${(progress * 100).toInt()}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$totalPages পৃষ্ঠা",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = CyanAccent
                            )
                            Text(
                                text = "পড়া পৃষ্ঠা",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Focus Reading Stopwatch / Timer
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = EmeraldDark.copy(alpha = 0.08f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Timer",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ফোকাস স্টাডি টাইমার (Study Stopwatch)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val minutes = timerSeconds / 60
                    val seconds = timerSeconds % 60
                    val formattedTime = String.format("%02d:%02d", minutes, seconds)

                    Text(
                        text = formattedTime,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldPrimary
                    )
                    Text(
                        text = if (isTimerRunning) "পড়াশোনা চলমান..." else "টাইমার প্রস্তুত",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { isTimerRunning = !isTimerRunning },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isTimerRunning) Color(0xFFE11D48) else EmeraldPrimary
                            ),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
                        ) {
                            Icon(
                                imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isTimerRunning) "বিরতি নিন" else "পড়া শুরু করুন",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (timerSeconds > 0) {
                            OutlinedButton(
                                onClick = {
                                    isTimerRunning = false
                                    timerSeconds = 0
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Reset",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("রিসেট")
                            }
                        }
                    }
                }
            }
        }

        // Subject Breakdown Progress
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "বিষয়ভিত্তিক অধ্যয়ন প্রগ্রেস",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    val subjectProgresses = listOf(
                        Triple("পদার্থবিজ্ঞান (Physics)", 0.45f, Color(0xFF0284C7)),
                        Triple("রসায়ন (Chemistry)", 0.60f, Color(0xFF0D9488)),
                        Triple("উচ্চতর গণিত (Higher Math)", 0.35f, Color(0xFFE11D48)),
                        Triple("জীববিজ্ঞান (Biology)", 0.20f, Color(0xFF16A34A)),
                        Triple("আইসিটি (ICT)", 0.50f, Color(0xFF7C3AED)),
                        Triple("ভর্তি প্রশ্নব্যাংক (Question Bank)", 0.75f, Color(0xFFDC2626))
                    )

                    subjectProgresses.forEach { (subject, frac, col) ->
                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = subject,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${(frac * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = col
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { frac },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = col,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Quick Tip for Admission & Board Exams
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AmberStreak.copy(alpha = 0.10f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Tips",
                        tint = AmberStreak,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "চর্চা টিপস: মেমরি ও রিভিশন টেকনিক",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AmberStreak
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "প্রতিটি অধ্যায় পড়ার পর গুরুত্বপূর্ণ সূত্রগুলো নোটসে সংরক্ষণ করুন। সপ্তাহে একদিন পেছনের অধ্যায়গুলো রিভিশন দিলে পরীক্ষার হলে দ্বিধা কেটে যায়।",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
