package com.armutyus.ninova.ui.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armutyus.ninova.repository.OpenLibRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val openLibRepository: OpenLibRepositoryInterface
) : ViewModel() {

    private val _categoryCoverId = MutableLiveData<MutableMap<String, String>>()
    val categoryCoverId: LiveData<MutableMap<String, String>>
        get() = _categoryCoverId

    fun getRandomBookCoverForCategory(category: String) = viewModelScope.launch {
        val coverUrl = openLibRepository.getRandomBookCoverForCategory(category)
        _categoryCoverId.value?.set(category, coverUrl)
    }

}