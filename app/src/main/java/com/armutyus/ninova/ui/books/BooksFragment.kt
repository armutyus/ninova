package com.armutyus.ninova.ui.books

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.databinding.FragmentBooksBinding
import com.armutyus.ninova.model.DataModel
import com.armutyus.ninova.roomdb.entities.BookShelfCrossRef
import com.armutyus.ninova.roomdb.entities.LocalShelf
import com.armutyus.ninova.ui.books.adapters.BooksRecyclerViewAdapter
import com.armutyus.ninova.ui.shelves.ShelvesViewModel
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class BooksFragment @Inject constructor(
    private val booksAdapter: BooksRecyclerViewAdapter
) : Fragment(R.layout.fragment_books) {

    private var fragmentBinding: FragmentBooksBinding? = null
    private lateinit var shelvesViewModel: ShelvesViewModel
    private lateinit var booksViewModel: BooksViewModel
    private val checkFirstTime: SharedPreferences
        get() = requireActivity().getPreferences(Context.MODE_PRIVATE)

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
        shelvesViewModel = ViewModelProvider(requireActivity())[ShelvesViewModel::class.java]

        val recyclerView = binding.mainBooksRecyclerView
        recyclerView.adapter = booksAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        ItemTouchHelper(swipeCallBack).attachToRecyclerView(recyclerView)

        booksViewModel.getBookList()
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


    private fun fetchBooks() {
        booksViewModel.collectBooksFromFirestore().observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Loading -> {
                    Toast.makeText(
                        requireContext(),
                        "Checking user library, please wait..", Toast.LENGTH_SHORT
                    ).show()
                    fragmentBinding?.progressBar?.visibility = View.VISIBLE
                }
                is Response.Success -> {
                    val firebaseBookList = response.data
                    var i = 0
                    while (i < firebaseBookList.size) {

                        val bookId = firebaseBookList[i].bookId
                        val bookAuthor = firebaseBookList[i].bookAuthors
                        val bookCategories = firebaseBookList[i].bookCategories
                        val bookSmallThumbnail = firebaseBookList[i].bookCoverSmallThumbnail
                        val bookThumbnail = firebaseBookList[i].bookCoverThumbnail
                        val bookDescription = firebaseBookList[i].bookDescription
                        val bookNotes = firebaseBookList[i].bookNotes
                        val bookPages = firebaseBookList[i].bookPages
                        val bookPublishedDate = firebaseBookList[i].bookPublishedDate
                        val bookPublisher = firebaseBookList[i].bookPublisher
                        val bookSubtitle = firebaseBookList[i].bookSubtitle
                        val bookTitle = firebaseBookList[i].bookTitle

                        booksViewModel.insertBook(
                            DataModel.LocalBook(
                                bookId, bookAuthor, bookCategories,
                                bookSmallThumbnail, bookThumbnail, bookDescription,
                                bookNotes, bookPages, bookPublishedDate,
                                bookPublisher, bookSubtitle, bookTitle
                            )
                        )
                        i++
                    }
                    fragmentBinding?.progressBar?.visibility = View.GONE
                }
                is Response.Failure -> {
                    println("Firebase Fetch Books Error: " + response.errorMessage)
                    Toast.makeText(requireContext(), response.errorMessage, Toast.LENGTH_LONG)
                        .show()
                    fragmentBinding?.progressBar?.visibility = View.GONE
                }
            }
        }
    }

    private fun fetchCrossRefs() {
        booksViewModel.collectCrossRefFromFirestore().observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Loading -> {
                    fragmentBinding?.progressBar?.visibility = View.VISIBLE
                    println("Downloading CrossRefs")
                }
                is Response.Success -> {
                    val firebaseCrossRefList = response.data
                    var i = 0
                    while (i < firebaseCrossRefList.size) {
                        val bookId = firebaseCrossRefList[i].bookId
                        val shelfId = firebaseCrossRefList[i].shelfId
                        shelvesViewModel.insertBookShelfCrossRef(BookShelfCrossRef(bookId, shelfId))
                        i++
                    }
                    fragmentBinding?.progressBar?.visibility = View.GONE
                }
                is Response.Failure -> {
                    println("Firebase Fetch CrossRefs Error: " + response.errorMessage)
                    Toast.makeText(requireContext(), response.errorMessage, Toast.LENGTH_LONG)
                        .show()
                    fragmentBinding?.progressBar?.visibility = View.GONE
                }
            }
        }
    }

    private fun fetchShelves() {
        booksViewModel.collectShelvesFromFirestore().observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Loading -> {
                    fragmentBinding?.progressBar?.visibility = View.VISIBLE
                    println("Downloading Shelves")
                }
                is Response.Success -> {
                    val firebaseShelvesList = response.data
                    var i = 0
                    while (i < firebaseShelvesList.size) {
                        val shelfId = firebaseShelvesList[i].shelfId
                        val shelfTitle = firebaseShelvesList[i].shelfTitle
                        val createdAt = firebaseShelvesList[i].createdAt
                        val shelfCover = firebaseShelvesList[i].shelfCover
                        shelvesViewModel.insertShelf(
                            LocalShelf(
                                shelfId,
                                shelfTitle,
                                createdAt,
                                shelfCover
                            )
                        )
                        i++
                    }
                    checkFirstTime.edit().putBoolean("first_time", false).apply()
                    fragmentBinding?.progressBar?.visibility = View.GONE
                }
                is Response.Failure -> {
                    println("Firebase Fetch Shelves Error: " + response.errorMessage)
                    Toast.makeText(requireContext(), response.errorMessage, Toast.LENGTH_LONG)
                        .show()
                    fragmentBinding?.progressBar?.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }
}