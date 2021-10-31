package com.tsai.shakeit.ui.addshop

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tsai.shakeit.databinding.ShopDateRowBinding
import com.tsai.shakeit.ui.addshop.AddShopAdapter.DateViewHolder

class AddShopAdapter : ListAdapter<String, DateViewHolder>(DiffCallback) {


    private companion object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }

    }

    class DateViewHolder(private val binding: ShopDateRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(date: String) {
            binding.textView10.text = date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        return DateViewHolder(
            ShopDateRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}
