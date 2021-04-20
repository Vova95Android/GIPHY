package com.example.forgiphyapp.api

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query


interface GiphyService {
    @GET("search")
    fun getGifList(
        @Query("api_key") key: String,
        @Query("q") search: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("rating") rating: String,
        @Query("lang") lang: String
    ): Deferred<GifParams>
}

