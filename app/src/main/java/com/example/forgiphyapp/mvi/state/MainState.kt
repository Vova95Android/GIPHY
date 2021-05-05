package com.example.forgiphyapp.mvi.state

import androidx.lifecycle.MutableLiveData
import com.example.forgiphyapp.database.GifData

data class MainState(
    val isLoading: Boolean = false,
    val data: List<GifData> = listOf(),
    val error: List<GifData>? = null
){
    var likeGif = false
    var search = "B"
    var newData = listOf<GifData>()
    var previousActiveButton = false
}
