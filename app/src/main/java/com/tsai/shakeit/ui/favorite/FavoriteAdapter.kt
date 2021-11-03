package com.tsai.shakeit.ui.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.databinding.FavoriteShopimgRowBinding
import com.tsai.shakeit.databinding.FavoriteShoptitleRowBinding

class FavoriteAdapter(val viewModel: FavoriteViewModel) :
    ListAdapter<FavoriteItem, RecyclerView.ViewHolder>(DiffCallback) {

    inner class ShopImageViewHolder(private var binding: FavoriteShopimgRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(favorite: List<Shop>) {
            val adapter = FavoriteImageAdapter(viewModel)
            adapter.submitList(favorite)
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

    private companion object DiffCallback : DiffUtil.ItemCallback<FavoriteItem>() {
        override fun areItemsTheSame(oldItem: FavoriteItem, newItem: FavoriteItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: FavoriteItem, newItem: FavoriteItem): Boolean {
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
                holder.bind((getItem(position) as FavoriteItem.ShopImg).img)
            }
            is TitleViewHolder -> {
                holder.bind((getItem(position) as FavoriteItem.ShopName).name)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is FavoriteItem.ShopImg -> ITEM_VIEW_TYPE_FAVORITE
            is FavoriteItem.ShopName -> ITEM_VIEW_TYPE_TITLE
            else -> throw ClassCastException("Unknown viewType")
        }
    }
}

sealed class FavoriteItem {
    data class ShopName(val name: String) : FavoriteItem()
    data class ShopImg(val img: List<Shop>) : FavoriteItem()
}