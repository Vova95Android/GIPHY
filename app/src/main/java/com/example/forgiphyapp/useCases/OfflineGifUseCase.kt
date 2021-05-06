package com.example.forgiphyapp.useCases

import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.database.GifDatabaseDao

interface OfflineGifUseCase {

    suspend fun getGif(nextPage: Boolean?): List<GifData>

    fun previousButtonIsActive(): Boolean

    fun nextButtonIsActive(): Boolean
}

class OfflineGifUseCaseImpl(val database: GifDatabaseDao) : OfflineGifUseCase {


    private var offsetData = 0
    private val limit = 30
    private var startPage = 0
    private var endPage = 0
    private var searchData = "H"
    private var nextButtonActive = true

    override fun previousButtonIsActive(): Boolean {
        return startPage >= limit
    }

    override fun nextButtonIsActive(): Boolean {
        return nextButtonActive
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
            val listResultTemp: List<GifData> = getFromDatabase(limitTemp, offsetData)

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

    private suspend fun getFromDatabase(limit: Int, offsetData: Int): List<GifData> {
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