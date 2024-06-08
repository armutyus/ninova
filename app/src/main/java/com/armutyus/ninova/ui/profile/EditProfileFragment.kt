package com.armutyus.ninova.ui.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.armutyus.ninova.R
import com.armutyus.ninova.databinding.FragmentEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class EditProfileFragment @Inject constructor(
    auth: FirebaseAuth
) : Fragment(R.layout.fragment_edit_profile) {

    private var fragmentBinding: FragmentEditProfileBinding? = null

    private val profileViewModel by activityViewModels<ProfileViewModel>()
    private val user = auth.currentUser

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentEditProfileBinding.bind(view)
        fragmentBinding = binding
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }
}