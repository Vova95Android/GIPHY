package com.example.forgiphyapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.database.GifDatabaseDao
import com.example.forgiphyapp.useCases.LikeGifUseCase
import com.example.forgiphyapp.useCases.LoadGifUseCase
import com.example.forgiphyapp.useCases.OfflineGifUseCase
import com.example.forgiphyapp.useCases.RemoveGifUseCase


interface GifRepository {
    val savedGifLiveData: LiveData<List<GifData>>

    var actualData: List<GifData>?

    val dataPagingLiveData: MutableLiveData<PagingData<GifData>>

    suspend fun getGif(
        search: String = "H",
        likeGif: Boolean = false,
        nextPage: Boolean? = null
    ): List<GifData>

    fun resetPos()

    fun previousButtonIsActive(): Boolean

    fun nextButtonIsActive(): Boolean

    suspend fun removeGif(gif: GifData)
    suspend fun likeGif(gif: GifData)
}

class GifRepositoryImpl(
    dataBase: GifDatabaseDao,
    private val loadGifUseCase: LoadGifUseCase,
    private val removeGifUseCase: RemoveGifUseCase,
    private val likeGifUseCase: LikeGifUseCase,
    private val offlineGifUseCase: OfflineGifUseCase
) : GifRepository {

    override val savedGifLiveData = dataBase.getAllGifDataLiveData()

    override var actualData: List<GifData>? = null

    override val dataPagingLiveData = MutableLiveData<PagingData<GifData>>()

    private var offsetData = 0
    private val limit = 30
    private var startPage = 0
    private var endPage = 0
    private var searchData = "H"
    private var nextButtonActive = true

    override fun previousButtonIsActive(): Boolean {
        return startPage >= limit
    }

    override fun nextButtonIsActive(): Boolean {
        return nextButtonActive
    }

    override fun resetPos() {
        offsetData = 0
        startPage = 0
        endPage = 0
    }

    override suspend fun getGif(
        search: String,
        likeGif: Boolean,
        nextPage: Boolean?
    ): List<GifData> {


        var limitTemp = limit
        nextButtonActive = true

        if (search != searchData) {
            offsetData = 0
            startPage = 0
            endPage = 0
        }
        searchData = search

        when (nextPage) {
            true -> {
                startPage = endPage + 1
                offsetData = startPage
            }
            false -> {
                endPage = startPage - 1
                offsetData = endPage - limit
            }
            null -> {
                offsetData = startPage
            }
        }

        var listSize = 0
        var listResult = listOf<GifData>()
        while (listSize < limit) {
            val listSizeOld = listSize
            val listResultTemp: List<GifData> = try {
                if (likeGif) likeGifUseCase.getListLikeGif(limitTemp, offsetData)
                else loadGifUseCase.getGif(searchData, limitTemp, offsetData)
            } catch (e: Exception) {
                listOf(
                    GifData(
                        "ERROR",
                        e.message,
                        e.message,
                        false,
                        false
                    )
                ).plus(offlineGifUseCase.getGif(limitTemp, offsetData))
            }

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
            if (listSizeOld == listSize) {
                listSize = limit
                nextButtonActive = false
            }
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