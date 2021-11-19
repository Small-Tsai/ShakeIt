package com.tsai.shakeit.ui.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tsai.shakeit.data.Product
import com.tsai.shakeit.databinding.MenuProductRowBinding
import com.tsai.shakeit.databinding.MenuTitleRowBinding

class MenuAdapter(val viewModel : MenuViewModel) : ListAdapter<Menu, RecyclerView.ViewHolder>(DiffCallback) {

    inner class ProductViewHolder(private var binding: MenuProductRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product, viewModel: MenuViewModel) {
            binding.product = product
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }

    inner class TitleViewHolder(private var binding: MenuTitleRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(title: String) {
            binding.typeTitle.text = title
            binding.executePendingBindings()
        }
    }

    private companion object DiffCallback : DiffUtil.ItemCallback<Menu>() {
        override fun areItemsTheSame(oldItem: Menu, newItem: Menu): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Menu, newItem: Menu): Boolean {
            return oldItem == newItem
        }

        private const val ITEM_VIEW_TYPE_TITLE = 0x00
        private const val ITEM_VIEW_TYPE_PRODUCT = 0x01
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_TITLE -> TitleViewHolder(
                MenuTitleRowBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            ITEM_VIEW_TYPE_PRODUCT -> ProductViewHolder(
                MenuProductRowBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProductViewHolder -> {
                holder.bind((getItem(position) as Menu.MenuProduct).product , viewModel)
            }
            is TitleViewHolder -> {
                holder.bind((getItem(position) as Menu.Title).type)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Menu.MenuProduct -> ITEM_VIEW_TYPE_PRODUCT
            is Menu.Title -> ITEM_VIEW_TYPE_TITLE
            else -> throw ClassCastException("Unknown viewType")
        }
    }
}

sealed class Menu{
    data class Title (val type: String):Menu()
    data class MenuProduct (val product: Product):Menu()
}
