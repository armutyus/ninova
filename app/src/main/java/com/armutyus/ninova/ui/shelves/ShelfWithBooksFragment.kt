package com.armutyus.ninova.ui.shelves

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.armutyus.ninova.R
import com.armutyus.ninova.databinding.FragmentBooksBinding
import com.armutyus.ninova.databinding.FragmentShelfWithBooksBinding
import javax.inject.Inject

class ShelfWithBooksFragment @Inject constructor(

) : Fragment(R.layout.fragment_shelf_with_books) {

    private var fragmentBinding: FragmentShelfWithBooksBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentShelfWithBooksBinding.bind(view)
        fragmentBinding = binding

    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }

}