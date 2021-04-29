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


//class PagingSourceGif @Inject constructor(

interface PagingSourceGif {

    var searchData: String
    var actualData: List<GifData>?
    var likeGif: Boolean
    fun clear()
}


class PagingSourceGifImpl(
    private val database: GifDatabaseDao,
    private val api: GiphyService
) : PagingSource<Int, GifData>(), PagingSourceGif {
    private val apiKey = "N8ddDH1PCkpXqWiwiprA3ghbUz7bRC3J"
    private var offsetData = 0
    override var searchData = "A"
    override var actualData: List<GifData>? = null
    override var likeGif = false

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GifData> {
        Log.i("PagingSource", "load size - " + params.loadSize)
        Log.i("PagingSource", "key - " + params.key)
        var limitTemp = params.loadSize
        if ((params.key == null) && (offsetData == 0)) offsetData = 0
        else if (params.key != null) offsetData = params.key!!
        var listSize = 0
        var listResultTemp = GifParams(listOf())
        if (!likeGif) while (listSize < limitTemp) {
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
        var list = listOf<GifData>()
        if (likeGif) {
            var count = 0
            actualData!!.forEach {
                if (it.like) {
                    if ((count >= offsetData) && (count < offsetData + limitTemp)) list =
                        list.plus(it)
                    count++
                }
            }
        } else {
            list = setGifToDatabase(listResultTemp)
        }

        var offsetTemp: Int? = null

        if (list.size >= limitTemp) {
            offsetData += params.loadSize
            offsetTemp = offsetData
        }
        return LoadResult.Page(
            data = list,
            prevKey = null,
            nextKey = offsetTemp
        )
    }

    override fun getRefreshKey(state: PagingState<Int, GifData>): Int? {
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

    private suspend fun setGifToDatabase(data: GifParams): List<GifData> {
        var list: List<GifData> = listOf()
        data.data.forEach { dataTemp ->
            val temp = actualData?.firstOrNull {
                dataTemp.id == it.id
            }
            if (temp == null) {
                database.insert(DataTransform.getGifData(dataTemp, true, false))
                list = list.plus(DataTransform.getGifData(dataTemp, true, false))
            } else {
                list = list.plus(temp)
            }
        }
        return list
    }


    override fun clear() {
        offsetData = 0
    }
}