package com.example.forgiphyapp.viewModels

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.forgiphyapp.api.*
import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.database.GifDatabaseDao
import com.example.forgiphyapp.pagingApi.PagingSourceGif
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


abstract class GifListViewModel : ViewModel() {

    abstract val linearOrGridLiveData: LiveData<Boolean>

    abstract val dataParamsLiveData: LiveData<GifParams>

    abstract val dataPagingLiveData: LiveData<PagingData<Data>>
}

class GifListViewModelImpl(val database: GifDatabaseDao, val pagingSource: PagingSourceGif) :
    GifListViewModel() {

    override val linearOrGridLiveData = MutableLiveData<Boolean>()

    override val dataParamsLiveData = MutableLiveData<GifParams>()

    override val dataPagingLiveData = MutableLiveData<PagingData<Data>>()

    var actualData: List<GifData>? = null

    private var searchData = "A"

    val savedGifLiveData = database.getAllGifData()

    private var job: Job? = null

    fun refresh() {
        job?.cancel()
        job = viewModelScope.launch {
            fetchGif().collect {
                dataPagingLiveData.value = it
            }
        }
    }

    private fun fetchGif(): Flow<PagingData<Data>> {
        pagingSource.actualData = actualData
        pagingSource.searchData = searchData
        pagingSource.clear()
        return Pager(PagingConfig(pageSize = 20, enablePlaceholders = true))
        { pagingSource }
            .flow
            .cachedIn(viewModelScope)
    }

    fun searchNewData(data: String) {
        searchData = data
        refresh()
    }

    fun linearOrGrid(set: Boolean) {
        linearOrGridLiveData.value = set
    }

    fun newDataOrRefresh(newData: List<GifData>) {
        var needRefresh = false
        if (!actualData.isNullOrEmpty()) {
            for (dataPos in actualData!!.indices) {
                if (newData[dataPos].active != actualData!![dataPos].active) needRefresh = true
            }
            actualData = newData
        } else {
            actualData = newData
            refresh()
        }
        if (needRefresh) {
            refresh()
        }
    }

}