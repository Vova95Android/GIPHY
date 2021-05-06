package com.example.forgiphyapp.useCases

import com.example.forgiphyapp.database.GifData
import kotlinx.coroutines.flow.MutableSharedFlow

data class LikeGif(var data: MutableSharedFlow<GifData> = MutableSharedFlow())