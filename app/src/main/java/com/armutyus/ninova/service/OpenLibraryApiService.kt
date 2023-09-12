package com.armutyus.ninova.service

import com.armutyus.ninova.model.openlibrarymodel.OpenLibraryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface OpenLibraryApiService {
    @GET("subjects/{category}.json?limit=50")
    suspend fun getBooksByCategory(@Path("category") category: String): Response<OpenLibraryResponse>
}