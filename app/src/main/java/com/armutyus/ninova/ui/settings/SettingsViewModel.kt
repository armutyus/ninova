package com.armutyus.ninova.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.armutyus.ninova.model.DataModel
import com.armutyus.ninova.repository.FirebaseRepositoryInterface
import com.armutyus.ninova.roomdb.NinovaLocalDB
import com.armutyus.ninova.roomdb.entities.BookShelfCrossRef
import com.armutyus.ninova.roomdb.entities.LocalShelf
import com.google.firebase.auth.AuthCredential
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

    fun createUser() = liveData(Dispatchers.IO) {
        repository.createUserInFirestore().collect { response ->
            emit(response)
        }
    }

    fun registerAnonymousUser(credential: AuthCredential) = liveData(Dispatchers.IO) {
        repository.anonymousToPermanent(credential).collect { response ->
            emit(response)
        }
    }

    fun uploadUserBooksToFirestore(localBook: DataModel.LocalBook) = liveData(Dispatchers.IO) {
        repository.uploadUserBooksToFirestore(localBook).collect { response ->
            emit(response)
        }
    }

    fun uploadUserCrossRefToFirestore(bookShelfCrossRef: BookShelfCrossRef) =
        liveData(Dispatchers.IO) {
            repository.uploadUserCrossRefToFirestore(bookShelfCrossRef).collect { response ->
                emit(response)
            }
        }

    fun uploadUserShelvesToFirestore(shelf: LocalShelf) = liveData(Dispatchers.IO) {
        repository.uploadUserShelvesToFirestore(shelf).collect { response ->
            emit(response)
        }
    }

    fun signOut() = liveData(Dispatchers.IO) {
        repository.signOut().collect { response ->
            emit(response)
        }
    }

    fun clearDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            db.clearAllTables()
        }
    }

}