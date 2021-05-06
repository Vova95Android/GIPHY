package com.example.forgiphyapp.mvi.state

import com.example.forgiphyapp.database.GifData

data class ErrorState(
    val errorMessage: String = "",
    val offlineData: List<GifData> = emptyList()
)
