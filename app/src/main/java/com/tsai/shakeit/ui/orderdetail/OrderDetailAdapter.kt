package com.tsai.shakeit.ui.orderdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.tsai.shakeit.data.OrderProduct
import com.tsai.shakeit.databinding.OrderDetailRowBinding
import com.tsai.shakeit.databinding.OrderDetailRowBtnBinding
import com.tsai.shakeit.ext.mToast
import com.tsai.shakeit.util.Logger

class OrderDetailAdapter(private val viewModel: OrderDetailViewModel) :
    ListAdapter<OrderDetail, RecyclerView.ViewHolder>(DiffCallback) {

    private val viewBinderHelper = ViewBinderHelper()

    class OrderProductViewHolder(
        private val binding: OrderDetailRowBinding,
        private val viewModel: OrderDetailViewModel,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        var swipeRevealLayout: SwipeRevealLayout = binding.swipeLayout

        fun bind(orderProduct: OrderProduct) {

            binding.orderProduct = orderProduct
            binding.viewModel = viewModel
            binding.executePendingBindings()

            binding.swipeDeleteBtn.setOnClickListener {

                viewModel.notifyOrderChange()

                viewModel.removeOrderProduct(
                    orderProductId = orderProduct.orderProduct_Id,
                )

                swipeRevealLayout.close(true)
            }
        }
    }

    class OrderProductBtnViewHolder(
        private val binding: OrderDetailRowBtnBinding,
        private val viewModel: OrderDetailViewModel
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(name: String) {
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }

    private companion object DiffCallback : DiffUtil.ItemCallback<OrderDetail>() {
        override fun areItemsTheSame(oldItem: OrderDetail, newItem: OrderDetail): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: OrderDetail, newItem: OrderDetail): Boolean {
            return oldItem == newItem
        }

        private const val ITEM_VIEW_TYPE_PRODUCT = 0x00
        private const val ITEM_VIEW_TYPE_BTN = 0x01

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {

            ITEM_VIEW_TYPE_PRODUCT -> OrderProductViewHolder(
                OrderDetailRowBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ), viewModel
            )

            ITEM_VIEW_TYPE_BTN -> OrderProductBtnViewHolder(
                OrderDetailRowBtnBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ), viewModel
            )
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is OrderProductViewHolder -> {
                viewBinderHelper.bind(holder.swipeRevealLayout, position.toString())
                holder.bind(
                    (getItem(position) as OrderDetail.MyOrderProduct).orderProduct
                )
            }
            is OrderProductBtnViewHolder -> {
                holder.bind((getItem(position) as OrderDetail.AddProductBtn).name)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is OrderDetail.MyOrderProduct -> ITEM_VIEW_TYPE_PRODUCT
            is OrderDetail.AddProductBtn -> ITEM_VIEW_TYPE_BTN
            else -> throw ClassCastException("Unknown viewType")
        }
    }

}

sealed class OrderDetail {
    data class MyOrderProduct(val orderProduct: OrderProduct) : OrderDetail()
    data class AddProductBtn(val name: String) : OrderDetail()
}