package com.armutyus.ninova.ui.search.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.armutyus.ninova.R
import com.armutyus.ninova.model.Books
import com.bumptech.glide.RequestManager
import javax.inject.Inject

class SearchArchiveRecyclerViewAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<SearchArchiveRecyclerViewAdapter.SearchArchiveViewHolder>() {

    class SearchArchiveViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private val diffUtil = object : DiffUtil.ItemCallback<Books>() {
        override fun areItemsTheSame(oldItem: Books, newItem: Books): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Books, newItem: Books): Boolean {
            return oldItem == newItem
        }
    }

    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var searchArchiveBooksList: List<Books>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchArchiveViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.search_archive_row, parent, false)

        return SearchArchiveViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchArchiveViewHolder, position: Int) {

        val booksCover = holder.itemView.findViewById<ImageView>(R.id.bookImage)
        val booksTitle = holder.itemView.findViewById<TextView>(R.id.bookTitleText)
        val booksAuthor = holder.itemView.findViewById<TextView>(R.id.bookAuthorText)
        val booksPages = holder.itemView.findViewById<TextView>(R.id.bookPageText)
        val booksReleaseDate = holder.itemView.findViewById<TextView>(R.id.bookReleaseDateText)
        val books = searchArchiveBooksList[position]

        holder.itemView.apply {

            booksTitle.text = books.bookTitle
            booksAuthor.text = books.bookAuthor
            booksPages.text = books.bookPages
            booksReleaseDate.text = books.releaseDate

        }
    }

    override fun getItemCount(): Int {
        return searchArchiveBooksList.size
    }

}