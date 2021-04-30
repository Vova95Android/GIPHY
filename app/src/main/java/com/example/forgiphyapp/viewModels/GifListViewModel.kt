package com.example.forgiphyapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.mvi.state.MainState
import com.example.forgiphyapp.repository.GifRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


abstract class GifListViewModel : ViewModel() {

    abstract val linearOrGridLiveData: LiveData<Boolean>

    abstract val dataPagingLiveData: LiveData<PagingData<GifData>>

    abstract val savedGifLiveData: LiveData<List<GifData>>

    abstract fun searchNewData(data: String)

    abstract fun refresh()

    abstract fun linearOrGrid(set: Boolean)

    abstract fun newDataOrRefresh()

    abstract val state: MutableStateFlow<MainState>

    abstract var newData: List<GifData>

    abstract fun getLikeGif()
}

class GifListViewModelImpl(private val repository: GifRepository) :
    GifListViewModel() {

    override val state = MutableStateFlow<MainState>(MainState.Loading)

    override val linearOrGridLiveData = MutableLiveData<Boolean>()

    override val dataPagingLiveData: LiveData<PagingData<GifData>>
        get() = repository.dataPagingLiveData

    private var searchData = "A"
    private var likeGif = false

    override val savedGifLiveData: LiveData<List<GifData>>
        get() = repository.savedGifLiveData


    override var newData = listOf<GifData>()

    override fun refresh() {
        handleAction()
    }

    override fun searchNewData(data: String) {
        searchData = data
        newDataOrRefresh()
    }

    override fun linearOrGrid(set: Boolean) {
        linearOrGridLiveData.value = set
    }

    private var search = "B"

    override fun newDataOrRefresh() {

        var needRefresh = false
        if ((!repository.actualData.isNullOrEmpty()) && (!newData.isNullOrEmpty()) && (searchData == search)) {
            for (dataPos in repository.actualData!!.indices) {
                if ((newData[dataPos].active != repository.actualData!![dataPos].active) || (newData[dataPos].like != repository.actualData!![dataPos].like))
                    needRefresh = true
            }
            repository.actualData = newData
        } else {
            repository.actualData = newData
            handleAction()
        }
        if (needRefresh) {
            handleAction()
        }
        search = searchData
    }

    init {
        newDataOrRefresh()
    }

    private var job: Job? = null

    private fun handleAction() {
        job?.cancel()
        job = viewModelScope.launch {
            fetchGif()
        }
    }

    override fun getLikeGif() {
        job?.cancel()
        job = viewModelScope.launch {
            likeGif = !likeGif
            repository.getGif(searchData, viewModelScope, likeGif)
        }
    }

    private suspend fun fetchGif() {
        state.value = MainState.Loading
        try {
            repository.getGif(searchData, viewModelScope, false)
        } catch (e: Exception) {
            state.value = MainState.Error
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}