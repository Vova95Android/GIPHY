package com.example.forgiphyapp.mvi.state

import androidx.lifecycle.LiveData
import com.example.forgiphyapp.database.GifData

data class MainState(
    val isLoading: Boolean = false,
    val data: List<GifData> = listOf(),
    val error: List<GifData>? = null,
    val likeGif: Boolean = false,
    val search: String = "B",
    val newData: List<GifData> = listOf(),
    val previousActiveButton: Boolean = false,
    val linearOrGrid: Boolean = false,
    val savedGifLiveData: LiveData<List<GifData>>
)
