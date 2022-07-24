package com.armutyus.ninova.repository

import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.model.DataModel
import com.armutyus.ninova.roomdb.NinovaDao
import com.armutyus.ninova.roomdb.entities.BookWithShelves
import com.armutyus.ninova.service.GoogleBooksApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BooksRepository @Inject constructor(
    private val ninovaDao: NinovaDao,
    private val googleBooksApiService: GoogleBooksApiService
) : BooksRepositoryInterface {

    /*override fun getBookList(): List<Book> {
        val book1 = Book("Semerkand", listOf("Amin Maalouf", "ahmet mehmet"), "300", "01.02.1989")
        val book2 = Book(
            "Fedailerin Kalesi: Alamut",
            listOf("Vladimir Bartol", "Yusuf Mususf"),
            "290",
            "03.12.1975"
        )
        val book3 = Book("Cesur Yeni Dünya", listOf("Aldous Huxley"), "519", "12.09.1945")
        val book4 =
            Book("Beyaz Geceler", listOf("Fyodor Mihayloviç Dostoyevski"), "417", "30.05.1780")
        val book5 = Book("Nietzsche Ağladığında", listOf("Irvin Yalom"), "236", "06.03.2000")

        return mutableListOf(book1, book2, book3, book4, book5)
    }

    override fun searchBookFromLocal(searchString: String): List<Book> {
        val book1 =
            Book("Körlük", listOf("Jose Saramago", "Oramago Maburamago"), "451", "01.02.1998")
        val book2 = Book("Satranç", listOf("Stefan Zweig"), "243", "02.01.1946")
        val book3 = Book("Altıncı Koğuş", listOf("Anton Çehov"), "187", "10.07.1966")
        val book4 = Book("Gurur ve Önyargı", listOf("Jane Austen"), "432", "30.05.1780")
        val book5 = Book("Martin Eden", listOf("Jack London", "Test Case"), "218", "06.03.1993")

        val localBooks = listOf(book1, book2, book3, book4, book5)

        val filteredBooks: List<Book> = localBooks.filter { books ->
            books.bookTitle.lowercase().contains(searchString) || books.bookAuthor.toString()
                .lowercase()
                .contains(searchString)
        }

        return filteredBooks

    }

    override fun searchBookFromApi(searchString: String): List<Book> {

        val book1 = Book("Hayvan Çiftliği", listOf("George Orwell", "Pol Fol"), "319", "07.02.1952")
        val book2 = Book("Dönüşüm", listOf("Franz Kafka"), "290", "03.12.1961")
        val book3 = Book(
            "İçimizdeki Şeytan",
            listOf("Sabahattin Ali", "Duman", "Mor ve Ötesi"),
            "396",
            "22.11.1935"
        )
        val book4 =
            Book(
                "Hayvanlardan Tanrılara Sapiens",
                listOf("Yuval", "Noah", "Harari"),
                "288",
                "19.05.2010"
            )
        val book5 = Book("Bir İdam Mahkumunun Son Günü", listOf(), "956", "04.08.1856")

        val apiBooks = listOf(book1, book2, book3, book4, book5)

        val filteredBooks: List<Book> = apiBooks.filter { books ->
            books.bookTitle.lowercase().contains(searchString) || books.bookAuthor.toString()
                .lowercase()
                .contains(searchString)
        }

        return filteredBooks
    }*/

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

    override suspend fun insert(localBook: DataModel.LocalBook) {
        ninovaDao.insertBook(localBook)
    }

    override suspend fun update(localBook: DataModel.LocalBook) {
        ninovaDao.updateBook(localBook)
    }

    override suspend fun delete(localBook: DataModel.LocalBook) {
        ninovaDao.deleteBook(localBook)
    }

    override fun getLocalBooks(): Flow<List<DataModel.LocalBook>> {
        return ninovaDao.getLocalBooks()
    }

    override fun searchLocalBooks(searchString: String): Flow<List<DataModel.LocalBook>> {
        return ninovaDao.searchLocalBooks(searchString)
    }

    override suspend fun getBookWithShelves(bookId: String): Flow<List<BookWithShelves>> {
        return ninovaDao.getShelvesOfBook(bookId)
    }

}