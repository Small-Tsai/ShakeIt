package com.tsai.shakeit.ui.addshop

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tsai.shakeit.R
import com.tsai.shakeit.databinding.AddShopFragmentBinding
import com.tsai.shakeit.ext.getVmFactory

class AddShopFragment : Fragment() {


    private val viewModel by viewModels<AddShopViewModel> {
        getVmFactory()
    }
    private lateinit var binding: AddShopFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = AddShopFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val adapter = AddShopAdapter()
        adapter.submitList(viewModel.dateList)
        binding.dateRev.adapter = adapter

        viewModel.popBack.observe(viewLifecycleOwner,{
            it?.let { findNavController().navigateUp() }
        })

        return binding.root
    }
}