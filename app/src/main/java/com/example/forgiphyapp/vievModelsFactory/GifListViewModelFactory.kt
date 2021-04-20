package com.example.forgiphyapp.vievModelsFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.forgiphyapp.database.GifDatabaseDao
import com.example.forgiphyapp.pagingApi.PagingSourceGif
import com.example.forgiphyapp.viewModels.GifListViewModel
import com.example.forgiphyapp.viewModels.GifListViewModelImpl

class GifListViewModelFactory(
    private val dataSource: GifDatabaseDao,
    var pagingSource: PagingSourceGif
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GifListViewModelImpl::class.java)) {
            return GifListViewModelImpl(dataSource,pagingSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}