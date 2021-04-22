package com.example.forgiphyapp.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface GifDatabaseDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(gifData: GifData)

    @Update
    suspend fun update(gifData: GifData)

    @Query("SELECT * FROM gif_data")
    fun getAllGifData(): LiveData<List<GifData>>
}