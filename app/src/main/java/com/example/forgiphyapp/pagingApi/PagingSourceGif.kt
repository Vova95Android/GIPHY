package com.example.forgiphyapp.pagingApi

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.forgiphyapp.api.Data
import com.example.forgiphyapp.api.GifParams
import com.example.forgiphyapp.api.GiphyService
import com.example.forgiphyapp.database.DataTransform
import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.database.GifDatabaseDao
import javax.inject.Inject

val api_key = "N8ddDH1PCkpXqWiwiprA3ghbUz7bRC3J"

class PagingSourceGif @Inject constructor(
    val database: GifDatabaseDao,
    val api: GiphyService
) : PagingSource<Int, Data>() {
    var offseData = 0
    var searchData = "A"
    var actualData: List<GifData>? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Data> {
        Log.i("PagingSource", "load size - " + params.loadSize)
        Log.i("PagingSource", "key - " + params.key)
        var limitTemp = params.loadSize
        if ((params.key == null) && (offseData == 0)) offseData = 0
        else if (params.key != null) offseData = params.key!!
        var listSize = 0
        var listResultTemp = GifParams(listOf())
        while (listSize < limitTemp) {
            val getPropetiesDeferred = api.getGifList(
                api_key,
                searchData,
                limitTemp,
                offseData,
                "g",
                "en"
            )
            try {
                val listResult = getPropetiesDeferred.await()
                if (listSize == 0) listResultTemp = listResult
                else listResultTemp.data = listResultTemp.data.plus(listResult.data)
            } catch (t: Throwable) {
                Log.e("PagingSource", "error")
                if (t.message != null) Log.e("PagingSource", t.message!!)
                return LoadResult.Error(t)
            }
            actualData?.let { listResultTemp = removeGif(listResultTemp, it) }
            listSize = listResultTemp.data.size
            limitTemp = limitTemp - listSize
        }
        setGifToDatabase(listResultTemp)
        offseData += params.loadSize
        return LoadResult.Page(
            data = listResultTemp.data,
            prevKey = null,
            nextKey = offseData
        )
    }

    override fun getRefreshKey(state: PagingState<Int, Data>): Int? {return null}

    private fun removeGif(listResultTemp: GifParams, actualData: List<GifData>): GifParams {
        var listDataRemove = listOf<Data>()
        for (i in listResultTemp.data.indices) {
            for (z in actualData.indices) {
                if ((!actualData.isNullOrEmpty()) &&
                    (actualData[z].id == listResultTemp.data[i].id) &&
                    (!actualData[z].active)
                ) {
                    Log.i("PagingSource", "data minus " + i)
                    listDataRemove = listDataRemove.plus(listResultTemp.data[i])
                }
            }
        }
        if (listDataRemove.isNotEmpty()) listResultTemp.data =
            listResultTemp.data.minus(listDataRemove)
        return listResultTemp
    }

    private suspend fun setGifToDatabase(data: GifParams) {
        var findToDatabase = false
        for (gif_pos in data.data.indices) {
            if (actualData != null) {
                for (element in actualData!!) {
                    if (element.id == data.data[gif_pos].id) findToDatabase = true
                }
                if (!findToDatabase) database.insert(
                    DataTransform.getGifData(
                        data.data[gif_pos],
                        true
                    )
                )
            }
        }
    }

    private fun getUrlFromDatabase(limit: Int, offset: Int): GifParams {
        val dataTemp = listOf<Data>()
        if (actualData != null)
            for (data_count in offset..offset + limit) {
                dataTemp.plus(DataTransform.getData(actualData!![data_count]))
            }
        return GifParams(dataTemp)
    }

    fun clear() {
        offseData = 0
    }
}