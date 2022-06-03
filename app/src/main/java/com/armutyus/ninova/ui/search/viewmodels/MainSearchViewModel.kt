package com.armutyus.ninova.ui.search.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainSearchViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Main Search Fragment"
    }
    val text: LiveData<String> = _text
}