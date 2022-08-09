package com.armutyus.ninova.roomdb.entities

import androidx.room.Entity

@Entity(primaryKeys = ["bookId", "shelfId"])
data class BookShelfCrossRef(
    val bookId: String,
    val shelfId: Int
) {
    constructor(): this("",0)
}