package com.example.forgiphyapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.forgiphyapp.R
import com.example.forgiphyapp.databinding.FragmentGifListBinding
import com.example.forgiphyapp.databinding.GifDetailFragmentBinding
import com.example.forgiphyapp.viewModels.GifDetailViewModel
import com.example.forgiphyapp.viewModels.GifDetailViewModelImpl
import com.example.forgiphyapp.viewModels.GifListViewModel
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class GifDetailFragment : BaseFragment<GifDetailFragmentBinding, GifDetailViewModel>() {

    override val viewModel by viewModel<GifDetailViewModel>()
    { parametersOf(GifDetailFragmentArgs.fromBundle(requireArguments()).gifData) }

    override fun getViewBinding(): GifDetailFragmentBinding =
        GifDetailFragmentBinding.inflate(layoutInflater)


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