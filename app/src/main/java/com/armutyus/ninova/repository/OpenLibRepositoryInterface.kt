package com.armutyus.ninova.repository

import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.model.openlibrarymodel.OpenLibraryResponse
import kotlinx.coroutines.flow.Flow

interface OpenLibRepositoryInterface {

    suspend fun getBooksByCategory(
        category: String,
        offset: Int
    ): Flow<Response<OpenLibraryResponse>>

    suspend fun getRandomBookCoverForCategory(category: String): Flow<Response<String>>

}