package com.tsai.shakeit.ui.home.search

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tsai.shakeit.data.Product
import com.tsai.shakeit.databinding.SearchListBinding
import com.tsai.shakeit.ui.home.search.SearchAdapter.SearchProductViewHolder

class SearchAdapter : ListAdapter<Product, SearchProductViewHolder>(Diff) {

    private lateinit var context: Context


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

    class SearchProductViewHolder(private var binding: SearchListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.product = product
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchProductViewHolder {
        context = parent.context
        return SearchProductViewHolder(
            SearchListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: SearchProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}