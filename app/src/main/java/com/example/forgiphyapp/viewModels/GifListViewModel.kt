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


abstract class GifListViewModelImpl : ViewModel() {


    abstract val linearOrGrid: LiveData<Boolean>

    abstract val dataParams: LiveData<GifParams>

    abstract val dataPagimg: LiveData<PagingData<Data>>
}

class GifListViewModel(val database: GifDatabaseDao) : GifListViewModelImpl() {

    override val linearOrGrid = MutableLiveData<Boolean>()

    override val dataParams = MutableLiveData<GifParams>()

    override val dataPagimg = MutableLiveData<PagingData<Data>>()

    var actualData: List<GifData>? = null

    private var _searchData: String?

    val saveGifs = database.getAllGifData()


    init {
        _searchData = "A"
    }

    fun refresh() {
        viewModelScope.launch {
            fetchGif().collect {
                dataPagimg.value = it
            }
        }
    }

    fun fetchGif(): Flow<PagingData<Data>> {
        return Pager(PagingConfig(pageSize = 20, enablePlaceholders = true))
        { PagingSourceGif(_searchData!!, actualData, database) }
            .flow
            .cachedIn(viewModelScope)
    }

    fun searchNewData(data: String) {
        _searchData = data
    }

    fun onLinearOrGrid(set: Boolean) {
        linearOrGrid.value = set
    }

}