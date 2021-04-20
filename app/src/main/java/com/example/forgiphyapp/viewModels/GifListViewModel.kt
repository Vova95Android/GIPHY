package com.example.forgiphyapp.viewModels

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.forgiphyapp.api.*
import com.example.forgiphyapp.dagger.App
import com.example.forgiphyapp.dagger.ApplicationGraph
import com.example.forgiphyapp.dagger.DaggerApplicationGraph
import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.database.GifDatabaseDao
import com.example.forgiphyapp.pagingApi.PagingSourceGif
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


abstract class GifListViewModel : ViewModel() {


    abstract val linearOrGrid: LiveData<Boolean>

    abstract val dataParams: LiveData<GifParams>

    abstract val dataPagimg: LiveData<PagingData<Data>>
}

class GifListViewModelImpl(val database: GifDatabaseDao, val pagingSource: PagingSourceGif) : GifListViewModel() {

    override val linearOrGrid = MutableLiveData<Boolean>()

    override val dataParams = MutableLiveData<GifParams>()

    override val dataPagimg = MutableLiveData<PagingData<Data>>()

    var actualData: List<GifData>? = null

    var _searchData="A"

    val saveGifs = database.getAllGifData()

    var job: Job?=null


    fun refresh() {
        job?.cancel()
        job=viewModelScope.launch {
            fetchGif().collect {
                dataPagimg.value = it
            }
        }
    }

    fun fetchGif(): Flow<PagingData<Data>> {
        pagingSource.actualData=actualData
        pagingSource.searchData=_searchData
        pagingSource.cleare()
        return Pager(PagingConfig(pageSize = 20, enablePlaceholders = true))
        { pagingSource }
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