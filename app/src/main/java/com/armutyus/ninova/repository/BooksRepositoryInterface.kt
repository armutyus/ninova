package com.armutyus.ninova.repository

import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.model.GoogleApiBooks
import com.armutyus.ninova.roomdb.entities.BookWithShelves
import com.armutyus.ninova.roomdb.entities.LocalBook
import kotlinx.coroutines.flow.Flow

interface BooksRepositoryInterface {

    /*fun getBookList(): List<Book>

    fun searchBookFromLocal(searchString: String): List<Book>

    fun searchBookFromApi(searchString: String): List<Book>*/

    suspend fun searchBooksFromApi(searchQuery: String): Flow<Response<GoogleApiBooks>>

    suspend fun insert(localBook: LocalBook)

    suspend fun update(localBook: LocalBook)

    suspend fun delete(localBook: LocalBook)

    fun getLocalBooks(): Flow<List<LocalBook>>

    fun searchLocalBooks(searchString: String): Flow<List<LocalBook>>

    suspend fun getBookWithShelves(bookId: String): Flow<List<BookWithShelves>>

}