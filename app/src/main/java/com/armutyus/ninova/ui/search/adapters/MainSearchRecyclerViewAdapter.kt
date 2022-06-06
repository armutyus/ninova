package com.armutyus.ninova.ui.search.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.armutyus.ninova.R
import com.bumptech.glide.RequestManager
import javax.inject.Inject

class MainSearchRecyclerViewAdapter @Inject constructor(
    private val glide: RequestManager
): RecyclerView.Adapter<MainSearchRecyclerViewAdapter.MainSearchViewHolder>() {

    class MainSearchViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainSearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_main_row, parent, false)

        return MainSearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainSearchViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            Toast.makeText(it.context, "Clicked!", Toast.LENGTH_LONG).show()
        }
    }

    override fun getItemCount(): Int {
        return 10
    }

}