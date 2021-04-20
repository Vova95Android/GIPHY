package com.example.forgiphyapp

import android.app.Application
import com.example.forgiphyapp.dagger.ApplicationGraph
import com.example.forgiphyapp.dagger.DaggerApplicationGraph

class App : Application() {
    val component: ApplicationGraph by lazy {
        DaggerApplicationGraph.factory().create(applicationContext)
    }
}