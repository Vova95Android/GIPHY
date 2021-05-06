package com.example.forgiphyapp.test

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.forgiphyapp.api.Data
import com.example.forgiphyapp.database.DataTransform
import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.database.GifDatabaseDao
import com.example.forgiphyapp.pagingApi.PagingSourceGif

class PagingSourceTest(private val database: GifDatabaseDao) : PagingSource<Int, GifData>(),
    PagingSourceGif {


    override fun getRefreshKey(state: PagingState<Int, GifData>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GifData> {
        val list = actualData
        var listRemov = list
        list?.forEach { if (!it.active) listRemov = listRemov?.minus(it) }
        return LoadResult.Page(
            data = listRemov!!,
            prevKey = null,
            nextKey = null
        )
    }

    override var searchData: String = "A"

    override var actualData: List<GifData>? = null
    override var likeGif = false

    override fun clear() {
        TODO("Not yet implemented")
    }
}