package com.armutyus.ninova.ui.discover

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.armutyus.ninova.R
import com.armutyus.ninova.databinding.FragmentDiscoverBinding
import javax.inject.Inject

class DiscoverFragment @Inject constructor(

) : Fragment(R.layout.fragment_discover) {

    private var fragmentBinding: FragmentDiscoverBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentDiscoverBinding.bind(view)
        fragmentBinding = binding

        binding.animationView.visibility = View.VISIBLE

    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }
}