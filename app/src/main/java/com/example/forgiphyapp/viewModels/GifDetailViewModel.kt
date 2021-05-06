package com.example.forgiphyapp.viewModels

import android.util.Log
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.forgiphyapp.R
import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.database.GifDatabaseDao
import com.example.forgiphyapp.mvi.state.GifDetailState
import com.example.forgiphyapp.mvi.state.GifListState
import com.example.forgiphyapp.repository.GifRepository
import com.example.forgiphyapp.repository.LikeGif
import com.example.forgiphyapp.repository.RemoveGif
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

abstract class GifDetailViewModel : ViewModel() {
    abstract val state: StateFlow<GifDetailState>
    abstract fun removeGif()
    abstract fun setGifToScreen(img: ImageView)
    abstract fun likeGif()
}

class GifDetailViewModelImpl(
    private val repository: GifRepository,
    data: GifData,
    private val likeGifId: LikeGif,
    private val removeGifId: RemoveGif
) : GifDetailViewModel() {

    override val state = MutableStateFlow(GifDetailState(data))


    override fun removeGif() {
        viewModelScope.launch {
            val data = state.value.gifData.copy(active = false)
            repository.removeGif(data)
            removeGifId.data.value = state.value.gifData
            state.value = state.value.copy(gifData = data)
            cancel()
        }
    }

    override fun likeGif() {
        viewModelScope.launch {
            try {
                delay(500)
                val data = state.value.gifData.copy(like = !state.value.gifData.like)
                repository.likeGif(data)
                likeGifId.data.emit(data)
                state.value = state.value.copy(gifData = data)
            } catch (e: Exception) {
                e.message?.let { state.value = state.value.copy(errorGif = e.message!!) }
            }

            cancel()
        }
    }


    override fun setGifToScreen(img: ImageView) {
        state.value.gifData.full_url?.let {
            val imgUri = it.toUri().buildUpon().scheme("https").build()
            Glide.with(img.context)
                .load(imgUri)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image)
                )
                .into(img)
        }
    }
}