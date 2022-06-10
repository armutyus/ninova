package com.armutyus.ninova.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.armutyus.ninova.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: AuthRepository
): ViewModel() {

    fun signOut() = liveData(Dispatchers.IO) {
        repository.signOut().collect { response ->
            emit(response)
        }
    }

}