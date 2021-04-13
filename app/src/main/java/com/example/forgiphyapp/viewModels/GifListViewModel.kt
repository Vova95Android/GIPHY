package com.example.forgiphyapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.forgiphyapp.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class GifListViewModel: ViewModel() {

    lateinit var api_key:String
    private var viewModelJob= Job()
    private val corutineScope= CoroutineScope(viewModelJob+ Dispatchers.Main)
    var limit: Int=30

    private var _offsetData=0

    private val _previousActiveButton=MutableLiveData<Boolean>()
    val previousActiveButton: LiveData<Boolean>
        get() = _previousActiveButton

    private val _linearOrGrid=MutableLiveData<Boolean>()
    val linearOrGrid: LiveData<Boolean>
        get() = _linearOrGrid

    private val _DataParams=MutableLiveData<GifParams>()
    val DataParams: LiveData<GifParams>
        get() = _DataParams

    private var _SearchData: String

    init {
        _SearchData="a"
        _previousActiveButton.value=false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun searchNewData(data: String){
        _SearchData=data
        _offsetData=0
        _previousActiveButton.value=false
    }

    fun setLimits(l:Int){
        if (limit!=l) {
            limit = l
            _offsetData = 0
            _previousActiveButton.value = false
            getGiphyRealEstateProperties("g")
        }
    }

    fun nextPage(){
        _offsetData=_offsetData+limit
        _previousActiveButton.value=true
        getGiphyRealEstateProperties("g")
    }

    fun previousPage(){
        if (_offsetData >=limit) _offsetData=_offsetData-limit
        if (_offsetData<limit) _previousActiveButton.value=false
        getGiphyRealEstateProperties("g")
    }

    fun onLinearOrGrid(set: Boolean){ _linearOrGrid.value=set }


    fun getGiphyRealEstateProperties(rating: String) {
        corutineScope.launch {
            val getPropetiesDeferred=GiphyAPI.retrofitService.getGifList(api_key,_SearchData,limit,_offsetData,"g","en")
            try {
                val listResult=getPropetiesDeferred.await()
                 _DataParams.value=listResult
            }catch (t: Throwable){
                _DataParams.value= GifParams(listOf(Data("", Images( Original(t.message!!),Original("")))))
            }
        }
    }
}