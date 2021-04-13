package com.example.forgiphyapp.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.forgiphyapp.R
import com.example.forgiphyapp.databinding.FragmentGifListBinding
import com.example.forgiphyapp.databinding.GifDetailFragmentBinding
import com.example.forgiphyapp.viewModels.GifDetailViewModel

class GifDetailFragment : Fragment() {

    private lateinit var viewModel: GifDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: GifDetailFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.gif_detail_fragment,
            container,
            false
        )
        viewModel = ViewModelProvider(this).get(GifDetailViewModel::class.java)
        binding.viewModel=viewModel
        viewModel.setUrl(GifDetailFragmentArgs.fromBundle(requireArguments()).detailUrl)
        return binding.root
    }


}