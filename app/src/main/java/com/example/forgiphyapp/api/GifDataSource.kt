package com.example.forgiphyapp.api


interface GifDataSource {
    suspend fun getGif(searchData: String, limit: Int, offsetData: Int): GifParams
}

class GifDataSourceImpl(private val api: GiphyService) : GifDataSource {

    private val apiKey = "N8ddDH1PCkpXqWiwiprA3ghbUz7bRC3J"

    override suspend fun getGif(searchData: String, limit: Int, offsetData: Int): GifParams {
        val getProperties = api.getGifListAsync(
            apiKey,
            searchData,
            limit,
            offsetData,
            "g",
            "en"
        )
        return getProperties.await()
    }
}