package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey
    val id: String,
    val title: String,
    val author: String,
    val subject: String,
    val description: String,
    val coverUrl: String,
    val pdfUrl: String,
    val pageCount: Int = 10,
    val fileSizeBytes: Long = 2_500_000L,
    val isCached: Boolean = false,
    val localCachePath: String? = null,
    val isFavorite: Boolean = false,
    val lastReadPage: Int = 1,
    val progressPercent: Float = 0f,
    val lastOpenedTimestamp: Long = 0L,
    val isPublished: Boolean = true,
    val chaptersJson: String = "[]"
)
