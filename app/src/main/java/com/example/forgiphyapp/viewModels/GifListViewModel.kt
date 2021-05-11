package com.example.forgiphyapp.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.mvi.state.ErrorState
import com.example.forgiphyapp.mvi.state.GifListState
import com.example.forgiphyapp.useCases.LikeGif
import com.example.forgiphyapp.useCases.RemoveGif
import com.example.forgiphyapp.useCases.LikeGifUseCase
import com.example.forgiphyapp.useCases.LoadGifUseCase
import com.example.forgiphyapp.useCases.OfflineGifUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect

abstract class BaseViewModel : ViewModel() {

    val errorState = MutableStateFlow("")

    fun launch(work: suspend CoroutineScope.() -> Unit): Job {
        return viewModelScope.launch(handler) {
            work()
        }
    }

    private val handler = CoroutineExceptionHandler { _, throwable ->
        Log.e("BaseViewModel", throwable.message!!)
        errorState.value = throwable.message!!
    }
}


abstract class GifListViewModel : BaseViewModel() {

    abstract var lastLinearOrGridState: Boolean

    abstract val state: StateFlow<GifListState>

    abstract fun searchNewData(data: String)

    abstract fun refresh()

    abstract fun linearOrGrid(set: Boolean)

    abstract fun getLikeGif()

    abstract fun nextPage()

    abstract fun previousPage()

    abstract fun likeGif(data: GifData)

}

class GifListViewModelImpl(
    private val likeGifUseCase: LikeGifUseCase,
    private val loadGifUseCase: LoadGifUseCase,
    private val offlineGifUseCase: OfflineGifUseCase,
    private val likeGifId: LikeGif,
    private val removeGifId: RemoveGif
) :
    GifListViewModel() {

    override var lastLinearOrGridState: Boolean = false

    override val state = MutableStateFlow(GifListState(isLoading = true))

    override fun refresh() {
        handleAction()
    }

    override fun searchNewData(data: String) {
        if (data != state.value.search) {
            state.value = state.value.copy(search = data)
            handleAction()
        }
    }

    override fun linearOrGrid(set: Boolean) {
        state.value = state.value.copy(linearOrGrid = set)
    }

    private var job: Job? = null
    private var jobLike: Job = launch {
        likeGifId.data.collect { data ->
            if (state.value.data.isNotEmpty()) {
                val temp = state.value.data.map { gif ->
                    if (gif.id != data.id) {
                        gif
                    } else {
                        data
                    }
                }
                state.value = state.value.copy(
                    data = temp,
                    onlyLikeOrRemove = true
                )
            } else if (state.value.error.errorMessage.isNotEmpty()) {
                state.value = state.value.copy(isLoading = true)
                val temp = state.value.error.offlineData.map { gif ->
                    if (gif.id != data.id) gif
                    else data
                }
                state.value = state.value.copy(
                    isLoading = false,
                    data = emptyList(),
                    error = ErrorState(state.value.error.errorMessage, temp),
                    onlyLikeOrRemove = true
                )
            }
        }
    }
    private var jobRemove: Job
    private var nextPage: Boolean? = null

    init {
        jobRemove = launch {
            removeGifId.data.collect {
                handleAction(needLoader = false)
            }
        }

        launch {
            errorState.collect { error ->
                if (error.isNotEmpty()) {
                    state.value = state.value.copy(
                        isLoading = false,
                        data = emptyList(),
                        error = ErrorState(error, offlineGifUseCase.getGif(nextPage)),
                        previousActiveButton = offlineGifUseCase.previousButtonIsActive(),
                        nextActiveButton = offlineGifUseCase.nextButtonIsActive(),
                        onlyLikeOrRemove = false
                    )
                    errorState.value = ""
                }
            }
        }

        handleAction()
    }

    private fun handleAction(nextPage: Boolean? = null, needLoader: Boolean = true) {

        this.nextPage = nextPage

        if (needLoader) state.value = state.value.copy(isLoading = true, data = listOf())
        job?.cancel()
        job = launch {
            if (state.value.likeGif) state.value = state.value.copy(
                isLoading = false,
                data = likeGifUseCase.getGif(nextPage),
                error = ErrorState(),
                previousActiveButton = likeGifUseCase.previousButtonIsActive(),
                nextActiveButton = likeGifUseCase.nextButtonIsActive(),
                onlyLikeOrRemove = false
            )
            else {
                state.value = state.value.copy(
                    isLoading = false,
                    data = loadGifUseCase.getGif(
                        state.value
                            .search, nextPage
                    ),
                    error = ErrorState(),
                    previousActiveButton = loadGifUseCase.previousButtonIsActive(),
                    nextActiveButton = loadGifUseCase.nextButtonIsActive(),
                    onlyLikeOrRemove = false
                )
            }
        }

    }

    override fun getLikeGif() {
        state.value = state.value.copy(likeGif = !state.value.likeGif)
        handleAction()
    }

    override fun nextPage() {
        handleAction(true)
    }

    override fun previousPage() {
        handleAction(false)
    }

    override fun likeGif(data: GifData) {
        launch {
            likeGifUseCase.likeGif(data.copy(like = !data.like))
            likeGifId.data.emit(data.copy(like = !data.like))
            cancel()
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
        jobLike.cancel()
        jobRemove.cancel()
    }

}