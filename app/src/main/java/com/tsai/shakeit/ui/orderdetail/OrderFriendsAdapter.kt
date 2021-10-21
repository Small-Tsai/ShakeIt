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
    ListAdapter<OrderProduct, OrderFriendsViewHolder>(DiffCallback) {

    var count = 0

    fun submitCount(size:Int){
        count = size
    }

    class OrderFriendsViewHolder(
        private val binding: OrderFriendsRowBinding,
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

    override fun getItemCount(): Int = count

}