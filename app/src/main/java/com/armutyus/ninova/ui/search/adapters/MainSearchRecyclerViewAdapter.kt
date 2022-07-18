package com.armutyus.ninova.ui.search.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Constants.BOOK_DETAILS_INTENT
import com.armutyus.ninova.model.GoogleBookItem
import com.armutyus.ninova.model.GoogleBookItemInfo
import com.armutyus.ninova.roomdb.entities.LocalBook
import com.armutyus.ninova.ui.books.BooksViewModel
import com.armutyus.ninova.ui.search.MainSearchFragment
import com.armutyus.ninova.ui.search.MainSearchViewModel
import com.bumptech.glide.RequestManager
import javax.inject.Inject
import javax.inject.Named

class MainSearchRecyclerViewAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<MainSearchRecyclerViewAdapter.MainSearchViewHolder>() {

    @Named(BOOK_DETAILS_INTENT)
    @Inject
    lateinit var bookDetailsIntent: Intent

    private lateinit var searchFragment: MainSearchFragment
    private lateinit var booksViewModel: BooksViewModel

    class MainSearchViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private val diffUtil = object : DiffUtil.ItemCallback<GoogleBookItem>() {
        override fun areItemsTheSame(oldItem: GoogleBookItem, newItem: GoogleBookItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: GoogleBookItem, newItem: GoogleBookItem): Boolean {
            return oldItem == newItem
        }
    }

    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var mainSearchBooksList: List<GoogleBookItem>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainSearchViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.search_main_row, parent, false)

        return MainSearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainSearchViewHolder, position: Int) {
        val bookCover = holder.itemView.findViewById<ImageView>(R.id.bookImage)
        val bookTitle = holder.itemView.findViewById<TextView>(R.id.bookTitleText)
        val bookAuthor = holder.itemView.findViewById<TextView>(R.id.bookAuthorText)
        val bookPages = holder.itemView.findViewById<TextView>(R.id.bookPageText)
        val bookReleaseDate = holder.itemView.findViewById<TextView>(R.id.bookReleaseDateText)
        val book = mainSearchBooksList[position]

        val addButton = holder.itemView.findViewById<ImageButton>(R.id.main_search_add_button)
        val addedButton =
            holder.itemView.findViewById<ImageButton>(R.id.main_search_add_checked_button)

        if (book.isBookAddedCheck(booksViewModel)) {
            addButton.visibility = View.GONE
            addedButton.visibility = View.VISIBLE
        } else {
            addButton.visibility = View.VISIBLE
            addedButton.visibility = View.GONE
        }

        addButton?.setOnClickListener {
            searchFragment.onClick(
                LocalBook(
                    book.id,
                    book.volumeInfo.title,
                    book.volumeInfo.subtitle,
                    book.volumeInfo.authors,
                    book.volumeInfo.pageCount.toString(),
                    book.volumeInfo.imageLinks.smallThumbnail,
                    book.volumeInfo.imageLinks.thumbnail,
                    book.volumeInfo.description,
                    book.volumeInfo.publishedDate,
                    book.volumeInfo.categories,
                    book.volumeInfo.publisher,
                    ""
                )
            )
            addButton.visibility = View.GONE
            addedButton.visibility = View.VISIBLE
        }

        addedButton?.setOnClickListener {
            Toast.makeText(
                holder.itemView.context,
                "Already added to your library",
                Toast.LENGTH_SHORT
            ).show()
        }

        holder.itemView.setOnClickListener {
            //currentBook = book
            holder.itemView.context.startActivity(bookDetailsIntent)
        }

        holder.itemView.apply {
            glide.load(book.volumeInfo.imageLinks.smallThumbnail).centerCrop().into(bookCover)
            bookTitle.text = book.volumeInfo.title
            bookAuthor.text = book.volumeInfo.authors.joinToString(", ")
            bookPages.text = book.volumeInfo.pageCount.toString()
            bookReleaseDate.text = book.volumeInfo.publishedDate
        }

    }

    override fun getItemCount(): Int {
        return mainSearchBooksList.size
    }

    fun setFragment(fragment: MainSearchFragment) {
        this.searchFragment = fragment
    }

    fun setViewModel(booksViewModel: BooksViewModel) {
        this.booksViewModel = booksViewModel
    }

}