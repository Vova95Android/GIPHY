package com.example.forgiphyapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.forgiphyapp.R
import com.example.forgiphyapp.Static
import com.example.forgiphyapp.databinding.GifDetailFragmentBinding
import com.example.forgiphyapp.viewModels.GifDetailViewModel
import kotlinx.coroutines.flow.collect

class GifDetailFragment :
    BaseFragment<GifDetailFragmentBinding, GifDetailViewModel>(
        Static().gifDetailFragmentId,
        { GifDetailFragmentBinding.inflate(it) }) {
    
    override fun getParameters(): Any {
        return GifDetailFragmentArgs.fromBundle(requireArguments()).gifData
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setGifToScreen(binding!!.imageView)

        binding!!.removeGifButton.setOnClickListener { viewModel.removeGif() }

        binding!!.imageLikeDetail.setOnClickListener { viewModel.likeGif() }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            try {
                viewModel.state.collect { state ->
                    Log.i("GifDetailFragment", "new State")
                    val drawable =
                        if (state.gifData.like) context?.let {
                            AppCompatResources.getDrawable(
                                it,
                                R.drawable.ic_like
                            )
                        }
                        else context?.let {
                            AppCompatResources.getDrawable(
                                it,
                                R.drawable.ic_no_like
                            )
                        }
                    binding!!.imageLikeDetail.setImageDrawable(drawable)
                    if (state.errorGif.isNotEmpty()) Toast.makeText(
                        context,
                        state.errorGif,
                        Toast.LENGTH_LONG
                    ).show()

                    if (!state.gifData.active) findNavController().popBackStack()


                }
            } catch (e: Exception) {
                e.message?.let { Log.i("GifDetailFragment", it) }
            }
        }

    }


}