package com.example.forgiphyapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.forgiphyapp.R
import com.example.forgiphyapp.database.GifData
import com.example.forgiphyapp.databinding.GifItemBinding

class GifListAdapter(
    private val listener: OnClickListener
) :
    ListAdapter<GifData, GifListAdapter.GifListViewHolder>(DiffCallback()) {


    override fun onBindViewHolder(holder: GifListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifListViewHolder {
        return GifListViewHolder(GifItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    class GifListViewHolder(private var binding: GifItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: GifData, listener: OnClickListener) {

            if ((data.id != "error") && (data.full_url != "error")) {
                itemView.setOnClickListener { listener.onClick(data) }
                binding.executePendingBindings()
                val drawable = if (data.like) AppCompatResources.getDrawable(
                    binding.imageLike.context,
                    R.drawable.ic_like
                )
                else AppCompatResources.getDrawable(
                    binding.imageLike.context,
                    R.drawable.ic_no_like
                )
                binding.imageLike.setImageDrawable(drawable)

                data.preview_url?.let {
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
    }

    class OnClickListener(val clickListener: (data: GifData) -> Unit) {
        fun onClick(data: GifData) = clickListener(data)
    }

    class DiffCallback : DiffUtil.ItemCallback<GifData>() {
        override fun areItemsTheSame(oldItem: GifData, newItem: GifData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GifData, newItem: GifData): Boolean {
            return oldItem.id == newItem.id
        }
    }
}