package com.example.forgiphyapp.repository

import com.example.forgiphyapp.database.GifData
import kotlinx.coroutines.flow.MutableStateFlow

data class LikeGif (var data: MutableStateFlow<GifData> = MutableStateFlow(GifData("","","",true,false)))