package com.armutyus.ninova.ui.main

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.armutyus.ninova.repository.AuthRepositoryInterface
import com.google.rpc.context.AttributeContext
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: AuthRepositoryInterface
) : ViewModel() {

    fun exportDbToStorage(dbFileUri: Uri) = liveData(Dispatchers.IO) {
        repository.exportUserDbToStorage(dbFileUri).collect { response ->
            emit(response)
        }
    }

}