package com.armutyus.ninova.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchApiViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Search From API Fragment"
    }
    val text: LiveData<String> = _text
}