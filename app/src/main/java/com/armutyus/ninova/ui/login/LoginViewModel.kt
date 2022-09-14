package com.armutyus.ninova.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.repository.FirebaseRepositoryInterface
import com.google.firebase.auth.AuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: FirebaseRepositoryInterface
) : ViewModel() {

    fun signInUser(email: String, password: String, onComplete: (Response<Boolean>) -> Unit) =
        viewModelScope.launch {
            val response = repository.signInWithEmailPassword(email, password)
            onComplete(response)
        }

    fun signInAnonymously(onComplete: (Response<Boolean>) -> Unit) = viewModelScope.launch {
        val response = repository.signInAnonymous()
        onComplete(response)
    }

    fun signUpUser(email: String, password: String, onComplete: (Response<Boolean>) -> Unit) =
        viewModelScope.launch {
            val response = repository.signUpWithEmailPassword(email, password)
            onComplete(response)
        }

    fun registerAnonymousUser(credential: AuthCredential, onComplete: (Response<Boolean>) -> Unit) =
        viewModelScope.launch {
            val response = repository.anonymousToPermanent(credential)
            onComplete(response)
        }

    fun createUser(onComplete: (Response<Boolean>) -> Unit) = viewModelScope.launch {
        val response = repository.createUserInFirestore()
        onComplete(response)
    }

    fun reAuthUser(credential: AuthCredential, onComplete: (Response<Boolean>) -> Unit) =
        viewModelScope.launch {
            val response = repository.reAuthUser(credential)
            onComplete(response)
        }

    fun changeUserEmail(email: String, onComplete: (Response<Boolean>) -> Unit) =
        viewModelScope.launch {
            val response = repository.changeUserEmail(email)
            onComplete(response)
        }

    fun changeUserPassword(password: String, onComplete: (Response<Boolean>) -> Unit) =
        viewModelScope.launch {
            val response = repository.changeUserPassword(password)
            onComplete(response)
        }

    fun sendPasswordEmail(email: String, onComplete: (Response<Boolean>) -> Unit) =
        viewModelScope.launch {
            val response = repository.sendResetPassword(email)
            onComplete(response)
        }

    fun signOut(onComplete: (Response<Boolean>) -> Unit) = viewModelScope.launch {
        val response = repository.signOut()
        onComplete(response)
    }

}