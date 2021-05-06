package com.example.forgiphyapp.useCases

import com.example.forgiphyapp.api.Data
import com.example.forgiphyapp.api.GifDataSource
import com.example.forgiphyapp.api.GifParams
import com.example.forgiphyapp.database.DataTransform
import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.database.GifDatabaseDao

interface LoadGifUseCase {

    suspend fun getGif(search: String, nextPage: Boolean?): List<GifData>

    fun previousButtonIsActive(): Boolean

    fun nextButtonIsActive(): Boolean
}

class LoadGifUseCaseImpl(
    private val dataSource: GifDataSource,
    private val database: GifDatabaseDao
) : LoadGifUseCase {

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


    override suspend fun getGif(search: String, nextPage: Boolean?): List<GifData> {
        var limitTemp = limit
        nextButtonActive = true

        if (search != searchData) {
            offsetData = 0
            startPage = 0
            endPage = 0
        }
        searchData = search

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
            val listResultTemp: List<GifData> = loadGifFromSource(searchData, limitTemp, offsetData)

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

    private suspend fun loadGifFromSource(
        searchData: String,
        limit: Int,
        offsetData: Int
    ): List<GifData> {
        var listTemp = dataSource.getGif(searchData, limit, offsetData)
        val actualData = database.getAllGifData()
        listTemp = removeGif(listTemp, actualData)
        return setGifToDatabase(listTemp, actualData)
    }

    private fun removeGif(listResultTemp: GifParams, actualData: List<GifData>): GifParams {
        var listDataRemove = listOf<Data>()

        listResultTemp.data.forEach { data ->
            val remove = actualData.firstOrNull {
                data.id == it.id && !it.active
            }
            if (remove != null) listDataRemove = listDataRemove.plus(data)
        }
        if (listDataRemove.isNotEmpty()) listResultTemp.data =
            listResultTemp.data.minus(listDataRemove)
        return listResultTemp
    }


    private suspend fun setGifToDatabase(
        data: GifParams,
        actualData: List<GifData>
    ): List<GifData> {
        var list: List<GifData> = listOf()
        data.data.forEach { dataTemp ->
            val temp = actualData.firstOrNull {
                dataTemp.id == it.id
            }
            list = if (temp == null) {
                database.insert(DataTransform.getGifData(dataTemp))
                list.plus(DataTransform.getGifData(dataTemp))
            } else {
                list.plus(temp)
            }
        }
        return list
    }


}