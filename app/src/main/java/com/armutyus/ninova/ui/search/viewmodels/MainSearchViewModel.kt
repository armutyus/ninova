package com.armutyus.ninova.ui.search.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armutyus.ninova.model.Books
import com.armutyus.ninova.repository.BooksRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainSearchViewModel @Inject constructor(
    private val booksRepository: BooksRepositoryInterface
) : ViewModel() {

    private val booksList = MutableLiveData<List<Books>>()
    val fakeBooksList: LiveData<List<Books>>
        get() = booksList

    fun getBooksList() {
        viewModelScope.launch {
            booksList.value = booksRepository.getBooksList()
        }
    }

    private val booksArchiveList = MutableLiveData<List<Books>>()
    val fakeBooksArchiveList: LiveData<List<Books>>
        get() = booksArchiveList

    fun getBooksArchiveList(searchString: String) {
        viewModelScope.launch {
            booksArchiveList.value = booksRepository.searchBooksFromLocal(searchString)
        }
    }

    private val booksApiList = MutableLiveData<List<Books>>()
    val fakeBooksApiList: LiveData<List<Books>>
        get() = booksApiList

    fun getBooksApiList(searchString: String) {
        viewModelScope.launch {
            booksApiList.value = booksRepository.searchBooksFromApi(searchString)
        }
    }

}