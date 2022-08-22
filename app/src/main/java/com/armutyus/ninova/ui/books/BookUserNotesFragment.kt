package com.armutyus.ninova.ui.books

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Cache.currentLocalBook
import com.armutyus.ninova.constants.Constants.DETAILS_STRING_EXTRA
import com.armutyus.ninova.constants.Constants.FROM_DETAILS_ACTIVITY
import com.armutyus.ninova.databinding.FragmentBookUserNotesBinding
import javax.inject.Inject

class BookUserNotesFragment @Inject constructor(
) : Fragment(R.layout.fragment_book_user_notes) {

    private var fragmentBinding: FragmentBookUserNotesBinding? = null
    private val booksViewModel by activityViewModels<BooksViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentBookUserNotesBinding.bind(view)
        fragmentBinding = binding

        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            when (activity?.intent?.getStringExtra(DETAILS_STRING_EXTRA)) {
                FROM_DETAILS_ACTIVITY -> {
                    activity?.finish()
                }
            }
        }

        if (currentLocalBook!!.bookNotes != null) {
            binding.userBookNotesEditText.setText(currentLocalBook!!.bookNotes)
        }

        binding.saveUserNotes.setOnClickListener {
            currentLocalBook!!.bookNotes = binding.userBookNotesEditText.text.toString()
            booksViewModel.updateBook(currentLocalBook!!)
            activity?.finish()
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }
}