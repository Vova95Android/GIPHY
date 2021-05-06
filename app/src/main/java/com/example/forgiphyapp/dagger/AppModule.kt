package com.example.forgiphyapp.dagger

import android.content.Context
import androidx.room.Room
import androidx.work.Constraints
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkRequest
import com.example.forgiphyapp.api.GiphyService
import com.example.forgiphyapp.database.GifDatabase
import com.example.forgiphyapp.database.GifDatabaseDao
import com.example.forgiphyapp.workManager.ClearDbWork
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun getDatabase(context: Context): GifDatabaseDao {
        var INSTANCE: GifDatabase? = null
        synchronized(this) {
            var instance = INSTANCE
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    GifDatabase::class.java,
                    "gif_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
            }
            return instance.gifDatabaseDao
        }
    }

    @Singleton
    @Provides
    fun getApiService(): GiphyService {
        val baseUrl = "https://api.giphy.com/v1/gifs/"

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl(baseUrl)
            .build()
        return retrofit.create(GiphyService::class.java)
    }

    @Provides
    fun getWorker(): WorkRequest {
        val constrain = Constraints.Builder()
            .setRequiresDeviceIdle(true)
            .build()

        return PeriodicWorkRequestBuilder<ClearDbWork>(20, TimeUnit.MINUTES)
            .setInitialDelay(5, TimeUnit.MINUTES)
            .build()
    }
}