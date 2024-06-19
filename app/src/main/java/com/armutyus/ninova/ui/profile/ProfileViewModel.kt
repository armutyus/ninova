package com.armutyus.ninova.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.repository.FirebaseRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: FirebaseRepositoryInterface
) : ViewModel() {

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