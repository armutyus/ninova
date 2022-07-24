package com.armutyus.ninova.model

data class Book(
    val bookTitle: String,
    val bookAuthor: List<String>,
    val bookPages: String,
    val releaseDate: String
) /*val bookCover = holder.itemView.findViewById<ImageView>(R.id.bookImage)
        val bookTitle = holder.itemView.findViewById<TextView>(R.id.bookTitleText)
        val bookAuthor = holder.itemView.findViewById<TextView>(R.id.bookAuthorText)
        val bookPages = holder.itemView.findViewById<TextView>(R.id.bookPageText)
        val bookReleaseDate = holder.itemView.findViewById<TextView>(R.id.bookReleaseDateText)
        val book = mainSearchBooksList[position]

        val addButton = holder.itemView.findViewById<ImageButton>(R.id.main_search_add_button)
        val addedButton =
            holder.itemView.findViewById<ImageButton>(R.id.main_search_add_checked_button)

        if (book.isBookAddedCheck(booksViewModel)) {
            addButton.visibility = View.GONE
            addedButton.visibility = View.VISIBLE
        } else {
            addButton.visibility = View.VISIBLE
            addedButton.visibility = View.GONE
        }

        addButton?.setOnClickListener {
            searchFragment.onClick(
                DataModel.LocalBook(
                    book.id!!,
                    book.volumeInfo?.title,
                    book.volumeInfo?.subtitle,
                    book.volumeInfo?.authors,
                    book.volumeInfo?.pageCount.toString(),
                    book.volumeInfo?.imageLinks?.smallThumbnail,
                    book.volumeInfo?.imageLinks?.thumbnail,
                    book.volumeInfo?.description,
                    book.volumeInfo?.publishedDate,
                    book.volumeInfo?.categories,
                    book.volumeInfo?.publisher,
                    ""
                )
            )
            addButton.visibility = View.GONE
            addedButton.visibility = View.VISIBLE
        }

        addedButton?.setOnClickListener {
            Toast.makeText(
                holder.itemView.context,
                "Already added to your library",
                Toast.LENGTH_SHORT
            ).show()
        }

        holder.itemView.setOnClickListener {
            currentBook = book
            holder.itemView.context.startActivity(bookDetailsIntent)
        }

        holder.itemView.apply {
            glide.load(book.volumeInfo?.imageLinks?.smallThumbnail).centerCrop().into(bookCover)
            bookTitle.text = book.volumeInfo?.title
            bookAuthor.text = book.volumeInfo?.authors?.joinToString(", ")
            bookPages.text = book.volumeInfo?.pageCount.toString()
            bookReleaseDate.text = book.volumeInfo?.publishedDate
        }*/
