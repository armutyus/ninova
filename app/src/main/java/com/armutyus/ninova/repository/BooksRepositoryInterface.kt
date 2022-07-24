package com.armutyus.ninova.repository

import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.model.DataModel
import com.armutyus.ninova.model.GoogleApiBooks
import com.armutyus.ninova.roomdb.entities.BookWithShelves
import kotlinx.coroutines.flow.Flow

interface BooksRepositoryInterface {

    /*fun getBookList(): List<Book>

    fun searchBookFromLocal(searchString: String): List<Book>

    fun searchBookFromApi(searchString: String): List<Book>*/

    suspend fun searchBooksFromApi(searchQuery: String): Flow<Response<GoogleApiBooks>>

    suspend fun insert(localBook: DataModel.LocalBook)

    suspend fun update(localBook: DataModel.LocalBook)

    suspend fun delete(localBook: DataModel.LocalBook)

    fun getLocalBooks(): Flow<List<DataModel.LocalBook>>

    fun searchLocalBooks(searchString: String): Flow<List<DataModel.LocalBook>>

    suspend fun getBookWithShelves(bookId: String): Flow<List<BookWithShelves>>

}