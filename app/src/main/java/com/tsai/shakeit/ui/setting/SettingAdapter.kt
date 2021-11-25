package com.tsai.shakeit.ui.setting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tsai.shakeit.MainViewModel
import com.tsai.shakeit.databinding.SettingItemRowBinding

class SettingAdapter(
    private val viewModel: SettingViewModel,
    private val mainViewModel: MainViewModel
) :
    ListAdapter<String, SettingAdapter.SettingListViewHolder>(DiffCallBack) {

    private companion object DiffCallBack : ItemCallback<String>() {

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    class SettingListViewHolder(private val binding: SettingItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(shopName: String, viewModel: SettingViewModel, mainViewModel: MainViewModel) {
            binding.shopName = shopName
            binding.viewModel = viewModel
            binding.mainViewModel = mainViewModel
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingListViewHolder {
        return SettingListViewHolder(
            SettingItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: SettingListViewHolder, position: Int) {
        holder.bind(getItem(position), viewModel, mainViewModel)
    }
}
