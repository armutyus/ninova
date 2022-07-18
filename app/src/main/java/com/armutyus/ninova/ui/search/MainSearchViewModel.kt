package com.armutyus.ninova.ui.search

import androidx.lifecycle.*
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.model.Book
import com.armutyus.ninova.model.GoogleApiBooks
import com.armutyus.ninova.repository.BooksRepositoryInterface
import com.armutyus.ninova.roomdb.entities.LocalBook
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainSearchViewModel @Inject constructor(
    private val booksRepository: BooksRepositoryInterface
) : ViewModel() {

    /*private val _currentList = MutableLiveData<List<Book>>()
    val currentList: LiveData<List<Book>>
        get() = _currentList

    private val booksList = MutableLiveData<List<Book>>()
    val fakeBooksList: LiveData<List<Book>>
        get() = booksList

    fun getBooksList() {
        viewModelScope.launch {
            booksList.value = booksRepository.getBookList()
        }
    }

    private val booksApiList = MutableLiveData<List<Book>>()
    val fakeBooksApiList: LiveData<List<Book>>
        get() = booksApiList

    fun getBooksApiList(searchString: String) {

        viewModelScope.launch {
            booksApiList.value = booksRepository.searchBookFromApi(searchString)
        }

    }

    fun setCurrentList(bookList: List<Book>) {
        _currentList.value = bookList
    }*/

    private val _searchLocalBookList = MutableLiveData<List<LocalBook>>()
    val searchLocalBookList: LiveData<List<LocalBook>>
        get() = _searchLocalBookList

    fun searchLocalBooks(searchString: String) {
        CoroutineScope(Dispatchers.IO).launch {
            booksRepository.searchLocalBooks(searchString).collectLatest {
                _searchLocalBookList.postValue(it)
            }
        }
    }

    private val _searchBooksResponse = MutableLiveData<Response<GoogleApiBooks>>()
    val searchBooksResponse: LiveData<Response<GoogleApiBooks>>
        get() = _searchBooksResponse

    fun searchBooksFromApi(searchQuery: String)= liveData(Dispatchers.IO) {
        booksRepository.searchBooksFromApi(searchQuery).collect { response ->
            emit(response)
        }
    }

    fun insertBook(localBook: LocalBook) = CoroutineScope(Dispatchers.IO).launch {
        booksRepository.insert(localBook)
    }
}