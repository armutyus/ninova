package com.armutyus.ninova.ui.search.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchArchiveViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Search Fragment"
    }
    val text: LiveData<String> = _text
}