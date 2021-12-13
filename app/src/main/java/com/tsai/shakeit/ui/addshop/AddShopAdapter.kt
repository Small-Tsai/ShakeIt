package com.tsai.shakeit.ui.addshop

import android.app.TimePickerDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tsai.shakeit.databinding.ShopDateRowBinding
import com.tsai.shakeit.ui.addshop.AddShopAdapter.DateViewHolder
import java.util.*

class AddShopAdapter(private val viewModel: AddShopViewModel) :
    ListAdapter<HashMap<String, String>, DateViewHolder>(DiffCallback) {

    private companion object DiffCallback : DiffUtil.ItemCallback<HashMap<String, String>>() {
        override fun areItemsTheSame(
            oldItem: HashMap<String, String>,
            newItem: HashMap<String, String>
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: HashMap<String, String>,
            newItem: HashMap<String, String>
        ): Boolean {
            return oldItem == newItem
        }
    }

    class DateViewHolder(
        private val binding: ShopDateRowBinding,
        private val viewModel: AddShopViewModel
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(date: HashMap<String, String>) {

            val hashKey = date.keys.first()
            val hashValue = date[hashKey]
            binding.dateTxt.text = hashKey

            hashValue?.let {
                if (it.length > 6) {
                    viewModel.timeOpen.value = date[hashKey]?.substring(0, 6)
                    viewModel.timeClose.value = date[hashKey]?.substring(7)
                }
            }

            binding.viewModel = viewModel
            binding.viewHolder = this

            // Get Current Time
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)

            binding.openTime.setOnClickListener {
                // Launch Time Picker Dialog
                TimePickerDialog(binding.root.context, { _, hour, minute ->

                    viewModel.getAdapterPosition(absoluteAdapterPosition)

                    binding.openTime.setText(String.format("%02d:%02d", hour, minute))
                }, hour, minute, true).show()
                }

                binding.closeTime.setOnClickListener {
                    // Launch Time Picker Dialog
                    TimePickerDialog(binding.root.context, { _, hour, minute ->

                        viewModel.getAdapterPosition(absoluteAdapterPosition)

                        binding.closeTime.setText(String.format("%02d:%02d", hour, minute))
                    }, hour, minute, true).show()
                    }
                    binding.executePendingBindings()
                }
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
                return DateViewHolder(
                    ShopDateRowBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                    viewModel
                )
            }

            override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
                holder.bind(getItem(position))
            }
        }
        