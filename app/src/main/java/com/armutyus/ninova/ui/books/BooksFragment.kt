package com.armutyus.ninova.ui.books

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.armutyus.ninova.R
import com.armutyus.ninova.databinding.FragmentBooksBinding
import com.armutyus.ninova.roomdb.entities.LocalBook
import com.armutyus.ninova.ui.books.adapters.BooksRecyclerViewAdapter
import com.armutyus.ninova.ui.books.listeners.OnBookLongClickListener
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class BooksFragment @Inject constructor(
    private val booksAdapter: BooksRecyclerViewAdapter
) : Fragment(R.layout.fragment_books), OnBookLongClickListener {

    private var fragmentBinding: FragmentBooksBinding? = null
    private lateinit var booksViewModel: BooksViewModel
    private val swipeCallBack = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val layoutPosition = viewHolder.layoutPosition
            val swipedBook = booksAdapter.mainBooksList[layoutPosition]
            booksViewModel.deleteBook(swipedBook).invokeOnCompletion {
                Snackbar.make(requireView(), "Book deleted from your library", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        booksViewModel.insertBook(swipedBook)
                    }.show()
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentBooksBinding.bind(view)
        fragmentBinding = binding
        booksViewModel = ViewModelProvider(requireActivity())[BooksViewModel::class.java]

        val recyclerView = binding.mainBooksRecyclerView
        recyclerView.adapter = booksAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        booksAdapter.setFragment(this)
        ItemTouchHelper(swipeCallBack).attachToRecyclerView(recyclerView)

    }

    override fun onResume() {
        super.onResume()

        booksViewModel.getBookList()
        observeBookList()

    }

    private fun observeBookList() {
        booksViewModel.bookList.observe(this) { localBookList ->
            if (localBookList.isEmpty()) {
                fragmentBinding?.linearLayoutBooksError?.visibility = View.VISIBLE
            } else {
                fragmentBinding?.linearLayoutBooksError?.visibility = View.GONE
                fragmentBinding?.mainBooksRecyclerView?.visibility = View.VISIBLE
                booksAdapter.mainBooksList = localBookList
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }

    override fun onLongClick(localBook: LocalBook) {
        Toast.makeText(requireContext(),"Clicked ${localBook.bookId}",Toast.LENGTH_LONG).show()
    }
}