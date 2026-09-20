package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.Book
import com.example.data.model.Bookmark
import com.example.data.model.Note
import com.example.data.model.StudySession
import com.example.data.model.UserProfile

@Database(
    entities = [
        Book::class,
        Bookmark::class,
        Note::class,
        StudySession::class,
        UserProfile::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun noteDao(): NoteDao
    abstract fun studyDao(): StudyDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "chorcha_reader_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
