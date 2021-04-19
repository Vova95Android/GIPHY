package com.example.forgiphyapp.dagger

import android.app.Application

class App: Application() {
    val component: ApplicationGraph by lazy {
        DaggerApplicationGraph.factory().create(applicationContext)
    }
}