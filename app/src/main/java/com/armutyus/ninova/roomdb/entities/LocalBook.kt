package com.armutyus.ninova.roomdb.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Book")
data class LocalBook(
    @PrimaryKey(autoGenerate = true) val bookId: Int,
    val bookTitle: String?,
    val bookSubtitle: String?,
    val bookAuthors: List<String>?,
    val bookPages: String?,
    val bookCoverUrl: String?,
    val bookDescription: String?,
    val bookPublishedDate: String?,
    val bookCategories: List<String>?,
    val bookPublisher: String?,
    val bookNotes: String?
)
