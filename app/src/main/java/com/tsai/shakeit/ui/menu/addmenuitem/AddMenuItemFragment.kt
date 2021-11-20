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
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.button.MaterialButton
import com.tsai.shakeit.databinding.AddMenuItemFragmentBinding
import com.tsai.shakeit.ext.getVmFactory
import com.tsai.shakeit.util.Logger
import com.tsai.shakeit.util.UserInfo


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
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.initSelectItem()

        val addCapacityAdapter = AddMenuItemAdapter(viewModel)
        val addIceAdapter = AddMenuItemAdapter(viewModel)
        val addSugarAdapter = AddMenuItemAdapter(viewModel)
        val addOtherAdapter = AddMenuItemAdapter(viewModel)

        binding.productPhotoBtn.setOnClickChoosePhoto(fromProduct)

        viewModel.addCapacityListLiveData.observe(viewLifecycleOwner, {
            Logger.d("observe it")
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
            it?.let { viewModel.setListContent(it) }
        })

        viewModel.price.observe(viewLifecycleOwner, {
            viewModel.setListPrice(it)
        })

        viewModel.popBack.observe(viewLifecycleOwner, {
            it?.let { findNavController().navigateUp() }
        })

        viewModel.navToMenu.observe(viewLifecycleOwner, {
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
            ImagePicker.with(fragment = this@AddMenuItemFragment)
                .galleryOnly()
                .crop(16f, 9f)
                .compress(1024)
                .createIntent { intent ->
                    startActivityForResult(intent,buttonName)
                }
        }
    }
}


