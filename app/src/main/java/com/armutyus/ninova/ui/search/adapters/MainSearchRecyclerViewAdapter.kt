package com.armutyus.ninova.ui.search.adapters

import android.content.Intent
import android.os.Build
import android.text.Html
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
import com.armutyus.ninova.constants.Constants.BOOK_TYPE_FOR_DETAILS
import com.armutyus.ninova.constants.Constants.GOOGLE_BOOK_TYPE
import com.armutyus.ninova.constants.Constants.LOCAL_BOOK_TYPE
import com.armutyus.ninova.constants.Cache.currentBook
import com.armutyus.ninova.constants.Cache.currentLocalBook
import com.armutyus.ninova.model.DataModel
import com.armutyus.ninova.ui.books.BooksViewModel
import com.armutyus.ninova.ui.search.MainSearchFragment
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
    private val adapterData = mutableListOf<DataModel>()

    private val diffUtilGoogleBook = object : DiffUtil.ItemCallback<DataModel.GoogleBookItem>() {
        override fun areItemsTheSame(
            oldItem: DataModel.GoogleBookItem,
            newItem: DataModel.GoogleBookItem
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: DataModel.GoogleBookItem,
            newItem: DataModel.GoogleBookItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val recyclerListDiffer = AsyncListDiffer(this, diffUtilGoogleBook)

    var mainSearchBooksList: List<DataModel.GoogleBookItem>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    private val diffUtilLocalBook = object : DiffUtil.ItemCallback<DataModel.LocalBook>() {
        override fun areItemsTheSame(
            oldItem: DataModel.LocalBook,
            newItem: DataModel.LocalBook
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: DataModel.LocalBook,
            newItem: DataModel.LocalBook
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val recyclerLocalBookListDiffer = AsyncListDiffer(this, diffUtilLocalBook)

    var mainSearchLocalBooksList: List<DataModel.LocalBook>
        get() = recyclerLocalBookListDiffer.currentList
        set(value) = recyclerLocalBookListDiffer.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainSearchViewHolder {

        val layout = when (viewType) {
            GOOGLE_BOOK_TYPE -> R.layout.search_main_row
            LOCAL_BOOK_TYPE -> R.layout.search_local_book_row
            else -> throw IllegalArgumentException("Invalid view type")
        }

        val view =
            LayoutInflater.from(parent.context).inflate(layout, parent, false)

        return MainSearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainSearchViewHolder, position: Int) {
        holder.bind(adapterData[position])
    }

    override fun getItemViewType(position: Int) = when (adapterData[position]) {
        is DataModel.GoogleBookItem -> GOOGLE_BOOK_TYPE
        is DataModel.LocalBook -> LOCAL_BOOK_TYPE
    }

    override fun getItemCount(): Int {
        return if (adapterData.size == adapterData.lastIndex || adapterData.isEmpty()) {
            mainSearchLocalBooksList.size
        } else {
            adapterData.size
        }
    }

    inner class MainSearchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindApiBook() {
            val bookCover = itemView.findViewById<ImageView>(R.id.bookImage)
            val bookTitle = itemView.findViewById<TextView>(R.id.bookTitleText)
            val bookAuthor = itemView.findViewById<TextView>(R.id.bookAuthorText)
            val bookPages = itemView.findViewById<TextView>(R.id.bookPageText)
            val bookReleaseDate = itemView.findViewById<TextView>(R.id.bookReleaseDateText)
            val book = mainSearchBooksList[layoutPosition]

            val addButton = itemView.findViewById<ImageButton>(R.id.main_search_add_button)
            val addedButton =
                itemView.findViewById<ImageButton>(R.id.main_search_add_checked_button)

            if (book.isBookAddedCheck(booksViewModel)) {
                addButton.visibility = View.GONE
                addedButton.visibility = View.VISIBLE
            } else {
                addButton.visibility = View.VISIBLE
                addedButton.visibility = View.GONE
            }

            addButton?.setOnClickListener {
                searchFragment.onClick(
                    DataModel.LocalBook(
                        book.id!!,
                        book.volumeInfo?.authors ?: listOf(),
                        book.volumeInfo?.categories ?: listOf(),
                        book.volumeInfo?.imageLinks?.smallThumbnail,
                        book.volumeInfo?.imageLinks?.thumbnail,
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Html.fromHtml(
                                book.volumeInfo?.description ?: "",
                                Html.FROM_HTML_OPTION_USE_CSS_COLORS
                            ).toString()
                        } else {
                            Html.fromHtml(book.volumeInfo?.description ?: "")
                                .toString()
                        },
                        "",
                        book.volumeInfo?.pageCount.toString(),
                        book.volumeInfo?.publishedDate,
                        book.volumeInfo?.publisher,
                        book.volumeInfo?.subtitle,
                        book.volumeInfo?.title
                    )
                )
                addButton.visibility = View.GONE
                addedButton.visibility = View.VISIBLE
            }

            addedButton?.setOnClickListener {
                Toast.makeText(
                    itemView.context,
                    "Already added to your library",
                    Toast.LENGTH_SHORT
                ).show()
            }

            itemView.setOnClickListener {
                bookDetailsIntent.putExtra(BOOK_TYPE_FOR_DETAILS, GOOGLE_BOOK_TYPE)
                currentBook = book
                currentLocalBook = null
                itemView.context.startActivity(bookDetailsIntent)
            }

            itemView.apply {
                glide.load(book.volumeInfo?.imageLinks?.smallThumbnail).centerCrop().into(bookCover)
                bookTitle.text = book.volumeInfo?.title
                bookAuthor.text = book.volumeInfo?.authors?.joinToString(", ")
                bookPages.text = book.volumeInfo?.pageCount.toString()
                bookReleaseDate.text = book.volumeInfo?.publishedDate
            }
        }

        fun bindLocalBook() {
            val bookCover = itemView.findViewById<ImageView>(R.id.bookImage)
            val bookTitle = itemView.findViewById<TextView>(R.id.bookTitleText)
            val bookAuthor = itemView.findViewById<TextView>(R.id.bookAuthorText)
            val bookPages = itemView.findViewById<TextView>(R.id.bookPageText)
            val bookReleaseDate = itemView.findViewById<TextView>(R.id.bookReleaseDateText)
            val book = mainSearchLocalBooksList[layoutPosition]

            itemView.setOnClickListener {
                bookDetailsIntent.putExtra(BOOK_TYPE_FOR_DETAILS, LOCAL_BOOK_TYPE)
                currentLocalBook = book
                currentBook = null
                itemView.context.startActivity(bookDetailsIntent)
            }

            itemView.apply {
                glide.load(book.bookCoverSmallThumbnail).centerCrop().into(bookCover)
                bookTitle.text = book.bookTitle
                bookAuthor.text = book.bookAuthors?.joinToString(", ")
                bookPages.text = book.bookPages
                bookReleaseDate.text = book.bookPublishedDate
            }
        }

        fun bind(dataModel: DataModel) {
            when (dataModel) {
                is DataModel.GoogleBookItem -> bindApiBook()
                is DataModel.LocalBook -> bindLocalBook()
            }
        }

    }

    fun setFragment(fragment: MainSearchFragment) {
        this.searchFragment = fragment
    }

    fun setViewModel(booksViewModel: BooksViewModel) {
        this.booksViewModel = booksViewModel
    }

    fun setDataType(data: List<DataModel>) {
        adapterData.apply {
            clear()
            addAll(data)
        }
        notifyDataSetChanged()
    }

}