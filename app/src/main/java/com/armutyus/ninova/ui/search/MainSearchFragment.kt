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
import androidx.fragment.app.viewModels
import com.armutyus.ninova.R
import com.armutyus.ninova.databinding.FragmentMainSearchBinding
import com.armutyus.ninova.ui.search.adapters.MainSearchViewPagerAdapter
import com.armutyus.ninova.ui.search.viewmodels.MainSearchViewModel
import com.google.android.material.tabs.TabLayoutMediator
import javax.inject.Inject

class MainSearchFragment @Inject constructor(
) : Fragment(R.layout.fragment_main_search), SearchView.OnQueryTextListener {

    private var fragmentBinding: FragmentMainSearchBinding? = null
    private val binding get() = fragmentBinding
    private lateinit var isSearchActive: SharedPreferences

    private val mainSearchViewModel by viewModels<MainSearchViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isSearchActive = requireActivity().getPreferences(Context.MODE_PRIVATE) ?: return
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBinding = FragmentMainSearchBinding.inflate(inflater, container, false)
        val searchView = binding?.mainSearch
        searchView?.setOnQueryTextListener(this)
        searchView?.setIconifiedByDefault(false)

        return binding?.root
    }

    private fun showChildSearchFragments() {
        binding?.animationView?.visibility = View.GONE
        val tabLayout = binding?.mainSearchTabLayout
        val viewPager = binding?.mainSearchViewPager
        val vpAdapter = MainSearchViewPagerAdapter(childFragmentManager, lifecycle)
        viewPager?.adapter = vpAdapter

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

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(searchQuery: String?): Boolean {
        if (searchQuery?.length!! > 0) {

            showChildSearchFragments()

        } else if (searchQuery.isNullOrBlank()) {

            binding?.animationView?.visibility = View.VISIBLE
            binding?.mainSearchTabLayout?.visibility = View.GONE
            binding?.mainSearchViewPager?.visibility = View.GONE

        }


        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }

}