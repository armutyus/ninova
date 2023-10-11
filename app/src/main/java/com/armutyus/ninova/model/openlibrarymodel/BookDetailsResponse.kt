package com.armutyus.ninova.model.openlibrarymodel

data class BookDetailsResponse(
    val publishers: List<String>?,
    val number_of_pages: String?,
    val description: String?
)
