package com.tsai.shakeit.ui.orderdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tsai.shakeit.data.OrderProduct
import com.tsai.shakeit.databinding.OrderDetailRowBinding
import com.tsai.shakeit.ui.orderdetail.OrderDetailAdapter.*

class OrderDetailAdapter(private val viewModel: OrderDetailViewModel) :
    ListAdapter<OrderProduct, OrderProductViewHolder>(DiffCallback) {

    class OrderProductViewHolder(
        private val binding: OrderDetailRowBinding,
        viewModel: OrderDetailViewModel
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(orderProduct: OrderProduct) {
            binding.orderProduct = orderProduct
            binding.executePendingBindings()
        }
    }

    private companion object DiffCallback : DiffUtil.ItemCallback<OrderProduct>() {
        override fun areItemsTheSame(oldItem: OrderProduct, newItem: OrderProduct): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: OrderProduct, newItem: OrderProduct): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductViewHolder {
        return OrderProductViewHolder(
            OrderDetailRowBinding
                .inflate(LayoutInflater.from(parent.context), parent, false),
            viewModel
        )
    }

    override fun onBindViewHolder(holder: OrderProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}