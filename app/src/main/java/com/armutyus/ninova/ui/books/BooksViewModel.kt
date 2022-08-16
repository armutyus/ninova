package com.armutyus.ninova.ui.books

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.model.BookDetails
import com.armutyus.ninova.model.DataModel
import com.armutyus.ninova.repository.ApiBooksRepositoryInterface
import com.armutyus.ninova.repository.FirebaseRepositoryInterface
import com.armutyus.ninova.repository.LocalBooksRepositoryInterface
import com.armutyus.ninova.roomdb.entities.BookShelfCrossRef
import com.armutyus.ninova.roomdb.entities.BookWithShelves
import com.armutyus.ninova.roomdb.entities.LocalShelf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BooksViewModel @Inject constructor(
    private val apiBooksRepository: ApiBooksRepositoryInterface,
    private val booksRepository: LocalBooksRepositoryInterface,
    private val firebaseRepository: FirebaseRepositoryInterface
) : ViewModel() {
    // Google Book Works

    private val _bookDetails = MutableLiveData<Response<BookDetails>>()
    val bookDetails: LiveData<Response<BookDetails>>
        get() = _bookDetails

    fun getBookDetailsById(id: String) = viewModelScope.launch {
        apiBooksRepository.getBookDetails(id).collectLatest { response ->
            _bookDetails.postValue(response)
        }
    }

    // Local Book Works

    private val _bookShelfCrossRefList = MutableLiveData<List<BookShelfCrossRef>>()
    val bookShelfCrossRefList: LiveData<List<BookShelfCrossRef>>
        get() = _bookShelfCrossRefList

    private val _bookWithShelvesList = MutableLiveData<List<BookWithShelves>>()
    val bookWithShelvesList: LiveData<List<BookWithShelves>>
        get() = _bookWithShelvesList

    private val _localBookList = MutableLiveData<List<DataModel.LocalBook>>()
    val localBookList: LiveData<List<DataModel.LocalBook>>
        get() = _localBookList

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
            _localBookList.value = booksRepository.getLocalBooks()
        }
    }

    fun getBookWithShelves(bookId: String) {
        viewModelScope.launch {
            _bookWithShelvesList.value = booksRepository.getBookWithShelves(bookId)
        }
    }

    fun getBookShelfCrossRef() {
        viewModelScope.launch {
            _bookShelfCrossRefList.value = booksRepository.getBookShelfCrossRef()
        }
    }

    //Firebase Works

    private val _firebaseBookList = MutableLiveData<Response<List<DataModel.LocalBook>>>()
    val firebaseBookList: LiveData<Response<List<DataModel.LocalBook>>>
        get() = _firebaseBookList

    private val _firebaseCrossRefList = MutableLiveData<Response<List<BookShelfCrossRef>>>()
    val firebaseCrossRefList: LiveData<Response<List<BookShelfCrossRef>>>
        get() = _firebaseCrossRefList

    private val _firebaseShelfList = MutableLiveData<Response<List<LocalShelf>>>()
    val firebaseShelfList: LiveData<Response<List<LocalShelf>>>
        get() = _firebaseShelfList

    fun collectBooksFromFirestore() = viewModelScope.launch {
        _firebaseBookList.value = firebaseRepository.downloadUserBooksFromFirestore()
    }

    fun collectCrossRefFromFirestore() = viewModelScope.launch {
        _firebaseCrossRefList.value = firebaseRepository.downloadUserCrossRefFromFirestore()
    }

    fun collectShelvesFromFirestore() = viewModelScope.launch {
        _firebaseShelfList.value = firebaseRepository.downloadUserShelvesFromFirestore()
    }

}