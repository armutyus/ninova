package com.armutyus.ninova.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.armutyus.ninova.constants.Constants.LOGIN_INTENT
import com.armutyus.ninova.constants.Constants.MAIN_INTENT
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.databinding.ActivityRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    @Named(LOGIN_INTENT)
    @Inject
    lateinit var loginIntent: Intent

    @Named(MAIN_INTENT)
    @Inject
    lateinit var mainIntent: Intent

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when (intent.getStringExtra("action")) {
            "register" -> binding.registerUserLayout.visibility = View.VISIBLE
            "change_email" -> binding.changeEmailLayout.visibility = View.VISIBLE
            "change_password" -> binding.changePasswordLayout.visibility = View.VISIBLE
            "forgot_password" -> binding.forgotPasswordLayout.visibility = View.VISIBLE
        }

        binding.sendResetPasswordButton.setOnClickListener {
            sendPasswordEmail()
        }
    }

    private var email = ""
    private var password = ""

    private fun sendPasswordEmail() {
        email = binding.forgotPasswordEmailText.text.toString().trim()

        if (email.isNotEmpty()) {
            viewModel.sendPasswordEmail(email).observe(this) { response ->
                when (response) {
                    is Response.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is Response.Success -> {
                        Toast.makeText(this, "Reset password e-mail sent.", Toast.LENGTH_LONG)
                            .show()
                        binding.progressBar.visibility = View.GONE
                        goToLogInActivity()
                    }
                    is Response.Failure -> {
                        Toast.makeText(this, response.errorMessage, Toast.LENGTH_LONG)
                            .show()
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        } else {
            Toast.makeText(this, "Please enter your information correctly!", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun goToMainActivity() {
        startActivity(mainIntent)
        finish()
    }

    private fun goToLogInActivity() {
        startActivity(loginIntent)
        finish()
    }

}