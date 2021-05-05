package com.example.forgiphyapp.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.forgiphyapp.adapters.GifListAdapter
import com.example.forgiphyapp.databinding.FragmentGifListBinding
import com.example.forgiphyapp.viewModels.GifListViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class GifListFragment : Fragment() {

    private val adapter: GifListAdapter by lazy {
        GifListAdapter(GifListAdapter.OnClickListener {
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
        binding = FragmentGifListBinding.inflate(inflater)

        WorkManager.getInstance(this.requireActivity()).enqueue(uploadWorkerRequest)

        binding!!.imageList.adapter = adapter

        return binding!!.root
    }

    override fun onStart() {
        super.onStart()

        binding!!.refreshLayout.setOnRefreshListener {
            viewModel.refresh()
            binding!!.refreshLayout.isRefreshing = false
        }


        binding!!.editTextNewData.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if ((s != null) && (s.isNotEmpty()) && (count != before)) {
                    viewModel.searchNewData(s.toString())
                }
            }
        })

        binding!!.buttonError.setOnClickListener { viewModel.refresh() }

        binding!!.linearButton.setOnClickListener { viewModel.linearOrGrid(true) }

        binding!!.gridButton.setOnClickListener { viewModel.linearOrGrid(false) }

        binding!!.nextPageButton.setOnClickListener { viewModel.nextPage() }

        binding!!.previousPageButton.setOnClickListener { viewModel.previousPage() }

        binding!!.likeButton.setOnClickListener { viewModel.getLikeGif() }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            try {
                viewModel.state.collect { state ->
                    if (state.isLoading) {
                        binding!!.progressBar.visibility = View.VISIBLE
                        binding!!.imageList.visibility = View.GONE
                        binding!!.buttonError.visibility = View.GONE
                        binding!!.textError.visibility = View.GONE
                    }
                    if (!state.data.isNullOrEmpty()) {
                        binding!!.progressBar.visibility = View.GONE
                        binding!!.imageList.visibility = View.VISIBLE
                        binding!!.buttonError.visibility = View.GONE
                        binding!!.textError.visibility = View.GONE
                        adapter.submitList(state.data)
                    }
                    if (!state.error.isNullOrEmpty()) {
                        binding!!.progressBar.visibility = View.GONE
                        binding!!.imageList.visibility = View.VISIBLE
                        binding!!.buttonError.visibility = View.VISIBLE
                        binding!!.textError.visibility = View.VISIBLE
                        binding!!.textError.text = state.error[0].full_url
                        adapter.submitList(state.error.minus(state.error[0]))
                    }
                    binding!!.previousPageButton.isEnabled = state.previousActiveButton

                    if (state.linearOrGrid) {
                        binding!!.imageList.layoutManager = LinearLayoutManager(activity)
                    } else {
                        binding!!.imageList.layoutManager = GridLayoutManager(activity, 3)
                    }

                }
            } catch (e: Exception) {
                e.message?.let { Log.i("GifListFragment", it) }
            }
        }

        viewModel.state.value.savedGifLiveData.observe(viewLifecycleOwner, {
            viewModel.newDataFromDatabase(it)
            Log.i("GifListFragment", it.size.toString())
            viewModel.newDataOrRefresh(viewModel.state.value.search)
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}