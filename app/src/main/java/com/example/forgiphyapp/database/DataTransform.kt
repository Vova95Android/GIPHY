package com.example.forgiphyapp.database

import com.example.forgiphyapp.api.Data
import com.example.forgiphyapp.api.Images
import com.example.forgiphyapp.api.Original

abstract class DataTransform {
    companion object {
        fun getGifData(dataGif: Data, active: Boolean, like: Boolean): GifData {
            return GifData(
                    dataGif.id,
                    dataGif.images.original.url,
                    dataGif.images.preview_gif.url,
                    active,
                    like
            )
        }

        fun getData(gifData: GifData): Data {
            return Data(
                    gifData.id,
                    Images(
                            Original(gifData.full_url),
                            Original(gifData.preview_url)
                    )
            )
        }
    }
}