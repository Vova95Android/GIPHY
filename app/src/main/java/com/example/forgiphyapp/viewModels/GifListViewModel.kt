package com.example.forgiphyapp .viewModels

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


abstract class GifListViewModelImpl : ViewModel() {
    abstract fun refresh()

    abstract val previousActiveButton: LiveData<Boolean>

    abstract val linearOrGrid: LiveData<Boolean>

    abstract val dataParams: LiveData<GifParams>

    abstract val dataPagimg: LiveData<PagingData<Data>>
}

class GifListViewModel(
    val database: GifDatabaseDao
) : GifListViewModelImpl() {
    override fun refresh() {
        viewModelScope.launch {
            fetchGif().collect {
                dataPagimg.value=it
            }
        }
    }

    override val previousActiveButton = MutableLiveData<Boolean>()

    override val linearOrGrid = MutableLiveData<Boolean>()

    override val dataParams = MutableLiveData<GifParams>()

    override val dataPagimg= MutableLiveData<PagingData<Data>>()





    var actualData: List<GifData>?=null

    private var _searchData: String?

    var start: Boolean

    val saveGifs = database.getAllGifData()

    private var viewModelJob = Job()
    var limit: Int = 15

    private var _offsetData = 0


    init {
        start=false
        _searchData = "A"
        previousActiveButton.value = false
    }


    fun fetchGif(): Flow<PagingData<Data>> {
        return Pager(PagingConfig(pageSize = 20, enablePlaceholders = false))
        { PagingSourceGif(_searchData!!, actualData, database) }
            .flow
            .cachedIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


    fun searchNewData(data: String) {
        _searchData = data
        _offsetData = 0
        previousActiveButton.value = false
    }


    fun onLinearOrGrid(set: Boolean) {
        linearOrGrid.value = set
    }


}