package com.armutyus.ninova.ui.search

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.armutyus.ninova.R
import com.armutyus.ninova.databinding.FragmentMainSearchBinding
import com.armutyus.ninova.roomdb.LocalBook
import com.armutyus.ninova.ui.search.adapters.MainSearchRecyclerViewAdapter
import com.armutyus.ninova.ui.search.listeners.OnBookAddButtonClickListener
import javax.inject.Inject

class MainSearchFragment @Inject constructor(
    private val recyclerViewAdapter: MainSearchRecyclerViewAdapter
) : Fragment(R.layout.fragment_main_search), SearchView.OnQueryTextListener,
    OnBookAddButtonClickListener {

    private var fragmentBinding: FragmentMainSearchBinding? = null
    private val binding get() = fragmentBinding
    private lateinit var isSearchActive: SharedPreferences
    private lateinit var mainSearchViewModel: MainSearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isSearchActive = requireActivity().getPreferences(Context.MODE_PRIVATE) ?: return
        mainSearchViewModel = ViewModelProvider(requireActivity())[MainSearchViewModel::class.java]

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

        val recyclerView = binding?.mainSearchRecyclerView
        recyclerView?.adapter = recyclerViewAdapter
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        recyclerView?.visibility = View.VISIBLE
        recyclerViewAdapter.setFragment(this)

        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        mainSearchViewModel.getBooksList()
        getFakeBooksList()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(searchQuery: String?): Boolean {
        if (searchQuery?.length!! > 0) {

            mainSearchViewModel.getBooksArchiveList(searchQuery)
            mainSearchViewModel.getBooksApiList(searchQuery)

            binding?.progressBar?.visibility = View.VISIBLE
            binding?.mainSearchRecyclerView?.visibility = View.GONE
            binding?.mainSearchBooksTitle?.visibility = View.GONE

            showResultsFromLocal()

            val toggleButtonGroup = binding?.searchButtonToggleGroup
            toggleButtonGroup?.visibility = View.VISIBLE
            toggleButtonGroup?.addOnButtonCheckedListener { group, checkedId, isChecked ->
                if (!isChecked) {
                    showResultsFromLocal()
                } else {
                    when (checkedId) {
                        R.id.localSearchButton -> {
                            showResultsFromLocal()
                        }

                        R.id.apiSearchButton -> {
                            showResultsFromApi()
                        }
                    }
                }
            }

        } else if (searchQuery.isNullOrBlank()) {

            binding?.mainSearchRecyclerView?.visibility = View.VISIBLE
            binding?.mainSearchBooksTitle?.visibility = View.VISIBLE
            binding?.itemDivider?.visibility = View.VISIBLE
            binding?.searchButtonToggleGroup?.visibility = View.GONE
            binding?.linearLayoutSearchError?.visibility = View.GONE

            getFakeBooksList()

        }

        return true
    }

    private fun getFakeBooksList() {

        mainSearchViewModel.fakeBooksList.observe(viewLifecycleOwner) {
            val newBooksList = it.toList()
            recyclerViewAdapter.mainSearchBooksList = newBooksList
        }
    }

    private fun showResultsFromLocal() {

        mainSearchViewModel.fakeBooksArchiveList.observe(viewLifecycleOwner) {
            val searchedLocalList = it?.toList()
            recyclerViewAdapter.mainSearchBooksList = searchedLocalList!!

            if (searchedLocalList.isEmpty()) {
                binding?.linearLayoutSearchError?.visibility = View.VISIBLE
                binding?.progressBar?.visibility = View.GONE
                binding?.mainSearchBooksTitle?.visibility = View.GONE
                binding?.mainSearchRecyclerView?.visibility = View.GONE
            } else {
                binding?.linearLayoutSearchError?.visibility = View.GONE
                binding?.progressBar?.visibility = View.GONE
                binding?.mainSearchBooksTitle?.visibility = View.GONE
                binding?.mainSearchRecyclerView?.visibility = View.VISIBLE
            }

        }

    }

    private fun showResultsFromApi() {
        mainSearchViewModel.fakeBooksApiList.observe(viewLifecycleOwner) {
            val searchedApiList = it?.toList()
            recyclerViewAdapter.mainSearchBooksList = searchedApiList!!

            if (searchedApiList.isEmpty()) {
                binding?.linearLayoutSearchError?.visibility = View.VISIBLE
                binding?.progressBar?.visibility = View.GONE
                binding?.mainSearchRecyclerView?.visibility = View.GONE
                binding?.mainSearchBooksTitle?.visibility = View.GONE
            } else {
                binding?.linearLayoutSearchError?.visibility = View.GONE
                binding?.progressBar?.visibility = View.GONE
                binding?.mainSearchBooksTitle?.visibility = View.GONE
                binding?.mainSearchRecyclerView?.visibility = View.VISIBLE
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }

    override fun onClick(localBook: LocalBook) {
        mainSearchViewModel.insertBook(localBook)
    }

}