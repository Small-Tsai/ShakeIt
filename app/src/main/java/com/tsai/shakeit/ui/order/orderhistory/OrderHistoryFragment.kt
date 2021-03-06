package com.tsai.shakeit.ui.order.orderhistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tsai.shakeit.NavDirections
import com.tsai.shakeit.databinding.OrderHistoryFragmentBinding
import com.tsai.shakeit.ext.getVmFactory

class OrderHistoryFragment : Fragment() {

    private val viewModel by viewModels<OrderHistoryViewModel> {
        getVmFactory()
    }

    private lateinit var binding: OrderHistoryFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = OrderHistoryFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        val adapter = OrderHistoryAdapter(viewModel)

        viewModel.navToOrder.observe(viewLifecycleOwner, {
            findNavController().navigate(NavDirections.navToOrder())
        })

        viewModel.orderHistory.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

        viewModel.navToOrderDetail.observe(viewLifecycleOwner, {
            it?.let { order ->
                findNavController().navigate(
                    NavDirections.navToOrderDetail(
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
