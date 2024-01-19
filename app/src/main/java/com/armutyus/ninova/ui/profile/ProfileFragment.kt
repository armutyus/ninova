package com.armutyus.ninova.ui.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.armutyus.ninova.R
import com.armutyus.ninova.databinding.FragmentProfileBinding
import javax.inject.Inject

class ProfileFragment @Inject constructor() : Fragment(R.layout.fragment_profile) {

    private var fragmentBinding: FragmentProfileBinding? = null

    private val profileViewModel by activityViewModels<ProfileViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentProfileBinding.bind(view)
        fragmentBinding = binding
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }
}