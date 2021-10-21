package com.tsai.shakeit.ui.orderdetail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.tsai.shakeit.R
import com.tsai.shakeit.databinding.OrderDetailFragmentBinding
import com.tsai.shakeit.ext.getVmFactory
import com.tsai.shakeit.ui.detail.DrinksDetailFragmentArgs
import com.tsai.shakeit.ui.detail.DrinksDetailViewModel
import com.tsai.shakeit.ui.home.TAG

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
            it?.let { friendsAdapter.submitList(it) }
            val count = it.map { order -> order.userName }.distinct().size
            Log.d(TAG,count.toString())
            friendsAdapter.submitCount(count)
        })

        binding.orderDetailRev.adapter = adapter
        binding.friendsRev.adapter = friendsAdapter
        return binding.root
    }

}