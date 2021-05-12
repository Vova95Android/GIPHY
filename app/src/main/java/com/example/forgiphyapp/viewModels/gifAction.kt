package com.example.forgiphyapp.viewModels

import com.example.forgiphyapp.database.GifData

sealed class GifAction {
    class getLikeGif(
        val nextPage: Boolean?=null
    ): GifAction()
    class searchGif(
        val search: String="",
        val nextPage: Boolean?=null
    ): GifAction()
    object refresh: GifAction()
    class likeGif(val data: GifData): GifAction()
}