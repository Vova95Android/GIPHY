package com.example.forgiphyapp.koin

import android.content.Context
import androidx.room.Room
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkRequest
import com.example.forgiphyapp.api.GiphyService
import com.example.forgiphyapp.database.GifDatabase
import com.example.forgiphyapp.database.GifDatabaseDao
import com.example.forgiphyapp.pagingApi.PagingSourceGif
import com.example.forgiphyapp.repository.GifRepository
import com.example.forgiphyapp.vievModelsFactory.GifDetailViewModelFactory
import com.example.forgiphyapp.vievModelsFactory.GifListViewModelFactory
import com.example.forgiphyapp.workManager.ClearDbWork
import com.example.forgiphyapp.workManager.Notification
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit



    val appModule= module {
        single<GifDatabaseDao> {
            var INSTANCE: GifDatabase? = null
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        androidContext(),
                        GifDatabase::class.java,
                        "gif_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return@single instance.gifDatabaseDao
            }
        }
        single<GiphyService> {  val baseUrl = "https://api.giphy.com/v1/gifs/"

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val retrofit = Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .baseUrl(baseUrl)
                .build()
            return@single retrofit.create(GiphyService::class.java)
        }

        factory <WorkRequest>{
            PeriodicWorkRequestBuilder<ClearDbWork>(20, TimeUnit.MINUTES)
                .setInitialDelay(5, TimeUnit.MINUTES)
                .build()
        }

        factory { PagingSourceGif(get(),get()) }

        factory { GifListViewModelFactory(get()) }

        factory { GifDetailViewModelFactory(get()) }

        factory {GifRepository(get(),get())  }

        factory { Notification(androidContext()) }






    }