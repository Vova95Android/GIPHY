package com.example.forgiphyapp.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.mvi.state.MainState
import com.example.forgiphyapp.repository.GifRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


abstract class GifListViewModel : ViewModel() {

    abstract val linearOrGridLiveData: LiveData<Boolean>

    abstract val savedGifLiveData: LiveData<List<GifData>>

    abstract var previousActiveButton: MutableLiveData<Boolean>

    abstract fun searchNewData(data: String)

    abstract fun refresh()

    abstract fun linearOrGrid(set: Boolean)

    abstract fun newDataOrRefresh()

    abstract val state: MutableStateFlow<MainState>

    abstract var newData: List<GifData>

    abstract fun getLikeGif()

    abstract fun nextPage()

    abstract fun previousPage()


}

class GifListViewModelImpl(private val repository: GifRepository) :
    GifListViewModel() {

    override val state = MutableStateFlow(MainState())

    override val linearOrGridLiveData = MutableLiveData<Boolean>()

    private var searchData = "H"
    private var likeGif = false

    override val savedGifLiveData: LiveData<List<GifData>>
        get() = repository.savedGifLiveData

    override var previousActiveButton = MutableLiveData(false)

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
            handleAction(needLoad = false)
        }
        search = searchData
    }

    init {
        newDataOrRefresh()
    }

    private var job: Job? = null

    private fun handleAction(
        nextPage: Boolean? = null,
        likeGif: Boolean = false,
        needLoad: Boolean = true
    ) {
        job?.cancel()
        state.value = MainState(isLoading = needLoad)
        job = viewModelScope.launch {
            try {
                state.value = MainState(data = repository.getGif(searchData, likeGif, nextPage))
                previousActiveButton.value = repository.previousButtonIsActive()
                delay(500)
            } catch (e: Exception) {
                state.value = MainState(error = e.message)
            }
        }
    }

    override fun getLikeGif() {
        likeGif = !likeGif
        handleAction(likeGif = likeGif)
    }

    override fun nextPage() {
        handleAction(true)
    }

    override fun previousPage() {
        handleAction(false)
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}