package com.example.forgiphyapp.viewModels

import androidx.lifecycle.*
import androidx.paging.PagingData
import com.example.forgiphyapp.api.*
import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.database.GifDatabaseDao
import com.example.forgiphyapp.repository.GifRepository


abstract class GifListViewModel : ViewModel() {

    abstract val linearOrGridLiveData: LiveData<Boolean>

    abstract val dataPagingLiveData: LiveData<PagingData<Data>>
}

class GifListViewModelImpl(private val repository: GifRepository) :
    GifListViewModel() {

    override val linearOrGridLiveData = MutableLiveData<Boolean>()

    override val dataPagingLiveData: LiveData<PagingData<Data>>
        get() = repository.dataPagingLiveData

    private var searchData = "A"

    val savedGifLiveData: LiveData<List<GifData>>
        get() = repository.savedGifLiveData


    fun refresh() {
        repository.newDataOrRefresh(searchData,viewModelScope)
    }

    fun searchNewData(data: String) {
        searchData = data
        repository.newDataOrRefresh(searchData,viewModelScope)
    }

    fun linearOrGrid(set: Boolean) {
        linearOrGridLiveData.value = set
    }

    fun newDataOrRefresh() {
        repository.newDataOrRefresh(searchData,viewModelScope)
    }

}