package com.armutyus.ninova.ui.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armutyus.ninova.constants.Constants.discoverScreenCategories
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.model.openlibrarymodel.BookDetailsResponse
import com.armutyus.ninova.model.openlibrarymodel.OpenLibraryResponse
import com.armutyus.ninova.repository.OpenLibRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val openLibRepository: OpenLibRepositoryInterface
) : ViewModel() {

    private val _bookCoverFromApiResponse = MutableLiveData<Response<String>>()
    val bookCoverFromApiResponse: LiveData<Response<String>>
        get() = _bookCoverFromApiResponse

    private val _booksFromApiResponse = MutableLiveData<Response<OpenLibraryResponse>>()
    val booksFromApiResponse: LiveData<Response<OpenLibraryResponse>>
        get() = _booksFromApiResponse

    private val _bookDetailsResponse = MutableLiveData<Response<BookDetailsResponse>>()
    val bookDetailsResponse: LiveData<Response<BookDetailsResponse>>
        get() = _bookDetailsResponse

    private val _categoryCoverId = MutableLiveData<MutableMap<String, String>>(mutableMapOf())
    val categoryCoverId: LiveData<MutableMap<String, String>>
        get() = _categoryCoverId

    init {
        getRandomBookCoverForCategory().invokeOnCompletion {
            _bookCoverFromApiResponse.postValue(Response.Success("Success"))
        }
    }

    private fun getRandomBookCoverForCategory() = viewModelScope.launch {
        discoverScreenCategories.forEach { category ->
            openLibRepository.getRandomBookCoverForCategory(category).collectLatest { response ->
                when (response) {
                    is Response.Success -> {
                        val coverUrl = response.data
                        val currentMap = _categoryCoverId.value ?: mutableMapOf()
                        currentMap[category] = coverUrl
                        _categoryCoverId.postValue(currentMap)
                    }

                    else -> _bookCoverFromApiResponse.postValue(response)
                }
            }
        }
    }

    fun booksFromApi(category: String, offset: Int) = viewModelScope.launch {
        openLibRepository.getBooksByCategory(category, offset).collectLatest { response ->
            _booksFromApiResponse.postValue(response)
        }
    }

    fun getBookDetails(bookKey: String) = viewModelScope.launch {
        openLibRepository.getBookDetails(bookKey).collectLatest { response ->
            _bookDetailsResponse.postValue(response)
        }
    }

}