package com.tsai.shakeit.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.databinding.MenuFragmentBinding
import com.tsai.shakeit.ext.getVmFactory
import com.tsai.shakeit.ui.menu.detail.DrinksDetailFragmentDirections
import com.tsai.shakeit.ui.order.OrderFragmentDirections
import com.tsai.shakeit.util.Logger

class MenuFragment : Fragment() {

    private val viewModel by viewModels<MenuViewModel> {
        getVmFactory(

            shopData =
            MenuFragmentArgs.fromBundle(
                requireArguments()
            ).shopData,

            userId = MenuFragmentArgs.fromBundle(
                requireArguments()
            ).userId

        )
    }

    private lateinit var binding: MenuFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = MenuFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val adapter = MenuAdapter(viewModel)

        viewModel.productList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        viewModel.navToDetail.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.otherUserId?.let { otherUserId ->
                    findNavController().navigate(
                        DrinksDetailFragmentDirections.navToDetail(
                            it,
                            Shop(
                                shop_Id = viewModel.selectedShop.shop_Id,
                                branch = viewModel.selectedShop.branch
                            ),
                            userId = otherUserId
                        )
                    )
                }
            }
        })

        viewModel.popback.observe(viewLifecycleOwner, Observer {
            it?.let { findNavController().navigate(MenuFragmentDirections.navToHome()) }
        })

        viewModel.navToOrder.observe(viewLifecycleOwner, Observer {
            it?.let { findNavController().navigate(OrderFragmentDirections.navToOrder()) }
        })

        viewModel.shop.observe(viewLifecycleOwner,  {
            it?.let { binding.shopInfo = it }
        })

        viewModel.orderProduct.observe(viewLifecycleOwner,  { orderList ->
            if (!orderList.isNullOrEmpty()) {
                viewModel.hasOrder()
                viewModel.updateOrderTotalPrice(orderList.sumOf { it.price * it.qty })
            } else {
                viewModel.noOrder()
            }
            binding.textView9.text = orderList.size.toString()
        })

        viewModel.branchProduct.observe(viewLifecycleOwner,  {
            viewModel.filterMyList(it)
        })

        viewModel.navToAddItem.observe(viewLifecycleOwner,{
            it?.let { findNavController().navigate(MenuFragmentDirections.navToAddItem(viewModel.selectedShop)) }
        })

        binding.recyclerView.adapter = adapter
        return binding.root
    }
}