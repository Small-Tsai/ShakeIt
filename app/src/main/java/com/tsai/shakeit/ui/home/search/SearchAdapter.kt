package com.tsai.shakeit.ui.home.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tsai.shakeit.data.Product
import com.tsai.shakeit.databinding.SearchListBinding
import com.tsai.shakeit.ui.home.HomeViewModel
import com.tsai.shakeit.ui.home.search.SearchAdapter.SearchProductViewHolder

class SearchAdapter(private val viewModel: HomeViewModel) :
    ListAdapter<Product, SearchProductViewHolder>(Diff) {

    companion object Diff : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: Product,
            newItem: Product
        ): Boolean {
            return oldItem == newItem
        }
    }

    class SearchProductViewHolder(
        private val binding: SearchListBinding,
        private val viewModel: HomeViewModel
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.viewModel = viewModel
            binding.product = product
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchProductViewHolder {

        return SearchProductViewHolder(
            SearchListBinding.inflate(LayoutInflater.from(parent.context), parent, false), viewModel
        )
    }

    override fun onBindViewHolder(holder: SearchProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}