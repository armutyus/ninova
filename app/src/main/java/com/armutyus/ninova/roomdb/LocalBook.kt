package com.armutyus.ninova.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Book")
data class LocalBook(
    @PrimaryKey(autoGenerate = true) val bookId: Int,
    val bookTitle: String?,
    val bookAuthor: String?,
    val bookPages: String?,
    val bookCategory: String?,
    val bookCoverUrl: String?,
    val bookDescription: String?,
    val releaseDate: String?
)
