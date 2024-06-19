package com.armutyus.ninova.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.repository.FirebaseRepositoryInterface
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: FirebaseRepositoryInterface
) : ViewModel() {

    private val _userProfile = MutableLiveData<Response<DocumentSnapshot>>()
    val userProfile: LiveData<Response<DocumentSnapshot>>
        get() = _userProfile

    fun getUserProfile() = viewModelScope.launch {
        _userProfile.postValue(repository.getUserProfile())
    }

    fun updateUserProfile(userUpdates: Map<String, Any?>, onComplete: (Response<Boolean>) -> Unit) =
        viewModelScope.launch {
            val response = repository.updateUserProfile(userUpdates)
            onComplete(response)
        }

    fun uploadCustomProfileImageToFirestore(
        uri: Uri,
        isBannerImage: Boolean,
        onComplete: (Response<Uri>) -> Unit
    ) =
        viewModelScope.launch {
            val response = repository.uploadCustomProfileImageToFirestore(uri, isBannerImage)
            onComplete(response)
        }


}