package com.example.forgiphyapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.database.GifDatabaseDao
import com.example.forgiphyapp.pagingApi.PagingSourceGif
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect


interface GifRepository {
    val savedGifLiveData: LiveData<List<GifData>>

    var actualData: List<GifData>?

    val dataPagingLiveData: MutableLiveData<PagingData<GifData>>

    suspend fun getGif(searchData: String, viewModelScope: CoroutineScope, likeGif: Boolean)

    suspend fun removeGif(gif: GifData)
    suspend fun likeGif(gif: GifData)
    fun getLikeGif()
}

class GifRepositoryImpl(
    private val dataBase: GifDatabaseDao,
    private var pagingSource: PagingSourceGif
) : GifRepository {

    override val savedGifLiveData = dataBase.getAllGifDataLiveData()

    override var actualData: List<GifData>? = null

    override val dataPagingLiveData = MutableLiveData<PagingData<GifData>>()


    override suspend fun getGif(
        searchData: String,
        viewModelScope: CoroutineScope,
        likeGif: Boolean
    ) {

        fetchGif(searchData, viewModelScope, likeGif).collect {
            dataPagingLiveData.value = it
        }
    }

    private fun fetchGif(
        searchData: String,
        viewModelScope: CoroutineScope,
        likeGif: Boolean
    ): Flow<PagingData<GifData>> {
        pagingSource.actualData = actualData
        pagingSource.searchData = searchData
        pagingSource.likeGif = likeGif
        pagingSource.clear()
        return Pager(PagingConfig(pageSize = 20, enablePlaceholders = true))
        { pagingSource as PagingSource<Int, GifData> }
            .flow
            .cachedIn(viewModelScope)
    }

    override fun getLikeGif() {

    }


    override suspend fun removeGif(gif: GifData) {
        dataBase.update(gif)
    }

    override suspend fun likeGif(gif: GifData) {
        dataBase.update(gif)
    }

}