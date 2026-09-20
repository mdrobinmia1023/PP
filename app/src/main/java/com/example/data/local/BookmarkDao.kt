package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.Bookmark
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<Bookmark>>

    @Query("SELECT * FROM bookmarks WHERE bookId = :bookId ORDER BY pageNumber ASC")
    fun getBookmarksForBook(bookId: String): Flow<List<Bookmark>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: Bookmark): Long

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmarkById(id: Long)

    @Query("DELETE FROM bookmarks WHERE bookId = :bookId AND pageNumber = :page")
    suspend fun deleteBookmarkForPage(bookId: String, page: Int)

    @Query("SELECT COUNT(*) > 0 FROM bookmarks WHERE bookId = :bookId AND pageNumber = :page")
    suspend fun isPageBookmarked(bookId: String, page: Int): Boolean
}
