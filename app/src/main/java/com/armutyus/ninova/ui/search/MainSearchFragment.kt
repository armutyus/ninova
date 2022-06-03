package com.armutyus.ninova.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.armutyus.ninova.R
import com.armutyus.ninova.databinding.FragmentMainSearchBinding
import com.armutyus.ninova.ui.search.viewmodels.MainSearchViewModel

class MainSearchFragment : Fragment(R.layout.fragment_main_search) {

    private var fragmentBinding: FragmentMainSearchBinding? = null
    private lateinit var mainSearchViewModel: MainSearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().actionBar?.hide()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentMainSearchBinding.bind(view)
        fragmentBinding = binding
        mainSearchViewModel = ViewModelProvider(requireActivity())[MainSearchViewModel::class.java]

    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }
}