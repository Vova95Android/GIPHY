package com.example.forgiphyapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.forgiphyapp.R
import com.example.forgiphyapp.api.Data
import com.example.forgiphyapp.databinding.GifItemBinding

class GifListPagingAdapter(private val listener: OnClickListener) :
    PagingDataAdapter<Data, GifListPagingAdapter.GifListViewHolder>(DiffCallback()) {


    override fun onBindViewHolder(holder: GifListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item!!, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifListViewHolder {
        return GifListViewHolder(GifItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    class GifListViewHolder(private var binding: GifItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Data, listener: OnClickListener) {
            itemView.setOnClickListener { listener.onClick(data) }
            binding.executePendingBindings()
            data.images.preview_gif.url?.let {

                Glide.with(binding.gifItem.context)
                    .load(
                        it.toUri().buildUpon().scheme("https").build()
                    )
                    .apply(
                        RequestOptions()
                            .placeholder(R.drawable.loading_animation)
                            .error(R.drawable.ic_broken_image)
                    )
                    .into(binding.gifItem)
            }

        }
    }

    class OnClickListener(val clickListener: (data: Data) -> Unit) {
        fun onClick(data: Data) = clickListener(data)
    }

    class DiffCallback : DiffUtil.ItemCallback<Data>() {
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.id == newItem.id
        }
    }

}