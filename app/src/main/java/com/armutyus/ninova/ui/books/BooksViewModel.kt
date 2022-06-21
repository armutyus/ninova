package com.armutyus.ninova.ui.books

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.armutyus.ninova.repository.BooksRepositoryInterface
import com.armutyus.ninova.roomdb.LocalBook
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

    private val mainBookList = MutableLiveData<List<LocalBook>>()
    val bookList: LiveData<List<LocalBook>>
        get() = mainBookList

    fun getBookList() {
        CoroutineScope(Dispatchers.IO).launch {
            booksRepositoryInterface.getLocalBooks().collectLatest {
                mainBookList.postValue(it)
            }
        }
    }
}