package com.armutyus.ninova.ui.search.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.armutyus.ninova.ui.search.SearchApiFragment
import com.armutyus.ninova.ui.search.SearchArchiveFragment
import javax.inject.Inject

class MainSearchViewPagerAdapter @Inject constructor(
    private val fragmentManager: FragmentManager,
    private val lifecycle: Lifecycle
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                SearchArchiveFragment()
            }

            1 -> {
                SearchApiFragment()
            }
            else -> {
                Fragment()
            }
        }
    }
}