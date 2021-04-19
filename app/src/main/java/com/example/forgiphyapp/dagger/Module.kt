package com.example.forgiphyapp.dagger

import android.content.Context
import com.example.forgiphyapp.database.GifDatabase
import com.example.forgiphyapp.database.GifDatabaseDao
import com.example.forgiphyapp.vievModelsFactory.GifListViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class Module {

    @Provides
    fun getDatabase(context: Context): GifDatabaseDao{
        return GifDatabase.getInstance(context).gifDatabaseDao
    }

    @Provides
    fun getViewModelFactory(databaseDao: GifDatabaseDao): GifListViewModelFactory {
        return GifListViewModelFactory(databaseDao)
    }

}