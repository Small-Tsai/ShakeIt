package com.tsai.shakeit.ui.addshop

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.tsai.shakeit.ShakeItApplication
import com.tsai.shakeit.databinding.AddShopFragmentBinding
import com.tsai.shakeit.ext.getVmFactory
import com.tsai.shakeit.ext.mToast
import com.tsai.shakeit.util.Logger

class AddShopFragment : Fragment() {


    private val viewModel by viewModels<AddShopViewModel> {
        getVmFactory()
    }
    private lateinit var binding: AddShopFragmentBinding

    private val fromShop = 0
    private val fromMenu = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = AddShopFragmentBinding.inflate(inflater, container, false)

        val adapter = AddShopAdapter(viewModel)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.menuPhoto.setOnClickChoosePhoto(fromMenu)
        binding.shopPhoto.setOnClickChoosePhoto(fromShop)
        binding.dateRev.adapter = adapter

        adapter.submitList(viewModel.dateList)

        viewModel.popBack.observe(viewLifecycleOwner, {
            it?.let { findNavController().navigateUp() }
        })

        viewModel.timeOpen.observe(viewLifecycleOwner, {
            Logger.d("$it")
            Logger.d("p1 = ${viewModel.adapterPostion.value}")
            viewModel.adapterPostion.value?.let { it1 ->
                viewModel.timeClose.value?.let { it2 ->
                    viewModel.setTimeList(
                        it,
                        it2,
                        it1
                    )
                }
            }
        })

        viewModel.timeClose.observe(viewLifecycleOwner,{
            viewModel.adapterPostion.value?.let { it1 ->
                viewModel.timeOpen.value?.let { it2 ->
                    viewModel.setTimeList(
                        it2,
                        it,
                        it1
                    )
                }
            }
        })

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {

            fromShop -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->

                        // 將照片顯示
                        val bitmap =
                            MediaStore.Images.Media.getBitmap(activity?.contentResolver, uri)

                        binding.shopPhoto.foreground = ((BitmapDrawable(bitmap)))

                        mToast("${uri.encodedPath}")
                        //傳Uri到viewModel
                        viewModel.shopImageUri.value = uri
                    }
                }
            }

            fromMenu -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->

                        // 將照片顯示
                        val bitmap = getBitmapFromUri(uri)

                        binding.menuPhoto.foreground = ((BitmapDrawable(bitmap)))

                        //傳Uri到viewModel
                        viewModel.menuImageUri.value = uri
                    }
                }
            }
        }
    }

    private fun getBitmapFromUri(uri: Uri) =
        ShakeItApplication.instance.contentResolver.openFileDescriptor(uri, "r")?.use {
            BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
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