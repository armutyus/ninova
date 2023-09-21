package com.armutyus.ninova.model.openlibrarymodel

data class OpenLibraryWork(
    val authors: List<Author>,
    val cover_id: String,
    val first_publish_year: Int,
    val key: String, // This is the key for book itself. So we can also get book details from open library.
    val subject: List<String>,
    val title: String
)