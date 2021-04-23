package com.example.forgiphyapp.dagger

import android.content.Context
import com.example.forgiphyapp.fragments.GifDetailFragment
import com.example.forgiphyapp.fragments.GifListFragment
import com.example.forgiphyapp.workManager.ClearDbWork
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface ApplicationGraph {

    fun inject(gifListFragment: GifListFragment)
    fun inject(gifDetailFragment: GifDetailFragment)
    fun inject(clearDbWork: ClearDbWork)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): ApplicationGraph
    }
}