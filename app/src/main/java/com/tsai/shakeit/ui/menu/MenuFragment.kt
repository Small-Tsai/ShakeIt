package com.tsai.shakeit.ui.menu

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tsai.shakeit.R
import com.tsai.shakeit.databinding.MenuFragmentBinding

class MenuFragment : Fragment() {

    private lateinit var viewModel: MenuViewModel
    private lateinit var binding: MenuFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(MenuViewModel::class.java)
        binding = MenuFragmentBinding.inflate(inflater, container, false)
        val adapter = MenuAdapter()

//        binding.recyclerView.adapter = adapter
        return binding.root
    }


}