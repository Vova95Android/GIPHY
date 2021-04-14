package com.example.forgiphyapp.vievModelsFactory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.forgiphyapp.database.GifDatabaseDao
import com.example.forgiphyapp.viewModels.GifListViewModel

class GifListViewModelFactory(
    private val dataSource: GifDatabaseDao,
    private val application: Application): ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GifListViewModel::class.java)) {
            return GifListViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}