package com.tsai.shakeit.ui.home

import android.app.Dialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tsai.shakeit.R
import com.tsai.shakeit.databinding.FragmentHomeBinding
import com.tsai.shakeit.databinding.HomeDialogFragmentBinding
import com.tsai.shakeit.ui.menu.MenuFragmentDirections

class HomeDialogFragment : BottomSheetDialogFragment() {


    private lateinit var viewModel: HomeDialogViewModel
    private lateinit var binding: HomeDialogFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProvider(this).get(HomeDialogViewModel::class.java)
        binding = HomeDialogFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        viewModel.hasNavToMenu.observe(viewLifecycleOwner, Observer {
            if (it == true) {
              findNavController().navigate(MenuFragmentDirections.navToMenu())
                viewModel.navToMenuDone()
            }
        })
        return binding.root
    }


}