package com.tsai.shakeit.ui.menu

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.tsai.shakeit.databinding.MenuFragmentBinding
import com.tsai.shakeit.ui.detail.DrinksDetailFragmentDirections
import com.tsai.shakeit.ui.order.OrderFragmentDirections

class MenuFragment : Fragment() {

    private lateinit var viewModel: MenuViewModel
    private lateinit var binding: MenuFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProvider(this).get(MenuViewModel::class.java)
        binding = MenuFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        val adapter = MenuAdapter(viewModel)

        viewModel.productList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        viewModel.navToDetail.observe(viewLifecycleOwner, Observer {
           it?.let {  findNavController().navigate(DrinksDetailFragmentDirections.navToDetail(it)) }
        })

        viewModel.popback.observe(viewLifecycleOwner, Observer {
            it?.let { findNavController().popBackStack() }
        })

        viewModel.navToOrder.observe(viewLifecycleOwner, Observer {
            it?.let { findNavController().navigate(OrderFragmentDirections.navToOrder()) }
        })

        binding.recyclerView.adapter = adapter
        return binding.root
    }


}