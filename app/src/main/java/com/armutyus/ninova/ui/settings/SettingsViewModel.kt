package com.armutyus.ninova.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.model.DataModel
import com.armutyus.ninova.repository.FirebaseRepositoryInterface
import com.armutyus.ninova.roomdb.NinovaLocalDB
import com.armutyus.ninova.roomdb.entities.BookShelfCrossRef
import com.armutyus.ninova.roomdb.entities.LocalShelf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: FirebaseRepositoryInterface,
    private val db: NinovaLocalDB
) : ViewModel() {

    private val _firebaseAuthResponse = MutableLiveData<Response<Boolean>>()
    val firebaseAuthResponse: LiveData<Response<Boolean>>
        get() = _firebaseAuthResponse

    fun uploadUserBooksToFirestore(localBook: DataModel.LocalBook) = viewModelScope.launch {
        _firebaseAuthResponse.value = repository.uploadUserBooksToFirestore(localBook)
    }

    fun uploadUserCrossRefToFirestore(bookShelfCrossRef: BookShelfCrossRef) =
        viewModelScope.launch {
            _firebaseAuthResponse.value =
                repository.uploadUserCrossRefToFirestore(bookShelfCrossRef)
        }

    fun uploadUserShelvesToFirestore(shelf: LocalShelf) = viewModelScope.launch {
        _firebaseAuthResponse.value = repository.uploadUserShelvesToFirestore(shelf)
    }

    fun signOut() = viewModelScope.launch {
        _firebaseAuthResponse.value = repository.signOut()
    }

    fun clearDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            db.clearAllTables()
        }
    }

}