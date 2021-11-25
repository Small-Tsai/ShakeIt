package com.tsai.shakeit.ui.menu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.google.android.material.tabs.TabLayout
import com.tsai.shakeit.R
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.databinding.DialogMenuOrderNameBinding
import com.tsai.shakeit.databinding.MenuFragmentBinding
import com.tsai.shakeit.ext.getVmFactory
import com.tsai.shakeit.ui.menu.detail.DrinksDetailFragmentDirections
import com.tsai.shakeit.ui.order.OrderFragmentDirections

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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
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

        viewModel.menuList.observe(viewLifecycleOwner, { menuList ->
            adapter.submitList(menuList)
            var x = 0
            binding.tableLayout.removeAllTabs()
            menuList.forEach {
                x++
                if (it is Menu.Title) {
                    binding.tableLayout.addTab(
                        binding.tableLayout.newTab().setText(it.type).setTag(x - 1)
                    )
                }
            }
        })

        viewModel.navToDetail.observe(viewLifecycleOwner, {
            it?.let { product ->
                viewModel.otherUserId?.let { otherUserId ->
                    findNavController().navigate(
                        DrinksDetailFragmentDirections.navToDetail(
                            product,
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
        })

        viewModel.popback.observe(viewLifecycleOwner, {
            it?.let { findNavController().navigateUp() }
        })

        viewModel.navToOrder.observe(viewLifecycleOwner, {
            it?.let { findNavController().navigate(OrderFragmentDirections.navToOrder()) }
        })

        viewModel.shop.observe(viewLifecycleOwner, {
            it?.let { binding.shopInfo = it }
        })

        viewModel.orderProductList.observe(viewLifecycleOwner, { orderProductList ->
            if (!orderProductList.isNullOrEmpty()) {
                viewModel.hasOrderProduct()
                viewModel.updateOrderTotalPrice(orderProductList.sumOf { it.price * it.qty })
            } else {
                viewModel.noOrderProduct()
            }
            binding.orderProductCount.text = orderProductList.size.toString()
        })

        viewModel.branchProductList.observe(viewLifecycleOwner, {
            viewModel.filterProductList(it)
        })

        viewModel.navToAddItem.observe(viewLifecycleOwner, {
            it?.let {
                findNavController()
                    .navigate(MenuFragmentDirections.navToAddItem(viewModel.selectedShop))
            }
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

        viewModel.orderList.observe(viewLifecycleOwner, {
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
                binding.productRev.layoutManager?.startSmoothScroll(smoothScroller)
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
        binding.productRev.adapter = adapter
        return binding.root
    }
}
