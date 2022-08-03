package com.armutyus.ninova.ui.search.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Constants.GOOGLE_BOOK_TYPE
import com.armutyus.ninova.constants.Constants.LOCAL_BOOK_TYPE
import com.armutyus.ninova.model.DataModel
import com.armutyus.ninova.ui.books.BooksViewModel
import com.armutyus.ninova.ui.search.MainSearchFragment
import com.bumptech.glide.RequestManager
import javax.inject.Inject

class MainSearchRecyclerViewAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<MainSearchViewHolder>() {

    private lateinit var searchFragment: MainSearchFragment
    private lateinit var booksViewModel: BooksViewModel
    private val adapterData = mutableListOf<DataModel>()

    init {
        try {
            setHasStableIds(true)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainSearchViewHolder {
        val layout = when (viewType) {
            GOOGLE_BOOK_TYPE -> R.layout.search_main_row
            LOCAL_BOOK_TYPE -> R.layout.search_local_book_row
            else -> throw IllegalArgumentException("Invalid view type")
        }

        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return MainSearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainSearchViewHolder, position: Int) {
        holder.bind(adapterData[position], glide, searchFragment, booksViewModel)
    }

    override fun getItemViewType(position: Int) = when (adapterData[position]) {
        is DataModel.GoogleBookItem -> GOOGLE_BOOK_TYPE
        is DataModel.LocalBook -> LOCAL_BOOK_TYPE
    }

    override fun getItemId(position: Int): Long = adapterData[position].hashCode().toLong()

    override fun getItemCount(): Int {
        return adapterData.size
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