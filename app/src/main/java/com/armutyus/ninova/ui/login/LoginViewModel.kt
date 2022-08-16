package com.armutyus.ninova.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val _firebaseAuthResponse = MutableLiveData<Response<Boolean>>()
    val firebaseAuthResponse: LiveData<Response<Boolean>>
        get() = _firebaseAuthResponse

    fun signInUser(email: String, password: String) = viewModelScope.launch {
        _firebaseAuthResponse.value = repository.signInWithEmailPassword(email, password)
    }

    fun signInAnonymously() = viewModelScope.launch {
        _firebaseAuthResponse.value = repository.signInAnonymous()
    }

    fun signUpUser(email: String, password: String) = viewModelScope.launch {
        _firebaseAuthResponse.value = repository.signUpWithEmailPassword(email, password)
    }

    fun registerAnonymousUser(credential: AuthCredential) = viewModelScope.launch {
        _firebaseAuthResponse.value = repository.anonymousToPermanent(credential)
    }

    fun createUser() = viewModelScope.launch {
        _firebaseAuthResponse.value = repository.createUserInFirestore()
    }

    fun reAuthUser(credential: AuthCredential) = viewModelScope.launch {
        _firebaseAuthResponse.value = repository.reAuthUser(credential)
    }

    fun changeUserEmail(email: String) = viewModelScope.launch {
        _firebaseAuthResponse.value = repository.changeUserEmail(email)
    }

    fun changeUserPassword(password: String) = viewModelScope.launch {
        _firebaseAuthResponse.value = repository.changeUserPassword(password)
    }

    fun sendPasswordEmail(email: String) = viewModelScope.launch {
        _firebaseAuthResponse.value = repository.sendResetPassword(email)
    }

    fun signOut() = viewModelScope.launch {
        _firebaseAuthResponse.value = repository.signOut()
    }

}