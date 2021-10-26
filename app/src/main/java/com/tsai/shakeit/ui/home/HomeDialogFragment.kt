package com.tsai.shakeit.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tsai.shakeit.databinding.HomeDialogFragmentBinding
import com.tsai.shakeit.ext.getVmFactory
import com.tsai.shakeit.ui.menu.MenuFragmentDirections

class HomeDialogFragment : BottomSheetDialogFragment() {


    private val viewModel by viewModels<HomeDialogViewModel> {
        getVmFactory()
    }

    private lateinit var binding: HomeDialogFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = HomeDialogFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        viewModel.getMyFavorite()

        viewModel.hasNavToMenu.observe(viewLifecycleOwner, Observer {
            it?.let { findNavController().navigate(MenuFragmentDirections.navToMenu(it,viewModel.orderId)) }
        })

        viewModel.shop.observe(viewLifecycleOwner, Observer {
            viewModel.checkHasFavorite(it)
            binding.viewModel = viewModel
        })

        viewModel._order.observe(viewLifecycleOwner, Observer {
            viewModel.checkHasOrder(it)
        })
        return binding.root
    }


}