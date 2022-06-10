package com.armutyus.ninova.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Constants.LOGIN_INTENT
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class SettingsFragment @Inject constructor(
    private val auth: FirebaseAuth
) : Fragment(R.layout.fragment_settings) {

    @Named(LOGIN_INTENT)
    @Inject
    lateinit var loginIntent: Intent

    private var fragmentBinding: FragmentSettingsBinding? = null
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = auth.currentUser

        val binding = FragmentSettingsBinding.bind(view)
        fragmentBinding = binding
        settingsViewModel =
            ViewModelProvider(requireActivity())[SettingsViewModel::class.java]

        binding.signOut.setOnClickListener {
            if (user != null) {
                signOut()
                startActivity(loginIntent)
                activity?.finish()
            } else {
                Toast.makeText(requireContext(),"There is no user", Toast.LENGTH_LONG).show()
            }

        }

    }

    private fun signOut() {
        settingsViewModel.signOut().observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Loading -> fragmentBinding?.progressBar?.show()
                is Response.Success -> fragmentBinding?.progressBar?.hide()
                is Response.Failure -> {
                    println("Create Error: " + response.errorMessage)
                    Toast.makeText(requireContext(), response.errorMessage, Toast.LENGTH_LONG)
                        .show()
                    fragmentBinding?.progressBar?.hide()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }
}