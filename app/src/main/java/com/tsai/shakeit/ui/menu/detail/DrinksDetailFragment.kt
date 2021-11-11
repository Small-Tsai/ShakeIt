package com.tsai.shakeit.ui.menu.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tsai.shakeit.R
import com.tsai.shakeit.ShakeItApplication
import com.tsai.shakeit.databinding.DialogOrderNameBinding
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

            hasOrder = DrinksDetailFragmentArgs.fromBundle(
                requireArguments()
            ).hasOrder
        )
    }


    private lateinit var binding: DrinksDetailFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val dialogBinding: DialogOrderNameBinding? =
            DataBindingUtil.inflate(
                LayoutInflater.from(requireActivity()),
                R.layout.dialog_order_name,
                null,
                false
            )

        val customDialog = AlertDialog.Builder(requireActivity(), 0).create()

        binding = DrinksDetailFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.qty.observe(viewLifecycleOwner, {
            binding.textView3.text = it.toString()
        })

        val adapter = DrinksAdapter(viewModel)
        viewModel.product.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

        viewModel.popBack.observe(viewLifecycleOwner, {
            it?.let { findNavController().popBackStack() }
        })

        viewModel.refresh.observe(viewLifecycleOwner, {
            it?.let { adapter.notifyDataSetChanged() }
        })

        viewModel.showDialog.observe(viewLifecycleOwner, {
            it?.let {
                when (it) {
                    true -> customDialog.apply { setView(dialogBinding?.root) }.show()
                    false -> customDialog.dismiss()
                }
            }
        })


        dialogBinding?.viewModel = viewModel
        dialogBinding?.lifecycleOwner = viewLifecycleOwner
        binding.detailRev.adapter = adapter
        return binding.root
    }


}