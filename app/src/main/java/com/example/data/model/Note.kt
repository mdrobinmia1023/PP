package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: String,
    val bookTitle: String,
    val pageNumber: Int,
    val content: String,
    val colorTag: String = "#059669",
    val timestamp: Long = System.currentTimeMillis()
)
