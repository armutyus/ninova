package com.armutyus.ninova.roomdb.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Shelf")
data class LocalShelf(
    @PrimaryKey(autoGenerate = true) var shelfId: Int,
    var shelfTitle: String?,
    var createdAt: String?,
    var shelfCover: String?,
    var booksInShelf: Int?
)