package com.example.forgiphyapp.mvi.state

import androidx.paging.PagingData
import com.example.forgiphyapp.api.Data

open class MainState {
    object Idle : MainState()
    object Loading : MainState()
    object GifsLoad : MainState()
    object Error : MainState()
}