package com.tsai.shakeit.ui.orderdetail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.messaging.FirebaseMessaging
import com.tsai.shakeit.MainViewModel
import com.tsai.shakeit.databinding.OrderDetailFragmentBinding
import com.tsai.shakeit.ext.getVmFactory
import com.tsai.shakeit.ui.favorite.FavoriteFragmentDirections
import com.tsai.shakeit.ui.menu.MenuFragmentDirections
import com.tsai.shakeit.util.Logger

class OrderDetailFragment : Fragment() {

    private lateinit var telUri: Uri

    private val swipeHelper by lazy {
        object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
            0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            }
        }
    }

    private val viewModel by viewModels<OrderDetailViewModel> {
        getVmFactory(
            order =
            OrderDetailFragmentArgs.fromBundle(
                requireArguments()
            ).order,
            type =
            OrderDetailFragmentArgs.fromBundle(
                requireArguments()
            ).type
        )
    }

    private lateinit var binding: OrderDetailFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel.mOrder?.let {
            FirebaseMessaging.getInstance().subscribeToTopic(it.order_Id)
        }

        binding = OrderDetailFragmentBinding.inflate(inflater, container, false)

        val itemTouchHelper = ItemTouchHelper(swipeHelper)
        itemTouchHelper.attachToRecyclerView(binding.orderDetailRev)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val adapter = OrderDetailAdapter(viewModel)
        val friendsAdapter = OrderFriendsAdapter(viewModel)

        viewModel.orderProduct.observe(viewLifecycleOwner, { OrderProductList ->
            Logger.d("$OrderProductList")
            val user = OrderProductList.map { it.user }.distinctBy { it.user_Id }

            OrderProductList?.let { list ->
                val orderProduct = mutableListOf<OrderDetail>()
                list.forEach { orderProduct.add(OrderDetail.MyOrderProduct(it)) }
                orderProduct.add(OrderDetail.AddProductBtn(""))
                adapter.submitList(orderProduct)
                friendsAdapter.submitList(user)
            }

            val totalPrice = OrderProductList.sumOf { it.price * it.qty }
            viewModel.updateTotalPrice(totalPrice)
            binding.totalPrice = totalPrice
        })

        viewModel.navToMenu.observe(viewLifecycleOwner, {
            it?.let {
                viewModel.mOrder?.user_Id?.let { userId ->
                    findNavController().navigate(MenuFragmentDirections.navToMenu(it, userId))
                }
            }
        })

        viewModel.shop.observe(viewLifecycleOwner, { shop ->
            binding.shop = shop
            binding.orderTelBtn.setOnClickListener {
                telUri = Uri.parse("tel:${shop.tel}");
                startActivity(Intent(Intent.ACTION_DIAL, telUri))
            }
        })

        viewModel.navToHome.observe(viewLifecycleOwner, {
            val mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
            mainViewModel.selectedShop.value = it
            findNavController().navigate(FavoriteFragmentDirections.navToHome())
        })

        binding.orderDetailRev.adapter = adapter
        binding.friendsRev.adapter = friendsAdapter
        return binding.root
    }
}