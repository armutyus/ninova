package com.armutyus.ninova.ui.books

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.armutyus.ninova.R
import com.armutyus.ninova.databinding.FragmentBooksBinding
import com.armutyus.ninova.ui.books.adapters.BooksRecyclerViewAdapter
import javax.inject.Inject

class BooksFragment @Inject constructor(
    private val booksAdapter: BooksRecyclerViewAdapter
) : Fragment(R.layout.fragment_books) {

    private var fragmentBinding: FragmentBooksBinding? = null
    private lateinit var booksViewModel: BooksViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentBooksBinding.bind(view)
        fragmentBinding = binding
        booksViewModel = ViewModelProvider(requireActivity())[BooksViewModel::class.java]

        val recyclerView = binding.mainBooksRecyclerView
        recyclerView.adapter = booksAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

    }

    override fun onResume() {
        booksViewModel.getBookList()
        observeBookList()
        super.onResume()
    }

    private fun observeBookList() {
        booksViewModel.bookList.observe(this) { localBookList ->
            val userBookList = localBookList
            if (userBookList.isEmpty()) {
                fragmentBinding?.linearLayoutBooksError?.visibility = View.VISIBLE
            } else {
                fragmentBinding?.linearLayoutBooksError?.visibility = View.GONE
                fragmentBinding?.mainBooksRecyclerView?.visibility = View.VISIBLE
                booksAdapter.mainBooksList = userBookList
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }
}