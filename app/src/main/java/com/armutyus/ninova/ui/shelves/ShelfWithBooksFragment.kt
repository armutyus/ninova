package com.armutyus.ninova.ui.shelves

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.databinding.FragmentShelfWithBooksBinding
import com.armutyus.ninova.roomdb.entities.BookShelfCrossRef
import com.armutyus.ninova.ui.books.adapters.BooksRecyclerViewAdapter
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class ShelfWithBooksFragment @Inject constructor(
    private val booksAdapter: BooksRecyclerViewAdapter
) : Fragment(R.layout.fragment_shelf_with_books) {

    private var fragmentBinding: FragmentShelfWithBooksBinding? = null
    private val shelvesViewModel by activityViewModels<ShelvesViewModel>()
    private var currentShelfId = ""
    private val args: ShelfWithBooksFragmentArgs by navArgs()
    private val swipeCallBack = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {

            val deleteIcon =
                AppCompatResources.getDrawable(requireContext(), R.drawable.ic_delete_account)
            val intrinsicWidth = deleteIcon!!.intrinsicWidth
            val intrinsicHeight = deleteIcon.intrinsicHeight
            val cornerRadius = 32
            val swipeBackground = ShapeDrawable(
                RoundRectShape(
                    floatArrayOf(
                        0f,
                        0f,
                        cornerRadius.toFloat(),
                        cornerRadius.toFloat(),
                        cornerRadius.toFloat(),
                        cornerRadius.toFloat(),
                        0f,
                        0f
                    ), null, null
                )
            )
            val swipeBackgroundColor = R.color.md_theme_dark_errorContainer
            val deleteIconColor = R.color.md_theme_dark_onErrorContainer
            val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.DARKEN) }
            val itemView = viewHolder.itemView
            val itemHeight = itemView.bottom - itemView.top
            val isCanceled = dX == 0f || !isCurrentlyActive

            if (isCanceled) {
                c.drawRect(
                    itemView.right + dX,
                    itemView.top.toFloat(),
                    itemView.right.toFloat(),
                    itemView.bottom.toFloat(),
                    clearPaint
                )
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                return
            }

            swipeBackground.paint.color = resources.getColor(swipeBackgroundColor, context!!.theme)
            swipeBackground.setBounds(
                itemView.right + dX.toInt(),
                itemView.top,
                itemView.right,
                itemView.bottom
            )
            swipeBackground.draw(c)

            val iconMargin = (itemHeight - intrinsicHeight) / 2
            val iconTop = itemView.top - ((itemHeight / 3.2) - (intrinsicHeight * 2.7))
            val iconLeft = itemView.right - intrinsicWidth - (iconMargin / 2)
            val iconRight = itemView.right - (iconMargin / 3)
            val iconBottom = itemView.bottom - ((itemHeight * 0.6) - (intrinsicHeight))

            deleteIcon.setBounds(iconLeft, iconTop.toInt(), iconRight, iconBottom.toInt())
            deleteIcon.setTint(resources.getColor(deleteIconColor, context!!.theme))
            deleteIcon.draw(c)
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val layoutPosition = viewHolder.layoutPosition
            val swipedBook = booksAdapter.mainBooksList[layoutPosition]
            val crossRef = BookShelfCrossRef(swipedBook.bookId, currentShelfId)
            shelvesViewModel.deleteBookShelfCrossRef(crossRef).invokeOnCompletion {
                Snackbar.make(requireView(), R.string.book_deleted_in_shelf, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo) {
                        shelvesViewModel.insertBookShelfCrossRef(crossRef).invokeOnCompletion {
                            uploadCrossRefToFirestore(crossRef)
                            shelvesViewModel.loadShelfWithBookList()
                        }
                    }.show()
                deleteCrossRefFromFirestore(crossRef.bookId + crossRef.shelfId)
                shelvesViewModel.loadShelfWithBookList()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentShelfWithBooksBinding.bind(view)
        fragmentBinding = binding

        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        val recyclerView = binding.shelfWithBooksRecyclerView
        recyclerView.adapter = booksAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        ItemTouchHelper(swipeCallBack).attachToRecyclerView(recyclerView)

        currentShelfId = args.currentShelfId

        observeBookList()

    }

    override fun onResume() {
        super.onResume()
        shelvesViewModel.loadShelfWithBookList()
    }

    private fun observeBookList() {
        shelvesViewModel.shelfWithBooksList.observe(viewLifecycleOwner) { booksOfShelfList ->
            val currentBookList =
                booksOfShelfList.find { it.shelf.shelfId == currentShelfId }?.bookList
            currentBookList?.let {
                if (it.isEmpty()) {
                    fragmentBinding?.shelfWithBooksRecyclerView?.visibility = View.GONE
                    fragmentBinding?.linearLayoutShelfWithBooksError?.visibility = View.VISIBLE

                } else {
                    fragmentBinding?.linearLayoutShelfWithBooksError?.visibility = View.GONE
                    fragmentBinding?.shelfWithBooksRecyclerView?.visibility = View.VISIBLE
                    booksAdapter.mainBooksList = it.sortedBy { localBook -> localBook.bookTitle }
                }
            }
        }
    }

    private fun deleteCrossRefFromFirestore(crossRefId: String) {
        shelvesViewModel.deleteCrossRefFromFirestore(crossRefId) { response ->
            when (response) {
                is Response.Loading ->
                    Log.i("crossRefDelete", "Deleting from firestore")

                is Response.Success ->
                    Log.i("crossRefDelete", "Deleted from firestore")

                is Response.Failure ->
                    Log.e("crossRefDelete", response.errorMessage)
            }
        }
    }

    private fun uploadCrossRefToFirestore(crossRef: BookShelfCrossRef) {
        shelvesViewModel.uploadCrossRefToFirestore(crossRef) { response ->
            when (response) {
                is Response.Loading ->
                    Log.i("crossRefUpload", "Uploading to firestore")

                is Response.Success ->
                    Log.i("crossRefUpload", "Uploaded to firestore")

                is Response.Failure ->
                    Log.e("crossRefUpload", response.errorMessage)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }

}