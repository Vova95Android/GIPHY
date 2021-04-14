package com.example.forgiphyapp.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.forgiphyapp.R
import com.example.forgiphyapp.database.GifDatabase
import com.example.forgiphyapp.databinding.FragmentGifListBinding
import com.example.forgiphyapp.databinding.GifDetailFragmentBinding
import com.example.forgiphyapp.vievModelsFactory.GifDetailViewModelFactory
import com.example.forgiphyapp.vievModelsFactory.GifListViewModelFactory
import com.example.forgiphyapp.viewModels.GifDetailViewModel

class GifDetailFragment : Fragment() {

    private lateinit var viewModel: GifDetailViewModel
    var binding: GifDetailFragmentBinding? = null
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.gif_detail_fragment,
                container,
                false
        )
        val application = requireNotNull(this.activity).application

        val dataSource = GifDatabase.getInstance(application).gifDatabaseDao

        val viewModelFactory = GifDetailViewModelFactory(dataSource, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(GifDetailViewModel::class.java)
        binding!!.viewModel = viewModel
        viewModel.setData(
            GifDetailFragmentArgs.fromBundle(requireArguments()).id,
            GifDetailFragmentArgs.fromBundle(requireArguments()).detailUrl,
            GifDetailFragmentArgs.fromBundle(requireArguments()).previewUrl)
        viewModel.removeGifLiveData.observe(viewLifecycleOwner, Observer {
            if(it) {
                findNavController().navigate(GifDetailFragmentDirections.actionGifDetailFragmentToGifListFragment())
                viewModel.navigateOk()
            }
        })
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}