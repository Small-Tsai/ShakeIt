package com.tsai.shakeit.ui.menu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.tsai.shakeit.R
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.databinding.DialogMenuOrderNameBinding
import com.tsai.shakeit.databinding.MenuFragmentBinding
import com.tsai.shakeit.ext.getVmFactory
import com.tsai.shakeit.ui.menu.detail.DrinksDetailFragmentDirections
import com.tsai.shakeit.ui.order.OrderFragmentDirections
import androidx.recyclerview.widget.LinearSmoothScroller

import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.google.android.material.tabs.TabLayout
import java.lang.Exception
import java.lang.reflect.Field


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

        val adapter = MenuAdapter(viewModel, binding)

        viewModel.productList.observe(viewLifecycleOwner, { list ->
            adapter.submitList(list)
            var x = 0
            binding.tableLayout.removeAllTabs()
            list.forEach {
                x++
                if (it is Menu.Title) {
                    binding.tableLayout.addTab(
                        binding.tableLayout.newTab().setText(it.type).setTag(x - 1)
                    )
                }
            }
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

        val smoothScroller: SmoothScroller = object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }

        binding.tableLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                smoothScroller.targetPosition = tab?.tag as Int
                binding.recyclerView.layoutManager?.startSmoothScroll(smoothScroller)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // TODO("Handle tab unselect")
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // TODO("Handle tab reselect")
            }
        })



        dialogBinding?.viewModel = viewModel
        dialogBinding?.lifecycleOwner = viewLifecycleOwner
        binding.recyclerView.adapter = adapter
        return binding.root
    }
}