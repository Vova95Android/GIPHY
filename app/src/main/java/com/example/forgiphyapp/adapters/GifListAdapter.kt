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
    private val clickListener: (GifData) -> Unit,
    private val likeListener: (GifData) -> Unit
) :
    ListAdapter<GifData, GifListAdapter.GifListViewHolder>(DiffCallback()) {


    override fun onBindViewHolder(holder: GifListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, false, clickListener, likeListener)
    }

    override fun onBindViewHolder(
        holder: GifListViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val item = getItem(position)
        val onlyLike: Boolean = if (payloads.isNotEmpty()) {
            payloads[0] as Boolean
        } else { false }
        holder.bind(item, onlyLike, clickListener, likeListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifListViewHolder {
        return GifListViewHolder(GifItemBinding.inflate(LayoutInflater.from(parent.context)))
    }


    class GifListViewHolder(private var binding: GifItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            data: GifData,
            onliLike: Boolean,
            clickListener: (GifData) -> Unit,
            likeListener: (GifData) -> Unit
        ) {

            if (!onliLike) {
                itemView.setOnClickListener { clickListener(data) }
                binding.imageLike.setOnClickListener { likeListener(data) }


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
                val drawable = if (data.like) AppCompatResources.getDrawable(
                    binding.imageLike.context,
                    R.drawable.ic_like
                )
                else AppCompatResources.getDrawable(
                    binding.imageLike.context,
                    R.drawable.ic_no_like
                )
                binding.imageLike.setImageDrawable(drawable)
        }
    }

    class OnClickListener(val clickListener: (data: GifData) -> Unit) {
        fun onClick(data: GifData) = clickListener(data)
    }

    class OnLikeListener(val likeListener: (data: GifData) -> Unit) {
        fun onClick(data: GifData) = likeListener(data)
    }

    class DiffCallback : DiffUtil.ItemCallback<GifData>() {
        override fun areItemsTheSame(oldItem: GifData, newItem: GifData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GifData, newItem: GifData): Boolean {
            return oldItem.like == newItem.like
        }

        override fun getChangePayload(oldItem: GifData, newItem: GifData): Any {
            return oldItem.like == newItem.like
        }
    }
}