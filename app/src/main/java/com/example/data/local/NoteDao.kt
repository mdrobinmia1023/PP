package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE bookId = :bookId ORDER BY pageNumber ASC, timestamp DESC")
    fun getNotesForBook(bookId: String): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE bookId = :bookId AND pageNumber = :page ORDER BY timestamp DESC")
    fun getNotesForPage(bookId: String, page: Int): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Update
    suspend fun updateNote(note: Note)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: Long)
}
