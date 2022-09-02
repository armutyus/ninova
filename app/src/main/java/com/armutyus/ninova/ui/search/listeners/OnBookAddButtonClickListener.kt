package com.armutyus.ninova.ui.search.listeners

import com.armutyus.ninova.model.DataModel

interface OnBookAddButtonClickListener {
    fun onClick(localBook: DataModel.LocalBook)
    fun onAddedButtonClick(id: String)
}