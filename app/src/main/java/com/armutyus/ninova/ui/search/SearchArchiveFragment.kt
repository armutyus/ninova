package com.armutyus.ninova.ui.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.armutyus.ninova.R
import com.armutyus.ninova.databinding.FragmentSearchArchiveBinding
import com.armutyus.ninova.ui.search.viewmodels.SearchArchiveViewModel
import javax.inject.Inject

class SearchArchiveFragment @Inject constructor(

) : Fragment(R.layout.fragment_search_archive) {

    private var fragmentBinding: FragmentSearchArchiveBinding? = null
    private lateinit var searchArchiveViewModel: SearchArchiveViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSearchArchiveBinding.bind(view)
        fragmentBinding = binding
        searchArchiveViewModel =
            ViewModelProvider(requireActivity())[SearchArchiveViewModel::class.java]

        binding.linearLayoutSearchError.visibility = View.VISIBLE

    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }
}