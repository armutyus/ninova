package com.armutyus.ninova.ui.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Constants.NAME
import com.armutyus.ninova.constants.Constants.PHOTO_URL
import com.armutyus.ninova.constants.Constants.PROFILE_BANNER
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.databinding.FragmentEditProfileBinding
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class EditProfileFragment @Inject constructor(
    auth: FirebaseAuth,
    private val glide: RequestManager
) : Fragment(R.layout.fragment_edit_profile) {

    private var fragmentBinding: FragmentEditProfileBinding? = null
    private val profileViewModel by activityViewModels<ProfileViewModel>()
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var permissionResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    private val user = auth.currentUser
    private var isBannerImage = false
    private var bannerImageUrl = ""
    private var profileImageUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentEditProfileBinding.bind(view)
        fragmentBinding = binding

        fragmentBinding?.profileImageView?.setOnClickListener {
            isBannerImage = false
            onImageClicked()
        }

        fragmentBinding?.bannerImageView?.setOnClickListener {
            isBannerImage = true
            onImageClicked()
        }

        fragmentBinding?.saveChangesButton?.setOnClickListener {
            val userUpdates = mutableMapOf<String, Any?>()
            if (bannerImageUrl.isNotEmpty()) {
                userUpdates[PROFILE_BANNER] = bannerImageUrl
            } else {
                userUpdates[PROFILE_BANNER] = ""
            }
            if (profileImageUrl.isNotEmpty()) {
                userUpdates[PHOTO_URL] = profileImageUrl
            } else {
                userUpdates[PHOTO_URL] = ""
            }
            userUpdates[NAME] = fragmentBinding?.usernameEditText?.text.toString()
            updateUserProfile(userUpdates)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }

    private fun updateUserProfile(userUpdates: MutableMap<String, Any?>) {
        profileViewModel.updateUserProfile(userUpdates) { response ->
            when (response) {
                is Response.Loading -> {
                    fragmentBinding?.saveChangesButton?.text = ""
                    fragmentBinding?.progressBar?.visibility = View.VISIBLE
                }

                is Response.Success -> {
                    fragmentBinding?.saveChangesButton?.text = ""
                    fragmentBinding?.progressBar?.visibility = View.GONE
                    fragmentBinding?.doneButton?.visibility = View.VISIBLE
                    lifecycleScope.launch {
                        delay(3000)
                        fragmentBinding?.doneButton?.visibility = View.GONE
                        delay(300)
                        fragmentBinding?.saveChangesButton?.text = getText(R.string.save)
                    }
                    Toast.makeText(
                        requireContext(),
                        "Profile Changes Saved",
                        Toast.LENGTH_LONG
                    ).show()
                }

                is Response.Failure -> {
                    Log.e(
                        "EditProfileFragment",
                        "Save Profile Changes Error: " + response.errorMessage
                    )
                    Toast.makeText(requireContext(), response.errorMessage, Toast.LENGTH_LONG)
                        .show()
                    fragmentBinding?.progressBar?.visibility = View.GONE
                }
            }
        }
    }

    private fun uploadProfileImageToFirestore(uri: Uri) {
        profileViewModel.uploadCustomProfileImageToFirestore(uri, isBannerImage) { response ->
            when (response) {
                is Response.Loading ->
                    Log.i("shelfCoverUpload", "Uploading to firestore")

                is Response.Success -> {
                    val downloadUrl = response.data.toString()
                    if (isBannerImage) {
                        bannerImageUrl = downloadUrl
                    } else {
                        profileImageUrl = downloadUrl
                    }
                    Log.i("shelfCoverUpload", "Uploaded to firestore")
                }

                is Response.Failure ->
                    Log.e("shelfCoverUpload", response.errorMessage)
            }
        }
    }

    private fun onImageClicked() {
        if (isPhotoPickerAvailable()) {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    Snackbar.make(
                        requireView(),
                        R.string.permission_needed,
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction(R.string.give_permission) {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }.show()
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                permissionResultLauncher.launch(galleryIntent)
            }
        }
    }

    private fun isPhotoPickerAvailable(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }

    private fun registerLauncher() {
        permissionResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    loadImageWithGlide(uri, isBannerImage)
                    uploadProfileImageToFirestore(uri)
                }
            }
        }
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result ->
            if (result) {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                permissionResultLauncher.launch(galleryIntent)
            } else {
                Toast.makeText(requireContext(), R.string.permission_needed, Toast.LENGTH_LONG)
                    .show()
            }
        }
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                requireContext().contentResolver.takePersistableUriPermission(uri, flag)
                loadImageWithGlide(uri, isBannerImage)
                uploadProfileImageToFirestore(uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    }

    private fun loadImageWithGlide(uri: Uri, isBannerImage: Boolean) {
        val imageView = if (isBannerImage) {
            fragmentBinding?.bannerImageView
        } else {
            fragmentBinding?.profileImageView
        }
        imageView?.let {
            glide.load(uri).centerCrop().into(it)
        }
    }
}