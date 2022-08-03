package com.armutyus.ninova.ui.search.adapters

import android.content.Intent
import android.os.Build
import android.text.Html
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Cache
import com.armutyus.ninova.constants.Constants
import com.armutyus.ninova.model.DataModel
import com.armutyus.ninova.ui.books.BooksViewModel
import com.armutyus.ninova.ui.search.MainSearchFragment
import com.bumptech.glide.RequestManager
import javax.inject.Inject
import javax.inject.Named

class MainSearchViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bindApiBook(
        book: DataModel.GoogleBookItem,
        glide: RequestManager,
        searchFragment: MainSearchFragment,
        booksViewModel: BooksViewModel,
        bookDetailsIntent: Intent
    ) {
        val bookCover = itemView.findViewById<ImageView>(R.id.bookImage)
        val bookTitle = itemView.findViewById<TextView>(R.id.bookTitleText)
        val bookAuthor = itemView.findViewById<TextView>(R.id.bookAuthorText)
        val bookPages = itemView.findViewById<TextView>(R.id.bookPageText)
        val bookReleaseDate = itemView.findViewById<TextView>(R.id.bookReleaseDateText)
        val addButton = itemView.findViewById<ImageButton>(R.id.main_search_add_button)
        val addedButton = itemView.findViewById<ImageButton>(R.id.main_search_add_checked_button)

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

        itemView.apply {
            glide.load(book.volumeInfo?.imageLinks?.smallThumbnail).centerCrop().into(bookCover)
            bookTitle.text = book.volumeInfo?.title
            bookAuthor.text = book.volumeInfo?.authors?.joinToString(", ")
            bookPages.text = book.volumeInfo?.pageCount.toString()
            bookReleaseDate.text = book.volumeInfo?.publishedDate
            setOnClickListener {
                bookDetailsIntent.putExtra(Constants.BOOK_TYPE_FOR_DETAILS, Constants.GOOGLE_BOOK_TYPE)
                Cache.currentBook = book
                Cache.currentLocalBook = null
                itemView.context.startActivity(bookDetailsIntent)
            }
        }
    }

    fun bindLocalBook(book: DataModel.LocalBook, glide: RequestManager, bookDetailsIntent: Intent) {
        val bookCover = itemView.findViewById<ImageView>(R.id.bookImage)
        val bookTitle = itemView.findViewById<TextView>(R.id.bookTitleText)
        val bookAuthor = itemView.findViewById<TextView>(R.id.bookAuthorText)
        val bookPages = itemView.findViewById<TextView>(R.id.bookPageText)
        val bookReleaseDate = itemView.findViewById<TextView>(R.id.bookReleaseDateText)

        itemView.apply {
            glide.load(book.bookCoverSmallThumbnail).centerCrop().into(bookCover)
            bookTitle.text = book.bookTitle
            bookAuthor.text = book.bookAuthors?.joinToString(", ")
            bookPages.text = book.bookPages
            bookReleaseDate.text = book.bookPublishedDate
            setOnClickListener {
                bookDetailsIntent.putExtra(Constants.BOOK_TYPE_FOR_DETAILS, Constants.LOCAL_BOOK_TYPE)
                Cache.currentLocalBook = book
                Cache.currentBook = null
                itemView.context.startActivity(bookDetailsIntent)
            }
        }
    }

    fun bind(
        dataModel: DataModel,
        glide: RequestManager,
        searchFragment: MainSearchFragment,
        booksViewModel: BooksViewModel,
        bookDetailsIntent: Intent
    ) {
        when (dataModel) {
            is DataModel.GoogleBookItem -> bindApiBook(
                dataModel,
                glide,
                searchFragment,
                booksViewModel,
                bookDetailsIntent
            )
            is DataModel.LocalBook -> bindLocalBook(dataModel, glide, bookDetailsIntent)
        }
    }
}