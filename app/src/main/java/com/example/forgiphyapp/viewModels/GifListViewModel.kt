package com.example.forgiphyapp.viewModels

import android.util.Log
import androidx.lifecycle.*
import com.example.forgiphyapp.api.*
import com.example.forgiphyapp.database.DataTransform
import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.database.GifDatabaseDao
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


abstract class GifListViewModelImpl : ViewModel() {

    abstract val previousActiveButton: LiveData<Boolean>

    abstract val linearOrGrid: LiveData<Boolean>

    abstract val dataParams: LiveData<GifParams>
}

class GifListViewModel(
    val database: GifDatabaseDao
) : GifListViewModelImpl() {

    override val previousActiveButton = MutableLiveData<Boolean>()

    override val linearOrGrid = MutableLiveData<Boolean>()

    override val dataParams = MutableLiveData<GifParams>()

    lateinit var actualData: List<GifData>

    private var _searchData: String

    val saveGifs = database.getAllGifData()

    val api_key="N8ddDH1PCkpXqWiwiprA3ghbUz7bRC3J"

    private var viewModelJob = Job()
    private val corutineScope = viewModelScope
    var limit: Int = 30

    private var _offsetData = 0

    init {
        _searchData = "a"
        previousActiveButton.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun searchNewData(data: String) {
        _searchData = data
        _offsetData = 0
        previousActiveButton.value = false
    }

    fun setLimits(l: Int) {
        if (limit != l) {
            limit = l
            _offsetData = 0
            previousActiveButton.value = false
            getGiphyRealEstateProperties("g")
        }
    }

    fun nextPage() {
        _offsetData = _offsetData + limit
        previousActiveButton.value = true
        getGiphyRealEstateProperties("g")
    }

    fun previousPage() {
        if (_offsetData >= limit) _offsetData = _offsetData - limit
        if (_offsetData < limit) previousActiveButton.value = false
        getGiphyRealEstateProperties("g")
    }

    fun onLinearOrGrid(set: Boolean) {
        linearOrGrid.value = set
    }


    fun getGiphyRealEstateProperties(rating: String) {
        corutineScope.launch {
            val getPropetiesDeferred = GiphyAPI.retrofitService.getGifList(
                api_key,
                _searchData,
                limit,
                _offsetData,
                "g",
                "en"
            )
            try {
                val listResult = getPropetiesDeferred.await()
                var listDataRemov= listOf<Data>()
                for (i in 0..listResult.data.size-1) {
                    for (z in 0..actualData.size-1) {
                        if ((!actualData.isNullOrEmpty()) &&
                            (actualData[z].id==listResult.data[i].id)
                        ) {
                            Log.i("GifListViewModel", "data minus" + i)
                            listDataRemov = listDataRemov.plus(listResult.data[i])
                        }
                    }
                }
                if(listDataRemov.size>0) listResult.data=listResult.data.minus(listDataRemov)
                dataParams.value = listResult
            } catch (t: Throwable) {
                if (t.message!=null) Log.e("GifListViewModel", t.message!!)
            }
        }
    }
}