package com.tsai.shakeit.ui.orderdetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.tsai.shakeit.databinding.OrderDetailFragmentBinding
import com.tsai.shakeit.ext.getVmFactory

class OrderDetailFragment : Fragment() {

    private val viewModel by viewModels<OrderDetailViewModel> {
        getVmFactory(
            order =
            OrderDetailFragmentArgs.fromBundle(
                requireArguments()
            ).order
        )
    }

    private lateinit var binding: OrderDetailFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = OrderDetailFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        val adapter = OrderDetailAdapter(viewModel)
        val friendsAdapter = OrderFriendsAdapter(viewModel)

        viewModel.order.observe(viewLifecycleOwner, Observer { it ->
            it?.let { adapter.submitList(it) }
            val nameList = it.map { order -> order.user_Name }.distinct()
            it?.let { friendsAdapter.submitList(nameList) }
            binding.totalPrice = it.sumOf { it.price }
        })

        binding.orderDetailRev.adapter = adapter
        binding.friendsRev.adapter = friendsAdapter
        return binding.root
    }

}