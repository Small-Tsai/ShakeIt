package com.tsai.shakeit.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tsai.shakeit.MainViewModel
import com.tsai.shakeit.databinding.FragmentOrderBinding
import com.tsai.shakeit.ext.getVmFactory
import com.tsai.shakeit.util.CurrentFragmentType
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

        //set backPressed behavior
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(OrderFragmentDirections.navToHome())
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            callback
        )

        binding = FragmentOrderBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val adapter = OrderAdapter(viewModel)

        viewModel.userOrderList.observe(viewLifecycleOwner, {
            adapter.submitList(it)
            viewModel.hasOrder.value = it.isNotEmpty()
        })

        viewModel.navToOrderDetail.observe(viewLifecycleOwner, {
            it?.let { order ->
                findNavController().navigate(
                    OrderFragmentDirections.navToOrderDetail(
                        order,
                        "current"
                    )
                )
            }
        })

        viewModel.shopId.observe(viewLifecycleOwner, {
            it?.let { findNavController().navigate(OrderFragmentDirections.navToSendComment(it)) }
        })

        viewModel.navToOrderHistory.observe(viewLifecycleOwner, {
            it?.let { findNavController().navigate(OrderFragmentDirections.navToOrderHistory()) }
        })

        viewModel.orderProduct.observe(viewLifecycleOwner, {
            Logger.d("$it")
        })


        binding.orderRev.adapter = adapter
        return binding.root
    }


}