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
import com.example.forgiphyapp.adapters.GifListAdapter
import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.databinding.FragmentGifListBinding
import com.example.forgiphyapp.viewModels.BaseViewModel
import com.example.forgiphyapp.viewModels.GifListViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass


abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel> : Fragment() {

    private val baseViewModel: VM by viewModel(clazz = viewModelClass(), parameters = {
        parametersOf(getParameters())
    })

    @Suppress("UNCHECKED_CAST")
    private fun viewModelClass(): KClass<VM> {
        return ((javaClass.genericSuperclass as ParameterizedType)
            .actualTypeArguments[1] as Class<VM>).kotlin
    }

    @Suppress("UNCHECKED_CAST")
    protected open fun createBindingInstance(inflater: LayoutInflater, container: ViewGroup?): VB {
        val vbType = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
        val vbClass = vbType as Class<VB>
        val method = vbClass.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )

        return method.invoke(null, inflater, container, false) as VB
    }

    open fun getParameters(): Any? = null

    protected val viewModel: VM
        get() {
            return baseViewModel
        }

    private var baseBinding: VB? = null
    protected val binding get() = baseBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        createBindingInstance(inflater, container).also { baseBinding = it }.root


    override fun onDestroyView() {
        super.onDestroyView()
        baseBinding = null
    }

}


class GifListFragment :
    BaseFragment<FragmentGifListBinding, GifListViewModel>() {


    private val adapter: GifListAdapter by lazy {
        GifListAdapter(
            { viewModel.navigateToGifDetailFragment(this, it) },
            { viewModel.likeGif(it) })
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

                    if (viewModel.lastLinearOrGridState != state.linearOrGrid) {
                        viewModel.lastLinearOrGridState = state.linearOrGrid
                        if (state.linearOrGrid) {
                            val layout = binding!!.imageList.layoutManager as GridLayoutManager
                            val pos = layout.findLastVisibleItemPosition() - 3
                            binding!!.imageList.layoutManager = LinearLayoutManager(activity)
                            binding!!.imageList.scrollToPosition(pos)
                        } else {
                            val layout = binding!!.imageList.layoutManager as LinearLayoutManager
                            val pos = layout.findLastVisibleItemPosition() - 3
                            binding!!.imageList.layoutManager = GridLayoutManager(activity, 3)
                            binding!!.imageList.scrollToPosition(pos)
                        }
                    }

                }
            } catch (e: Exception) {
                e.message?.let { Log.i("GifListFragment", it) }
            }
        }
    }


}