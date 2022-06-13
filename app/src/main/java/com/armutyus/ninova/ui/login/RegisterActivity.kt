package com.armutyus.ninova.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.armutyus.ninova.constants.Constants
import com.armutyus.ninova.databinding.ActivityRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    @Named(Constants.MAIN_INTENT)
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
        }

    }
}