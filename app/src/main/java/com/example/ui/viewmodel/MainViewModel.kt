package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Book
import com.example.data.model.Bookmark
import com.example.data.model.Note
import com.example.data.model.UserProfile
import com.example.data.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val repository = BookRepository.getInstance(application)

    val allBooks: StateFlow<List<Book>> = repository.allBooks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentlyReadBooks: StateFlow<List<Book>> = repository.recentlyReadBooks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteBooks: StateFlow<List<Book>> = repository.favoriteBooks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cachedBooks: StateFlow<List<Book>> = repository.cachedBooks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBookmarks: StateFlow<List<Bookmark>> = repository.allBookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotes: StateFlow<List<Note>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val totalMinutesStudied: StateFlow<Int> = repository.totalMinutesStudied
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 28)

    val totalPagesRead: StateFlow<Int> = repository.totalPagesRead
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 14)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedSubject = MutableStateFlow("All")
    val selectedSubject: StateFlow<String> = _selectedSubject.asStateFlow()

    private val _activeFilter = MutableStateFlow("All") // "All", "Cached", "Favorites"
    val activeFilter: StateFlow<String> = _activeFilter.asStateFlow()

    private val _cacheSizeBytes = MutableStateFlow(0L)
    val cacheSizeBytes: StateFlow<Long> = _cacheSizeBytes.asStateFlow()

    val filteredBooks: StateFlow<List<Book>> = combine(
        allBooks,
        _searchQuery,
        _selectedSubject,
        _activeFilter
    ) { books, query, subject, filter ->
        books.filter { book ->
            val matchesQuery = query.isBlank() ||
                    book.title.contains(query, ignoreCase = true) ||
                    book.author.contains(query, ignoreCase = true) ||
                    book.subject.contains(query, ignoreCase = true)

            val matchesSubject = subject == "All" || book.subject.equals(subject, ignoreCase = true)

            val matchesFilter = when (filter) {
                "Cached" -> book.isCached
                "Favorites" -> book.isFavorite
                else -> true
            }

            matchesQuery && matchesSubject && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
            refreshCacheSize()
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onSubjectSelected(subject: String) {
        _selectedSubject.value = subject
    }

    fun onFilterSelected(filter: String) {
        _activeFilter.value = filter
    }

    fun refreshCacheSize() {
        _cacheSizeBytes.value = repository.cacheManager.getTotalCacheSizeBytes()
    }

    fun formatBytes(bytes: Long): String {
        return repository.cacheManager.formatBytes(bytes)
    }

    fun toggleFavorite(bookId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(bookId)
        }
    }

    fun clearBookCache(bookId: String) {
        viewModelScope.launch {
            repository.clearBookCache(bookId)
            refreshCacheSize()
        }
    }

    fun clearAllCache(onCompleted: (String) -> Unit = {}) {
        viewModelScope.launch {
            val bytes = repository.clearAllCache()
            refreshCacheSize()
            onCompleted(formatBytes(bytes))
        }
    }

    fun addNewBook(
        title: String,
        author: String,
        subject: String,
        description: String,
        pdfUrl: String,
        coverUrl: String,
        pageCount: Int
    ) {
        viewModelScope.launch {
            val id = "book_" + System.currentTimeMillis()
            val finalCover = if (coverUrl.isNotBlank()) coverUrl else "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600&auto=format&fit=crop&q=60"
            val finalPdf = if (pdfUrl.isNotBlank()) pdfUrl else "https://chorcha-cloud.r2.dev/$id.pdf"
            val newBook = Book(
                id = id,
                title = title,
                author = author.ifBlank { "শিক্ষক মণ্ডলী" },
                subject = subject,
                description = description.ifBlank { "ক্লাউড স্টোরেজে সংগৃহীত একাডেমিক পাঠ্যবই।" },
                coverUrl = finalCover,
                pdfUrl = finalPdf,
                pageCount = if (pageCount > 0) pageCount else 15,
                fileSizeBytes = 2_800_000L
            )
            repository.insertBook(newBook)
        }
    }

    fun deleteBook(bookId: String) {
        viewModelScope.launch {
            repository.deleteBook(bookId)
            refreshCacheSize()
        }
    }

    fun addBookmark(bookId: String, bookTitle: String, page: Int, title: String) {
        viewModelScope.launch {
            repository.addBookmark(bookId, bookTitle, page, title)
        }
    }

    fun removeBookmark(id: Long) {
        viewModelScope.launch {
            repository.removeBookmark(id)
        }
    }

    fun addNote(bookId: String, bookTitle: String, page: Int, content: String, colorTag: String = "#059669") {
        viewModelScope.launch {
            repository.addNote(bookId, bookTitle, page, content, colorTag)
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            repository.deleteNote(id)
        }
    }

    fun updateProfile(name: String, academicTarget: String, dailyGoal: Int) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfile()
            repository.updateProfile(
                current.copy(
                    name = name,
                    academicTarget = academicTarget,
                    dailyGoalMinutes = dailyGoal
                )
            )
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            repository.setDarkMode(enabled)
        }
    }
}
