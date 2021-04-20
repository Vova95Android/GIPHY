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
import com.example.forgiphyapp.R
import com.example.forgiphyapp.adapters.GifListPagingAdapter
import com.example.forgiphyapp.dagger.App
import com.example.forgiphyapp.databinding.FragmentGifListBinding
import com.example.forgiphyapp.vievModelsFactory.GifListViewModelFactory
import com.example.forgiphyapp.viewModels.GifListViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Use the [GifListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GifListFragment : Fragment() {

    private lateinit var viewModel: GifListViewModel
    private lateinit var adapter: GifListPagingAdapter

    @Inject
    lateinit var viewModelFactory: GifListViewModelFactory

    var binding: FragmentGifListBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_gif_list,
            container,
            false
        )

        (this.requireActivity().application as App).component.inject(this)

        viewModel = ViewModelProvider(this, viewModelFactory).get(GifListViewModel::class.java)

        viewModel.saveGifs.observe(viewLifecycleOwner, {

            if ((viewModel.actualData.isNullOrEmpty()) || (it.size == viewModel.actualData!!.size)) {
                viewModel.actualData = it
                viewModel.refresh()
            } else viewModel.actualData = it
        })

        binding!!.viewModel = viewModel

        adapter = GifListPagingAdapter(GifListPagingAdapter.onClickListener {
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

        adapter.addLoadStateListener { loadState ->
            binding!!.progressBar.isVisible = loadState.refresh is LoadState.Loading
            binding!!.imageList.isVisible = loadState.refresh !is LoadState.Loading
            binding!!.buttonError.isVisible = loadState.refresh is LoadState.Error
            binding!!.textError.isVisible = loadState.refresh is LoadState.Error
        }

        viewModel.dataPagimg.observe(viewLifecycleOwner, {
            lifecycleScope.launch {
                adapter.submitData(it)
            }
        })

        binding!!.imageList.adapter = adapter

        binding!!.lifecycleOwner = this

        return binding!!.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.linearOrGrid.observe(viewLifecycleOwner, {
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
                    viewModel.refresh()
                }
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}