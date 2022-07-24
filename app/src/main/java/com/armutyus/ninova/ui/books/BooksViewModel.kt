package com.armutyus.ninova.ui.books

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val booksRepositoryInterface: BooksRepositoryInterface
) : ViewModel() {

    private val _localBookList = MutableLiveData<List<DataModel.LocalBook>>()
    val localBookList: LiveData<List<DataModel.LocalBook>>
        get() = _localBookList

    private val _bookWithShelvesList = MutableLiveData<List<BookWithShelves>>()
    val bookWithShelvesList: LiveData<List<BookWithShelves>>
        get() = _bookWithShelvesList

    fun getBookList() {
        CoroutineScope(Dispatchers.IO).launch {
            booksRepositoryInterface.getLocalBooks().collectLatest {
                _localBookList.postValue(it)
            }
        }
    }

    fun insertBook(localBook: DataModel.LocalBook) = CoroutineScope(Dispatchers.IO).launch {
        booksRepositoryInterface.insert(localBook)
    }

    fun updateBook(localBook: DataModel.LocalBook) = CoroutineScope(Dispatchers.IO).launch {
        booksRepositoryInterface.update(localBook)
    }

    fun deleteBook(localBook: DataModel.LocalBook) = viewModelScope.launch {
        booksRepositoryInterface.delete(localBook)
    }

    fun getBookWithShelves(bookId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            booksRepositoryInterface.getBookWithShelves(bookId).collectLatest {
                _bookWithShelvesList.postValue(it)
            }
        }
    }

}