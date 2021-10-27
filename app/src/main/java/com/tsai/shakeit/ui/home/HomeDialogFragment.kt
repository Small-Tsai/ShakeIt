package com.tsai.shakeit.ui.home

import android.animation.Animator
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tsai.shakeit.R
import com.tsai.shakeit.ShakeItApplication
import com.tsai.shakeit.databinding.HomeDialogFragmentBinding
import com.tsai.shakeit.ext.getVmFactory
import com.tsai.shakeit.ui.menu.MenuFragmentDirections
import android.animation.AnimatorListenerAdapter


class HomeDialogFragment : BottomSheetDialogFragment() {


    private val viewModel by viewModels<HomeDialogViewModel> {
        getVmFactory(
            shopData =
            HomeDialogFragmentArgs.fromBundle(requireArguments()).shopData
        )
    }

    private lateinit var binding: HomeDialogFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = HomeDialogFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        viewModel.getMyFavorite()

        viewModel.hasNavToMenu.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(
                    MenuFragmentDirections.navToMenu(
                        it,
                        viewModel.orderId
                    )
                )
            }
        })

        viewModel.shop.observe(viewLifecycleOwner, Observer {
            viewModel.checkHasFavorite(it)
            binding.viewModel = viewModel
        })

        viewModel.order.observe(viewLifecycleOwner, Observer {
            viewModel.checkHasOrder(it)
        })

        binding.dropDown.setOnClickListener {
            if (binding.timeLayout.visibility == View.GONE){
                binding.timeLayout.visibility = View.VISIBLE
            }else{
                binding.timeLayout.visibility = View.GONE
            }
        }

        return binding.root
    }


}