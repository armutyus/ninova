package com.armutyus.ninova.ui.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.armutyus.ninova.R
import com.armutyus.ninova.databinding.FragmentSearchApiBinding
import com.armutyus.ninova.ui.search.viewmodels.SearchApiViewModel
import javax.inject.Inject

class SearchApiFragment @Inject constructor(

) : Fragment(R.layout.fragment_search_api) {

    private var fragmentBinding: FragmentSearchApiBinding? = null
    private lateinit var searchApiViewModel: SearchApiViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSearchApiBinding.bind(view)
        fragmentBinding = binding
        searchApiViewModel = ViewModelProvider(requireActivity())[SearchApiViewModel::class.java]

        binding.linearLayoutSearchError.visibility = View.VISIBLE

    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }
}