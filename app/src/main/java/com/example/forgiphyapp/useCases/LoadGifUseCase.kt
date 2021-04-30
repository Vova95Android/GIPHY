package com.example.forgiphyapp.useCases

import com.example.forgiphyapp.api.Data
import com.example.forgiphyapp.api.GifDataSource
import com.example.forgiphyapp.api.GifParams
import com.example.forgiphyapp.database.DataTransform
import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.database.GifDatabaseDao

interface LoadGifUseCase {

    suspend fun getGif(searchData: String, limit: Int, offsetData: Int): List<GifData>
}

class LoadGifUseCaseImpl(
    private val dataSource: GifDataSource,
    private val database: GifDatabaseDao
) : LoadGifUseCase {


    override suspend fun getGif(searchData: String, limit: Int, offsetData: Int): List<GifData> {
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