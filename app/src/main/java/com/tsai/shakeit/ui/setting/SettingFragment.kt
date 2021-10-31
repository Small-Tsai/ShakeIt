package com.tsai.shakeit.ui.setting

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.tsai.shakeit.MainViewModel
import com.tsai.shakeit.databinding.SettingFragmentBinding
import com.tsai.shakeit.ext.getVmFactory
import com.tsai.shakeit.util.Logger

class SettingFragment : Fragment() {

    private val viewModel by viewModels<SettingViewModel> {
        getVmFactory(
            shopList =
            SettingFragmentArgs.fromBundle(requireArguments()).shopInfo
        )
    }

    private lateinit var binding: SettingFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = SettingFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        val adapter = SettingAdapter(viewModel, mainViewModel)

        viewModel.shopList.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

        viewModel.getFilterList()

        viewModel.dbFilterShopList.observe(viewLifecycleOwner, {
            Logger.d("$it")
           viewModel.filteredList = it as MutableList<String>
        })

        binding.settingShopRev.adapter = adapter
        return binding.root
    }
}