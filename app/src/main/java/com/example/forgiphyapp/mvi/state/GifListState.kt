package com.example.forgiphyapp.mvi.state

import com.example.forgiphyapp.database.GifData

data class GifListState(
    val isLoading: Boolean = false,
    val data: List<GifData> = listOf(),
    val error: ErrorState = ErrorState(),
    val likeGif: Boolean = false,
    val search: String = "B",
    val previousActiveButton: Boolean = false,
    val nextActiveButton: Boolean = true,
    val linearOrGrid: Boolean = false,
    val onlyLikeOrRemove: Boolean = false
)
