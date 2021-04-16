package com.example.forgiphyapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.forgiphyapp.api.Data
import com.example.forgiphyapp.databinding.GifItemBinding

class GifListPagingAdapter(val listener: onClickListener):
    PagingDataAdapter<Data, GifListPagingAdapter.GifListViewHolder>(DiffCallback) {


    override fun onBindViewHolder(holder: GifListViewHolder, position: Int) {
        val item=getItem(position)
        holder.itemView.setOnClickListener {
            listener.onClick(item!!)
        }
        holder.bind(item!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifListViewHolder {
       return GifListViewHolder(GifItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    class GifListViewHolder(private var binding: GifItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Data) {
            binding.data = data
            binding.executePendingBindings()
        }
    }

    class onClickListener(val clickListener: (marsProperty: Data) -> Unit) {
        fun onClick(marsProperty: Data) = clickListener(marsProperty)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Data>() {
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.id == newItem.id
        }
    }

}