package com.armutyus.ninova.repository

import com.armutyus.ninova.roomdb.NinovaDao
import com.armutyus.ninova.roomdb.entities.BookShelfCrossRef
import com.armutyus.ninova.roomdb.entities.LocalShelf
import com.armutyus.ninova.roomdb.entities.ShelfWithBooks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class ShelfRepositoryImpl @Inject constructor(
    private val ninovaDao: NinovaDao,
    private val coroutineContext: CoroutineContext = Dispatchers.IO
) : ShelfRepositoryInterface {
    override suspend fun insert(localShelf: LocalShelf) {
        ninovaDao.insertShelf(localShelf)
    }

    override suspend fun update(localShelf: LocalShelf) {
        ninovaDao.updateShelf(localShelf)
    }

    override suspend fun delete(localShelf: LocalShelf) {
        ninovaDao.deleteShelf(localShelf)
    }

    override suspend fun getLocalShelves(): List<LocalShelf> = withContext(coroutineContext) {
        ninovaDao.getLocalShelves()
    }

    override suspend fun searchLocalShelves(searchString: String): List<LocalShelf> =
        withContext(coroutineContext) {
            ninovaDao.searchLocalShelf(searchString)
        }

    override suspend fun insertBookShelfCrossRef(crossRef: BookShelfCrossRef) {
        ninovaDao.insertBookShelfCrossRef(crossRef)
    }

    override suspend fun deleteBookShelfCrossRef(crossRef: BookShelfCrossRef) {
        ninovaDao.deleteBookShelfCrossRef(crossRef)
    }

    override suspend fun getShelfWithBooks(): List<ShelfWithBooks> = withContext(coroutineContext) {
        ninovaDao.getBooksOfShelf()
    }
}