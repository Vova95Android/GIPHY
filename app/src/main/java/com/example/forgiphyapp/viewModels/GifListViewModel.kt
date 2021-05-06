package com.example.forgiphyapp.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.mvi.state.ErrorState
import com.example.forgiphyapp.mvi.state.GifListState
import com.example.forgiphyapp.repository.GifRepository
import com.example.forgiphyapp.repository.LikeGif
import com.example.forgiphyapp.repository.RemoveGif
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


abstract class GifListViewModel : ViewModel() {

    abstract val state: StateFlow<GifListState>

    abstract fun searchNewData(data: String)

    abstract fun refresh()

    abstract fun linearOrGrid(set: Boolean)

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

    override val state = MutableStateFlow(GifListState(isLoading = true))


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
                    val temp = state.value.data.map { gif ->
                        if (gif.id != data.id) {
                            gif
                        } else {
                            data
                        }
                    }
                    state.value = state.value.copy(data = temp)
                } else if (state.value.error.errorMessage.isNotEmpty()) {
                    state.value = newState(isLoading = true)
                    val temp = state.value.error.offlineData.map { gif ->
                        if (gif.id != data.id) gif
                        else data
                    }
                    state.value = newState(
                        isLoading = false,
                        data = emptyList(),
                        error = ErrorState(state.value.error.errorMessage, temp)
                    )
                }
            }
        }
        jobRemove = viewModelScope.launch {
            removeGifId.data.collect {
                handleAction(needLoader = false)
                removeGifId.data.value = GifData("", "", "", false, false)
            }
        }

        handleAction()
    }


    private fun handleAction(nextPage: Boolean? = null, needLoader: Boolean = true) {
        job?.cancel()
        if (needLoader) state.value = newState(isLoading = true, data = listOf())
        job = viewModelScope.launch {
            val list = repository.getGif(state.value.search, state.value.likeGif, nextPage)
            if (list.isNotEmpty())
                if (list[0].id != "ERROR") {

                    state.value = newState(isLoading = false, data = list)

                } else {
                    state.value = newState(
                        isLoading = false,
                        data = listOf(),
                        error = ErrorState(errorMessage = list[0].full_url!!, list.minus(list[0]))
                    )

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
        repository.likeGif(data.copy(like = !data.like))
        likeGifId.data.emit(data.copy(like = !data.like))
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
        error: ErrorState = state.value.error,
        likeGif: Boolean = state.value.likeGif,
        search: String = state.value.search,
        linearOrGrid: Boolean = state.value.linearOrGrid
    ): GifListState {
        return GifListState(
            isLoading,
            data,
            error,
            likeGif,
            search,
            repository.previousButtonIsActive(),
            linearOrGrid
        )
    }

}