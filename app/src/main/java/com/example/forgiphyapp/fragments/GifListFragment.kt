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
import androidx.viewbinding.ViewBinding
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.forgiphyapp.Static
import com.example.forgiphyapp.adapters.GifListAdapter
import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.databinding.FragmentGifListBinding
import com.example.forgiphyapp.viewModels.BaseViewModel
import com.example.forgiphyapp.viewModels.GifListViewModel
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named


abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel>(
    name: String,
    val bindingInflate: (LayoutInflater) -> VB
) : Fragment() {

    private val baseViewModel: BaseViewModel by viewModel(qualifier = named(name), parameters = {
        parametersOf(
            getParameters()
        )
    })

    abstract fun getParameters(): Any?

    protected val viewModel: VM
        get() {
            return baseViewModel as VM
        }

    private var baseBinding: VB? = null
    protected val binding get() = baseBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        baseBinding = bindingInflate(layoutInflater)
        return baseBinding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        baseBinding = null
    }

}


class GifListFragment :
    BaseFragment<FragmentGifListBinding, GifListViewModel>(
        Static().gifListFragmentId,
        { FragmentGifListBinding.inflate(it) }) {

    override fun getParameters(): Any? = null


    private val adapter: GifListAdapter by lazy {
        GifListAdapter({ toDetailFragment(it) }, { viewModel.likeGif(it) })
    }

    private fun toDetailFragment(data: GifData) {
        if (!data.full_url.isNullOrEmpty())
            this.findNavController()
                .navigate(
                    GifListFragmentDirections
                        .actionGifListFragmentToGifDetailFragment(data)
                )
    }

    private val uploadWorkerRequest: WorkRequest by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        WorkManager.getInstance(this.requireActivity()).enqueue(uploadWorkerRequest)

        binding!!.imageList.adapter = adapter

        binding!!.refreshLayout.setOnRefreshListener {
            viewModel.refresh()
            binding!!.refreshLayout.isRefreshing = false
        }


        binding!!.editTextNewData.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) viewModel.searchNewData(s.toString())
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
        lifecycleScope.launchWhenStarted {
            try {
                viewModel.state.collect { state ->
                    Log.i("GifListFragment", "new State")
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
                    if (state.error.errorMessage.isNotEmpty()) {
                        binding!!.progressBar.visibility = View.GONE
                        binding!!.imageList.visibility = View.VISIBLE
                        binding!!.buttonError.visibility = View.VISIBLE
                        binding!!.textError.visibility = View.VISIBLE
                        binding!!.textError.text = state.error.errorMessage
                        adapter.submitList(state.error.offlineData)
                    }

                    binding!!.previousPageButton.isEnabled = state.previousActiveButton
                    binding!!.nextPageButton.isEnabled = state.nextActiveButton

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
    }


}