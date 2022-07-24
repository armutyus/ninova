package com.armutyus.ninova.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.armutyus.ninova.ui.books.BooksViewModel

sealed class DataModel {

    data class GoogleBookItem(
        val id: String?,
        val volumeInfo: GoogleBookItemInfo?
    ) : DataModel() {
        fun isBookAddedCheck(booksViewModel: BooksViewModel): Boolean {
            val searchBookList = booksViewModel.localBookList.value?.find { it.bookId == id }
            return searchBookList != null
        }
    }

    @Entity(tableName = "Book")
    data class LocalBook(
        @PrimaryKey(autoGenerate = false) var bookId: String,
        var bookTitle: String?,
        var bookSubtitle: String?,
        var bookAuthors: List<String>?,
        var bookPages: String?,
        var bookCoverSmallThumbnail: String?,
        var bookCoverThumbnail: String?,
        var bookDescription: String?,
        var bookPublishedDate: String?,
        var bookCategories: List<String>?,
        var bookPublisher: String?,
        var bookNotes: String?
    ) : DataModel()

}
