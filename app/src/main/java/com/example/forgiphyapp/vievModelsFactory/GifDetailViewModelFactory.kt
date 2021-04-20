package com.example.forgiphyapp.vievModelsFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.forgiphyapp.database.GifDatabaseDao
import com.example.forgiphyapp.viewModels.GifDetailViewModel

class GifDetailViewModelFactory(
    private val dataSource: GifDatabaseDao
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GifDetailViewModel::class.java)) {
            return GifDetailViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}