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


class PagingSourceGif @Inject constructor(
    private val database: GifDatabaseDao,
    private val api: GiphyService
) : PagingSource<Int, Data>() {
    private val apiKey = "N8ddDH1PCkpXqWiwiprA3ghbUz7bRC3J"
    private var offsetData = 0
    var searchData = "A"
    var actualData: List<GifData>? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Data> {
        Log.i("PagingSource", "load size - " + params.loadSize)
        Log.i("PagingSource", "key - " + params.key)
        var limitTemp = params.loadSize
        if ((params.key == null) && (offsetData == 0)) offsetData = 0
        else if (params.key != null) offsetData = params.key!!
        var listSize = 0
        var listResultTemp = GifParams(listOf())
        while (listSize < limitTemp) {
            val getProperties = api.getGifListAsync(
                apiKey,
                searchData,
                limitTemp,
                offsetData,
                "g",
                "en"
            )
            try {
                val listResult = getProperties.await()
                if (listSize == 0) listResultTemp = listResult
                else listResultTemp.data = listResultTemp.data.plus(listResult.data)
            } catch (t: Throwable) {
                Log.e("PagingSource", "error")
                if (t.message != null) Log.e("PagingSource", t.message!!)
                return LoadResult.Error(t)
            }
            actualData?.let { listResultTemp = removeGif(listResultTemp, it) }
            listSize = listResultTemp.data.size
            limitTemp -= listSize
        }
        setGifToDatabase(listResultTemp)
        offsetData += params.loadSize
        return LoadResult.Page(
            data = listResultTemp.data,
            prevKey = null,
            nextKey = offsetData
        )
    }

    override fun getRefreshKey(state: PagingState<Int, Data>): Int? {
        return null
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

    private suspend fun setGifToDatabase(data: GifParams) {

        data.data.forEach { dataTemp ->
            val temp = actualData?.firstOrNull {
                dataTemp.id == it.id
            }
            if (temp == null) database.insert(DataTransform.getGifData(dataTemp, true))
        }
    }


    fun clear() {
        offsetData = 0
    }
}