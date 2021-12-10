package com.tsai.shakeit.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tsai.shakeit.MainViewModel
import com.tsai.shakeit.NavDirections
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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = SettingFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        val adapter = SettingAdapter(viewModel, mainViewModel)

        viewModel.shopList.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

        viewModel.filteredShopName.observe(viewLifecycleOwner, {
            when {
                it.isNotEmpty() -> viewModel.filterShop(it, mainViewModel)

                viewModel.isAllChecked.value == true ->
                    mainViewModel.localFilteredShopList.value = viewModel.shopList.value?.distinct()

                else -> mainViewModel.localFilteredShopList.value = mutableListOf()
            }
        })

        mainViewModel.firebaseFilteredShopList.observe(viewLifecycleOwner, {
            Logger.d("filteredShop--->$it")
            viewModel.filteredShopList = it as MutableList<String>
            viewModel.checkIsFilteredShopListEmpty(it)
            adapter.notifyDataSetChanged()
        })

        viewModel.popBack.observe(viewLifecycleOwner, {
            it?.let { findNavController().navigate(NavDirections.navToHome()) }
        })

        binding.mainViewModel = mainViewModel
        binding.settingShopRev.adapter = adapter
        return binding.root
    }
}
