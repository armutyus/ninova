package com.armutyus.ninova.ui.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.armutyus.ninova.R
import com.armutyus.ninova.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class ProfileFragment @Inject constructor(
    auth: FirebaseAuth
) : Fragment(R.layout.fragment_profile) {

    private var fragmentBinding: FragmentProfileBinding? = null

    private val profileViewModel by activityViewModels<ProfileViewModel>()
    private val user = auth.currentUser

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentProfileBinding.bind(view)
        fragmentBinding = binding

        binding.appSettings.setOnClickListener {
            val action = ProfileFragmentDirections.actionNavigationProfileToSettingsFragment()
            Navigation.findNavController(it).navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }
}