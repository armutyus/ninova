package com.armutyus.ninova.ui.books.listeners

import com.armutyus.ninova.roomdb.entities.LocalBook

interface OnBookLongClickListener {
    fun onLongClick(localBook: LocalBook)
}