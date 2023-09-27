package com.armutyus.ninova.ui.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armutyus.ninova.constants.Constants.discoverScreenCategories
import com.armutyus.ninova.constants.Response
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

    private val _booksFromApiResponse = MutableLiveData<Response<OpenLibraryResponse>>()
    val booksFromApiResponse: LiveData<Response<OpenLibraryResponse>>
        get() = _booksFromApiResponse

    private val _categoryCoverId = MutableLiveData<MutableMap<String, String>>(mutableMapOf())
    val categoryCoverId: LiveData<MutableMap<String, String>>
        get() = _categoryCoverId

    init {
        discoverScreenCategories.forEach {
            getRandomBookCoverForCategory(it)
        }
    }

    private fun getRandomBookCoverForCategory(category: String) = viewModelScope.launch {
        val coverUrl = openLibRepository.getRandomBookCoverForCategory(category)
        val currentMap = _categoryCoverId.value ?: mutableMapOf()
        currentMap[category] = coverUrl
        _categoryCoverId.postValue(currentMap)
    }

    fun booksFromApi(category: String, offset: Int) = viewModelScope.launch {
        openLibRepository.getBooksByCategory(category, offset).collectLatest { response ->
            _booksFromApiResponse.postValue(response)
        }
    }

}