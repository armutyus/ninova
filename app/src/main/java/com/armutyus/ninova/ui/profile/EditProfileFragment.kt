package com.armutyus.ninova.ui.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
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
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.databinding.FragmentEditProfileBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class EditProfileFragment @Inject constructor(
    auth: FirebaseAuth
) : Fragment(R.layout.fragment_edit_profile) {

    private var fragmentBinding: FragmentEditProfileBinding? = null
    private val profileViewModel by activityViewModels<ProfileViewModel>()
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var permissionResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    private val user = auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentEditProfileBinding.bind(view)
        fragmentBinding = binding
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
                    fragmentBinding?.progressBar?.visibility = View.GONE
                    fragmentBinding?.doneButton?.visibility = View.VISIBLE
                    lifecycleScope.launch {
                        delay(3000)
                        fragmentBinding?.doneButton?.visibility = View.GONE
                        delay(300)
                        fragmentBinding?.saveChangesButton?.text = R.string.save.toString()
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

    private fun onProfilePhotoClicked() {
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
                val uri = result.data?.data
                if (uri != null) {
                    //upload profile photo or banner to firestore
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
                //upload profile photo or banner to firestore
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    }
}