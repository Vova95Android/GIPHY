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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

open class BaseGifViewModel: ViewModel(){
    private var mutex = Mutex()

    val state = MutableStateFlow(GifListState(isLoading = true))

    fun mainLaunch(work: suspend CoroutineScope.() -> Unit){
        if (!mutex.isLocked)
        viewModelScope.launch (context = Dispatchers.Main+handler ){
            mutex.withLock {
                work()
            }
            cancel()
        }
    }

    val handler= CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e("BaseGifViewModel", throwable.message!!)
        state.value=state.value.copy(isLoading = false, data = emptyList(), error = ErrorState(throwable.message!!))
    }
}


abstract class GifListViewModel : BaseGifViewModel() {

    //abstract val state: StateFlow<GifListState>

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
    private var jobLike: Job = viewModelScope.launch {
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
                state.value = state.value.copy(isLoading = true)
                val temp = state.value.error.offlineData.map { gif ->
                    if (gif.id != data.id) gif
                    else data
                }
                state.value = state.value.copy(
                    isLoading = false,
                    data = emptyList(),
                    error = ErrorState(state.value.error.errorMessage, temp)
                )
            }
        }
    }
    private var jobRemove: Job
    private var mutex = Mutex()

    init {
        jobRemove = viewModelScope.launch{
            removeGifId.data.collect {
                handleAction(needLoader = false)
            }
        }

        handleAction()
    }

    private fun handleAction(nextPage: Boolean? = null, needLoader: Boolean = true) {


            if (needLoader) state.value = state.value.copy(isLoading = true, data = listOf())
            //job?.cancel()
            mainLaunch {
//                    try {
                        if (state.value.likeGif) state.value = state.value.copy(
                            isLoading = false,
                            data = likeGifUseCase.getGif(nextPage),
                            error = ErrorState(),
                            previousActiveButton = likeGifUseCase.previousButtonIsActive(),
                            nextActiveButton = likeGifUseCase.nextButtonIsActive()
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
                                nextActiveButton = loadGifUseCase.nextButtonIsActive()
                            )
                        }
//                    } catch (e: Exception) {
//                        state.value = state.value.copy(
//                            isLoading = false,
//                            data = emptyList(),
//                            error = ErrorState(e.message!!, offlineGifUseCase.getGif(nextPage)),
//                            previousActiveButton = offlineGifUseCase.previousButtonIsActive(),
//                            nextActiveButton = offlineGifUseCase.nextButtonIsActive()
//                        )
//                    }
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
        viewModelScope.launch {
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