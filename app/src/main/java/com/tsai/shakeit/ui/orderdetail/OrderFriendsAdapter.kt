package com.tsai.shakeit.ui.orderdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tsai.shakeit.data.OrderProduct
import com.tsai.shakeit.databinding.OrderFriendsRowBinding
import com.tsai.shakeit.ui.orderdetail.OrderDetailAdapter.*
import com.tsai.shakeit.ui.orderdetail.OrderFriendsAdapter.*

class OrderFriendsAdapter(private val viewModel: OrderDetailViewModel) :
    ListAdapter<String, OrderFriendsViewHolder>(DiffCallback) {


    class OrderFriendsViewHolder(
        private val binding: OrderFriendsRowBinding,
        viewModel: OrderDetailViewModel
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(name: String) {
            binding.userName.text = name
            binding.executePendingBindings()
        }
    }

    private companion object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderFriendsViewHolder {
        return OrderFriendsViewHolder(
            OrderFriendsRowBinding
                .inflate(LayoutInflater.from(parent.context), parent, false),
            viewModel
        )
    }

    override fun onBindViewHolder(holder: OrderFriendsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}