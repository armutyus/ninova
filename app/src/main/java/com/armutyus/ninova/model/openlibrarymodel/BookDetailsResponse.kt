package com.armutyus.ninova.model.openlibrarymodel

sealed class BookDetailsResponse {

    data class BookKeyResponse(
        val description: String?
    )

    data class BookLendingKeyResponse(
        val number_of_pages: String?,
        val publishers: List<String>?
    )

    data class CombinedResponse(
        val description: String?,
        val number_of_pages: String?,
        val publishers: List<String>?,
        val loading: Boolean,
        val keyError: String,
        val lendingKeyError: String
    )

}
