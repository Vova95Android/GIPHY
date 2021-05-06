package com.example.forgiphyapp.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.mvi.state.MainState
import com.example.forgiphyapp.repository.GifRepository
import com.example.forgiphyapp.repository.LikeGif
import com.example.forgiphyapp.repository.RemoveGif
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


abstract class GifListViewModel : ViewModel() {

    abstract val state: StateFlow<MainState>

    abstract fun searchNewData(data: String)

    abstract fun refresh()

    abstract fun linearOrGrid(set: Boolean)

    abstract fun newDataFromDatabase(listData: List<GifData>)

    abstract fun getLikeGif()

    abstract fun nextPage()

    abstract fun previousPage()

    abstract suspend fun likeGif(data: GifData)


}

class GifListViewModelImpl(
    private val repository: GifRepository,
    private val likeGifId: LikeGif,
    private val removeGifId: RemoveGif
) :
    GifListViewModel() {

    override val state = MutableStateFlow(
        MainState(
            isLoading = true
        )
    )


    override fun newDataFromDatabase(listData: List<GifData>) {
        state.value = newState(newData = listData)
    }

    override fun refresh() {
        handleAction()
    }

    override fun searchNewData(data: String) {
        if (data != state.value.search) {
            state.value = newState(search = data)
            handleAction()
        }
    }

    override fun linearOrGrid(set: Boolean) {
        state.value = newState(linearOrGrid = set)
    }

    private var job: Job? = null
    private var jobLike: Job
    private var jobRemove: Job

    init {
        jobLike = viewModelScope.launch {
            likeGifId.data.collect { data ->
                if (state.value.data.isNotEmpty()) {
                    state.value = newState(isLoading = true)
                    Log.e("GifListViewModel", "Like detect")
                    Log.e("GifListViewModel", "id - ${data.id}")
                    val temp = state.value.data.map { gif ->
                        if (gif.id != data.id) gif
                        else data
                    }
                    state.value = newState(isLoading = false, data = temp, error = listOf())
                    likeGifId.data.value = GifData("", "", "", false, false)
                } else if (!state.value.error.isNullOrEmpty()) {
                    state.value = newState(isLoading = true)
                    val temp = state.value.error!!.map { gif ->
                        if (gif.id != data.id) gif
                        else data
                    }
                    state.value = newState(isLoading = false, data = listOf(), error = temp)
                    likeGifId.data.value = GifData("", "", "", false, false)
                }
            }
        }
        jobRemove = viewModelScope.launch {
            removeGifId.data.collect {
                Log.e("GifListViewModel", "Remove detect")
                handleAction(needLoader = false)
                removeGifId.data.value = GifData("", "", "", false, false)
            }
        }

        handleAction()
    }


    private fun handleAction(nextPage: Boolean? = null, needLoader: Boolean = true) {
        job?.cancel()
        if (needLoader) state.value = newState(isLoading = true, data = listOf(), error = listOf())

        job = viewModelScope.launch {
            val list = repository.getGif(state.value.search, state.value.likeGif, nextPage)
            if (list.isNotEmpty())
                if (list[0].id != "ERROR") {

                    state.value = newState(isLoading = false, data = list, error = listOf())

                } else {
                    state.value = newState(isLoading = false, data = listOf(), error = list)

                }
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

    override suspend fun likeGif(data: GifData) {
        repository.likeGif(data)
        likeGifId.data.value = data
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
        jobLike.cancel()
        jobRemove.cancel()
    }

    private fun newState(
        isLoading: Boolean = state.value.isLoading,
        data: List<GifData> = state.value.data,
        error: List<GifData>? = state.value.error,
        likeGif: Boolean = state.value.likeGif,
        search: String = state.value.search,
        newData: List<GifData> = state.value.newData,
        linearOrGrid: Boolean = state.value.linearOrGrid
    ): MainState {
        return MainState(
            isLoading,
            data,
            error,
            likeGif,
            search,
            newData,
            repository.previousButtonIsActive(),
            linearOrGrid
        )
    }

}