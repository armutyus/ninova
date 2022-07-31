package com.armutyus.ninova.ui.books

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.armutyus.ninova.constants.Constants.BOOK_TYPE_FOR_DETAILS
import com.armutyus.ninova.constants.Constants.DETAILS_EXTRA
import com.armutyus.ninova.constants.Constants.DETAILS_STRING_EXTRA
import com.armutyus.ninova.constants.Constants.FROM_DETAILS_ACTIVITY
import com.armutyus.ninova.constants.Constants.FROM_DETAILS_TO_NOTES_EXTRA
import com.armutyus.ninova.constants.Constants.GOOGLE_BOOK_TYPE
import com.armutyus.ninova.constants.Constants.LOCAL_BOOK_TYPE
import com.armutyus.ninova.constants.Constants.MAIN_INTENT
import com.armutyus.ninova.constants.Constants.currentBook
import com.armutyus.ninova.constants.Constants.currentLocalBook
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.databinding.ActivityBookDetailsBinding
import com.armutyus.ninova.model.BookDetailsInfo
import com.armutyus.ninova.model.DataModel
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class BookDetailsActivity : AppCompatActivity() {

    var selectedPicture : Uri? = null
    private lateinit var binding: ActivityBookDetailsBinding
    private lateinit var bookDetails: BookDetailsInfo
    private lateinit var tabLayout: TabLayout
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private val viewModel by viewModels<BooksViewModel>()

    @Inject
    lateinit var glide: RequestManager

    @Named(MAIN_INTENT)
    @Inject
    lateinit var mainIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tabLayout = binding.bookDetailTabLayout
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                setVisibilities(tab)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                setVisibilities(tab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                setVisibilities(tab)
            }
        })

        when (intent.getIntExtra(BOOK_TYPE_FOR_DETAILS, -1)) {
            LOCAL_BOOK_TYPE -> {
                supportActionBar?.title = currentLocalBook?.bookTitle
                binding.addBookToLibraryButton.visibility = View.GONE
                registerLauncher()
                setupLocalBookInfo()

                binding.bookCoverImageView.setOnClickListener {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            Snackbar.make(it, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission") {
                                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                }.show()
                        } else {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    } else {
                        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        activityResultLauncher.launch(galleryIntent)
                    }
                }

                binding.shelvesOfBooks.setOnClickListener {
                    goToBookToShelfFragment()
                }

                binding.bookDetailUserNotes.setOnClickListener {
                    goToUserBookNotesFragment()
                }

            }

            GOOGLE_BOOK_TYPE -> {
                supportActionBar?.title = currentBook?.volumeInfo?.title
                setupBookInfo()

                binding.addBookToLibraryButton.setOnClickListener {
                    try {
                        viewModel.insertBook(DataModel.LocalBook(
                            currentBook?.id!!,
                            bookDetails.authors ?: listOf(),
                            bookDetails.categories ?: listOf(),
                            bookDetails.imageLinks?.smallThumbnail,
                            bookDetails.imageLinks?.thumbnail,
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Html.fromHtml(
                                    bookDetails.description,
                                    Html.FROM_HTML_OPTION_USE_CSS_COLORS
                                ).toString()
                            } else {
                                Html.fromHtml(bookDetails.description)
                                    .toString()
                            },
                            "",
                            bookDetails.pageCount.toString(),
                            bookDetails.publishedDate,
                            bookDetails.publisher,
                            bookDetails.subtitle,
                            bookDetails.title
                        )).also {
                            Toast.makeText(this,"Saved to your library", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this,"Failed to save!",Toast.LENGTH_LONG).show()
                        Log.e("BookDetailsActivity", e.localizedMessage ?: "Book not added to local db")
                    }
                }
            }

            else -> {}
        }

    }

    override fun onResume() {
        super.onResume()
        currentLocalBook?.bookId?.let { viewModel.getBookWithShelves(it) }
        observeBookDetailNotesChanges()
    }

    private fun goToBookToShelfFragment() {
        val currentBookId = currentLocalBook?.bookId
        mainIntent.putExtra(DETAILS_EXTRA, currentBookId)
        mainIntent.putExtra(DETAILS_STRING_EXTRA, FROM_DETAILS_ACTIVITY)
        startActivity(mainIntent)
    }

    private fun goToUserBookNotesFragment() {
        mainIntent.putExtra(DETAILS_EXTRA, FROM_DETAILS_TO_NOTES_EXTRA)
        mainIntent.putExtra(DETAILS_STRING_EXTRA, FROM_DETAILS_ACTIVITY)
        startActivity(mainIntent)
    }

    private var currentShelvesList = mutableListOf<String?>()

    private fun observeBookDetailNotesChanges() {
        viewModel.bookWithShelvesList.observe(this) { shelvesOfBook ->
            shelvesOfBook.forEach { bookWithShelves ->
                val shelfTitleList = bookWithShelves.shelf.map { it.shelfTitle }.toList()
                currentShelvesList.removeAll(shelfTitleList)
                currentShelvesList.addAll(shelfTitleList)
            }
            binding.shelvesOfBooks.text = currentShelvesList.joinToString(", ")
        }
        binding.bookDetailUserNotes.text = currentLocalBook?.bookNotes
    }

    private fun setVisibilities(tab: TabLayout.Tab?) {
        when (tab?.text) {
            "NOTES" -> {
                binding.bookDetailNotesLinearLayout.visibility = View.VISIBLE
                binding.bookDetailInfoLinearLayout.visibility = View.GONE
                binding.linearLayoutDetailsError.visibility = View.GONE
            }
            "INFO" -> {
                binding.bookDetailNotesLinearLayout.visibility = View.GONE
                binding.bookDetailInfoLinearLayout.visibility = View.VISIBLE
                binding.linearLayoutDetailsError.visibility = View.GONE
            }
            else -> {
                binding.bookDetailNotesLinearLayout.visibility = View.GONE
                binding.bookDetailInfoLinearLayout.visibility = View.VISIBLE
                binding.linearLayoutDetailsError.visibility = View.GONE
            }

        }

    }

    private fun setupBookInfo() {
        if (currentBook == null) {
            binding.linearLayoutDetailsError.visibility = View.VISIBLE
            binding.bookDetailNotesLinearLayout.visibility = View.GONE
            binding.bookDetailInfoLinearLayout.visibility = View.GONE
        } else {
            viewModel.bookDetailsResponse(currentBook?.id!!).also {
                observeBookDetailsResponse()
            }
        }
    }

    private fun setupLocalBookInfo() {
        if (currentLocalBook == null) {
            binding.linearLayoutDetailsError.visibility = View.VISIBLE
            binding.bookDetailNotesLinearLayout.visibility = View.GONE
            binding.bookDetailInfoLinearLayout.visibility = View.GONE
        } else {
            viewModel.bookDetailsResponse(currentLocalBook?.bookId!!).also {
                observeBookDetailsResponse()
            }
        }
    }


    private fun observeBookDetailsResponse() {
        viewModel.bookDetailsResponse.observe(this) { response ->
            when (response) {
                is Response.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Response.Success -> {
                    binding.progressBar.visibility = View.GONE
                    bookDetails = response.data.volumeInfo
                    if (intent.getIntExtra(BOOK_TYPE_FOR_DETAILS,-1) == LOCAL_BOOK_TYPE) {
                        applyLocalBookDetailChanges(bookDetails)
                    } else {
                        applyBookDetailChanges(bookDetails)
                    }

                }
                is Response.Failure -> {
                    if (intent.getIntExtra(BOOK_TYPE_FOR_DETAILS,-1) == LOCAL_BOOK_TYPE) {
                        showLocalBookDetails()
                    } else {
                        Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun applyBookDetailChanges(bookDetails: BookDetailsInfo) {
        glide
            .load(
                currentBook?.volumeInfo?.imageLinks?.thumbnail
                    ?: bookDetails.imageLinks?.smallThumbnail
            )
            .centerCrop()
            .into(binding.bookCoverImageView)
        binding.bookDetailTitleText.text = currentBook?.volumeInfo?.title ?: bookDetails.title
        binding.bookDetailSubTitleText.text =
            currentBook?.volumeInfo?.subtitle ?: bookDetails.subtitle
        binding.bookDetailAuthorsText.text =
            currentBook?.volumeInfo?.authors?.joinToString(", ")
                ?: bookDetails.authors?.joinToString(", ")
        binding.bookDetailPagesNumber.text =
            currentBook?.volumeInfo?.pageCount?.toString() ?: bookDetails.pageCount?.toString()
        binding.bookDetailCategories.text =
            currentBook?.volumeInfo?.categories?.joinToString(", ")
                ?: bookDetails.categories?.joinToString(", ")
        binding.bookDetailPublisher.text =
            currentBook?.volumeInfo?.publisher ?: bookDetails.publisher
        binding.bookDetailPublishDate.text =
            currentBook?.volumeInfo?.publishedDate ?: bookDetails.publishedDate

        val formattedBookDescription = if (bookDetails.description == null) {
            currentBook?.volumeInfo?.description
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(
                    bookDetails.description,
                    Html.FROM_HTML_OPTION_USE_CSS_COLORS
                ).toString()
            } else {
                Html.fromHtml(bookDetails.description)
                    .toString()
            }
        }
        binding.bookDetailDescription.text = formattedBookDescription

    }

    private fun applyLocalBookDetailChanges(bookDetails: BookDetailsInfo) {
        glide
            .load(
                bookDetails.imageLinks?.smallThumbnail
                    ?: currentLocalBook?.bookCoverSmallThumbnail
            )
            .centerCrop()
            .into(binding.bookCoverImageView)
        binding.bookDetailTitleText.text = bookDetails.title ?:  currentLocalBook?.bookTitle
        binding.bookDetailSubTitleText.text =
            bookDetails.subtitle ?: currentLocalBook?.bookSubtitle
        binding.bookDetailAuthorsText.text =
            bookDetails.authors?.joinToString(", ")
                ?: currentLocalBook?.bookAuthors?.joinToString(", ")
        binding.bookDetailPagesNumber.text =
            bookDetails.pageCount?.toString() ?: currentLocalBook?.bookPages
        binding.bookDetailCategories.text =
            bookDetails.categories?.joinToString(", ")
                ?: currentLocalBook?.bookCategories?.joinToString(", ")
        binding.bookDetailPublisher.text =
            bookDetails.publisher ?: currentLocalBook?.bookPublisher
        binding.bookDetailPublishDate.text =
            bookDetails.publishedDate ?: currentLocalBook?.bookPublishedDate

        val formattedBookDescription = if (bookDetails.description == null) {
            currentLocalBook?.bookDescription
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(
                    bookDetails.description,
                    Html.FROM_HTML_OPTION_USE_CSS_COLORS
                ).toString()
            } else {
                Html.fromHtml(bookDetails.description)
                    .toString()
            }
        }
        binding.bookDetailDescription.text = formattedBookDescription

        updateLocalBook(bookDetails)
    }

    private fun showLocalBookDetails() {
        glide.load(currentLocalBook?.bookCoverSmallThumbnail).centerCrop().into(binding.bookCoverImageView)
        binding.bookDetailTitleText.text = currentLocalBook?.bookTitle
        binding.bookDetailSubTitleText.text = currentLocalBook?.bookSubtitle
        binding.bookDetailAuthorsText.text = currentLocalBook?.bookAuthors?.joinToString(", ")
        binding.bookDetailPagesNumber.text = currentLocalBook?.bookPages
        binding.bookDetailCategories.text = currentLocalBook?.bookCategories?.joinToString(", ")
        binding.bookDetailPublisher.text = currentLocalBook?.bookPublisher
        binding.bookDetailPublishDate.text = currentLocalBook?.bookPublishedDate
        binding.bookDetailDescription.text = currentLocalBook?.bookDescription
    }

    private fun updateLocalBook(bookDetails: BookDetailsInfo) {
        currentLocalBook?.bookCoverSmallThumbnail.apply {
            if (this != bookDetails.imageLinks?.smallThumbnail && bookDetails.imageLinks?.smallThumbnail != null) {
                currentLocalBook?.bookCoverSmallThumbnail = bookDetails.imageLinks.smallThumbnail
            }
        }
        currentLocalBook?.bookTitle.apply {
            if (this != bookDetails.title && bookDetails.title != null) {
                currentLocalBook?.bookTitle = binding.bookDetailTitleText.text.toString()
            }
        }
        currentLocalBook?.bookSubtitle.apply {
            if (this != bookDetails.subtitle && bookDetails.subtitle != null) {
                currentLocalBook?.bookSubtitle = binding.bookDetailSubTitleText.text.toString()
            }
        }
        currentLocalBook?.bookAuthors.apply {
            if (this != bookDetails.authors && bookDetails.authors != null) {
                currentLocalBook?.bookAuthors = bookDetails.authors
            }
        }
        currentLocalBook?.bookPages.apply {
            if (this != bookDetails.pageCount?.toString() && bookDetails.pageCount != null) {
                currentLocalBook?.bookPages = binding.bookDetailPagesNumber.text.toString()
            }
        }
        currentLocalBook?.bookCategories.apply {
            if (this != bookDetails.categories && bookDetails.categories!= null) {
                currentLocalBook?.bookCategories = bookDetails.categories
            }
        }
        currentLocalBook?.bookPublisher.apply {
            if (this != bookDetails.publisher && bookDetails.publisher != null) {
                currentLocalBook?.bookPublisher = binding.bookDetailPublisher.text.toString()
            }
        }
        currentLocalBook?.bookPublishedDate.apply {
            if (this != bookDetails.publishedDate && bookDetails.publishedDate != null) {
                currentLocalBook?.bookPublishedDate = binding.bookDetailPublishDate.text.toString()
            }
        }
        currentLocalBook?.bookDescription.apply {
            if (this != bookDetails.description && bookDetails.description != null) {
                currentLocalBook?.bookDescription = binding.bookDetailDescription.text.toString()
            }
        }

        viewModel.updateBook(currentLocalBook!!)
    }

    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val intentFromResult = result.data?.data
                if (intentFromResult != null) {
                    glide.load(intentFromResult).centerCrop().into(binding.bookCoverImageView)
                    currentLocalBook?.bookCoverSmallThumbnail = intentFromResult.toString()
                    viewModel.updateBook(currentLocalBook!!)
                }
            }
        }
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result ->
            if (result) {
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(galleryIntent)
            } else {
                Toast.makeText(this, "Permission needed!", Toast.LENGTH_LONG).show()
            }
        }
    }

}