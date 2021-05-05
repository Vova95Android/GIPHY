package com.example.forgiphyapp.useCases

import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.database.GifDatabaseDao

interface LikeGifUseCase {
    suspend fun likeGif(gif: GifData)
    suspend fun getListLikeGif(limit: Int, offsetData: Int): List<GifData>
}

class LikeGifUseCaseImpl(val database: GifDatabaseDao) : LikeGifUseCase {

    override suspend fun likeGif(gif: GifData) {
        database.update(gif)
    }

    override suspend fun getListLikeGif(limit: Int, offsetData: Int): List<GifData> {
        var list = listOf<GifData>()
        val actualData = database.getAllGifData()
        var count = 0
        actualData.forEach {
            if (it.like) {
                if ((count >= offsetData) && (count < offsetData + limit)) list =
                    list.plus(it)
                count++
            }
        }
        return list
    }
}