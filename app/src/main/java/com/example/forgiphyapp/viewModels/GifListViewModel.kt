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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


abstract class GifListViewModel : ViewModel() {

    abstract val state: StateFlow<MainState>

    abstract fun searchNewData(data: String)

    abstract fun refresh()

    abstract fun linearOrGrid(set: Boolean)

    abstract fun newDataOrRefresh(searchData: String)

    abstract fun newDataFromDatabase(listData: List<GifData>)

    abstract fun getLikeGif()

    abstract fun nextPage()

    abstract fun previousPage()


}

class GifListViewModelImpl(private val repository: GifRepository) :
    GifListViewModel() {

    override val state = MutableStateFlow(
        MainState(
            isLoading = true,
            savedGifLiveData = repository.savedGifLiveData
        )
    )

    override fun newDataFromDatabase(listData: List<GifData>) {
        state.value = newState(newData = listData)
    }

    override fun refresh() {
        handleAction()
    }

    override fun searchNewData(data: String) {
        newDataOrRefresh(data)
    }

    override fun linearOrGrid(set: Boolean) {
        state.value = newState(linearOrGrid = set)
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
        state.value = newState(search = searchData)
        repository.actualData = state.value.newData
        if (needRefresh) {
            handleAction()
        }
    }

    init {
        newDataOrRefresh("H")
    }

    private var job: Job? = null

    private fun handleAction(nextPage: Boolean? = null) {
        job?.cancel()
        state.value = newState(isLoading = true, data = listOf(), error = listOf())

        job = viewModelScope.launch {
            val list = repository.getGif(state.value.search, state.value.likeGif, nextPage)
            if (list[0].id != "ERROR") {

                state.value = newState(isLoading = false, data = list, error = listOf())

            } else {
                state.value = newState(isLoading = false, data = listOf(), error = list)

            }
            delay(500)
        }
    }

    override fun getLikeGif() {
        state.value = newState(likeGif = !state.value.likeGif)
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

    private fun newState(
        isLoading: Boolean = state.value.isLoading,
        data: List<GifData> = state.value.data,
        error: List<GifData>? = state.value.error,
        likeGif: Boolean = state.value.likeGif,
        search: String = state.value.search,
        newData: List<GifData> = state.value.newData,
        linearOrGrid: Boolean = state.value.linearOrGrid,
        savedGifLiveData: LiveData<List<GifData>> = state.value.savedGifLiveData
    ): MainState {
        return MainState(
            isLoading,
            data,
            error,
            likeGif,
            search,
            newData,
            repository.previousButtonIsActive(),
            linearOrGrid,
            savedGifLiveData
        )
    }

}