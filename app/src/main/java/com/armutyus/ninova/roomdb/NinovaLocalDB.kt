package com.armutyus.ninova.roomdb

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.armutyus.ninova.model.googlebooksmodel.DataModel
import com.armutyus.ninova.roomdb.entities.BookShelfCrossRef
import com.armutyus.ninova.roomdb.entities.LocalShelf
import com.armutyus.ninova.roomdb.entities.UserProfile

@Database(
    entities = [
        DataModel.LocalBook::class,
        LocalShelf::class,
        BookShelfCrossRef::class,
        UserProfile::class
    ],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
@TypeConverters(DataConverter::class)
abstract class NinovaLocalDB : RoomDatabase() {
    abstract fun ninovaDao(): NinovaDao
}