package com.example.forgiphyapp.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.forgiphyapp.R
import com.example.forgiphyapp.adapters.GifListPagingAdapter
import com.example.forgiphyapp.databinding.FragmentGifListBinding
import com.example.forgiphyapp.mvi.state.MainState
import com.example.forgiphyapp.viewModels.GifListViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class GifListFragment : Fragment() {

    private val adapter: GifListPagingAdapter by lazy {
        GifListPagingAdapter(GifListPagingAdapter.OnClickListener {
            if (!it.full_url.isNullOrEmpty())
                this.findNavController()
                    .navigate(
                        GifListFragmentDirections
                            .actionGifListFragmentToGifDetailFragment(
                                it.id,
                                it.full_url!!,
                                it.preview_url,
                                it.like
                            )
                    )
        })
    }

    private val viewModel: GifListViewModel by viewModel()

    private val uploadWorkerRequest: WorkRequest by inject()

    var binding: FragmentGifListBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_gif_list,
            container,
            false
        )


        WorkManager.getInstance(this.requireActivity()).enqueue(uploadWorkerRequest)

        viewModel.savedGifLiveData.observe(viewLifecycleOwner, {
            viewModel.newData = it
            Log.i("GifListFragment", it.size.toString())
            viewModel.newDataOrRefresh()
        })

        binding!!.viewModel = viewModel

        binding!!.imageList.adapter = adapter

        binding!!.lifecycleOwner = this

        return binding!!.root
    }

    override fun onStart() {
        super.onStart()

        viewModel.dataPagingLiveData.observe(viewLifecycleOwner, {
            lifecycleScope.launch {
                adapter.submitData(it)
            }
        })
        viewModel.linearOrGridLiveData.observe(viewLifecycleOwner, {
            if (it) {
                binding!!.imageList.layoutManager = GridLayoutManager(activity, 3)
            } else {
                binding!!.imageList.layoutManager = LinearLayoutManager(activity)
            }
        })

        binding!!.editTextNewData.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if ((s != null) && (s.isNotEmpty()) && (count != before)) {
                    viewModel.searchNewData(s.toString())
                }
            }
        })




        adapter.addLoadStateListener { loadState ->
            binding?.apply {
//                progressBar.isVisible = loadState.refresh is LoadState.Loading
//                imageList.isVisible = loadState.refresh !is LoadState.Loading
//                buttonError.isVisible = loadState.refresh is LoadState.Error
//                textError.isVisible = loadState.refresh is LoadState.Error
                if (loadState.refresh !is LoadState.Loading) {
                    viewModel!!.state.value = MainState.GifsLoad
                }
                if (loadState.refresh is LoadState.Error) {
                    viewModel!!.state.value =
                        MainState.Error((loadState.refresh as LoadState.Error).error.message)
                }
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            try {
                viewModel.state.collect { state ->
                    when (state) {
                        is MainState.Idle -> {
                        }
                        is MainState.GifsLoad -> {
                            binding!!.progressBar.visibility = View.GONE
                            binding!!.imageList.visibility = View.VISIBLE
                            binding!!.buttonError.visibility = View.GONE
                            binding!!.textError.visibility = View.GONE
                        }
                        is MainState.Loading -> {
                            binding!!.progressBar.visibility = View.VISIBLE
                            binding!!.imageList.visibility = View.GONE
                            binding!!.buttonError.visibility = View.GONE
                            binding!!.textError.visibility = View.GONE
                        }
                        is MainState.Error -> {
                            binding!!.progressBar.visibility = View.GONE
                            binding!!.imageList.visibility = View.GONE
                            binding!!.buttonError.visibility = View.VISIBLE
                            binding!!.textError.visibility = View.VISIBLE
                            binding!!.textError.text = state.error
                        }
                    }
                }

            } catch (e: Exception) {
                e.message?.let { Log.i("GifListFragment", it) }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}