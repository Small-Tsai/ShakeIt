package com.tsai.shakeit.ui.favorite

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.tsai.shakeit.databinding.FavoriteShopimgRowBinding
import com.tsai.shakeit.databinding.FragmentFavoriteBinding
import com.tsai.shakeit.ext.getVmFactory
import com.tsai.shakeit.ui.home.TAG
import com.tsai.shakeit.ui.order.OrderViewModel

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


        val adapter = FavoriteAdapter(viewModel)


        viewModel.favorite.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, it.toString())
            it?.let { adapter.submitList(it) }
        })

        binding.favoriteRev.adapter = adapter
        return binding.root
    }


}