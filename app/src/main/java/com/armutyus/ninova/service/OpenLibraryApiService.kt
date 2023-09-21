package com.armutyus.ninova.service

import com.armutyus.ninova.model.openlibrarymodel.OpenLibraryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenLibraryApiService {
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
        "Platform: android"
    )
    @GET("subjects/{name}.json")
    suspend fun getBooksByCategory(
        @Path("name") category: String,
        @Query("limit") maxResults: Int = 50
    ): Response<OpenLibraryResponse>
}