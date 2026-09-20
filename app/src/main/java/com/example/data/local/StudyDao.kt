package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.StudySession
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyDao {
    @Query("SELECT * FROM study_sessions WHERE date = :date")
    suspend fun getSessionByDate(date: String): StudySession?

    @Query("SELECT * FROM study_sessions WHERE date = :date")
    fun getSessionByDateFlow(date: String): Flow<StudySession?>

    @Query("SELECT * FROM study_sessions ORDER BY date DESC LIMIT 30")
    fun getRecentSessions(): Flow<List<StudySession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSession(session: StudySession)

    @Query("SELECT COALESCE(SUM(minutesStudied), 0) FROM study_sessions")
    fun getTotalMinutesStudied(): Flow<Int>

    @Query("SELECT COALESCE(SUM(pagesRead), 0) FROM study_sessions")
    fun getTotalPagesRead(): Flow<Int>
}
