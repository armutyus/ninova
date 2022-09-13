package com.armutyus.ninova.ui.shelves.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Cache.currentBookIdExtra
import com.armutyus.ninova.roomdb.entities.BookShelfCrossRef
import com.armutyus.ninova.roomdb.entities.LocalShelf
import com.armutyus.ninova.ui.books.BooksViewModel
import com.armutyus.ninova.ui.shelves.ShelvesViewModel
import javax.inject.Inject

class BookToShelfRecyclerViewAdapter @Inject constructor(
) : RecyclerView.Adapter<BookToShelfRecyclerViewAdapter.BookToShelfViewHolder>() {

    private lateinit var shelvesViewModel: ShelvesViewModel
    private lateinit var booksViewModel: BooksViewModel

    class BookToShelfViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private val diffUtil = object : DiffUtil.ItemCallback<LocalShelf>() {
        override fun areItemsTheSame(oldItem: LocalShelf, newItem: LocalShelf): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: LocalShelf, newItem: LocalShelf): Boolean {
            return oldItem == newItem
        }
    }

    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var bookToShelfList: List<LocalShelf>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookToShelfViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.add_book_to_shelf_row, parent, false)

        return BookToShelfViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookToShelfViewHolder, position: Int) {
        val shelfRow = holder.itemView.findViewById<CheckBox>(R.id.bottomSheetShelfCheckBox)
        val shelf = bookToShelfList[position]

        holder.itemView.apply {
            shelfRow.text = shelf.shelfTitle
            val checkedShelfList =
                booksViewModel.bookWithShelvesList.value?.firstOrNull { it.shelfList.contains(shelf) }?.shelfList
            shelfRow.isChecked = checkedShelfList != null && checkedShelfList.isNotEmpty()
        }


        shelfRow.setOnCheckedChangeListener { _, isChecked ->
            val crossRef = BookShelfCrossRef(currentBookIdExtra!!, shelf.shelfId)
            if (isChecked) {
                shelvesViewModel.insertBookShelfCrossRef(crossRef).invokeOnCompletion {
                    booksViewModel.loadBookWithShelves(currentBookIdExtra!!)
                }
            } else {
                shelvesViewModel.deleteBookShelfCrossRef(crossRef).invokeOnCompletion {
                    booksViewModel.loadBookWithShelves(currentBookIdExtra!!)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return bookToShelfList.size
    }

    fun setViewModels(shelvesViewModel: ShelvesViewModel, booksViewModel: BooksViewModel) {
        this.shelvesViewModel = shelvesViewModel
        this.booksViewModel = booksViewModel
    }

}