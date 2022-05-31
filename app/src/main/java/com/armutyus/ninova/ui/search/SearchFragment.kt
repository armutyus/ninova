package com.armutyus.ninova.ui.search

import android.app.ActionBar
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.armutyus.ninova.R
import com.armutyus.ninova.databinding.FragmentSearchBinding
import com.armutyus.ninova.databinding.FragmentShelvesBinding
import com.armutyus.ninova.ui.shelves.ShelvesViewModel
import com.lapism.search.widget.MaterialSearchView
import com.lapism.search.widget.NavigationIconCompat

class SearchFragment : Fragment(R.layout.fragment_search) {

    private var fragmentBinding: FragmentSearchBinding? = null
    private lateinit var searchViewModel: SearchViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSearchBinding.bind(view)
        fragmentBinding = binding
        searchViewModel = ViewModelProvider(requireActivity())[SearchViewModel::class.java]

    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }
}