package com.example.forgiphyapp.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.forgiphyapp.R
import com.example.forgiphyapp.App
import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.databinding.GifDetailFragmentBinding
import com.example.forgiphyapp.vievModelsFactory.GifDetailViewModelFactory
import com.example.forgiphyapp.viewModels.GifDetailViewModel
import com.example.forgiphyapp.viewModels.GifDetailViewModelImpl
import org.koin.android.ext.android.bind
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import javax.inject.Inject

class GifDetailFragment : Fragment() {

    var binding: GifDetailFragmentBinding? = null

//    @Inject
//    lateinit var viewModelFactory: GifDetailViewModelFactory
    var data: GifData =get()

    val viewModel: GifDetailViewModel by viewModel()
    //val viewModelFactory: GifDetailViewModelFactory by inject()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.gif_detail_fragment,
                container,
                false
        )
        data.active=true
        data.id=GifDetailFragmentArgs.fromBundle(requireArguments()).id
        data.full_url=GifDetailFragmentArgs.fromBundle(requireArguments()).detailUrl
        data.preview_url=GifDetailFragmentArgs.fromBundle(requireArguments()).previewUrl
        data.like=GifDetailFragmentArgs.fromBundle(requireArguments()).like


       // (this.requireActivity().application as App).component.inject(this)

//        viewModel =
//                ViewModelProvider(this, viewModelFactory).get(GifDetailViewModelImpl::class.java)

        binding!!.viewModel = viewModel
        viewModel.removeGifLiveData.observe(viewLifecycleOwner, Observer {
            if (it) {
                findNavController().popBackStack()
            }
        })

        viewModel.setGifToScreen(
                binding!!.imageView
        )

        viewModel.errorLikeGifLiveData.observe(viewLifecycleOwner,{
            it?.let {  Toast.makeText(context,it,Toast.LENGTH_LONG).show()}
        })

        viewModel.likeGifLiveData.observe(viewLifecycleOwner,{
            val drawable= if(it) context?.let { AppCompatResources.getDrawable(it,R.drawable.ic_like) }
            else context?.let { AppCompatResources.getDrawable(it,R.drawable.ic_no_like) }
            binding!!.imageLikeDetail.setImageDrawable(drawable)
        })

        binding!!.imageLikeDetail.setOnClickListener {
            viewModel.likeGif()
        }
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}