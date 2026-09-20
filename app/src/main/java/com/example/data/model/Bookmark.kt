package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class Bookmark(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: String,
    val bookTitle: String,
    val pageNumber: Int,
    val title: String,
    val timestamp: Long = System.currentTimeMillis()
)
