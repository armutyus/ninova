package com.armutyus.ninova.model

data class BookDetailsInfo(
    val authors: List<String>?,
    val categories: List<String>?,
    val description: String?,
    val imageLinks: DetailsImageLinks,
    val pageCount: Int?,
    val publishedDate: String?,
    val publisher: String?,
    val title: String?
)