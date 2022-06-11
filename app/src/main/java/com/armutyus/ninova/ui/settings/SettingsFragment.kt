package com.armutyus.ninova.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Constants.LOGIN_INTENT
import com.armutyus.ninova.constants.Constants.MAIN_INTENT
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.databinding.FragmentSettingsBinding
import com.armutyus.ninova.databinding.RegisterUserBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
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

    @Named(MAIN_INTENT)
    @Inject
    lateinit var mainIntent: Intent

    private var fragmentBinding: FragmentSettingsBinding? = null
    private lateinit var bottomSheetBinding: RegisterUserBottomSheetBinding
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = auth.currentUser

        val binding = FragmentSettingsBinding.bind(view)
        fragmentBinding = binding
        settingsViewModel =
            ViewModelProvider(requireActivity())[SettingsViewModel::class.java]

        if (user!!.isAnonymous) {
            binding.profileSettingsAnonymous.visibility = View.VISIBLE
            binding.aboutApp.visibility = View.VISIBLE
            binding.registerLayout.visibility = View.VISIBLE
        } else {
            binding.profileSettingsUser.visibility = View.VISIBLE
            binding.aboutApp.visibility = View.VISIBLE
            binding.signOutLayout.visibility = View.VISIBLE
        }

        binding.signOut.setOnClickListener {
            signOut()
            goToLogInActivity()
        }

        binding.register.setOnClickListener {
            showRegisterDialog()
        }

        binding.signInAnotherAccount.setOnClickListener {
            signOut()
            goToLogInActivity()
        }
    }

    private fun showRegisterDialog() {
        val dialog = BottomSheetDialog(requireContext())
        bottomSheetBinding = RegisterUserBottomSheetBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)
        val signUpButton = dialog.findViewById<MaterialButton>(R.id.signUpButton)

        signUpButton?.setOnClickListener {
            registerUser()
        }

        dialog.show()
    }

    private var email = ""
    private var password = ""

    private fun registerUser() {
        email = bottomSheetBinding.registerEmailText.text.toString().trim()
        password = bottomSheetBinding.registerPasswordText.text.toString().trim()
        val confirmPassword = bottomSheetBinding.registerConfirmPasswordText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || password != confirmPassword) {
            Toast.makeText(requireContext(), "Please enter your information correctly!", Toast.LENGTH_LONG)
                .show()
        } else {
            val credential = EmailAuthProvider.getCredential(email, password)
            registerAnonymousUser(credential)
        }
    }

    private fun registerAnonymousUser(credential: AuthCredential) {
        settingsViewModel.registerAnonymousUser(credential).observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Loading -> fragmentBinding?.progressBar?.visibility = View.VISIBLE
                is Response.Success -> {
                    createUserProfile()
                    fragmentBinding?.progressBar?.visibility = View.GONE
                }
                is Response.Failure -> {
                    println("SignUp Error: " + response.errorMessage)
                    Toast.makeText(requireContext(), response.errorMessage, Toast.LENGTH_LONG)
                        .show()
                    fragmentBinding?.progressBar?.visibility = View.GONE
                }
            }
        }
    }

    private fun createUserProfile() {
        settingsViewModel.createUser().observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Loading -> fragmentBinding?.progressBar?.visibility = View.VISIBLE
                is Response.Success -> {
                    goToMainActivity()
                    fragmentBinding?.progressBar?.visibility = View.GONE
                }
                is Response.Failure -> {
                    println("Create Error: " + response.errorMessage)
                    Toast.makeText(requireContext(), response.errorMessage, Toast.LENGTH_LONG)
                        .show()
                    fragmentBinding?.progressBar?.visibility = View.GONE
                }
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

    private fun goToMainActivity() {
        startActivity(mainIntent)
        activity?.finish()
    }

    private fun goToLogInActivity() {
        startActivity(loginIntent)
        activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }
}