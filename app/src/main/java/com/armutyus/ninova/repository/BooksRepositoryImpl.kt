package com.armutyus.ninova.repository

import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.model.BookDetails
import com.armutyus.ninova.model.DataModel
import com.armutyus.ninova.roomdb.NinovaDao
import com.armutyus.ninova.roomdb.entities.BookShelfCrossRef
import com.armutyus.ninova.roomdb.entities.BookWithShelves
import com.armutyus.ninova.service.GoogleBooksApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BooksRepositoryImpl @Inject constructor(
    private val ninovaDao: NinovaDao,
    private val googleBooksApiService: GoogleBooksApiService,
    private val coroutineContext: CoroutineDispatcher = Dispatchers.IO
) : BooksRepositoryInterface {

    override suspend fun getBookDetails(bookId: String): Flow<Response<BookDetails>> = withContext(coroutineContext) {
        flow {
            try {
                emit(Response.Loading)
                val response = googleBooksApiService.getBookDetails(bookId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        return@let emit(Response.Success(it))
                    } ?: emit(Response.Failure("Something went wrong!"))
                } else {
                    emit(Response.Failure("Something went wrong!"))
                }
            } catch (e: Exception) {
                emit(Response.Failure("Error: ${e.localizedMessage}"))
            }
        }
    }

    override suspend fun searchBooksFromApi(searchQuery: String) = flow {
        try {
            emit(Response.Loading)
            val response = googleBooksApiService.searchBooks(searchQuery)
            if (response.isSuccessful) {
                response.body()?.let {
                    return@let emit(Response.Success(it))
                } ?: emit(Response.Failure("Something went wrong!"))
            } else {
                emit(Response.Failure("Something went wrong!"))
            }
        } catch (e: Exception) {
            emit(Response.Failure("Error: ${e.localizedMessage}"))
        }
    }

    override fun searchLocalBooks(searchString: String): Flow<List<DataModel.LocalBook>> {
        return ninovaDao.searchLocalBooks(searchString)
    }

    override suspend fun delete(localBook: DataModel.LocalBook) {
        ninovaDao.deleteBook(localBook)
    }

    override suspend fun insert(localBook: DataModel.LocalBook) {
        ninovaDao.insertBook(localBook)
    }

    override suspend fun update(localBook: DataModel.LocalBook) {
        ninovaDao.updateBook(localBook)
    }

    override fun getLocalBooks(): Flow<List<DataModel.LocalBook>> {
        return ninovaDao.getLocalBooks()
    }

    override suspend fun getBookWithShelves(bookId: String): Flow<List<BookWithShelves>> {
        return ninovaDao.getShelvesOfBook(bookId)
    }

    override suspend fun getBookShelfCrossRef(): Flow<List<BookShelfCrossRef>> {
        return ninovaDao.getBookShelfCrossRef()
    }

}