package com.example.data.repository

import android.content.Context
import com.example.data.cache.PdfCacheManager
import com.example.data.local.AppDatabase
import com.example.data.model.Book
import com.example.data.model.Bookmark
import com.example.data.model.Note
import com.example.data.model.StudySession
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BookRepository(
    private val database: AppDatabase,
    val cacheManager: PdfCacheManager
) {
    private val bookDao = database.bookDao()
    private val bookmarkDao = database.bookmarkDao()
    private val noteDao = database.noteDao()
    private val studyDao = database.studyDao()
    private val userProfileDao = database.userProfileDao()

    val allBooks: Flow<List<Book>> = bookDao.getAllBooks()
    val favoriteBooks: Flow<List<Book>> = bookDao.getFavoriteBooks()
    val cachedBooks: Flow<List<Book>> = bookDao.getCachedBooks()
    val recentlyReadBooks: Flow<List<Book>> = bookDao.getRecentlyReadBooks()
    val allBookmarks: Flow<List<Bookmark>> = bookmarkDao.getAllBookmarks()
    val allNotes: Flow<List<Note>> = noteDao.getAllNotes()
    val userProfile: Flow<UserProfile?> = userProfileDao.getUserProfile()
    val totalMinutesStudied: Flow<Int> = studyDao.getTotalMinutesStudied()
    val totalPagesRead: Flow<Int> = studyDao.getTotalPagesRead()

    fun getBooksBySubject(subject: String): Flow<List<Book>> = bookDao.getBooksBySubject(subject)
    fun getBookmarksForBook(bookId: String): Flow<List<Bookmark>> = bookmarkDao.getBookmarksForBook(bookId)
    fun getNotesForBook(bookId: String): Flow<List<Note>> = noteDao.getNotesForBook(bookId)
    fun getBookFlow(id: String): Flow<Book?> = bookDao.getBookByIdFlow(id)

    suspend fun getBook(id: String): Book? = bookDao.getBookById(id)

    suspend fun seedInitialDataIfEmpty() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        // Initialize user profile
        userProfileDao.insertOrUpdateProfile(
            UserProfile(
                id = "current_user",
                name = "Mohammad Robin",
                email = "mdrrrrobin1209@gmail.com",
                academicTarget = "HSC Science & University Admission",
                streakDays = 5,
                dailyGoalMinutes = 45,
                isDarkMode = false,
                role = "Admin"
            )
        )

        // Seed initial study session
        if (studyDao.getSessionByDate(today) == null) {
            studyDao.insertOrUpdateSession(
                StudySession(
                    date = today,
                    minutesStudied = 28,
                    pagesRead = 14,
                    lastSubject = "Physics"
                )
            )
        }

        // Check if books are seeded
        val existing = bookDao.getBookById("physics_1st")
        if (existing == null) {
            val initialBooks = listOf(
                Book(
                    id = "physics_1st",
                    title = "পদার্থবিজ্ঞান ১ম পত্র (গতিবিদ্যা ও বলবিদ্যা)",
                    author = "ড. শাহজাহান তপন ও ড. রানা চৌধুরী",
                    subject = "Physics",
                    description = "দ্বাদশ ও একাদশ শ্রেণির পদার্থবিজ্ঞান ১ম পত্রের গতিবিদ্যা, নিউটনিয়ান বলবিদ্যা, কাজ শক্তি ও ক্ষমতার পূর্ণাঙ্গ আলোচনা।",
                    coverUrl = "https://images.unsplash.com/photo-1636466497217-26a8cbeaf0aa?w=600&auto=format&fit=crop&q=60",
                    pdfUrl = "https://chorcha-cloud.r2.dev/physics_1st.pdf",
                    pageCount = 18,
                    fileSizeBytes = 3_450_000L,
                    isFavorite = true,
                    lastReadPage = 4,
                    progressPercent = 0.22f,
                    lastOpenedTimestamp = System.currentTimeMillis() - 3600000L
                ),
                Book(
                    id = "chemistry_1st",
                    title = "রসায়ন ১ম পত্র (পর্যায়বৃত্ত ধর্ম ও বন্ধন)",
                    author = "সঞ্জিত কুমার গুহ",
                    subject = "Chemistry",
                    description = "মৌলের পর্যায়বৃত্ত ধর্ম ও রাসায়নিক বন্ধন, সংকরায়ন এবং বিভিন্ন সমযোজী যৌগের জ্যামিতিক কাঠামোর বিস্তারিত ধারণা।",
                    coverUrl = "https://images.unsplash.com/photo-1603126857599-f6e157fa2fe6?w=600&auto=format&fit=crop&q=60",
                    pdfUrl = "https://chorcha-cloud.r2.dev/chemistry_1st.pdf",
                    pageCount = 20,
                    fileSizeBytes = 4_100_000L,
                    isFavorite = true,
                    lastReadPage = 6,
                    progressPercent = 0.30f,
                    lastOpenedTimestamp = System.currentTimeMillis() - 7200000L
                ),
                Book(
                    id = "math_calculus",
                    title = "উচ্চতর গণিত ১ম পত্র (ক্যালকুলাস ও ভেক্টর)",
                    author = "এস ইউ আহাম্মদ ও খন্দকার ফারুক",
                    subject = "Higher Mathematics",
                    description = "সীমা (Limit), অন্তরীকরণ (Differentiation) এবং যৌগিক আকারের যোগজীকরণের সহজবোধ্য নিয়ম ও গাণিতিক সমস্যা।",
                    coverUrl = "https://images.unsplash.com/photo-1509228468518-180dd4864904?w=600&auto=format&fit=crop&q=60",
                    pdfUrl = "https://chorcha-cloud.r2.dev/math_calculus.pdf",
                    pageCount = 24,
                    fileSizeBytes = 5_200_000L,
                    isFavorite = false,
                    lastReadPage = 1,
                    progressPercent = 0.04f,
                    lastOpenedTimestamp = System.currentTimeMillis() - 14400000L
                ),
                Book(
                    id = "biology_botany",
                    title = "উদ্ভিদবিজ্ঞান (কোষ ও জিনতত্ত্ব)",
                    author = "ড. আবুল হাসান",
                    subject = "Biology",
                    description = "কোষের গঠন, ডিএনএ রেপ্লিকেশন, প্রোটিন সংশ্লেষণ এবং টিস্যু ও টিস্যুতন্ত্র অধ্যায়ের মেডিকেল ভর্তিভিত্তিক প্রস্তুতি।",
                    coverUrl = "https://images.unsplash.com/photo-1530281700549-e82e7bf110d6?w=600&auto=format&fit=crop&q=60",
                    pdfUrl = "https://chorcha-cloud.r2.dev/biology_botany.pdf",
                    pageCount = 16,
                    fileSizeBytes = 3_800_000L,
                    isFavorite = false,
                    lastReadPage = 1,
                    progressPercent = 0f,
                    lastOpenedTimestamp = 0L
                ),
                Book(
                    id = "ict_hsc",
                    title = "তথ্য ও যোগাযোগ প্রযুক্তি (HTML ও C প্রোগ্রামিং)",
                    author = "প্রকৌশলী মুজিবুর রহমান",
                    subject = "ICT",
                    description = "এইচএসসি আইসিটি ৩য় ও ৫ম অধ্যায়: সংখ্যা পদ্ধতি, লজিক গেট এবং সি প্রোগ্রামিং ভাষার পূর্ণাঙ্গ গাইডলাইন।",
                    coverUrl = "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=600&auto=format&fit=crop&q=60",
                    pdfUrl = "https://chorcha-cloud.r2.dev/ict_hsc.pdf",
                    pageCount = 14,
                    fileSizeBytes = 2_900_000L,
                    isFavorite = true,
                    lastReadPage = 2,
                    progressPercent = 0.14f,
                    lastOpenedTimestamp = System.currentTimeMillis() - 86400000L
                ),
                Book(
                    id = "bangla_sahitya",
                    title = "বাংলা সাহিত্য পাঠ ও ব্যাকরণ",
                    author = "জাতীয় শিক্ষাক্রম ও পাঠ্যপুস্তক বোর্ড (NCTB)",
                    subject = "Bangla",
                    description = "এইচএসসি বাংলা ১ম ও ২য় পত্র: গুরুত্বপূর্ণ গদ্য, কবিতা এবং ব্যাকরণ অংশের শব্দগঠন ও উচ্চারণরীতি।",
                    coverUrl = "https://images.unsplash.com/photo-1457369804613-52c61a468e7d?w=600&auto=format&fit=crop&q=60",
                    pdfUrl = "https://chorcha-cloud.r2.dev/bangla_sahitya.pdf",
                    pageCount = 12,
                    fileSizeBytes = 2_100_000L,
                    isFavorite = false,
                    lastReadPage = 1,
                    progressPercent = 0f,
                    lastOpenedTimestamp = 0L
                ),
                Book(
                    id = "english_for_today",
                    title = "English Grammar & Academic Composition",
                    author = "NCTB & Academic Board",
                    subject = "English",
                    description = "Comprehensive modifier exercises, connectors, cloze test with/without clues, and formal letter writing.",
                    coverUrl = "https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?w=600&auto=format&fit=crop&q=60",
                    pdfUrl = "https://chorcha-cloud.r2.dev/english_for_today.pdf",
                    pageCount = 15,
                    fileSizeBytes = 2_600_000L,
                    isFavorite = false,
                    lastReadPage = 1,
                    progressPercent = 0f,
                    lastOpenedTimestamp = 0L
                ),
                Book(
                    id = "admission_question_bank",
                    title = "মেডিকেল ও বুয়েট ভর্তি প্রশ্নব্যাংক ও সমাধান",
                    author = "চর্চা একাডেমি স্পেশাল এনালাইসিস",
                    subject = "Exam Preparation",
                    description = "বিগত ১৫ বছরের ঢাকা বিশ্ববিদ্যালয় ‘ক’ ইউনিট, বুয়েট ও মেডিকেল ভর্তি পরীক্ষার নির্ভুল ব্যাখ্যাসহ সমাধান।",
                    coverUrl = "https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=600&auto=format&fit=crop&q=60",
                    pdfUrl = "https://chorcha-cloud.r2.dev/admission_question_bank.pdf",
                    pageCount = 30,
                    fileSizeBytes = 6_800_000L,
                    isFavorite = true,
                    lastReadPage = 5,
                    progressPercent = 0.17f,
                    lastOpenedTimestamp = System.currentTimeMillis() - 1800000L
                )
            )
            bookDao.insertBooks(initialBooks)

            // Add sample bookmark and note for demo
            bookmarkDao.insertBookmark(
                Bookmark(
                    bookId = "physics_1st",
                    bookTitle = "পদার্থবিজ্ঞান ১ম পত্র (গতিবিদ্যা ও বলবিদ্যা)",
                    pageNumber = 4,
                    title = "নিউটনের ৩য় সূত্র ও ভরবেগের সংরক্ষণশীলতা নীতি"
                )
            )
            noteDao.insertNote(
                Note(
                    bookId = "physics_1st",
                    bookTitle = "পদার্থবিজ্ঞান ১ম পত্র (গতিবিদ্যা ও বলবিদ্যা)",
                    pageNumber = 4,
                    content = "ভরবেগের নিত্যতা সূত্র প্রয়োগের সময় দিক (+ / -) সতর্কভাবে চিহ্নিত করতে হবে। বুয়েট ২০২৪ প্রশ্নে এসেছিল।"
                )
            )
        }
    }

    suspend fun openAndCacheBook(book: Book, onProgress: (Float) -> Unit): File {
        val file = cacheManager.getOrFetchPdfFile(book, onProgress)
        bookDao.updateCacheStatus(book.id, true, file.absolutePath)
        return file
    }

    suspend fun saveReadingProgress(bookId: String, page: Int, totalPages: Int) {
        val percent = if (totalPages > 0) page.toFloat() / totalPages.toFloat() else 0f
        bookDao.updateReadingProgress(bookId, page, percent, System.currentTimeMillis())

        // Update today's study progress
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val session = studyDao.getSessionByDate(today)
        if (session != null) {
            studyDao.insertOrUpdateSession(
                session.copy(
                    minutesStudied = session.minutesStudied + 1,
                    pagesRead = session.pagesRead + 1
                )
            )
        } else {
            studyDao.insertOrUpdateSession(
                StudySession(
                    date = today,
                    minutesStudied = 1,
                    pagesRead = 1,
                    lastSubject = "General"
                )
            )
        }
    }

    suspend fun toggleFavorite(bookId: String) = bookDao.toggleFavorite(bookId)

    suspend fun clearBookCache(bookId: String) {
        cacheManager.clearBookCache(bookId)
        bookDao.updateCacheStatus(bookId, false, null)
    }

    suspend fun clearAllCache(): Long {
        val deleted = cacheManager.clearAllCache()
        bookDao.clearAllCacheStatus()
        return deleted
    }

    suspend fun insertBook(book: Book) = bookDao.insertBook(book)
    suspend fun deleteBook(id: String) {
        cacheManager.clearBookCache(id)
        bookDao.deleteBookById(id)
    }

    // Bookmarks
    suspend fun addBookmark(bookId: String, bookTitle: String, page: Int, title: String) {
        bookmarkDao.insertBookmark(
            Bookmark(
                bookId = bookId,
                bookTitle = bookTitle,
                pageNumber = page,
                title = title
            )
        )
    }

    suspend fun removeBookmark(id: Long) = bookmarkDao.deleteBookmarkById(id)
    suspend fun removeBookmarkForPage(bookId: String, page: Int) = bookmarkDao.deleteBookmarkForPage(bookId, page)
    suspend fun isPageBookmarked(bookId: String, page: Int): Boolean = bookmarkDao.isPageBookmarked(bookId, page)

    // Notes
    suspend fun addNote(bookId: String, bookTitle: String, page: Int, content: String, colorTag: String = "#059669") {
        noteDao.insertNote(
            Note(
                bookId = bookId,
                bookTitle = bookTitle,
                pageNumber = page,
                content = content,
                colorTag = colorTag
            )
        )
    }

    suspend fun deleteNote(id: Long) = noteDao.deleteNoteById(id)

    // Profile
    suspend fun updateProfile(profile: UserProfile) = userProfileDao.updateProfile(profile)
    suspend fun setDarkMode(enabled: Boolean) = userProfileDao.setDarkMode(enabled)

    companion object {
        @Volatile
        private var INSTANCE: BookRepository? = null

        fun getInstance(context: Context): BookRepository {
            return INSTANCE ?: synchronized(this) {
                val db = AppDatabase.getInstance(context)
                val cache = PdfCacheManager(context)
                val repo = BookRepository(db, cache)
                INSTANCE = repo
                repo
            }
        }
    }
}
