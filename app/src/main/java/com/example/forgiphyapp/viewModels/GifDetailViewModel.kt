package com.example.forgiphyapp.viewModels

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
import kotlinx.coroutines.launch

abstract class GifDetailViewModel : ViewModel() {
    abstract val urlLiveData: LiveData<String>
    abstract val removeGifLiveData: LiveData<Boolean>
}

class GifDetailViewModelImpl(
        val database: GifDatabaseDao
) : GifDetailViewModel() {
    override val urlLiveData = MutableLiveData<String>()
    override val removeGifLiveData = MutableLiveData<Boolean>()

    private lateinit var data: GifData

    fun setData(id: String, detailUrl: String, prewUrl: String?) {
        urlLiveData.value = detailUrl
        data = GifData(id, detailUrl, prewUrl, true)
    }

    fun navigateOk() {
        removeGifLiveData.value = false
    }

    fun onRemoveGif() {
        viewModelScope.launch {
            data.active = false
            database.update(data)
            removeGifLiveData.value = true
        }
    }

    fun setGifToScreen(img: ImageView, url: String) {
        url.let {
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