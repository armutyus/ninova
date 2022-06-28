package com.armutyus.ninova.ui.books

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.armutyus.ninova.R
import com.armutyus.ninova.databinding.ActivityBookDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}