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

    private val _profileBannerUrl = MutableLiveData<String>()
    val profileBannerUrl: LiveData<String>
        get() = _profileBannerUrl

    private val _profilePhotoUrl = MutableLiveData<String>()
    val profilePhotoUrl: LiveData<String>
        get() = _profilePhotoUrl

    fun getUserProfile() = viewModelScope.launch {
        _userProfile.postValue(repository.getUserProfile())
    }

    fun updateUserProfile(userUpdates: Map<String, Any?>, onComplete: (Response<Boolean>) -> Unit) =
        viewModelScope.launch {
            val response = repository.updateUserProfile(userUpdates)
            onComplete(response)
        }

    fun saveImagesToFirestore(
        profileBannerUri: Uri?,
        profilePhotoUri: Uri?,
        onComplete: (Response<Boolean>) -> Unit
    ) = viewModelScope.launch {
        profileBannerUri?.let {
            val profileBannerResponse =
                repository.uploadCustomProfileImageToFirestore(profileBannerUri, true)
            if (profileBannerResponse is Response.Failure) {
                onComplete(profileBannerResponse)
                return@launch
            }

            if (profileBannerResponse is Response.Success) {
                _profileBannerUrl.value = profileBannerResponse.data.toString()
            }
        }

        profilePhotoUri?.let {
            val profilePhotoResponse =
                repository.uploadCustomProfileImageToFirestore(profilePhotoUri, false)
            if (profilePhotoResponse is Response.Failure) {
                onComplete(profilePhotoResponse)
                return@launch
            }

            if (profilePhotoResponse is Response.Success) {
                _profilePhotoUrl.value = profilePhotoResponse.data.toString()
            }
        }
        onComplete(Response.Success(true))
    }


}