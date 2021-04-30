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

        val limitTemp = params.loadSize
        if ((params.key == null) && (offsetData == 0)) offsetData = 0
        else if (params.key != null) offsetData = params.key!!

        val list: List<GifData> = try {

            if (likeGif) getLikeGif(limitTemp)
            else getAllGif(limitTemp)

        } catch (t: Throwable) {
            Log.e("PagingSource", "error")
            if (t.message != null) Log.e("PagingSource", t.message!!)
            loadGifFromDatabase(limitTemp)
            //return LoadResult.Error(t)
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


    private suspend fun getAllGif(limit: Int): List<GifData> {
        var listSize = 0
        var listResultTemp = GifParams(listOf())
        while (listSize < limit) {
            val getProperties = api.getGifListAsync(
                apiKey,
                searchData,
                limit,
                offsetData,
                "g",
                "en"
            )
            val listResult = getProperties.await()
            if (listSize == 0) listResultTemp = listResult
            else listResultTemp.data = listResultTemp.data.plus(listResult.data)

            actualData?.let { listResultTemp = removeGif(listResultTemp, it) }
            listSize = listResultTemp.data.size
        }
        return setGifToDatabase(listResultTemp)

    }

    private fun getLikeGif(limit: Int): List<GifData> {
        var list = listOf<GifData>()
        var count = 0
        actualData!!.forEach {
            if (it.like) {
                if ((count >= offsetData) && (count < offsetData + limit)) list =
                    list.plus(it)
                count++
            }
        }
        return list
    }

    private fun loadGifFromDatabase(limit: Int): List<GifData> {
        var list = listOf(GifData("error", "error", "error", true, false))
        var count = 0

        actualData?.let {
            if (it.size > offsetData)
                it.forEach { data ->
                    if (count > offsetData) {
                        list = list.plus(data)
                    }
                    if (count < offsetData + limit) count++
                    else return@let
                }
        }
        return list
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
            list = if (temp == null) {
                database.insert(DataTransform.getGifData(dataTemp))
                list.plus(DataTransform.getGifData(dataTemp))
            } else {
                list.plus(temp)
            }
        }
        return list
    }


    override fun clear() {
        offsetData = 0
    }
}