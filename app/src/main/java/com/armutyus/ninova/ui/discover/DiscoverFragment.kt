package com.armutyus.ninova.ui.discover

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Util.Companion.fadeIn
import com.armutyus.ninova.databinding.FragmentDiscoverBinding
import com.armutyus.ninova.ui.discover.adapters.DiscoverRecyclerViewAdapter
import javax.inject.Inject

class DiscoverFragment @Inject constructor(
    private val discoverAdapter: DiscoverRecyclerViewAdapter
) : Fragment(R.layout.fragment_discover) {
    private var fragmentBinding: FragmentDiscoverBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentDiscoverBinding.bind(view)
        fragmentBinding = binding

        binding.appNameTextView.fadeIn(1000)

        val recyclerView = binding.discoverRecyclerView
        recyclerView.adapter = discoverAdapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
        recyclerView.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }
}