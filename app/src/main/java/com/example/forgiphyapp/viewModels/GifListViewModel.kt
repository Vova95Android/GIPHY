package com.example.forgiphyapp .viewModels

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
    var limit: Int = 15

    private var _offsetData = 0
    private var _startOffsetThisPage=0
    private var _endOffsetThisPage=0

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
            getGiphyRealEstateProperties("g",true)
        }
    }

    fun nextPage() {
        previousActiveButton.value = true
        getGiphyRealEstateProperties("g", true)
    }

    fun previousPage() {
        //if (_offsetData >= limit) _offsetData = _offsetData - limit
        //if (_offsetData < limit) previousActiveButton.value = false
        getGiphyRealEstateProperties("g", false)
    }

    fun onLinearOrGrid(set: Boolean) {
        linearOrGrid.value = set
    }


    fun getGiphyRealEstateProperties(rating: String, nextPage: Boolean?) {
        corutineScope.launch {
            if(nextPage==true){
                _offsetData= _endOffsetThisPage
                _startOffsetThisPage = _offsetData
            }else if (nextPage==false){
                _offsetData=_startOffsetThisPage-limit
                _endOffsetThisPage=_startOffsetThisPage
            }else{_offsetData=_endOffsetThisPage}
            var listSize=0
            var limitTemp=limit
            while (listSize<limit) {
                val getPropetiesDeferred = GiphyAPI.retrofitService.getGifList(
                    api_key,
                    _searchData,
                    limitTemp,
                    _offsetData,
                    "g",
                    "en"
                )
                try {
                    val listResult = getPropetiesDeferred.await()
                    var listDataRemov = listOf<Data>()
                    for (i in 0..listResult.data.size - 1) {
                        for (z in 0..actualData.size - 1) {
                            if ((!actualData.isNullOrEmpty()) &&
                                (actualData[z].id == listResult.data[i].id)
                            ) {
                                Log.i("GifListViewModel", "data minus" + i)
                                listDataRemov = listDataRemov.plus(listResult.data[i])
                            }
                        }
                    }
                    if(nextPage!=false) {
                        _offsetData+=limitTemp
                        _endOffsetThisPage=_offsetData
                    }
                    else {
                        _offsetData-=listDataRemov.size
                        _startOffsetThisPage=_offsetData
                    }
                    if (_offsetData<0)_offsetData=0
                    Log.i("GifListViewModel", "offsetData-"+_offsetData)
                    Log.i("GifListViewModel", "limitTemp-"+limitTemp)
                    Log.i("GifListViewModel", "startOffset-"+_startOffsetThisPage)
                    Log.i("GifListViewModel", "endOffset-"+_endOffsetThisPage)


                    if (listDataRemov.size > 0) {
                        listResult.data = listResult.data.minus(listDataRemov)
                        limitTemp=listDataRemov.size
                    }

                    Log.i("GifListViewModel", "listSize-"+listSize)
                    if (listSize==0) dataParams.value = listResult
                    else {
                        if(nextPage==false){
                            val dataTemp=listResult
                            dataTemp.data=dataTemp.data.plus(dataParams.value!!.data)
                            dataParams.value=dataTemp
                        }
                        else {
                            val dataTemp = dataParams.value!!
                            dataTemp.data = dataTemp.data.plus(listResult.data)
                            dataParams.value = dataTemp
                        }
                    }
                    listSize= dataParams.value!!.data.size
                    if(_offsetData==0) previousActiveButton.value=false
                    if((nextPage==false)&&(_offsetData==0))listSize=limit
                } catch (t: Throwable) {
                    if (t.message != null) Log.e("GifListViewModel", t.message!!)
                }
            }
        }
    }

    private fun compareGif(list: GifParams, nextPage: Boolean): GifParams{

        return GifParams(listOf())
    }
}