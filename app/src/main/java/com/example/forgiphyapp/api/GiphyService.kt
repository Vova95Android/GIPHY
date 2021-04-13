package com.example.forgiphyapp.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://api.giphy.com/v1/gifs/"


private val moshi= Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit= Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

interface GiphyService {
    @GET("search")
    fun getGifList(
        @Query("api_key")key: String,
        @Query("q")search: String,
        @Query("limit")limit: Int,
        @Query("offset")offset: Int,
        @Query("rating")rating: String,
        @Query("lang")lang: String
    ): Deferred<GifParams>
}
object GiphyAPI{
    val retrofitService: GiphyService by lazy {
        retrofit.create(GiphyService::class.java)
    }
}