package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 'current_user' LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile)

    @Update
    suspend fun updateProfile(profile: UserProfile)

    @Query("UPDATE user_profile SET isDarkMode = :darkMode WHERE id = 'current_user'")
    suspend fun setDarkMode(darkMode: Boolean)
}
