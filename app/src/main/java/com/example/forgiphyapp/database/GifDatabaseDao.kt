package com.example.forgiphyapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface GifDatabaseDao {
    @Insert
    suspend fun insert(gifData: GifData)

    @Update
    suspend fun update(gifData: GifData)

    @Query("SELECT * FROM gif_data")
    fun getAllGifData(): LiveData<List<GifData>>
}