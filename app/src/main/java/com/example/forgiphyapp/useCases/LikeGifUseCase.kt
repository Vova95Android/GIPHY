package com.example.forgiphyapp.useCases

import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.database.GifDatabaseDao

interface LikeGifUseCase {
    suspend fun likeGif(gif: GifData)
    suspend fun getGif(nextPage: Boolean?): List<GifData>
    fun nextButtonIsActive(): Boolean
    fun previousButtonIsActive(): Boolean
}

class LikeGifUseCaseImpl(val database: GifDatabaseDao) : LikeGifUseCase {


    private var offsetData = 0
    private val limit = 30
    private var startPage = 0
    private var endPage = 0
    private var nextButtonActive = true

    override suspend fun likeGif(gif: GifData) {
        database.update(gif)
    }

    override suspend fun getGif(nextPage: Boolean?): List<GifData> {
        var limitTemp = limit
        nextButtonActive = true

        when (nextPage) {
            true -> {
                startPage = endPage + 1
                offsetData = startPage
            }
            false -> {
                endPage = startPage - 1
                offsetData = endPage - limit
            }
            null -> {
                offsetData = startPage
            }
        }

        var listSize = 0
        var listResult = listOf<GifData>()
        while (listSize < limit) {
            val listSizeOld = listSize
            val listResultTemp: List<GifData> = getListLikeGif(limitTemp, offsetData)

            if ((nextPage == true) || (nextPage == null)) {
                listResult = listResult.plus(listResultTemp)
                listSize = listResult.size
                offsetData += limitTemp
                limitTemp = limit - listSize
            } else if (nextPage == false) {
                listResult = listResultTemp.plus(listResult)
                listSize = listResult.size
                limitTemp = limit - listSize
                offsetData -= limitTemp
            }
            if (offsetData < 0) {
                listSize = limit
                offsetData = 0
            }
            if (listSizeOld == listSize) {
                listSize = limit
                nextButtonActive = false
            }
        }
        if ((nextPage == true) || (nextPage == null)) endPage = offsetData
        else startPage = offsetData + limitTemp
        return listResult
    }



    override fun previousButtonIsActive(): Boolean {
        return startPage >= limit
    }

    override fun nextButtonIsActive(): Boolean {
        return nextButtonActive
    }

    private suspend fun getListLikeGif(limit: Int, offsetData: Int): List<GifData> {
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