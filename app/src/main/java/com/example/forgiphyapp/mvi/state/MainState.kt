package com.example.forgiphyapp.mvi.state

import com.example.forgiphyapp.database.GifData

data class MainState(
    val isLoading: Boolean = false,
    val data: List<GifData> = listOf(),
    val error: List<GifData>? = null
)
