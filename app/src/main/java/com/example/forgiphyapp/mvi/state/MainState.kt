package com.example.forgiphyapp.mvi.state

import androidx.paging.PagingData
import com.example.forgiphyapp.api.Data

sealed class MainState {
    object Idle : MainState()
    object Loading : MainState()
    object GifsLoad : MainState()
    data class Error(val error: String?) : MainState()
}