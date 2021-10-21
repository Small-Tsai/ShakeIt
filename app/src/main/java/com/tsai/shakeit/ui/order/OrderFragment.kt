package com.tsai.shakeit.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.tsai.shakeit.databinding.FragmentOrderBinding

class OrderFragment : Fragment() {

    private lateinit var viewModel: OrderViewModel
    private lateinit var binding: FragmentOrderBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        binding = FragmentOrderBinding.inflate(inflater, container, false)

        val adapter = OrderAdapter(viewModel)
        viewModel.userOrderList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        binding.orderRev.adapter = adapter
        return binding.root
    }


}