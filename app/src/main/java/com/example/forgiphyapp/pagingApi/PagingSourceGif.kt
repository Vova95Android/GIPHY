package com.example.forgiphyapp.pagingApi

import android.util.Log
import androidx.paging.*
import com.example.forgiphyapp.api.Data
import com.example.forgiphyapp.api.GifParams
import com.example.forgiphyapp.api.GiphyAPI
import com.example.forgiphyapp.database.DataTransform
import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.database.GifDatabaseDao

val api_key = "N8ddDH1PCkpXqWiwiprA3ghbUz7bRC3J"

class PagingSourceGif(
    val searchData: String,
    val actualData: List<GifData>?,
    val database: GifDatabaseDao
) : PagingSource<Int, Data>() {
    var offseData = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Data> {
        Log.i("PagingSource", "load size - " + params.loadSize)
        Log.i("PagingSource", "key - " + params.key)
        var limitTemp = params.loadSize
        if ((params.key == null) && (offseData == 0)) offseData = 0
        else if (params.key != null) offseData = params.key!!
        var listSize = 0
        var listResultTemp = GifParams(listOf())
        while (listSize < limitTemp) {
            val getPropetiesDeferred = GiphyAPI.retrofitService.getGifList(
                api_key,
                searchData,
                limitTemp,
                offseData,
                "g",
                "en"
            )
            var listDataRemov = listOf<Data>()
            try {
                val listResult = getPropetiesDeferred.await()
                if (listSize == 0) listResultTemp = listResult
                else listResultTemp.data = listResultTemp.data.plus(listResult.data)
            } catch (t: Throwable) {
                Log.e("PagingSource", "error")
                if (t.message != null) Log.e("PagingSource", t.message!!)
                //return LoadResult.Error(t)
                listResultTemp = getUrlFromDatabase(limitTemp, offseData)
            }

            for (i in 0..listResultTemp.data.size - 1) {
                if (actualData != null) {
                    for (z in 0..actualData.size - 1) {
                        if ((!actualData.isNullOrEmpty()) &&
                            (actualData[z].id == listResultTemp.data[i].id) &&
                            (!actualData[z].active)
                        ) {
                            Log.i("PagingSource", "data minus " + i)
                            listDataRemov = listDataRemov.plus(listResultTemp.data[i])
                        }
                    }
                }
            }
            if (listDataRemov.size > 0) listResultTemp.data =
                listResultTemp.data.minus(listDataRemov)
            listSize = listResultTemp.data.size
            limitTemp = limitTemp - listSize
        }
        setGifTodatabase(listResultTemp)
        offseData += params.loadSize
        return LoadResult.Page(
            data = listResultTemp.data,
            prevKey = null,
            nextKey = offseData
        )
    }

    override fun getRefreshKey(state: PagingState<Int, Data>): Int? {
        TODO("Not yet implemented")
    }

    suspend private fun setGifTodatabase(data: GifParams) {
        var findToDatabase = false
        for (gif_pos in data.data.indices) {
            if (actualData != null) {
                for (element in actualData) {
                    if (element.id == data.data[gif_pos].id) findToDatabase = true
                }
                if (!findToDatabase) database.insert(
                    DataTransform().getGifData(
                        data.data[gif_pos],
                        true
                    )
                )
            }
        }
    }

    private fun getUrlFromDatabase(limit: Int, offset: Int): GifParams {
        var data_temp = listOf<Data>()
        if (actualData != null)
            for (data_count in offset..offset + limit) {
                data_temp.plus(DataTransform().getData(actualData[data_count]))
            }
        return GifParams(data_temp)
    }
}