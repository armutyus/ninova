package com.armutyus.ninova.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.model.DataModel
import com.armutyus.ninova.model.GoogleApiBooks
import com.armutyus.ninova.repository.BooksRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainSearchViewModel @Inject constructor(
    private val booksRepository: BooksRepositoryInterface
) : ViewModel() {

    //LocalBook Works

    private val _currentLocalBookList = MutableLiveData<List<DataModel.LocalBook>>()
    val currentLocalBookList: LiveData<List<DataModel.LocalBook>>
        get() = _currentLocalBookList

    private val _searchLocalBookList = MutableLiveData<List<DataModel.LocalBook>>()
    val searchLocalBookList: LiveData<List<DataModel.LocalBook>>
        get() = _searchLocalBookList

    fun searchLocalBooks(searchString: String) {
        viewModelScope.launch {
            booksRepository.searchLocalBooks(searchString).collectLatest {
                _searchLocalBookList.postValue(it)
            }
        }
    }

    fun setCurrentLocalBookList(bookList: List<DataModel.LocalBook>) {
        _currentLocalBookList.postValue(bookList)
    }

    fun insertBook(localBook: DataModel.LocalBook) = viewModelScope.launch {
        booksRepository.insert(localBook)
    }

    //GoogleBook Works

    private val _currentList = MutableLiveData<List<DataModel.GoogleBookItem>>()
    val currentList: LiveData<List<DataModel.GoogleBookItem>>
        get() = _currentList

    private val _searchBooksResponse = MutableLiveData<Response<GoogleApiBooks>>()
    val searchBooksResponse: LiveData<Response<GoogleApiBooks>>
        get() = _searchBooksResponse

    private val _randomBooksResponse = MutableLiveData<Response<GoogleApiBooks>>()
    val randomBooksResponse: LiveData<Response<GoogleApiBooks>>
        get() = _randomBooksResponse

    fun searchBooksFromApi(searchQuery: String) = viewModelScope.launch {
        booksRepository.searchBooksFromApi(searchQuery).collectLatest { response ->
            _searchBooksResponse.postValue(response)
        }
    }

    fun randomBooksFromApi() = viewModelScope.launch {
        booksRepository.searchBooksFromApi("Witcher").collectLatest { response ->
            _randomBooksResponse.postValue(response)
        }
    }

    fun setCurrentList(bookList: List<DataModel.GoogleBookItem>) {
        _currentList.postValue(bookList)
    }

}