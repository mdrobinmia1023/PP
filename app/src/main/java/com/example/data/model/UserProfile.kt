package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val id: String = "current_user",
    val name: String = "Mohammad Robin",
    val email: String = "mdrrrrobin1209@gmail.com",
    val academicTarget: String = "HSC Science & University Admission",
    val streakDays: Int = 7,
    val dailyGoalMinutes: Int = 60,
    val isDarkMode: Boolean = false,
    val role: String = "Admin"
)
