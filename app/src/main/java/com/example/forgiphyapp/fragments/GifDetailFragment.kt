package com.example.forgiphyapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.forgiphyapp.R
import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.databinding.GifDetailFragmentBinding
import com.example.forgiphyapp.viewModels.GifDetailViewModel
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel

class GifDetailFragment : Fragment() {

    var binding: GifDetailFragmentBinding? = null
    var data: GifData = get()

    private val viewModel: GifDetailViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = GifDetailFragmentBinding.inflate(inflater)
        data.active = true
        data.id = GifDetailFragmentArgs.fromBundle(requireArguments()).id
        data.full_url = GifDetailFragmentArgs.fromBundle(requireArguments()).detailUrl
        data.preview_url = GifDetailFragmentArgs.fromBundle(requireArguments()).previewUrl
        data.like = GifDetailFragmentArgs.fromBundle(requireArguments()).like


        return binding!!.root
    }

    override fun onStart() {
        super.onStart()

        viewModel.removeGifLiveData.observe(viewLifecycleOwner, Observer {
            if (it) {
                findNavController().popBackStack()
            }
        })

        viewModel.setGifToScreen(binding!!.imageView)

        viewModel.errorLikeGifLiveData.observe(viewLifecycleOwner, {
            it?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
        })

        binding!!.removeGifButton.setOnClickListener { viewModel.removeGif() }

        viewModel.likeGifLiveData.observe(viewLifecycleOwner, { like ->
            val drawable =
                if (like) context?.let { AppCompatResources.getDrawable(it, R.drawable.ic_like) }
                else context?.let { AppCompatResources.getDrawable(it, R.drawable.ic_no_like) }
            binding!!.imageLikeDetail.setImageDrawable(drawable)
        })

        binding!!.imageLikeDetail.setOnClickListener {
            viewModel.likeGif()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}