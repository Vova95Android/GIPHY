package com.example.forgiphyapp.api

class GifParams(val data: List<Data>)

class Data(
        val id: String, val images: Images
)

class Images(val original: Original, val preview_gif: Original)
class Original(val url: String)