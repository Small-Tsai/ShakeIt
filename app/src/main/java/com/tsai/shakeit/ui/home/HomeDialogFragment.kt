package com.tsai.shakeit.ui.home

import android.app.Dialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tsai.shakeit.R
import com.tsai.shakeit.databinding.FragmentHomeBinding
import com.tsai.shakeit.databinding.HomeDialogFragmentBinding
import com.tsai.shakeit.ext.getVmFactory
import com.tsai.shakeit.ui.menu.MenuFragmentDirections
import com.tsai.shakeit.ui.order.OrderViewModel

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
            if (it == true) {
                findNavController().navigate(MenuFragmentDirections.navToMenu())
            }
        })

        viewModel.shop.observe(viewLifecycleOwner, Observer {
            viewModel.checkHasFavorite(it)
            binding.viewModel = viewModel
        })

        return binding.root
    }


}