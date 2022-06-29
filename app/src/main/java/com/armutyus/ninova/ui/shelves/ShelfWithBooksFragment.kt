package com.armutyus.ninova.ui.shelves

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.armutyus.ninova.R
import com.armutyus.ninova.databinding.FragmentShelfWithBooksBinding
import javax.inject.Inject

class ShelfWithBooksFragment @Inject constructor(

) : Fragment(R.layout.fragment_shelf_with_books) {

    private var fragmentBinding: FragmentShelfWithBooksBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentShelfWithBooksBinding.bind(view)
        fragmentBinding = binding
        //activity?.actionBar?.title = shelfTitle

    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }

}