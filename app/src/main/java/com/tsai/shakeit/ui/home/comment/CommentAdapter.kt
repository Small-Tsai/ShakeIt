package com.tsai.shakeit.ui.home.comment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tsai.shakeit.data.Comment
import com.tsai.shakeit.databinding.CommentBinding

class CommentAdapter : ListAdapter<Comment, CommentAdapter.CommentViewHolder>(Diff) {

    companion object Diff : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: Comment,
            newItem: Comment
        ): Boolean {
            return oldItem.user?.user_Id == newItem.user?.user_Id
        }

    }

    class CommentViewHolder(private var binding: CommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            binding.comment = comment
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {

        return CommentViewHolder(
            CommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}