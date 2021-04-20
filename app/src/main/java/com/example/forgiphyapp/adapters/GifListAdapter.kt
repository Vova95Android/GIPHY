package com.example.forgiphyapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.forgiphyapp.api.Data
import com.example.forgiphyapp.api.GifParams
import com.example.forgiphyapp.databinding.GifItemBinding

class GifListAdapter(val listener: onClickListener) :
    ListAdapter<Data, GifListAdapter.GifPropertyViewHolder>(DiffCallback) {
    companion object DiffCallback : DiffUtil.ItemCallback<Data>() {
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.id == newItem.id
        }
    }

    class GifPropertyViewHolder(private var binding: GifItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Data) {
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifPropertyViewHolder {
        return GifPropertyViewHolder(GifItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: GifPropertyViewHolder, position: Int) {
        val gifProperty = getItem(position)
        holder.itemView.setOnClickListener {
            listener.onClick(gifProperty)
        }
        holder.bind(gifProperty)
    }

    class onClickListener(val clickListener: (marsProperty: Data) -> Unit) {
        fun onClick(marsProperty: Data) = clickListener(marsProperty)
    }
}