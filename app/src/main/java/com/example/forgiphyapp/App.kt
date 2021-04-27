package com.example.forgiphyapp

import android.app.Application
import com.example.forgiphyapp.dagger.ApplicationGraph
import com.example.forgiphyapp.dagger.DaggerApplicationGraph
import com.example.forgiphyapp.koin.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    val component: ApplicationGraph by lazy {
        DaggerApplicationGraph.factory().create(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger ()
            androidContext ( this@App )
            modules(listOf(appModule))
        }
    }
}