package com.tsai.shakeit.ui.menu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tsai.shakeit.MainViewModel
import com.tsai.shakeit.R
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.databinding.DialogMenuOrderNameBinding
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

        viewModel.initProduct()

        val dialogBinding: DialogMenuOrderNameBinding? =
            DataBindingUtil.inflate(
                LayoutInflater.from(requireActivity()),
                R.layout.dialog_menu_order_name,
                null,
                false
            )

        val customDialog = AlertDialog.Builder(requireActivity(), 0).create()

        binding = MenuFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val adapter = MenuAdapter(viewModel)

        viewModel.productList.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

        viewModel.navToDetail.observe(viewLifecycleOwner, {
            it?.let {
                viewModel.otherUserId?.let { otherUserId ->
                    viewModel.orderProduct.value?.let { orderProduct ->
                        findNavController().navigate(
                            DrinksDetailFragmentDirections.navToDetail(
                                it,
                                Shop(
                                    shop_Id = viewModel.selectedShop.shop_Id,
                                    branch = viewModel.selectedShop.branch,
                                    shop_Img = viewModel.selectedShop.shop_Img
                                ),
                                userId = otherUserId,
                                hasOrder = viewModel.hasOrder.value!!
                            )
                        )
                    }
                }
            }
        })

        viewModel.popback.observe(viewLifecycleOwner, Observer {
            it?.let { findNavController().navigateUp() }
        })

        viewModel.navToOrder.observe(viewLifecycleOwner, Observer {
            it?.let { findNavController().navigate(OrderFragmentDirections.navToOrder()) }
        })

        viewModel.shop.observe(viewLifecycleOwner, {
            it?.let { binding.shopInfo = it }
        })

        viewModel.orderProduct.observe(viewLifecycleOwner, { orderListProduct ->
            if (!orderListProduct.isNullOrEmpty()) {
                viewModel.hasOrderProduct()
                viewModel.updateOrderTotalPrice(orderListProduct.sumOf { it.price * it.qty })
            } else {
                viewModel.noOrderProduct()
            }
            binding.textView9.text = orderListProduct.size.toString()
        })

        viewModel.branchProduct.observe(viewLifecycleOwner, {
            viewModel.filterMyList(it)
        })

        viewModel.navToAddItem.observe(viewLifecycleOwner, {
            it?.let { findNavController().navigate(MenuFragmentDirections.navToAddItem(viewModel.selectedShop)) }
        })

        viewModel.showDialog.observe(viewLifecycleOwner, {
            it?.let {
                when (it) {
                    true -> customDialog.apply { setView(dialogBinding?.root) }.show()
                    false -> customDialog.dismiss()
                }
            }
        })

        viewModel.shareOrder.observe(viewLifecycleOwner, {
            it?.let { startActivity(Intent.createChooser(it, "choose:")) }
        })

        viewModel.order.observe(viewLifecycleOwner, {
            if (it.isEmpty()) viewModel.noOrder()
            else viewModel.hasOrder()
        })

        dialogBinding?.viewModel = viewModel
        dialogBinding?.lifecycleOwner = viewLifecycleOwner
        binding.recyclerView.adapter = adapter
        return binding.root
    }
}