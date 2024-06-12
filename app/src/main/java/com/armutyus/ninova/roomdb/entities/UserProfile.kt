package com.armutyus.ninova.roomdb.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserProfile(
    @PrimaryKey(autoGenerate = false) var uid: String,
    var name: String,
    var email: String,
    var photoUrl: String,
    var profileBanner: String,
    var createdAt: String,
) {
    constructor() : this("", "", "", "", "", "")
}