package com.tsai.shakeit.ui.orderdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tsai.shakeit.data.User
import com.tsai.shakeit.databinding.OrderFriendsRowBinding
import com.tsai.shakeit.ui.orderdetail.OrderFriendsAdapter.*

class OrderFriendsAdapter :
    ListAdapter<User, OrderFriendsViewHolder>(DiffCallback) {

    class OrderFriendsViewHolder(
        private val binding: OrderFriendsRowBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.userImg = user.user_Image
            binding.userName.text = user.user_Name
            binding.executePendingBindings()
        }
    }

    private companion object DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.user_Id == newItem.user_Id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.user_Id == newItem.user_Id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderFriendsViewHolder {
        return OrderFriendsViewHolder(
            OrderFriendsRowBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: OrderFriendsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
