package com.armutyus.ninova.ui.search

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.armutyus.ninova.R
import com.armutyus.ninova.databinding.FragmentMainSearchBinding
import com.armutyus.ninova.ui.search.adapters.MainSearchViewPagerAdapter
import com.armutyus.ninova.ui.search.viewmodels.MainSearchViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class MainSearchFragment @Inject constructor(
    private val viewPagerAdapter: MainSearchViewPagerAdapter
) : Fragment(R.layout.fragment_main_search), SearchView.OnQueryTextListener {

    private var fragmentBinding: FragmentMainSearchBinding? = null
    private val binding get() = fragmentBinding
    private lateinit var isSearchActive: SharedPreferences

    private val mainSearchViewModel by viewModels<MainSearchViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isSearchActive = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBinding = FragmentMainSearchBinding.inflate(inflater, container, false)

        if (isSearchActive.getBoolean("active", false)) {
            binding?.animationView?.visibility = View.VISIBLE
        } else {
            binding?.animationView?.visibility = View.GONE
            val tabLayout = binding?.mainSearchTabLayout
            val viewPager = binding?.mainSearchViewPager
            viewPager?.adapter = viewPagerAdapter

            if (tabLayout != null && viewPager != null) {
                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    when (position) {
                        0 -> {
                            tab.text = "YOUR LIBRARY"
                        }
                        1 -> {
                            tab.text = "FROM NINOVA"
                        }
                    }
                }.attach()
            }
        }

        return binding?.root
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(searchQuery: String?): Boolean {
        if (searchQuery!!.isNotEmpty()) {

            isSearchActive.edit {
                putBoolean("active", true)
                apply()
            }

        } else {
            return true
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }

}