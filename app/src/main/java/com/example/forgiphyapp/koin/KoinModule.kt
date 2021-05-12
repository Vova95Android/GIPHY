package com.example.forgiphyapp.koin

import androidx.room.Room
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkRequest
import com.example.forgiphyapp.api.GifDataSource
import com.example.forgiphyapp.api.GifDataSourceImpl
import com.example.forgiphyapp.api.GiphyService
import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.database.GifDatabase
import com.example.forgiphyapp.database.GifDatabaseDao
import com.example.forgiphyapp.navigation.Router
import com.example.forgiphyapp.navigation.RouterImpl
import com.example.forgiphyapp.pagingApi.PagingSourceGif
import com.example.forgiphyapp.pagingApi.PagingSourceGifImpl
import com.example.forgiphyapp.useCases.*
import com.example.forgiphyapp.viewModels.*
import com.example.forgiphyapp.workManager.ClearDbWork
import com.example.forgiphyapp.workManager.Notification
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit


val appModule = module {


    single<GiphyService> {
        val baseUrl = "https://api.giphy.com/v1/gifs/"

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

    factory<WorkRequest> {
        PeriodicWorkRequestBuilder<ClearDbWork>(20, TimeUnit.MINUTES)
            .setInitialDelay(10, TimeUnit.MINUTES)
            .build()
    }

    single {
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

    factory<PagingSourceGif> { PagingSourceGifImpl(get(), get()) }

//        single<PagingSourceGif> { PagingSourceTest(get()) }
//        single<GifRepository> {GifRepositoryTest(get(),get())  }
//        single <GifDatabaseDao> { DatabaseTest() }


    factory { Notification(androidContext()) }

//    viewModel<GifListViewModel> { GifListViewModelImpl(get(), get(), get(), get(), get()) }
//
//    viewModel<GifDetailViewModel> { (gifData: GifData) ->
//        GifDetailViewModelImpl(get(), get(), gifData, get(), get())
//    }

    viewModel<GifListViewModel>{GifListViewModelImpl(get(), get(), get(), get(), get(), get()) }

    viewModel<GifDetailViewModel>{(gifData: GifData) ->
        GifDetailViewModelImpl(get(), get(), get(), gifData, get(), get())}

    single<Router> { RouterImpl() }

    factory<LoadGifUseCase> { LoadGifUseCaseImpl(get(), get()) }

    factory<GifDataSource> { GifDataSourceImpl(get()) }

    factory<RemoveGifUseCase> { RemoveGifUseCaseImpl(get()) }

    factory<LikeGifUseCase> { LikeGifUseCaseImpl(get()) }

    factory<OfflineGifUseCase> { OfflineGifUseCaseImpl(get()) }

    single { RemoveGif() }

    single { LikeGif() }


}