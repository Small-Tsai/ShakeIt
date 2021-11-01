package com.tsai.shakeit.ui.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.databinding.FavoriteImageItemBinding

class FavoriteImageAdapter(val viewModel: FavoriteViewModel) :
    ListAdapter<Shop, FavoriteImageAdapter.FavoriteImageViewHolder>(DiffCallback) {

    inner class FavoriteImageViewHolder(
        private var binding: FavoriteImageItemBinding,
        viewModel: FavoriteViewModel
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(shop: Shop) {
            binding.viewModel = viewModel
            binding.shop = shop
            binding.executePendingBindings()
        }

    }

    private companion object DiffCallback : DiffUtil.ItemCallback<Shop>() {
        override fun areItemsTheSame(oldItem: Shop, newItem: Shop): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Shop, newItem: Shop): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteImageViewHolder {
        return FavoriteImageViewHolder(
            FavoriteImageItemBinding
                .inflate(LayoutInflater.from(parent.context), parent, false),
            viewModel
        )
    }

    override fun onBindViewHolder(holder: FavoriteImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}