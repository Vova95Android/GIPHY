package com.example.forgiphyapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.forgiphyapp.R
import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.databinding.GifDetailFragmentBinding
import com.example.forgiphyapp.viewModels.GifDetailViewModel
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class GifDetailFragment : Fragment() {

    var binding: GifDetailFragmentBinding? = null

    private val viewModel: GifDetailViewModel by viewModel()
    { parametersOf(GifDetailFragmentArgs.fromBundle(requireArguments()).gifData) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = GifDetailFragmentBinding.inflate(inflater)

        return binding!!.root
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


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}