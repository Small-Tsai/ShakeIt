package com.tsai.shakeit.ui.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tsai.shakeit.data.Menu
import com.tsai.shakeit.data.Order
import com.tsai.shakeit.databinding.OrderRowBinding
import com.tsai.shakeit.ui.order.OrderAdapter.*

class OrderAdapter(val viewModel: OrderViewModel) :
    ListAdapter<Order, OrderViewHolder>(DiffCallback) {


    inner class OrderViewHolder(private var binding: OrderRowBinding, viewModel: OrderViewModel) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.order = order
            binding.executePendingBindings()
        }

    }

    private companion object DiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        return OrderViewHolder(
            OrderRowBinding
                .inflate(LayoutInflater.from(parent.context), parent, false),
            viewModel
        )
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}