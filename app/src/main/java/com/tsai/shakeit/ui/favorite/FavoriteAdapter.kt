package com.tsai.shakeit.ui.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.databinding.*

class FavoriteAdapter(val viewModel: FavoriteViewModel) :
    ListAdapter<Favorite, RecyclerView.ViewHolder>(DiffCallback) {

    inner class ShopImageViewHolder(private var binding: FavoriteShopimgRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(img: List<Shop>) {
            val adapter = FavoriteImageAdapter(viewModel)
            adapter.submitList(img)
            binding.favoriteShopImgRev.adapter = adapter
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }

    inner class TitleViewHolder(private var binding: FavoriteShoptitleRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(title: String) {
            binding.title = title
            binding.executePendingBindings()
        }
    }

    private companion object DiffCallback : DiffUtil.ItemCallback<Favorite>() {
        override fun areItemsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
            return oldItem == newItem
        }

        private const val ITEM_VIEW_TYPE_TITLE = 0x00
        private const val ITEM_VIEW_TYPE_FAVORITE = 0x01
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_TITLE -> TitleViewHolder(
                FavoriteShoptitleRowBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            ITEM_VIEW_TYPE_FAVORITE -> ShopImageViewHolder(
                FavoriteShopimgRowBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ShopImageViewHolder -> {
                holder.bind((getItem(position) as Favorite.ShopImg).img)
            }
            is TitleViewHolder -> {
                holder.bind((getItem(position) as Favorite.ShopName).name)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Favorite.ShopImg -> ITEM_VIEW_TYPE_FAVORITE
            is Favorite.ShopName -> ITEM_VIEW_TYPE_TITLE
            else -> throw ClassCastException("Unknown viewType")
        }
    }
}
