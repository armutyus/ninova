package com.armutyus.ninova.model.openlibrarymodel

data class OpenLibraryWork(
    val authors: List<Author>,
    val cover_id: String,
    val first_publish_year: Int,
    val key: String, // This is the key for book itself. Get description from Works API
    val lending_edition: String, // This is the key for book itself. Get publisher and pages from Books API
    val title: String
)