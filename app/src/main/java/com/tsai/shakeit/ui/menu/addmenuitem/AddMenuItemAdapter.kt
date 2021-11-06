package com.tsai.shakeit.ui.menu.addmenuitem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tsai.shakeit.databinding.AddMenuItemBtnBinding
import com.tsai.shakeit.databinding.AddMenuItemRowBinding
import com.tsai.shakeit.databinding.AddMenuItemTitleBinding

class AddMenuItemAdapter(private val viewModel: AddMenuItemViewModel) :
    ListAdapter<AddMenuItem, RecyclerView.ViewHolder>(DiffCallback) {


    class AddItemTitleViewHolder(
        private val binding: AddMenuItemTitleBinding,
        private val viewModel: AddMenuItemViewModel
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(title: String) {
            binding.viewModel = viewModel
            binding.addmenuTitle.text = title
            binding.executePendingBindings()
        }
    }

    class AddItemViewHolder(
        private val binding: AddMenuItemRowBinding,
        private val viewModel: AddMenuItemViewModel
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(content: HashMap<String, Int>) {
            binding.position = adapterPosition
            binding.viewModel = viewModel
            viewModel.binding = binding
            binding.executePendingBindings()
        }
    }

    class AddItemBtnViewHolder(
        private val binding: AddMenuItemBtnBinding,
        private val viewModel: AddMenuItemViewModel
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(type: Int) {
            binding.type = type
            binding.viewModel = viewModel
            binding.position = adapterPosition
            binding.executePendingBindings()
        }
    }

    private companion object DiffCallback : DiffUtil.ItemCallback<AddMenuItem>() {
        override fun areItemsTheSame(oldItem: AddMenuItem, newItem: AddMenuItem): Boolean {
            return oldItem == newItem

        }

        override fun areContentsTheSame(oldItem: AddMenuItem, newItem: AddMenuItem): Boolean {
            return oldItem == newItem
        }

        private const val ITEM_VIEW_TYPE_TITLE = 0x00
        private const val ITEM_VIEW_TYPE_CONTENT = 0x01
        private const val ITEM_VIEW_TYPE_BTN = 0x02

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {

            ITEM_VIEW_TYPE_TITLE -> AddItemTitleViewHolder(
                AddMenuItemTitleBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ), viewModel
            )

            ITEM_VIEW_TYPE_CONTENT -> AddItemViewHolder(
                AddMenuItemRowBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ), viewModel
            )

            ITEM_VIEW_TYPE_BTN -> AddItemBtnViewHolder(
                AddMenuItemBtnBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ), viewModel
            )
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AddItemTitleViewHolder -> {
                holder.bind(
                    (getItem(position) as AddMenuItem.Title).title
                )
            }

            is AddItemViewHolder -> {
                holder.bind(
                    (getItem(position) as AddMenuItem.Detail).content
                )
            }

            is AddItemBtnViewHolder -> {
                holder.bind((getItem(position) as AddMenuItem.Button).text)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is AddMenuItem.Title -> ITEM_VIEW_TYPE_TITLE
            is AddMenuItem.Detail -> ITEM_VIEW_TYPE_CONTENT
            is AddMenuItem.Button -> ITEM_VIEW_TYPE_BTN
            else -> throw ClassCastException("Unknown viewType")
        }
    }
}

sealed class AddMenuItem {
    data class Title(val title: String) : AddMenuItem()
    data class Detail(val content: HashMap<String, Int>) : AddMenuItem()
    data class Button(val text: Int) : AddMenuItem()
}
