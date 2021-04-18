package com.example.forgiphyapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [GifData::class], version = 2, exportSchema = false)
abstract class GifDatabase : RoomDatabase() {
    abstract val gifDatabaseDao: GifDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: GifDatabase? = null

        fun getInstance(context: Context): GifDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        GifDatabase::class.java,
                        "gif_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}