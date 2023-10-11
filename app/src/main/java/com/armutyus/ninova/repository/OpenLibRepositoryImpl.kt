package com.armutyus.ninova.repository

import android.util.Log
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.constants.Util.Companion.toLocalizedString
import com.armutyus.ninova.model.openlibrarymodel.BookDetailsResponse
import com.armutyus.ninova.model.openlibrarymodel.OpenLibraryResponse
import com.armutyus.ninova.service.OpenLibraryApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OpenLibRepositoryImpl @Inject constructor(
    private val openLibraryApiService: OpenLibraryApiService,
    private val coroutineContext: CoroutineDispatcher = Dispatchers.IO
) : OpenLibRepositoryInterface {
    override suspend fun getBooksByCategory(
        category: String,
        offset: Int
    ): Flow<Response<OpenLibraryResponse>> =
        withContext(coroutineContext) {
            flow {
                try {
                    val categoryUrl = if (category.contains(" ")) {
                        category.replace(" ", "_")
                    } else {
                        category
                    }
                    val fixedUrl = "subjects/$categoryUrl.json?limit=50&offset=$offset"
                    emit(Response.Loading)
                    val response = openLibraryApiService.getBooksByCategory(fixedUrl)
                    if (response.isSuccessful) {
                        response.body()?.let {
                            return@let emit(Response.Success(it))
                        }
                            ?: emit(Response.Failure(R.string.something_went_wrong.toLocalizedString()))
                    } else {
                        emit(Response.Failure(R.string.something_went_wrong.toLocalizedString()))
                    }
                } catch (e: Exception) {
                    emit(Response.Failure(R.string.error_with_message.toLocalizedString(e.localizedMessage)))
                }
            }
        }

    override suspend fun getBookDetails(
        bookKey: String
    ): Flow<Response<BookDetailsResponse>> =
        withContext(coroutineContext) {
            flow {
                try {
                    val fixedBookKey = if (bookKey.startsWith("/")) {
                        bookKey.substringAfterLast("/")
                    } else {
                        bookKey
                    }
                    val fixedUrl = "works/$fixedBookKey.json"
                    emit(Response.Loading)
                    val response = openLibraryApiService.getBookDetails(fixedUrl)
                    if (response.isSuccessful) {
                        response.body()?.let {
                            return@let emit(Response.Success(it))
                        }
                            ?: emit(Response.Failure(R.string.something_went_wrong.toLocalizedString()))
                    } else {
                        emit(Response.Failure(R.string.something_went_wrong.toLocalizedString()))
                    }
                } catch (e: Exception) {
                    emit(Response.Failure(R.string.error_with_message.toLocalizedString(e.localizedMessage)))
                }
            }
        }

    override suspend fun getRandomBookCoverForCategory(category: String): Flow<Response<String>> =
        withContext(coroutineContext) {
            flow {
                try {
                    emit(Response.Loading)
                    val fixedUrl = "subjects/$category.json?limit=50"
                    val response = openLibraryApiService.getBooksByCategory(fixedUrl)
                    if (response.isSuccessful) {
                        response.body()?.let {
                            return@let emit(Response.Success(it.works.random().cover_id))
                        }
                            ?: emit(Response.Failure(R.string.something_went_wrong.toLocalizedString()))
                    } else {
                        emit(Response.Failure(R.string.something_went_wrong.toLocalizedString()))
                    }
                } catch (e: Exception) {
                    Log.i(
                        "CategoryCoverError",
                        R.string.error_with_message.toLocalizedString(e.localizedMessage)
                    )
                    emit(Response.Failure(R.string.something_went_wrong.toLocalizedString()))
                }
            }
        }
}