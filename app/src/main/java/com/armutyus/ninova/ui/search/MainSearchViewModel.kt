package com.armutyus.ninova.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armutyus.ninova.model.Book
import com.armutyus.ninova.repository.BooksRepositoryInterface
import com.armutyus.ninova.roomdb.LocalBook
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainSearchViewModel @Inject constructor(
    private val booksRepository: BooksRepositoryInterface
) : ViewModel() {

    private val booksList = MutableLiveData<List<Book>>()
    val fakeBooksList: LiveData<List<Book>>
        get() = booksList

    fun getBooksList() {
        viewModelScope.launch {
            booksList.value = booksRepository.getBookList()
        }
    }

    private val booksArchiveList = MutableLiveData<List<Book>>()
    val fakeBooksArchiveList: LiveData<List<Book>>
        get() = booksArchiveList

    fun getBooksArchiveList(searchString: String) {

        viewModelScope.launch {
            booksArchiveList.value = booksRepository.searchBookFromLocal(searchString)
        }

        /*CoroutineScope(Dispatchers.IO).launch {
            booksRepository.searchBookFromLocal(searchString).collectLatest {
                booksArchiveList.postValue(it)
            }
        }*/

    }

    private val booksApiList = MutableLiveData<List<Book>>()
    val fakeBooksApiList: LiveData<List<Book>>
        get() = booksApiList

    fun getBooksApiList(searchString: String) {

        viewModelScope.launch {
            booksApiList.value = booksRepository.searchBookFromApi(searchString)
        }

        /*CoroutineScope(Dispatchers.IO).launch {
            booksRepository.searchBookFromApi(searchString).collectLatest {
                booksApiList.postValue(it)
            }
        }*/

    }

    fun insertBook(localBook: LocalBook) = CoroutineScope(Dispatchers.IO).launch {
        booksRepository.insert(localBook)
    }

}