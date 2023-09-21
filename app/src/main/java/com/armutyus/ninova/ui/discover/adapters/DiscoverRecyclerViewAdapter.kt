package com.armutyus.ninova.ui.discover.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Constants.discoverScreenCategories
import com.armutyus.ninova.ui.discover.DiscoverViewModel
import com.bumptech.glide.RequestManager
import javax.inject.Inject

class DiscoverRecyclerViewAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var discoverViewModel: DiscoverViewModel

    class DiscoverScreenViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.book_category_item, parent, false)

        return DiscoverScreenViewHolder(view)
    }

    override fun getItemCount(): Int {
        return discoverScreenCategories.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val categoryCover = holder.itemView.findViewById<ImageView>(R.id.bookImage)
        val categoryTitle = holder.itemView.findViewById<TextView>(R.id.bookCategory)
        val categoryCoverId = // Find a way to get string
            discoverViewModel.getRandomBookCoverForCategory(discoverScreenCategories[position])
        val categoryCoverUrl = "https://covers.openlibrary.org/b/id/${categoryCoverId}-S.jpg"
        Log.i("AdapterCover", categoryCoverUrl)
        categoryTitle.isSelected = true

        holder.itemView.apply {
            glide.load(categoryCoverUrl).circleCrop().into(categoryCover)
            categoryTitle.text = discoverScreenCategories[position]
        }
    }

    fun setViewModel(discoverViewModel: DiscoverViewModel) {
        this.discoverViewModel = discoverViewModel
    }
}