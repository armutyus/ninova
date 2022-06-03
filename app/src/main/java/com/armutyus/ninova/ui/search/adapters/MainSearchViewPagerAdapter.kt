package com.armutyus.ninova.ui.search.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import javax.inject.Inject

class MainSearchViewPagerAdapter @Inject constructor(
    private val fragmentManager: FragmentManager,
    private val lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {


    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun createFragment(position: Int): Fragment {
        TODO("Not yet implemented")
    }
}