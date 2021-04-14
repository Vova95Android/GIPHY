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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.forgiphyapp.R
import com.example.forgiphyapp.adapters.GifListAdapter
import com.example.forgiphyapp.database.GifDatabase
import com.example.forgiphyapp.databinding.FragmentGifListBinding
import com.example.forgiphyapp.vievModelsFactory.GifListViewModelFactory
import com.example.forgiphyapp.viewModels.GifListViewModel


/**
 * A simple [Fragment] subclass.
 * Use the [GifListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GifListFragment : Fragment() {

    private lateinit var viewModel: GifListViewModel
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

        val dataSource = GifDatabase.getInstance(application).gifDatabaseDao

        val viewModelFactory = GifListViewModelFactory(dataSource)

        viewModel = ViewModelProvider(this,viewModelFactory).get(GifListViewModel::class.java)

        viewModel.saveGifs.observe(viewLifecycleOwner, Observer {
            viewModel.actualData=it
        })
        binding!!.viewModel = viewModel
        binding!!.imageList.adapter = GifListAdapter(GifListAdapter.onClickListener {
            this.findNavController()
                .navigate(GifListFragmentDirections
                    .actionGifListFragmentToGifDetailFragment(it.id,it.images.original.url,it.images.preview_gif.url))
        })

        binding!!.lifecycleOwner = this
        return binding!!.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.linearOrGrid.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding!!.imageList.layoutManager = GridLayoutManager(activity, 3)
            } else {
                binding!!.imageList.layoutManager = LinearLayoutManager(activity)
            }
        })

        binding!!.spinnerLimit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                viewModel.setLimits(binding!!.spinnerLimit.selectedItem.toString().toInt())
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        binding!!.editTextNewData.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if ((s != null) && (s.isNotEmpty()) && (count != before)) {
                    viewModel.searchNewData(s.toString())
                    viewModel.getGiphyRealEstateProperties("g")
                }
            }
        })
        viewModel.getGiphyRealEstateProperties("g")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}