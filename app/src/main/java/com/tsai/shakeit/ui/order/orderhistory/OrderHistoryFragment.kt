package com.tsai.shakeit.ui.order.orderhistory

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tsai.shakeit.R
import com.tsai.shakeit.databinding.FragmentOrderBinding
import com.tsai.shakeit.databinding.OrderHistoryFragmentBinding
import com.tsai.shakeit.ext.getVmFactory
import com.tsai.shakeit.ui.order.OrderAdapter
import com.tsai.shakeit.ui.order.OrderFragmentDirections
import com.tsai.shakeit.ui.order.OrderViewModel

class OrderHistoryFragment : Fragment() {

    private val viewModel by viewModels<OrderHistoryViewModel> {
        getVmFactory()
    }

    private lateinit var binding: OrderHistoryFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = OrderHistoryFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        val adapter = OrderHistoryAdapter(viewModel)

        viewModel.navToOrder.observe(viewLifecycleOwner, {
            findNavController().navigate(OrderHistoryFragmentDirections.navToOrder())
        })

        viewModel.orderHistory.observe(viewLifecycleOwner,{
            adapter.submitList(it)
        })

        viewModel.navToOrderDetail.observe(viewLifecycleOwner, {
            it?.let { order ->
                findNavController().navigate(
                    OrderFragmentDirections.navToOrderDetail(
                        order,
                        "history"
                    )
                )
            }
        })

        binding.historyRev.adapter = adapter
        return binding.root
    }

}