package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books ORDER BY lastOpenedTimestamp DESC")
    fun getAllBooks(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE subject = :subject ORDER BY title ASC")
    fun getBooksBySubject(subject: String): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE isFavorite = 1")
    fun getFavoriteBooks(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE isCached = 1")
    fun getCachedBooks(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE lastOpenedTimestamp > 0 ORDER BY lastOpenedTimestamp DESC LIMIT 5")
    fun getRecentlyReadBooks(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getBookById(id: String): Book?

    @Query("SELECT * FROM books WHERE id = :id")
    fun getBookByIdFlow(id: String): Flow<Book?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<Book>)

    @Update
    suspend fun updateBook(book: Book)

    @Query("UPDATE books SET lastReadPage = :page, progressPercent = :percent, lastOpenedTimestamp = :timestamp WHERE id = :bookId")
    suspend fun updateReadingProgress(bookId: String, page: Int, percent: Float, timestamp: Long)

    @Query("UPDATE books SET isCached = :isCached, localCachePath = :cachePath WHERE id = :bookId")
    suspend fun updateCacheStatus(bookId: String, isCached: Boolean, cachePath: String?)

    @Query("UPDATE books SET isCached = 0, localCachePath = NULL")
    suspend fun clearAllCacheStatus()

    @Query("UPDATE books SET isFavorite = NOT isFavorite WHERE id = :bookId")
    suspend fun toggleFavorite(bookId: String)

    @Query("DELETE FROM books WHERE id = :id")
    suspend fun deleteBookById(id: String)
}
