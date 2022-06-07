package com.armutyus.ninova.ui.search.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.armutyus.ninova.R
import com.bumptech.glide.RequestManager
import javax.inject.Inject

class SearchArchiveRecyclerViewAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<SearchArchiveRecyclerViewAdapter.SearchArchiveViewHolder>() {

    class SearchArchiveViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchArchiveViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.search_archive_row, parent, false)

        return SearchArchiveViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchArchiveViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            Toast.makeText(it.context, "Clicked!", Toast.LENGTH_LONG).show()
        }
    }

    override fun getItemCount(): Int {
        return 10
    }

}