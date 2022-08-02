package com.armutyus.ninova.ui.books

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.model.BookDetails
import com.armutyus.ninova.model.DataModel
import com.armutyus.ninova.repository.BooksRepositoryInterface
import com.armutyus.ninova.roomdb.entities.BookWithShelves
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BooksViewModel @Inject constructor(
    private val booksRepository: BooksRepositoryInterface
) : ViewModel() {

    private val _bookDetails = MutableLiveData<Response<BookDetails>>()
    val bookDetails: LiveData<Response<BookDetails>>
        get() = _bookDetails

    private val _bookWithShelvesList = MutableLiveData<List<BookWithShelves>>()
    val bookWithShelvesList: LiveData<List<BookWithShelves>>
        get() = _bookWithShelvesList

    private val _localBookList = MutableLiveData<List<DataModel.LocalBook>>()
    val localBookList: LiveData<List<DataModel.LocalBook>>
        get() = _localBookList

    private var _currentGoogleBook: DataModel.GoogleBookItem? = null
    val currentGoogleBook: DataModel.GoogleBookItem?
        get() = _currentGoogleBook

    private var _currentLocalBook: DataModel.LocalBook? = null
    val currentLocalBook: DataModel.LocalBook?
        get() = _currentLocalBook

    fun getBookDetailsById(id: String) = CoroutineScope(Dispatchers.IO).launch {
        booksRepository.getBookDetails(id).collectLatest { response ->
            _bookDetails.postValue(response)
        }
    }

    fun deleteBook(localBook: DataModel.LocalBook) = viewModelScope.launch {
        booksRepository.delete(localBook)
    }

    fun insertBook(localBook: DataModel.LocalBook) = CoroutineScope(Dispatchers.IO).launch {
        booksRepository.insert(localBook)
    }

    fun updateBook(localBook: DataModel.LocalBook) = CoroutineScope(Dispatchers.IO).launch {
        booksRepository.update(localBook)
    }

    fun getBookList() {
        CoroutineScope(Dispatchers.IO).launch {
            booksRepository.getLocalBooks().collectLatest {
                _localBookList.postValue(it)
            }
        }
    }

    fun getBookWithShelves(bookId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            booksRepository.getBookWithShelves(bookId).collectLatest {
                _bookWithShelvesList.postValue(it)
            }
        }
    }

    fun setCurrentGoogleBook(googleBook: DataModel.GoogleBookItem?) {
        _currentLocalBook = null
        _currentGoogleBook = googleBook
    }

    fun setCurrentLocalBook(localBook: DataModel.LocalBook?) {
        _currentGoogleBook = null
        _currentLocalBook = localBook
    }
}