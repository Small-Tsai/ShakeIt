package com.tsai.shakeit.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tsai.shakeit.MainViewModel
import com.tsai.shakeit.databinding.FragmentFavoriteBinding
import com.tsai.shakeit.ext.getVmFactory
import com.tsai.shakeit.util.CurrentFragmentType

class FavoriteFragment : Fragment() {

    private val viewModel by viewModels<FavoriteViewModel> {
        getVmFactory()
    }
    private lateinit var binding: FragmentFavoriteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        val adapter = FavoriteAdapter(viewModel)


        viewModel.favorite.observe(viewLifecycleOwner, Observer {
            it?.let { adapter.submitList(it) }
        })

        viewModel.shop.observe(viewLifecycleOwner, Observer {
            viewModel.buildFavoriteList(it)
        })

        viewModel.navToHome.observe(viewLifecycleOwner, Observer {
            val mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
            mainViewModel.currentFragmentType.value = CurrentFragmentType.FAVORITE_NAV_HOME
            mainViewModel.selectedFavorite.value = it
            findNavController().navigate(FavoriteFragmentDirections.navToHome())
        })

        binding.favoriteRev.adapter = adapter
        return binding.root
    }


}