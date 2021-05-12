package com.example.forgiphyapp.navigation

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.forgiphyapp.R
import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.fragments.GifListFragmentDirections

interface Router {

    fun setActivity(activity: AppCompatActivity)

    fun clearActivity()

    fun navigateToGifDetailFragment(data: GifData)

    fun navigateToGifListFragment()
}

class RouterImpl : Router{
    private var activity: AppCompatActivity? = null

    override fun setActivity(activity: AppCompatActivity) {
        this.activity=activity
    }

    override fun clearActivity() {
        activity = null
    }

    override fun navigateToGifDetailFragment(data: GifData){
        activity?.findNavController(R.id.nav_host)?.navigate(GifListFragmentDirections.actionGifListFragmentToGifDetailFragment(data))
    }

    override fun navigateToGifListFragment() {
        activity?.findNavController(R.id.nav_host)?.popBackStack()
    }
}