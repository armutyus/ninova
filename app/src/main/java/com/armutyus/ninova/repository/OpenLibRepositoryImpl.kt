package com.armutyus.ninova.repository

import android.util.Log
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.constants.Util.Companion.toLocalizedString
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
                    emit(Response.Loading)
                    val response = openLibraryApiService.getBooksByCategory(category, offset)
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

    override suspend fun getRandomBookCoverForCategory(category: String): String =
        withContext(coroutineContext) {
            try {
                val response = openLibraryApiService.getBooksByCategory(category, 0)
                if (response.isSuccessful) {
                    response.body()?.let {
                        return@let it.works.random().cover_id
                    }
                        ?: R.string.something_went_wrong.toLocalizedString()
                } else {
                    R.string.something_went_wrong.toLocalizedString()
                }
            } catch (e: Exception) {
                Log.i(
                    "CategoryCoverError",
                    R.string.error_with_message.toLocalizedString(e.localizedMessage)
                )
                R.string.something_went_wrong.toLocalizedString()
            }
        }
}