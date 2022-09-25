package com.armutyus.ninova.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.repository.FirebaseRepositoryInterface
import com.armutyus.ninova.repository.LocalBooksRepositoryInterface
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
    private val booksRepository: LocalBooksRepositoryInterface,
    private val repository: FirebaseRepositoryInterface,
    private val db: NinovaLocalDB
) : ViewModel() {

    fun uploadUserData(
        onComplete: (Response<Boolean>) -> Unit
    ) = viewModelScope.launch {
        val localBooks = booksRepository.getLocalBooks()
        localBooks.forEach {
            val response = repository.uploadUserBooksToFirestore(it)
            if (response is Response.Failure) {
                onComplete(response)
                return@launch
            }
        }
        onComplete(Response.Success(true))
    }

    fun uploadUserCrossRefToFirestore(
        bookShelfCrossRef: BookShelfCrossRef,
        onComplete: (Response<Boolean>) -> Unit
    ) =
        viewModelScope.launch {
            val response = repository.uploadUserCrossRefToFirestore(bookShelfCrossRef)
            onComplete(response)
        }

    fun uploadUserShelvesToFirestore(shelf: LocalShelf, onComplete: (Response<Boolean>) -> Unit) =
        viewModelScope.launch {
            val response = repository.uploadUserShelvesToFirestore(shelf)
            onComplete(response)
        }

    fun signOut(onComplete: (Response<Boolean>) -> Unit) = viewModelScope.launch {
        val response = repository.signOut()
        onComplete(response)
    }

    fun clearDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            db.clearAllTables()
        }
    }

}