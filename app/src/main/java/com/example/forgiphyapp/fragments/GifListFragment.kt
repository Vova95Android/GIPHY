package com.example.forgiphyapp.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.forgiphyapp.R
import com.example.forgiphyapp.adapters.GifListPagingAdapter
import com.example.forgiphyapp.App
import com.example.forgiphyapp.databinding.FragmentGifListBinding
import com.example.forgiphyapp.vievModelsFactory.GifListViewModelFactory
import com.example.forgiphyapp.viewModels.GifListViewModelImpl
import kotlinx.coroutines.launch
import javax.inject.Inject

class GifListFragment : Fragment() {

    private lateinit var viewModel: GifListViewModelImpl
    private val adapter: GifListPagingAdapter by lazy {
        GifListPagingAdapter(GifListPagingAdapter.OnClickListener {
            if (!it.images.original.url.isNullOrEmpty())
                this.findNavController()
                    .navigate(
                        GifListFragmentDirections
                            .actionGifListFragmentToGifDetailFragment(
                                it.id,
                                it.images.original.url,
                                it.images.preview_gif.url
                            )
                    )
        })
    }

    @Inject
    lateinit var viewModelFactory: GifListViewModelFactory


    @Inject
    lateinit var uploadWorkerRequest: WorkRequest

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

        (this.requireActivity().application as App).component.inject(this)


        WorkManager.getInstance(this.requireActivity()).enqueue(uploadWorkerRequest)

        viewModel = ViewModelProvider(this, viewModelFactory).get(GifListViewModelImpl::class.java)

        viewModel.savedGifLiveData.observe(viewLifecycleOwner, { it ->
            viewModel.newDataOrRefresh(it)
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
                progressBar.isVisible = loadState.refresh is LoadState.Loading
                imageList.isVisible = loadState.refresh !is LoadState.Loading
                buttonError.isVisible = loadState.refresh is LoadState.Error
                textError.isVisible = loadState.refresh is LoadState.Error
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}