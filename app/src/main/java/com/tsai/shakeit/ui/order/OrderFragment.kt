package com.tsai.shakeit.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tsai.shakeit.databinding.FragmentOrderBinding
import com.tsai.shakeit.ext.getVmFactory
import com.tsai.shakeit.ui.orderdetail.OrderDetailFragmentArgs
import com.tsai.shakeit.ui.orderdetail.OrderDetailViewModel

class OrderFragment : Fragment() {

    private val viewModel by viewModels<OrderViewModel> {
        getVmFactory()
    }

    private lateinit var binding: FragmentOrderBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentOrderBinding.inflate(inflater, container, false)

        val adapter = OrderAdapter(viewModel)
        viewModel.userOrderList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        viewModel.navToOrderDetail.observe(viewLifecycleOwner, Observer {
            it?.let { findNavController().navigate(OrderFragmentDirections.navToOrderDetail(it)) }
        })

        viewModel.shopImg.observe(viewLifecycleOwner, Observer {
            it?.let { adapter.submitImg(it) }
            adapter.notifyDataSetChanged()
        })

        binding.orderRev.adapter = adapter
        return binding.root
    }


}