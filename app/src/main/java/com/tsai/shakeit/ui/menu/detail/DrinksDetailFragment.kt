package com.tsai.shakeit.ui.menu.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tsai.shakeit.databinding.DrinksDetailFragmentBinding
import com.tsai.shakeit.ext.getVmFactory

class DrinksDetailFragment : BottomSheetDialogFragment() {

    private val viewModel by viewModels<DrinksDetailViewModel> {
        getVmFactory(
            product =
            DrinksDetailFragmentArgs.fromBundle(
                requireArguments()
            ).product,

            shopData = DrinksDetailFragmentArgs.fromBundle(
                requireArguments()
            ).shop,

            userId = DrinksDetailFragmentArgs.fromBundle(
                requireArguments()
            ).userId,

            orderSize = DrinksDetailFragmentArgs.fromBundle(
                requireArguments()
            ).orderSize
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

        viewModel.popBack.observe(viewLifecycleOwner, Observer {
            it?.let { findNavController().popBackStack() }
        })

        viewModel.refresh.observe(viewLifecycleOwner, Observer {
            it?.let { adapter.notifyDataSetChanged() }
        })

        binding.detailRev.adapter = adapter
        return binding.root
    }


}