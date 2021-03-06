package com.tsai.shakeit.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tsai.shakeit.MainViewModel
import com.tsai.shakeit.NavDirections
import com.tsai.shakeit.databinding.FragmentFavoriteBinding
import com.tsai.shakeit.ext.getVmFactory
import com.tsai.shakeit.util.CurrentFragmentType

class FavoriteFragment : Fragment() {

    private val viewModel by viewModels<FavoriteViewModel> {
        getVmFactory()
    }
    private lateinit var binding: FragmentFavoriteBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(NavDirections.navToHome())
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            callback
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val adapter = FavoriteAdapter(viewModel)

        viewModel.favoriteItem.observe(viewLifecycleOwner, {
            it?.let { adapter.submitList(it) }
        })

        viewModel.myFavorite.observe(viewLifecycleOwner, {
            viewModel.buildFavoriteList(it)
            if (it.isNotEmpty()) {
                viewModel.isFavoriteEmpty.value = false
            }
        })

        viewModel.navToHome.observe(viewLifecycleOwner, {
            val mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
            mainViewModel.currentFragmentType.value = CurrentFragmentType.FAVORITE
            mainViewModel.selectedShop.value = it
            findNavController().navigate(FavoriteFragmentDirections.navToHome())
        })

        binding.favoriteRev.adapter = adapter
        return binding.root
    }
}
