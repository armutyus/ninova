package com.armutyus.ninova.ui.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Constants.GLIDE_LOAD_SKIP
import com.armutyus.ninova.constants.Constants.NAME
import com.armutyus.ninova.constants.Constants.PHOTO_URL
import com.armutyus.ninova.constants.Constants.PROFILE_BANNER
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.databinding.FragmentEditProfileBinding
import com.bumptech.glide.RequestManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class EditProfileFragment @Inject constructor(
    private val glide: RequestManager
) : Fragment(R.layout.fragment_edit_profile) {

    private var fragmentBinding: FragmentEditProfileBinding? = null
    private val profileViewModel by activityViewModels<ProfileViewModel>()
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var permissionResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    private var isBannerImage = false
    private var bannerImageUri: Uri? = null
    private var profileImageUri: Uri? = null
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
            saveImagesAndUpdateProfile()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showExitConfirmationDialog()
                }
            }
        )

        removeBackButtonAndMenu()
        profileViewModel.getUserProfile()
        runObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }

    private fun updateUserProfile(userUpdates: Map<String, Any?>) {
        profileViewModel.updateUserProfile(userUpdates) { response ->
            when (response) {
                is Response.Loading -> {
                    fragmentBinding?.saveChangesButton?.text = getText(R.string.empty_text_view)
                    fragmentBinding?.progressBar?.visibility = View.VISIBLE
                }

                is Response.Success -> {
                    fragmentBinding?.saveChangesButton?.text = getText(R.string.empty_text_view)
                    fragmentBinding?.progressBar?.visibility = View.GONE
                    fragmentBinding?.doneButton?.visibility = View.VISIBLE
                    lifecycleScope.launch {
                        delay(3000)
                        fragmentBinding?.doneButton?.visibility = View.GONE
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
                    if (isBannerImage) bannerImageUri = uri else profileImageUri = uri
                    //uploadProfileImageToFirestore(uri)
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
                if (isBannerImage) bannerImageUri = uri else profileImageUri = uri
                //uploadProfileImageToFirestore(uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    }

    private fun runObservers() {
        profileViewModel.profileBannerUrl.observe(viewLifecycleOwner) {
            bannerImageUrl = it
        }

        profileViewModel.profilePhotoUrl.observe(viewLifecycleOwner) {
            profileImageUrl = it
        }

        profileViewModel.userProfile.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Success -> {
                    val userDocument = response.data
                    setProfileData(userDocument)
                }

                is Response.Failure -> {
                    Log.e("EditProfileFragment User Profile Observer", response.errorMessage)
                }

                is Response.Loading -> {
                    Log.i("EditProfileFragment User Profile Observer", "Loading")
                }
            }
        }
    }

    private fun loadImageWithGlide(uri: Uri?, isBannerImage: Boolean) {
        val imageView = if (isBannerImage) {
            fragmentBinding?.bannerImageView
        } else {
            fragmentBinding?.profileImageView
        }
        if (uri != null && uri.toString().isNotEmpty()) {
            imageView?.let {
                glide.load(uri).centerCrop().into(it)
            }
        } else {
            Log.d("GlideLoad", GLIDE_LOAD_SKIP)
        }
    }

    private fun saveImagesAndUpdateProfile() {
        profileViewModel.saveImagesToFirestore(bannerImageUri, profileImageUri) { response ->
            when (response) {
                is Response.Loading -> {
                    fragmentBinding?.saveChangesButton?.text = getText(R.string.empty_text_view)
                    fragmentBinding?.progressBar?.visibility = View.VISIBLE
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

                is Response.Success -> {
                    saveChanges()
                }
            }
        }
    }

    private fun saveChanges() {
        val userUpdates = mutableMapOf<String, Any?>()
        userUpdates[PROFILE_BANNER] = bannerImageUrl
        userUpdates[PHOTO_URL] = profileImageUrl
        userUpdates[NAME] = fragmentBinding?.usernameEditText?.text.toString()
        updateUserProfile(userUpdates)
    }

    private fun setProfileData(userDocument: DocumentSnapshot) {
        val profileImageUrl = userDocument.getString(PHOTO_URL)
        val profileBannerUrl = userDocument.getString(PROFILE_BANNER)
        val profileUserName = userDocument.getString(NAME)

        if (!profileUserName.isNullOrEmpty()) {
            fragmentBinding?.usernameEditText?.setText(profileUserName)
        }

        if (!profileImageUrl.isNullOrEmpty()) {
            fragmentBinding?.profileImageView?.let {
                glide.load(profileImageUrl).centerCrop().into(
                    it
                )
            }
        } else {
            Log.d("GlideLoad", GLIDE_LOAD_SKIP)
        }

        if (!profileBannerUrl.isNullOrEmpty()) {
            fragmentBinding?.bannerImageView?.let {
                glide.load(profileBannerUrl).centerCrop().into(
                    it
                )
            }
        } else {
            Log.d("GlideLoad", GLIDE_LOAD_SKIP)
        }
    }

    private fun showExitConfirmationDialog() {
        val builder =
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.warning))
                .setMessage(getString(R.string.unsaved_changes_will_be_lost_are_you_sure_you_want_to_exit))
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.cancel()
                }
                .setPositiveButton(getString(R.string.accept)) { _, _ ->
                    // Delete unsaved images from firebase storage
                    findNavController().navigateUp()
                }
        val createShelfDialog = builder.create()
        createShelfDialog.setCanceledOnTouchOutside(false)
        createShelfDialog.show()
    }

    private fun removeBackButtonAndMenu() {
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.CREATED)
    }
}