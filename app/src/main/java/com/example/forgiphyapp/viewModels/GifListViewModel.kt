package com.example.forgiphyapp.viewModels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.forgiphyapp.api.*
import com.example.forgiphyapp.database.DataTransform
import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.database.GifDatabaseDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class GifListViewModelImpl : ViewModel() {

    abstract val previousActiveButton: LiveData<Boolean>

    abstract val linearOrGrid: LiveData<Boolean>

    abstract val DataParams: LiveData<GifParams>
}

class GifListViewModel(
    val database: GifDatabaseDao,
    application: Application
) : GifListViewModelImpl() {

    override val previousActiveButton = MutableLiveData<Boolean>()

    override val linearOrGrid = MutableLiveData<Boolean>()

    override val DataParams = MutableLiveData<GifParams>()

    lateinit var actualData: List<GifData>

    private var _SearchData: String

    val saveGifs = database.getAllGifData()


    val databaseNotEmpty = Transformations.map(saveGifs) {
        it?.isNotEmpty()
    }

    lateinit var api_key: String
    private var viewModelJob = Job()
    private val corutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)
    var limit: Int = 30

    private var _offsetData = 0

    init {
        _SearchData = "a"
        previousActiveButton.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun searchNewData(data: String) {
        _SearchData = data
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
                _SearchData,
                limit,
                _offsetData,
                "g",
                "en"
            )
            try {
                val listResult = getPropetiesDeferred.await()
                for (i in 0..listResult.data.size) {
                    if ((!actualData.isNullOrEmpty()) &&
                        (actualData.contains(DataTransform().getGifData(listResult.data[i], false)))
                    ) {
                        println("data minus")
                        listResult.data.minus(listResult.data[i])
                    }
                }
                DataParams.value = listResult
            } catch (t: Throwable) {
            }
        }
    }
}