package com.tsai.shakeit.ui.addshop

import android.app.TimePickerDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tsai.shakeit.databinding.ShopDateRowBinding
import com.tsai.shakeit.ui.addshop.AddShopAdapter.DateViewHolder
import java.util.*

class AddShopAdapter : ListAdapter<String, DateViewHolder>(DiffCallback) {


    private companion object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }

    }

    class DateViewHolder(private val binding: ShopDateRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(date: String) {
            binding.textView10.text = date

            // Get Current Time
            val c = Calendar.getInstance();
            val hour = c.get(Calendar.HOUR_OF_DAY);
            val minute = c.get(Calendar.MINUTE);

            binding.openTime.setOnClickListener {
                // Launch Time Picker Dialog
                TimePickerDialog(binding.root.context, { _, hour, minute ->

                        binding.openTime.setText(String.format("%02d:%02d", hour, minute))

                }, hour, minute, true).show()
            }

            binding.closeTime.setOnClickListener {
                // Launch Time Picker Dialog
                TimePickerDialog(binding.root.context, { _, hour, minute ->
                    binding.closeTime.setText(String.format("%02d:%02d", hour, minute))
                }, hour, minute, true).show()
            }

            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        return DateViewHolder(
            ShopDateRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}
