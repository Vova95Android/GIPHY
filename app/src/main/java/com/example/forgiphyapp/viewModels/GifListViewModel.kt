package com.example.forgiphyapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    abstract fun searchNewData(data: String)

    abstract fun refresh()

    abstract fun linearOrGrid(set: Boolean)

    abstract fun newDataOrRefresh(searchData: String = "H")

    abstract val state: MutableStateFlow<MainState>


    abstract fun getLikeGif()

    abstract fun nextPage()

    abstract fun previousPage()


}

class GifListViewModelImpl(private val repository: GifRepository) :
    GifListViewModel() {

    override val state = MutableStateFlow(MainState(isLoading = true))

    override val linearOrGridLiveData = MutableLiveData<Boolean>()


    override val savedGifLiveData: LiveData<List<GifData>>
        get() = repository.savedGifLiveData

    override fun refresh() {
        handleAction()
    }

    override fun searchNewData(data: String) {
        newDataOrRefresh(data)
    }

    override fun linearOrGrid(set: Boolean) {
        linearOrGridLiveData.value = set
    }


    override fun newDataOrRefresh(searchData: String) {

        var needRefresh = false
        if ((!repository.actualData.isNullOrEmpty()) && (!state.value.newData.isNullOrEmpty()) && (searchData == state.value.search)) {
            for (dataPos in repository.actualData!!.indices) {
                if ((state.value.newData[dataPos].active != repository.actualData!![dataPos].active) || (state.value.newData[dataPos].like != repository.actualData!![dataPos].like))
                    needRefresh = true
            }
        } else {
            needRefresh = true
        }
        repository.actualData = state.value.newData
        if (needRefresh) {
            handleAction()
        }
        state.value.search = searchData
    }

    init {
        newDataOrRefresh()
    }

    private var job: Job? = null

    private fun handleAction(nextPage: Boolean? = null) {
        job?.cancel()

        var value = MainState(isLoading = true)
        value.search = state.value.search
        value.likeGif = state.value.likeGif
        value.newData = state.value.newData
        state.value = value

        job = viewModelScope.launch {
            val list = repository.getGif(state.value.search, state.value.likeGif, nextPage)
            if (list[0].id != "ERROR") {

                value = MainState(data = list)
                value.search = state.value.search
                value.likeGif = state.value.likeGif
                value.newData = state.value.newData
                value.previousActiveButton = repository.previousButtonIsActive()
                state.value = value

            } else {

                value = MainState(error = list)
                value.search = state.value.search
                value.likeGif = state.value.likeGif
                value.newData = state.value.newData
                value.previousActiveButton = repository.previousButtonIsActive()
                state.value = value

            }
            delay(500)
        }
    }

    override fun getLikeGif() {
        state.value.likeGif = !state.value.likeGif
        repository.resetPos()
        handleAction()
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