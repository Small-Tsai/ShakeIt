package com.tsai.shakeit.ui.menu.addmenuitem

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.tsai.shakeit.databinding.AddMenuItemFragmentBinding
import com.tsai.shakeit.ext.getVmFactory
import com.tsai.shakeit.util.Logger


class AddMenuItemFragment : Fragment() {

    private val fromProduct = 0

    private val viewModel by viewModels<AddMenuItemViewModel> {
        getVmFactory(
            shopData =
            AddMenuItemFragmentArgs.fromBundle(requireArguments()).shop
        )
    }

    private lateinit var binding: AddMenuItemFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddMenuItemFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        viewModel.initSelectItem()

        val addCapacityAdapter = AddMenuItemAdapter(viewModel)
        val addIceAdapter = AddMenuItemAdapter(viewModel)
        val addSugarAdapter = AddMenuItemAdapter(viewModel)
        val addOtherAdapter = AddMenuItemAdapter(viewModel)

        binding.productPhotoBtn.setOnClickChoosePhoto(fromProduct)

        viewModel.addCapacityListLiveData.observe(viewLifecycleOwner, {
            addCapacityAdapter.submitList(it.toMutableList())
        })

        viewModel.addIceListLiveData.observe(viewLifecycleOwner, {
            addIceAdapter.submitList(it.toMutableList())
        })

        viewModel.addSugarListLiveData.observe(viewLifecycleOwner, {
            addSugarAdapter.submitList(it.toMutableList())
        })

        viewModel.addOtherListLiveData.observe(viewLifecycleOwner, {
            addOtherAdapter.submitList(it.toMutableList())
        })

        viewModel.content.observe(viewLifecycleOwner, {
            viewModel.setListContent(it)
        })

        viewModel.price.observe(viewLifecycleOwner, {
            viewModel.setListPrice(it)
        })

        viewModel.popBack.observe(viewLifecycleOwner,{
            it?.let { findNavController().navigateUp() }
        })

        binding.productCapaRev.adapter = addCapacityAdapter
        binding.productIceRev.adapter = addIceAdapter
        binding.productSugarRev.adapter = addSugarAdapter
        binding.productOtherRev.adapter = addOtherAdapter

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {

            fromProduct -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->

                        // 將照片顯示
                        val bitmap =
                            MediaStore.Images.Media.getBitmap(activity?.contentResolver, uri)

                        binding.productPhotoBtn.foreground = ((BitmapDrawable(bitmap)))

                        //傳Uri到viewModel
                        viewModel.productImageUri.value = uri
                    }
                }
            }
        }
    }

    private fun MaterialButton.setOnClickChoosePhoto(buttonName: Int) {

        setOnClickListener {

            // open storage
            val intent = Intent(Intent.ACTION_PICK)

            // only display image
            intent.type = "image/*"
            startActivityForResult(intent, buttonName)
        }
    }
}


