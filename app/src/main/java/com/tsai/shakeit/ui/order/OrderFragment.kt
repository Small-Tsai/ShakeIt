package com.tsai.shakeit.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.tsai.shakeit.databinding.FragmentOrderBinding
import com.tsai.shakeit.ext.getVmFactory
import com.tsai.shakeit.util.Logger

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
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val adapter = OrderAdapter(viewModel)

        viewModel.userOrderList.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

        viewModel.navToOrderDetail.observe(viewLifecycleOwner, {
            it?.let { order ->
                findNavController().navigate(
                    OrderFragmentDirections.navToOrderDetail(
                        order,
                        order.shop_Img,
                        "current"
                    )
                )
            }
        })

        viewModel.shopId.observe(viewLifecycleOwner, {
            it?.let { findNavController().navigate(OrderFragmentDirections.navToSendComment(it)) }
        })

        viewModel.navToOrderHistory.observe(viewLifecycleOwner,{
            it?.let { findNavController().navigate(OrderFragmentDirections.navToOrderHistory()) }
        })

        viewModel.orderProduct.observe(viewLifecycleOwner,{
            Logger.d("$it")
        })

        binding.orderRev.adapter = adapter
        return binding.root
    }


}