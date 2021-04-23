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
    fun getAllGifDataLiveData(): LiveData<List<GifData>>

    @Query("SELECT * FROM gif_data")
    fun getAllGifData(): List<GifData>

    @Delete(entity = GifData::class)
    fun deleteAllGif(data: List<GifData>)

}