package com.example.forgiphyapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.forgiphyapp.api.Data
import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.database.GifDatabaseDao
import com.example.forgiphyapp.pagingApi.PagingSourceGif
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

//@Singleton
//class GifRepository @Inject constructor(
class GifRepository (
    private val dataBase: GifDatabaseDao,
    private var pagingSource: PagingSourceGif
) {

    val savedGifLiveData = dataBase.getAllGifDataLiveData()

    var actualData: List<GifData>? = null

    val dataPagingLiveData = MutableLiveData<PagingData<Data>>()

    private var job: Job?=null

    private var search="B"

    fun newDataOrRefresh(searchData: String, viewModelScope: CoroutineScope) {
        val newData=savedGifLiveData.value
        var needRefresh = false
        if ((!actualData.isNullOrEmpty())&&(!newData.isNullOrEmpty())&&(searchData==search)) {
            for (dataPos in actualData!!.indices) {
                if (newData[dataPos].active != actualData!![dataPos].active) needRefresh = true
            }
            actualData = newData
        } else {
            actualData = newData
            getGif(searchData, viewModelScope)
        }
        if (needRefresh) {
            getGif(searchData, viewModelScope)
        }
        search=searchData
    }

    private fun getGif(searchData: String, viewModelScope: CoroutineScope){
        job?.cancel()
        job = viewModelScope.launch {
            fetchGif(searchData,viewModelScope).collect {
                dataPagingLiveData.value = it
            }
        }
    }

    private fun fetchGif(searchData: String, viewModelScope: CoroutineScope): Flow<PagingData<Data>> {
        pagingSource.actualData = actualData
        pagingSource.searchData = searchData
        pagingSource.clear()
        return Pager(PagingConfig(pageSize = 20, enablePlaceholders = true))
        { pagingSource }
            .flow
            .cachedIn(viewModelScope)
    }

    suspend fun removeGif(gif: GifData){
        dataBase.update(gif)
    }

}