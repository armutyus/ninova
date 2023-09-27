package com.armutyus.ninova.ui.discover

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.constants.Util.Companion.fadeIn
import com.armutyus.ninova.databinding.FragmentDiscoverCategoryBinding
import com.armutyus.ninova.ui.discover.adapters.DiscoverCategoryRecyclerViewAdapter
import javax.inject.Inject

class DiscoverCategoryFragment @Inject constructor(
    private val discoverCategoryAdapter: DiscoverCategoryRecyclerViewAdapter
) : Fragment(R.layout.fragment_discover_category) {

    private var fragmentBinding: FragmentDiscoverCategoryBinding? = null
    private var offset = 0
    private val args: DiscoverCategoryFragmentArgs by navArgs()

    private val discoverViewModel by activityViewModels<DiscoverViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentDiscoverCategoryBinding.bind(view)
        fragmentBinding = binding

        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        binding.appNameTextView.fadeIn(1000)

        val recyclerView = binding.discoverCategoryRecyclerView
        recyclerView.adapter = discoverCategoryAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.visibility = View.VISIBLE

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(-1)) {
                    (requireActivity() as AppCompatActivity).supportActionBar?.show()
                } else {
                    (requireActivity() as AppCompatActivity).supportActionBar?.hide()
                }

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                if (lastVisibleItemPosition == discoverCategoryAdapter.itemCount - 1 && dy > 0) {
                    loadBooks()
                }

            }
        })

        loadBooks()
        runObservers()
    }

    override fun onResume() {
        super.onResume()
        discoverCategoryAdapter.clearData()
        offset = 0
    }

    override fun onPause() {
        super.onPause()
        discoverCategoryAdapter.clearData()
        offset = 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }

    private fun runObservers() {
        discoverViewModel.booksFromApiResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Loading -> {
                    fragmentBinding?.progressBar?.visibility = View.VISIBLE
                }

                is Response.Failure -> {
                    Log.i("OpenLibraryError", response.errorMessage)
                }

                is Response.Success -> {
                    val bookList = response.data.works.toList()
                    Log.i("CategoryObserver", bookList.size.toString())
                    discoverCategoryAdapter.setData(bookList)
                }
            }
        }
    }

    private fun loadBooks() {
        discoverViewModel.booksFromApi(args.categoryTitle, offset).invokeOnCompletion {
            offset += discoverCategoryAdapter.itemCount
        }
    }

}