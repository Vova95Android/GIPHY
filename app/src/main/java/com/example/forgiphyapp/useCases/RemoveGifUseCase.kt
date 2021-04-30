package com.example.forgiphyapp.useCases

import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.database.GifDatabaseDao

interface RemoveGifUseCase {
    suspend fun removeGif(gif: GifData)
}

class RemoveGifUseCaseImpl(val database: GifDatabaseDao) : RemoveGifUseCase {
    override suspend fun removeGif(gif: GifData) {
        database.update(gif)
    }

}