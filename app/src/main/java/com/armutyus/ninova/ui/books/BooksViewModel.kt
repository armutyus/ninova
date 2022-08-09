package com.armutyus.ninova.ui.books

import androidx.lifecycle.*
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.model.BookDetails
import com.armutyus.ninova.model.DataModel
import com.armutyus.ninova.repository.BooksRepositoryInterface
import com.armutyus.ninova.repository.FirebaseRepositoryInterface
import com.armutyus.ninova.roomdb.entities.BookShelfCrossRef
import com.armutyus.ninova.roomdb.entities.BookWithShelves
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BooksViewModel @Inject constructor(
    private val booksRepository: BooksRepositoryInterface,
    private val firebaseRepository: FirebaseRepositoryInterface
) : ViewModel() {

    private val _bookDetails = MutableLiveData<Response<BookDetails>>()
    val bookDetails: LiveData<Response<BookDetails>>
        get() = _bookDetails

    private val _bookShelfCrossRefList = MutableLiveData<List<BookShelfCrossRef>>()
    val bookShelfCrossRefList: LiveData<List<BookShelfCrossRef>>
        get() = _bookShelfCrossRefList

    private val _bookWithShelvesList = MutableLiveData<List<BookWithShelves>>()
    val bookWithShelvesList: LiveData<List<BookWithShelves>>
        get() = _bookWithShelvesList

    private val _localBookList = MutableLiveData<List<DataModel.LocalBook>>()
    val localBookList: LiveData<List<DataModel.LocalBook>>
        get() = _localBookList

    fun getBookDetailsById(id: String) = viewModelScope.launch {
        booksRepository.getBookDetails(id).collectLatest { response ->
            _bookDetails.postValue(response)
        }
    }

    fun deleteBook(localBook: DataModel.LocalBook) = viewModelScope.launch {
        booksRepository.delete(localBook)
    }

    fun insertBook(localBook: DataModel.LocalBook) = viewModelScope.launch {
        booksRepository.insert(localBook)
    }

    fun updateBook(localBook: DataModel.LocalBook) = viewModelScope.launch {
        booksRepository.update(localBook)
    }

    fun getBookList() {
        viewModelScope.launch {
            booksRepository.getLocalBooks().collectLatest {
                _localBookList.postValue(it)
            }
        }
    }

    fun getBookWithShelves(bookId: String) {
        viewModelScope.launch {
            booksRepository.getBookWithShelves(bookId).collectLatest {
                _bookWithShelvesList.postValue(it)
            }
        }
    }

    fun getBookShelfCrossRef() {
        viewModelScope.launch {
            booksRepository.getBookShelfCrossRef().collectLatest {
                _bookShelfCrossRefList.postValue(it)
            }
        }
    }

    //Firebase Works

    fun collectBooksFromFirestore() = liveData(Dispatchers.IO) {
        firebaseRepository.downloadUserBooksFromFirestore().collect { response ->
            emit(response)
        }
    }

    fun collectCrossRefFromFirestore() = liveData(Dispatchers.IO) {
        firebaseRepository.downloadUserCrossRefFromFirestore().collect { response ->
            emit(response)
        }
    }

    fun collectShelvesFromFirestore() = liveData(Dispatchers.IO) {
        firebaseRepository.downloadUserShelvesFromFirestore().collect { response ->
            emit(response)
        }
    }

}