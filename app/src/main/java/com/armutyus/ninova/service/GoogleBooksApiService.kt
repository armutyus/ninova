package com.armutyus.ninova.service

import com.armutyus.ninova.model.BookDetails
import com.armutyus.ninova.model.GoogleApiBooks
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GoogleBooksApiService {

    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") searchQuery: String,
        @Query("maxResults") maxResults: Int = 20
    ): Response<GoogleApiBooks>

    @GET("volumes/{id}")
    suspend fun getBookDetails(
        @Path("id") id: Int,
    ): Response<BookDetails>

}