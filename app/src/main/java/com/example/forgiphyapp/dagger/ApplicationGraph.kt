package com.example.forgiphyapp.dagger

import android.content.Context
import com.example.forgiphyapp.fragments.GifDetailFragment
import com.example.forgiphyapp.fragments.GifListFragment
import com.example.forgiphyapp.viewModels.GifListViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [Module::class])
interface ApplicationGraph {

    fun inject(gifListFragment: GifListFragment)
    fun inject(gifDetailFragment: GifDetailFragment)
    fun inject(gifListViewModel: GifListViewModel)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): ApplicationGraph
    }
}