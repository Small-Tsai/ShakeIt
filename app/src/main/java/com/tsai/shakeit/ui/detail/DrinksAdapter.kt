package com.tsai.shakeit.ui.detail

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tsai.shakeit.databinding.DrinksSelectRowBinding
import com.tsai.shakeit.databinding.DrinksSelectTitleBinding
import com.tsai.shakeit.ui.home.TAG
import com.tsai.shakeit.util.MyContext

class DrinksAdapter(val viewModel: DrinksDetailViewModel) :
    ListAdapter<DrinksDetail, RecyclerView.ViewHolder>(DiffCallback) {

    inner class ContentViewHolder(private var binding: DrinksSelectRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(content: String, viewModel: DrinksDetailViewModel) {
            binding.viewModel = viewModel
            binding.viewHolder = this
            binding.content = content
            binding.executePendingBindings()
        }
    }

    inner class TitleViewHolder(private var binding: DrinksSelectTitleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(title: String) {
            binding.selectTitle.text = title
            binding.executePendingBindings()
        }
    }

    private companion object DiffCallback : DiffUtil.ItemCallback<DrinksDetail>() {
        override fun areItemsTheSame(oldItem: DrinksDetail, newItem: DrinksDetail): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: DrinksDetail, newItem: DrinksDetail): Boolean {
            return oldItem == newItem
        }

        private const val ITEM_VIEW_TYPE_TITLE = 0x00
        private const val ITEM_VIEW_TYPE_CONTENT = 0x01
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_TITLE -> TitleViewHolder(
                DrinksSelectTitleBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            ITEM_VIEW_TYPE_CONTENT -> ContentViewHolder(
                DrinksSelectRowBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is ContentViewHolder -> {
                holder.bind((getItem(position) as DrinksDetail.DetailContent).content, viewModel)
            }
            is TitleViewHolder -> {
                holder.bind((getItem(position) as DrinksDetail.DetailTitle).type)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DrinksDetail.DetailContent -> ITEM_VIEW_TYPE_CONTENT
            is DrinksDetail.DetailTitle -> ITEM_VIEW_TYPE_TITLE
            else -> throw ClassCastException("Unknown viewType")
        }
    }
}

sealed class DrinksDetail {
    data class DetailTitle(val type: String) : DrinksDetail()
    data class DetailContent(val content: String) : DrinksDetail()
}