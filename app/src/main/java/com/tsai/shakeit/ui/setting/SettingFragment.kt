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
            adapter.submitList(it.distinct())
        })

        viewModel.doCheck.observe(viewLifecycleOwner, {
            Logger.d("docheckAll = $it")

            if (it.isNotEmpty()) {
                viewModel.filterShop(it, mainViewModel)
            } else if (viewModel.isAllChecked.value == true) {

                Logger.d("${viewModel.isAllChecked.value}")
                mainViewModel.shopFilterList.value = viewModel.shopList.value?.distinct()
            } else {
                mainViewModel.shopFilterList.value = mutableListOf()
            }
        })

        binding.mainViewModel = mainViewModel

        mainViewModel.dbFilterShopList.observe(viewLifecycleOwner, {
            Logger.d("總共有${viewModel.shopList.value?.distinct()?.size}間商店")
            Logger.d("隱藏商家--->$it")
            viewModel.filteredList = it as MutableList<String>
            Logger.d("filterList--->${viewModel.filteredList}")
            viewModel.isAllChecked.value = viewModel.filteredList.isEmpty()
            adapter.notifyDataSetChanged()
        })

        viewModel.isAllChecked.observe(viewLifecycleOwner, {
            Logger.d("isAllchecked = $it")
        })

        binding.settingShopRev.adapter = adapter
        return binding.root
    }
}