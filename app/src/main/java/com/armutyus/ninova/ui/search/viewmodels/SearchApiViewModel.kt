package com.armutyus.ninova.ui.search.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchApiViewModel @Inject constructor(

): ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Search From API Fragment"
    }
    val text: LiveData<String> = _text
}