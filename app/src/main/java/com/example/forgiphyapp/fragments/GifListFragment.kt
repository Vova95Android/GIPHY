package com.example.forgiphyapp.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.forgiphyapp.R
import com.example.forgiphyapp.adapters.GifListAdapter
import com.example.forgiphyapp.databinding.FragmentGifListBinding
import com.example.forgiphyapp.viewModels.BaseViewModel
import com.example.forgiphyapp.viewModels.GifAction
import com.example.forgiphyapp.viewModels.GifListViewModel
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.bind
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass


abstract class BaseFragment<VM : BaseViewModel>(id: Int) : Fragment(id) {

    inline fun <T : ViewBinding> Fragment.viewBinding(
        crossinline bindingInflater: (LayoutInflater) -> T
    ) =
        lazy(LazyThreadSafetyMode.NONE) {
            bindingInflater.invoke(layoutInflater)
        }

    private val baseViewModel: VM by viewModel(clazz = viewModelClass(), parameters = {
        parametersOf(getParameters())
    })

    @Suppress("UNCHECKED_CAST")
    private fun viewModelClass(): KClass<VM> {
        return ((javaClass.genericSuperclass as ParameterizedType)
            .actualTypeArguments[0] as Class<VM>).kotlin
    }

    open fun getParameters(): Any? = null

    protected val viewModel: VM
        get() {
            return baseViewModel
        }

    abstract val binding: ViewBinding

}


class GifListFragment :
    BaseFragment<GifListViewModel>(R.layout.fragment_gif_list) {


    override val binding by viewBinding(FragmentGifListBinding::bind)

    private val adapter: GifListAdapter by lazy {
        GifListAdapter(
            { viewModel.navigateToGifDetailFragment(it) },
            { viewModel.handleAction(GifAction.likeGif(it)) })
    }


    private val uploadWorkerRequest: WorkRequest by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        WorkManager.getInstance(this.requireActivity()).enqueue(uploadWorkerRequest)
        binding.imageList.adapter = adapter

        binding.refreshLayout.setOnRefreshListener {
            viewModel.handleAction(GifAction.refresh)
            binding.refreshLayout.isRefreshing = false
        }


        binding.editTextNewData.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) viewModel.handleAction(GifAction.searchGif(search = s.toString()))
            }
        })

        binding.buttonError.setOnClickListener { viewModel.handleAction(GifAction.refresh) }

        binding.linearButton.setOnClickListener { viewModel.linearOrGrid(true) }

        binding.gridButton.setOnClickListener { viewModel.linearOrGrid(false) }

        binding.nextPageButton.setOnClickListener { viewModel.handleAction(GifAction.searchGif(nextPage = true)) }

        binding.previousPageButton.setOnClickListener {  viewModel.handleAction(GifAction.searchGif(nextPage = false))}

        binding.likeButton.setOnClickListener {  viewModel.handleAction(GifAction.getLikeGif()) }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            try {
                viewModel.state.collect { state ->
                    Log.i("GifListFragment", "new State")
                    if (state.isLoading) {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.imageList.visibility = View.GONE
                        binding.buttonError.visibility = View.GONE
                        binding.textError.visibility = View.GONE
                    }
                    if (state.data.isNotEmpty()) {
                        adapter.submitList(state.data)
                        delay(200)
                        binding.progressBar.visibility = View.GONE
                        binding.imageList.visibility = View.VISIBLE
                        binding.buttonError.visibility = View.GONE
                        binding.textError.visibility = View.GONE
                    }
                    if (state.error.errorMessage.isNotEmpty()) {
                        adapter.submitList(state.error.offlineData)
                        delay(200)
                        binding.progressBar.visibility = View.GONE
                        binding.imageList.visibility = View.VISIBLE
                        binding.buttonError.visibility = View.VISIBLE
                        binding.textError.visibility = View.VISIBLE
                        binding.textError.text = state.error.errorMessage
                    }

                    binding.previousPageButton.isEnabled = state.previousActiveButton
                    binding.nextPageButton.isEnabled = state.nextActiveButton

                    if (viewModel.lastLinearOrGridState != state.linearOrGrid) {
                        viewModel.lastLinearOrGridState = state.linearOrGrid
                        if (state.linearOrGrid) {
                            val layout = binding.imageList.layoutManager as GridLayoutManager
                            val pos = layout.findLastVisibleItemPosition() - 3
                            binding.imageList.layoutManager = LinearLayoutManager(activity)
                            binding.imageList.scrollToPosition(pos)
                        } else {
                            val layout = binding.imageList.layoutManager as LinearLayoutManager
                            val pos = layout.findLastVisibleItemPosition() - 3
                            binding.imageList.layoutManager = GridLayoutManager(activity, 3)
                            binding.imageList.scrollToPosition(pos)
                        }
                    }

                }
                cancel()
            } catch (e: Exception) {
                e.message?.let { Log.i("GifListFragment", it) }
            }
        }
    }


}