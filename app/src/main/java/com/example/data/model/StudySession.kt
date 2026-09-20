package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_sessions")
data class StudySession(
    @PrimaryKey
    val date: String, // e.g. "2026-09-20"
    val minutesStudied: Int = 0,
    val pagesRead: Int = 0,
    val lastSubject: String = "Physics"
)
