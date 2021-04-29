package com.example.forgiphyapp.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.forgiphyapp.api.Data
import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.database.GifDatabaseDao
import com.example.forgiphyapp.pagingApi.PagingSourceGif
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

class GifRepository(
    private val dataBase: GifDatabaseDao,
    private var pagingSource: PagingSourceGif
) {

    val savedGifLiveData = dataBase.getAllGifDataLiveData()

    var actualData: List<GifData>? = null

    val dataPagingLiveData = MutableLiveData<PagingData<Data>>()


    suspend fun getGif(searchData: String, viewModelScope: CoroutineScope) {

        fetchGif(searchData, viewModelScope).collect {
            dataPagingLiveData.value = it
        }
    }

    private fun fetchGif(
        searchData: String,
        viewModelScope: CoroutineScope
    ): Flow<PagingData<Data>> {
        pagingSource.actualData = actualData
        pagingSource.searchData = searchData
        pagingSource.clear()
        return Pager(PagingConfig(pageSize = 20, enablePlaceholders = true))
        { pagingSource }
            .flow
            .cachedIn(viewModelScope)
    }


    suspend fun removeGif(gif: GifData) {
        dataBase.update(gif)
    }

}