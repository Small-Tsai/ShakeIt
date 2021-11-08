package com.tsai.shakeit.ui.order.orderhistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tsai.shakeit.data.Order
import com.tsai.shakeit.databinding.OrderHistoryRowBinding
import com.tsai.shakeit.databinding.OrderRowBinding
import com.tsai.shakeit.ui.order.OrderAdapter.OrderViewHolder

class OrderHistoryAdapter(val viewModel: OrderHistoryViewModel) :
    ListAdapter<Order, OrderHistoryAdapter.OrderHistoryViewHolder>(DiffCallback) {

    inner class OrderHistoryViewHolder(private var binding: OrderHistoryRowBinding, viewModel: OrderHistoryViewModel) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.order = order
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }

    private companion object DiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.order_Id == newItem.order_Id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        return OrderHistoryViewHolder(
            OrderHistoryRowBinding
                .inflate(LayoutInflater.from(parent.context), parent, false),
            viewModel
        )
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}