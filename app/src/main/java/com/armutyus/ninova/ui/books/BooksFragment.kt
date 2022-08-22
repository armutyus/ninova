package com.armutyus.ninova.ui.books

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.armutyus.ninova.R
import com.armutyus.ninova.databinding.FragmentBooksBinding
import com.armutyus.ninova.ui.books.adapters.BooksRecyclerViewAdapter
import com.armutyus.ninova.ui.shelves.ShelvesViewModel
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class BooksFragment @Inject constructor(
    private val booksAdapter: BooksRecyclerViewAdapter
) : Fragment(R.layout.fragment_books) {

    private var fragmentBinding: FragmentBooksBinding? = null

    private val booksViewModel by activityViewModels<BooksViewModel>()
    private val shelvesViewModel by activityViewModels<ShelvesViewModel>()

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
                        booksViewModel.insertBook(swipedBook).invokeOnCompletion {
                            booksViewModel.getBookList()
                        }
                    }.show()
                booksViewModel.getBookList()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentBooksBinding.bind(view)
        fragmentBinding = binding

        val recyclerView = binding.mainBooksRecyclerView
        recyclerView.adapter = booksAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        ItemTouchHelper(swipeCallBack).attachToRecyclerView(recyclerView)

        shelvesViewModel.getShelfWithBookList()
        observeBookList()
    }

    override fun onResume() {
        super.onResume()
        booksViewModel.getBookList()
    }

    private fun observeBookList() {
        booksViewModel.localBookList.observe(viewLifecycleOwner) { localBookList ->
            if (localBookList.isEmpty()) {
                fragmentBinding?.linearLayoutBooksError?.visibility = View.VISIBLE
                fragmentBinding?.mainBooksRecyclerView?.visibility = View.GONE
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
}