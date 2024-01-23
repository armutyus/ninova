package com.armutyus.ninova.roomdb.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserProfile(
    @PrimaryKey(autoGenerate = false) val uid: String,
    val name: String,
    val email: String,
    val imageUrl: String,
) {
    constructor() : this("", "", "", "")
}
