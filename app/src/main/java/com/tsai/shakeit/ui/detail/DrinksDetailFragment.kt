package com.tsai.shakeit.ui.detail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tsai.shakeit.databinding.DrinksDetailFragmentBinding
import com.tsai.shakeit.ext.getVmFactory
import com.tsai.shakeit.ui.home.TAG

class DrinksDetailFragment : BottomSheetDialogFragment() {

    private val viewModel by viewModels<DrinksDetailViewModel> {
        getVmFactory(
            DrinksDetailFragmentArgs.fromBundle(
                requireArguments()
            ).product
        )
    }

    private lateinit var binding: DrinksDetailFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DrinksDetailFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        viewModel.qty.observe(viewLifecycleOwner, Observer {
            binding.textView3.text = it.toString()
        })

        val adapter = DrinksAdapter(viewModel)
        viewModel.product.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        viewModel.popback.observe(viewLifecycleOwner, Observer {
            it?.let { findNavController().popBackStack() }
        })

        binding.detailRev.adapter = adapter
        return binding.root
    }


}