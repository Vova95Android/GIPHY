package com.example.forgiphyapp.dagger

import android.content.Context
import com.example.forgiphyapp.api.GiphyAPI
import com.example.forgiphyapp.database.GifDatabase
import com.example.forgiphyapp.database.GifDatabaseDao
import com.example.forgiphyapp.pagingApi.PagingSourceGif
import com.example.forgiphyapp.vievModelsFactory.GifDetailViewModelFactory
import com.example.forgiphyapp.vievModelsFactory.GifListViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class Module {

    @Provides
    fun getDatabase(context: Context): GifDatabaseDao {
        return GifDatabase.getInstance(context).gifDatabaseDao
    }

    @Provides
    fun getViewModelFactory(databaseDao: GifDatabaseDao, pagingSource: PagingSourceGif): GifListViewModelFactory {
        return GifListViewModelFactory(databaseDao, pagingSource)
    }

    @Provides
    fun getViewModelDetailFactory(databaseDao: GifDatabaseDao): GifDetailViewModelFactory {
        return GifDetailViewModelFactory(databaseDao)
    }

    @Provides
    fun getPagingSorce(databaseDao: GifDatabaseDao, api: GiphyAPI): PagingSourceGif{
        return PagingSourceGif(databaseDao,api)
    }

}