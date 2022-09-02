package com.armutyus.ninova.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.databinding.FragmentMainSearchBinding
import com.armutyus.ninova.model.DataModel
import com.armutyus.ninova.ui.books.BooksViewModel
import com.armutyus.ninova.ui.search.adapters.MainSearchRecyclerViewAdapter
import com.armutyus.ninova.ui.search.listeners.OnBookAddButtonClickListener
import javax.inject.Inject

class MainSearchFragment @Inject constructor(
    private val searchFragmentAdapter: MainSearchRecyclerViewAdapter
) : Fragment(R.layout.fragment_main_search), SearchView.OnQueryTextListener,
    OnBookAddButtonClickListener {

    private var fragmentBinding: FragmentMainSearchBinding? = null
    private val binding get() = fragmentBinding
    private val booksViewModel by activityViewModels<BooksViewModel>()
    private val mainSearchViewModel by activityViewModels<MainSearchViewModel>()

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
        recyclerView?.adapter = searchFragmentAdapter
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        recyclerView?.visibility = View.VISIBLE
        searchFragmentAdapter.setFragment(this)
        searchFragmentAdapter.setViewModel(booksViewModel)

        val toggleButtonGroup = binding?.searchButtonToggleGroup
        toggleButtonGroup?.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.localSearchButton -> {
                        val list = mainSearchViewModel.currentLocalBookList.value ?: listOf()
                        mainSearchViewModel.setCurrentLocalBookList(list)
                    }
                    R.id.apiSearchButton -> {
                        val list = mainSearchViewModel.currentList.value ?: listOf()
                        mainSearchViewModel.setCurrentList(list)
                    }
                }
            }
        }

        mainSearchViewModel.randomBooksFromApi()
        runObservers()

        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        booksViewModel.getBookList()
        setVisibilitiesForSearchQueryNull()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(searchQuery: String?): Boolean {
        if (searchQuery?.length!! > 0) {
            binding?.progressBar?.visibility = View.VISIBLE
            binding?.mainSearchRecyclerView?.visibility = View.GONE

            mainSearchViewModel.searchLocalBooks("%$searchQuery%")
            mainSearchViewModel.searchBooksFromApi(searchQuery)

            val toggleButtonGroup = binding?.searchButtonToggleGroup
            toggleButtonGroup?.visibility = View.VISIBLE

        } else if (searchQuery.isNullOrBlank()) {
            mainSearchViewModel.randomBooksFromApi()
            setVisibilitiesForSearchQueryNull()
        }

        return true
    }

    private fun runObservers() {
        val toggleButtonGroup = binding?.searchButtonToggleGroup

        mainSearchViewModel.searchLocalBookList.observe(viewLifecycleOwner) {
            //if (toggleButtonGroup?.checkedButtonId != R.id.localSearchButton) return@observe
            mainSearchViewModel.setCurrentLocalBookList(it.toList())
        }

        mainSearchViewModel.randomBooksResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Loading -> {
                    binding?.progressBar?.visibility = View.VISIBLE
                }

                is Response.Success -> {
                    val bookItemsList = response.data.items?.toList() ?: listOf()
                    mainSearchViewModel.setCurrentList(bookItemsList)
                }

                is Response.Failure -> {
                    setVisibilitiesForFailure()
                }
            }
        }

        mainSearchViewModel.searchBooksResponse.observe(viewLifecycleOwner) { response ->
            //if (toggleButtonGroup?.checkedButtonId != R.id.apiSearchButton) return@observe
            when (response) {
                is Response.Loading -> {
                    binding?.progressBar?.visibility = View.VISIBLE
                }

                is Response.Success -> {
                    val bookItemsList = response.data.items?.toList() ?: listOf()
                    mainSearchViewModel.setCurrentList(bookItemsList)
                }

                is Response.Failure -> {
                    setVisibilitiesForFailure()
                }
            }
        }

        mainSearchViewModel.currentList.observe(viewLifecycleOwner) {
            setVisibilities(it)
            if (toggleButtonGroup?.checkedButtonId != R.id.apiSearchButton) return@observe
            searchFragmentAdapter.setDataType(it)
        }

        mainSearchViewModel.currentLocalBookList.observe(viewLifecycleOwner) {
            setVisibilities(it)
            if (toggleButtonGroup?.checkedButtonId != R.id.localSearchButton) return@observe
            searchFragmentAdapter.setDataType(it)
        }

    }

    private fun setVisibilities(bookList: List<DataModel>) {
        if (bookList.isEmpty()) {
            binding?.linearLayoutSearchError?.visibility = View.VISIBLE
            binding?.progressBar?.visibility = View.GONE
            binding?.mainSearchRecyclerView?.visibility = View.GONE
        } else {
            binding?.linearLayoutSearchError?.visibility = View.GONE
            binding?.progressBar?.visibility = View.GONE
            binding?.mainSearchRecyclerView?.visibility = View.VISIBLE
        }
    }

    private fun setVisibilitiesForSearchQueryNull() {
        binding?.mainSearchRecyclerView?.visibility = View.VISIBLE
        binding?.searchButtonToggleGroup?.visibility = View.GONE
        binding?.linearLayoutSearchError?.visibility = View.GONE
    }

    private fun setVisibilitiesForFailure() {
        binding?.linearLayoutSearchError?.visibility = View.VISIBLE
        binding?.progressBar?.visibility = View.GONE
        binding?.mainSearchRecyclerView?.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }

    override fun onClick(localBook: DataModel.LocalBook) {
        mainSearchViewModel.insertBook(localBook)
    }

    override fun onAddedButtonClick(id: String) {
        mainSearchViewModel.deleteBookById(id)
    }

}