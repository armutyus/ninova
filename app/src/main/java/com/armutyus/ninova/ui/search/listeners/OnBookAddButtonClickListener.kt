package com.armutyus.ninova.ui.search.listeners

import android.widget.ImageButton
import com.armutyus.ninova.model.DataModel
import com.google.android.material.progressindicator.CircularProgressIndicator

interface OnBookAddButtonClickListener {
    fun onClick(
        localBook: DataModel.LocalBook,
        addButton: ImageButton,
        addedButton: ImageButton,
        progressBar: CircularProgressIndicator
    )

    fun onAddedButtonClick(
        id: String,
        addButton: ImageButton,
        addedButton: ImageButton,
        progressBar: CircularProgressIndicator
    )
}