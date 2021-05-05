package com.example.forgiphyapp.useCases

import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.database.GifDatabaseDao

interface OfflineGifUseCase {
    suspend fun getGif(limit: Int, offsetData: Int): List<GifData>
}

class OfflineGifUseCaseImpl(val database: GifDatabaseDao) : OfflineGifUseCase {

    override suspend fun getGif(limit: Int, offsetData: Int): List<GifData> {
        val actualData = database.getAllGifData()
        return if (offsetData + limit < actualData.size)
            removeGif(actualData.subList(offsetData, offsetData + limit), actualData)
        else
            listOf()
    }

    private fun removeGif(listResultTemp: List<GifData>, actualData: List<GifData>): List<GifData> {
        var listDataRemove = listOf<GifData>()
        var list = listResultTemp

        list.forEach { data ->
            val remove = actualData.firstOrNull {
                data.id == it.id && !it.active
            }
            if (remove != null) listDataRemove = listDataRemove.plus(data)
        }
        if (listDataRemove.isNotEmpty()) list = list.minus(listDataRemove)
        return list
    }
}