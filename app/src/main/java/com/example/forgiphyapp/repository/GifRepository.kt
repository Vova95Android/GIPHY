package com.example.forgiphyapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.database.GifDatabaseDao
import com.example.forgiphyapp.useCases.LikeGifUseCase
import com.example.forgiphyapp.useCases.LoadGifUseCase
import com.example.forgiphyapp.useCases.RemoveGifUseCase


interface GifRepository {
    val savedGifLiveData: LiveData<List<GifData>>

    var actualData: List<GifData>?

    val dataPagingLiveData: MutableLiveData<PagingData<GifData>>

    suspend fun getGif(search: String, likeGif: Boolean, nextPage: Boolean? = null): List<GifData>

    fun previousButtonIsActive(): Boolean

    suspend fun removeGif(gif: GifData)
    suspend fun likeGif(gif: GifData)
}

class GifRepositoryImpl(
    dataBase: GifDatabaseDao,
    private val loadGifUseCase: LoadGifUseCase,
    private val removeGifUseCase: RemoveGifUseCase,
    private val likeGifUseCase: LikeGifUseCase
) : GifRepository {

    override val savedGifLiveData = dataBase.getAllGifDataLiveData()

    override var actualData: List<GifData>? = null

    override val dataPagingLiveData = MutableLiveData<PagingData<GifData>>()

    private var offsetData = 0
    private var limit = 30
    private var startPage = 0

    override fun previousButtonIsActive(): Boolean {
        return startPage >= limit
    }

    private var endPage = 0
    private var searchData = "A"


    override suspend fun getGif(
        search: String,
        likeGif: Boolean,
        nextPage: Boolean?
    ): List<GifData> {

        var limitTemp = limit

        if (search != searchData) {
            offsetData = 0
            startPage = 0
            endPage = 0
        }
        searchData = search

        when (nextPage) {
            true -> {
                startPage = endPage+1
                offsetData = startPage
            }
            false -> {
                endPage = startPage-1
                offsetData = endPage - limit
            }
            null -> {
                offsetData = startPage
            }
        }

        var listSize = 0
        var listResult = listOf<GifData>()
        while (listSize < limit) {
            val listResultTemp =
                if (!likeGif) loadGifUseCase.getGif(searchData, limitTemp, offsetData)
                else likeGifUseCase.getListLikeGif(offsetData, limitTemp)
            if ((nextPage == true) || (nextPage == null)) {
                listResult = listResult.plus(listResultTemp)
                listSize = listResult.size
                offsetData += limitTemp
                limitTemp = limit - listSize
            } else if (nextPage == false) {
                listResult = listResultTemp.plus(listResult)
                listSize = listResult.size
                limitTemp = limit - listSize
                offsetData -= limitTemp
            }
            if (offsetData < 0) {
                listSize = limit
                offsetData = 0
            }
            if ((savedGifLiveData.value?.size!! < offsetData) && (likeGif)) listSize = limit
        }
        if ((nextPage == true) || (nextPage == null)) endPage = offsetData
        else startPage = offsetData + limitTemp
        return listResult
    }


    override suspend fun removeGif(gif: GifData) {
        removeGifUseCase.removeGif(gif)
    }

    override suspend fun likeGif(gif: GifData) {
        likeGifUseCase.likeGif(gif)
    }

}