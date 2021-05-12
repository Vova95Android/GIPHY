package com.example.forgiphyapp.navigation

import androidx.navigation.fragment.findNavController
import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.fragments.GifDetailFragment
import com.example.forgiphyapp.fragments.GifListFragment
import com.example.forgiphyapp.fragments.GifListFragmentDirections

interface Router {
    fun navigateToGifDetailFragment(fragment: GifListFragment, data: GifData)

    fun navigateToGifListFragment(fragment: GifDetailFragment)
}

class RouterImpl : Router{
    override fun navigateToGifDetailFragment(fragment: GifListFragment, data: GifData){
        fragment.findNavController().navigate(GifListFragmentDirections.actionGifListFragmentToGifDetailFragment(data))
    }

    override fun navigateToGifListFragment(fragment: GifDetailFragment) {
        fragment.findNavController().popBackStack()
    }
}