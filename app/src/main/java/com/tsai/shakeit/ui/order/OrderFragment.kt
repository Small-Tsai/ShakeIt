package com.tsai.shakeit.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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

        viewModel.navToOrderDetail.observe(viewLifecycleOwner, Observer {
            it?.let { findNavController().navigate(OrderFragmentDirections.navToOrderDetail(it)) }
        })

        binding.orderRev.adapter = adapter
        return binding.root
    }


}