package com.armutyus.ninova.ui.shelves

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armutyus.ninova.repository.ShelfRepositoryInterface
import com.armutyus.ninova.roomdb.entities.LocalShelf
import com.armutyus.ninova.roomdb.entities.ShelfWithBooks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShelvesViewModel @Inject constructor(
    private val shelfRepositoryInterface: ShelfRepositoryInterface
) : ViewModel() {

    private val _currentShelfList = MutableLiveData<List<LocalShelf>>()
    val currentShelfList: LiveData<List<LocalShelf>>
        get() = _currentShelfList

    private val _shelfWithBooksList = MutableLiveData<List<ShelfWithBooks>>()
    val shelfWithBooksList: LiveData<List<ShelfWithBooks>>
        get() = _shelfWithBooksList

    fun getShelfList() {
        CoroutineScope(Dispatchers.IO).launch {
            shelfRepositoryInterface.getLocalShelves().collectLatest {
                _currentShelfList.postValue(it)
            }
        }
    }

    fun insertShelf(localShelf: LocalShelf) = CoroutineScope(Dispatchers.IO).launch {
        shelfRepositoryInterface.insert(localShelf)
    }

    fun deleteShelf(localShelf: LocalShelf) = viewModelScope.launch {
        shelfRepositoryInterface.delete(localShelf)
    }

    fun getShelfWithBookList(shelfId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            shelfRepositoryInterface.getShelfWithBooks(shelfId).collectLatest {
                _shelfWithBooksList.postValue(it)
            }
        }
    }

}