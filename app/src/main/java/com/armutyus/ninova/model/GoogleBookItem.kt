package com.armutyus.ninova.model

import com.armutyus.ninova.ui.books.BooksViewModel

data class GoogleBookItem(
    val id: String?,
    val volumeInfo: GoogleBookItemInfo?
) {
    fun isBookAddedCheck(booksViewModel: BooksViewModel): Boolean {
        val searchBookList = booksViewModel.localBookList.value?.find { it.bookId == id }
        return searchBookList != null
    }
}