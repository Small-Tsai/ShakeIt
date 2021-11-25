package com.tsai.shakeit.ui.menu.addmenuitem

import android.app.Activity
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.button.MaterialButton
import com.tsai.shakeit.databinding.AddMenuItemFragmentBinding
import com.tsai.shakeit.ext.getVmFactory
import com.tsai.shakeit.util.Logger


class AddMenuItemFragment : Fragment() {

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

        binding.productPhotoBtn.setOnClickChoosePhoto()

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

        viewModel.addOthersListLiveData.observe(viewLifecycleOwner, {
            addOtherAdapter.submitList(it.toMutableList())
        })

        viewModel.optionName.observe(viewLifecycleOwner, {
            it?.let { viewModel.setOptionName(it) }
        })

        viewModel.optionPrice.observe(viewLifecycleOwner, {
            viewModel.setOptionPrice(it)
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

    private val productActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                val result = activityResult.data
                result?.data.let { uri ->
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(activity?.contentResolver, uri)
                    binding.productPhotoBtn.foreground = ((BitmapDrawable(bitmap)))
                    viewModel.productImageUri.value = uri
                }
            }
        }

    private fun MaterialButton.setOnClickChoosePhoto() {
        setOnClickListener {
            ImagePicker.with(fragment = this@AddMenuItemFragment)
                .galleryOnly()
                .crop()
                .compress(1024)
                .createIntent { intent ->
                    productActivityLauncher.launch(intent)
                }
        }
    }
}


