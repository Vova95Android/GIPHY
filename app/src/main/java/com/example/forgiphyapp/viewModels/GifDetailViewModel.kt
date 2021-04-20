package com.example.forgiphyapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.database.GifDatabaseDao
import kotlinx.coroutines.launch

abstract class GifDetailViewModelImpl : ViewModel() {
    abstract val urlLiveData: LiveData<String>
    abstract val removeGifLiveData: LiveData<Boolean>
}

class GifDetailViewModel(
    val database: GifDatabaseDao
) : GifDetailViewModelImpl() {
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
}