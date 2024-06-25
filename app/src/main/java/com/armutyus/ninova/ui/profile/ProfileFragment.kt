package com.armutyus.ninova.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Constants.GLIDE_LOAD_SKIP
import com.armutyus.ninova.constants.Constants.NAME
import com.armutyus.ninova.constants.Constants.PHOTO_URL
import com.armutyus.ninova.constants.Constants.PROFILE_BANNER
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.databinding.FragmentProfileBinding
import com.bumptech.glide.RequestManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import javax.inject.Inject

class ProfileFragment @Inject constructor(
    auth: FirebaseAuth,
    private val glide: RequestManager
) : Fragment(R.layout.fragment_profile) {

    private var fragmentBinding: FragmentProfileBinding? = null

    private val profileViewModel by activityViewModels<ProfileViewModel>()
    private val user = auth.currentUser

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentProfileBinding.bind(view)
        fragmentBinding = binding

        fragmentBinding?.appSettings?.setOnClickListener {
            val action = ProfileFragmentDirections.actionNavigationProfileToSettingsFragment()
            Navigation.findNavController(it).navigate(action)
        }

        fragmentBinding?.userReviews?.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.coming_soon), Toast.LENGTH_LONG)
                .show()
        }

        fragmentBinding?.userRatings?.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.coming_soon), Toast.LENGTH_LONG)
                .show()
        }

        fragmentBinding?.editProfile?.setOnClickListener {
            val action = ProfileFragmentDirections.actionNavigationProfileToEditProfileFragment()
            Navigation.findNavController(it).navigate(action)
        }

        fragmentBinding?.editProfileButton?.setOnClickListener {
            val action = ProfileFragmentDirections.actionNavigationProfileToEditProfileFragment()
            Navigation.findNavController(it).navigate(action)
        }

        profileViewModel.getUserProfile()
        userProfileObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }

    private fun userProfileObserver() {
        profileViewModel.userProfile.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Success -> {
                    val userDocument = response.data
                    setProfileData(userDocument)
                }

                is Response.Failure -> {
                    Log.e("ProfileFragment User Profile Observer", response.errorMessage)
                }

                is Response.Loading -> {
                    Log.i("ProfileFragment User Profile Observer", "Loading")
                }
            }
        }
    }

    private fun setProfileData(userDocument: DocumentSnapshot) {
        val profileImageUrl = userDocument.getString(PHOTO_URL)
        val profileBannerUrl = userDocument.getString(PROFILE_BANNER)
        val profileUserName = userDocument.getString(NAME)

        if (profileUserName.isNullOrEmpty()) {
            fragmentBinding?.usernameTextView?.text = user?.email?.substringBefore("@")
        } else {
            fragmentBinding?.usernameTextView?.text = profileUserName
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
}