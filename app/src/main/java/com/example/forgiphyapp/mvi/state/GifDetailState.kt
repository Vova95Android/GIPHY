package com.example.forgiphyapp.mvi.state

import com.example.forgiphyapp.database.GifData

data class GifDetailState(
    val gifData: GifData,
    val errorGif: String = ""
)
