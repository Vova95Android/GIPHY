package com.example.forgiphyapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GifDetailViewModel : ViewModel() {
    private val _url=MutableLiveData<String>()
    val url: LiveData<String>
        get() = _url

    fun setUrl(url: String){
        _url.value=url
    }
}