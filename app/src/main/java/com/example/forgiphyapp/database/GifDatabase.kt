package com.example.forgiphyapp.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [GifData::class], version = 2, exportSchema = false)
abstract class GifDatabase : RoomDatabase() {
    abstract val gifDatabaseDao: GifDatabaseDao
}