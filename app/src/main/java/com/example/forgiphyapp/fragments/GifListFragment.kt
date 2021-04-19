package com.example.forgiphyapp.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.forgiphyapp.R
import com.example.forgiphyapp.adapters.GifListAdapter
import com.example.forgiphyapp.adapters.GifListPagingAdapter
import com.example.forgiphyapp.dagger.App
import com.example.forgiphyapp.database.GifDatabase
import com.example.forgiphyapp.databinding.FragmentGifListBinding
import com.example.forgiphyapp.vievModelsFactory.GifListViewModelFactory
import com.example.forgiphyapp.viewModels.GifListViewModel
import kotlinx.coroutines.flow.collectLatest
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
        val application = requireNotNull(this.activity).application

        (this.requireActivity().application as App).component.inject(this)


        viewModel = ViewModelProvider(this,viewModelFactory).get(GifListViewModel::class.java)

        viewModel.saveGifs.observe(viewLifecycleOwner, {
            var update=false
            if ((viewModel.actualData.isNullOrEmpty())||(it.size == viewModel.actualData!!.size)) update=true
            viewModel.actualData = it

            if (update) viewModel.refresh()
        })


        binding!!.viewModel = viewModel


        adapter=GifListPagingAdapter(GifListPagingAdapter.onClickListener {
            if(!it.images.original.url.isNullOrEmpty())
                this.findNavController()
                    .navigate(GifListFragmentDirections
                        .actionGifListFragmentToGifDetailFragment(it.id,it.images.original.url,it.images.preview_gif.url))
        })
        binding!!.imageList.adapter=adapter

        fetchPosts()
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

    private fun fetchPosts() {
        lifecycleScope.launch {
            viewModel.fetchGif().collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }


}