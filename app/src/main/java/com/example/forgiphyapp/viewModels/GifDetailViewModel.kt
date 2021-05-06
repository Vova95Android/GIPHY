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
import com.example.forgiphyapp.repository.GifRepository
import com.example.forgiphyapp.repository.LikeGif
import com.example.forgiphyapp.repository.RemoveGif
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception

abstract class GifDetailViewModel : ViewModel() {
    abstract val urlLiveData: LiveData<String>
    abstract val removeGifLiveData: LiveData<Boolean>
    abstract val likeGifLiveData: LiveData<Boolean>
    abstract val errorLikeGifLiveData: LiveData<String?>
    abstract var dataTemp: GifData
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
    override var dataTemp = data
    override val urlLiveData = MutableLiveData<String>()
    override val removeGifLiveData = MutableLiveData<Boolean>()
    override val likeGifLiveData = MutableLiveData(dataTemp.like)
    override val errorLikeGifLiveData = MutableLiveData<String?>(null)


    override fun removeGif() {
        viewModelScope.launch {
            dataTemp.active = false
            repository.removeGif(dataTemp)
            removeGifId.data.value = dataTemp
            removeGifLiveData.value = true
            cancel()
        }
    }

    override fun likeGif() {
        viewModelScope.launch {
            try {
                delay(500)
                dataTemp.like = !dataTemp.like
                repository.likeGif(dataTemp)
                likeGifLiveData.value = dataTemp.like
                likeGifId.data.value = dataTemp
            } catch (e: Exception) {
                errorLikeGifLiveData.value = e.message
            }

            cancel()
        }
    }


    override fun setGifToScreen(img: ImageView) {
        dataTemp.full_url?.let {
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