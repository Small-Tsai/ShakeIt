package com.tsai.shakeit.ui.addshop

import android.app.Activity
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.button.MaterialButton
import com.tsai.shakeit.ShakeItApplication
import com.tsai.shakeit.databinding.AddShopFragmentBinding
import com.tsai.shakeit.ext.getVmFactory
import com.tsai.shakeit.util.Logger

class AddShopFragment : Fragment() {

    private val viewModel by viewModels<AddShopViewModel> {
        getVmFactory()
    }

    private lateinit var binding: AddShopFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = AddShopFragmentBinding.inflate(inflater, container, false)

        val adapter = AddShopAdapter(viewModel)

        viewModel.timeHashList.observe(viewLifecycleOwner, {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        })

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.menuPhoto.menuSetOnClickChoosePhoto()
        binding.shopPhoto.setOnClickChoosePhoto()
        binding.dateRev.adapter = adapter

        viewModel.popBack.observe(viewLifecycleOwner, {
            it?.let { findNavController().navigateUp() }
        })

        viewModel.timeOpen.observe(viewLifecycleOwner, { timeOpen ->
            viewModel.adapterPostion.value?.let { adapterPosition ->
                viewModel.timeClose.value?.let { timeClose ->
                    viewModel.setTimeList(timeOpen, timeClose, adapterPosition)
                }
            }
        })

        viewModel.timeClose.observe(viewLifecycleOwner, { timeClose ->
            viewModel.adapterPostion.value?.let { adapterPosition ->
                viewModel.timeOpen.value?.let { timeOpen ->
                    viewModel.setTimeList(timeOpen, timeClose, adapterPosition)
                }
            }
        })

        viewModel.navToHome.observe(viewLifecycleOwner, {
            it?.let {
                findNavController().navigate(AddShopFragmentDirections.navToHome())
            }
        })

        binding.addressEdt.setOnClickListener { startAutoCompleteIntent() }
        return binding.root
    }

    private val shopActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val resultData = result.data
                resultData?.data.let { uri ->
                    val bitmap = uri?.let { getBitmapFromUri(it) }
                    binding.shopPhoto.foreground = ((BitmapDrawable(resources, bitmap)))
                    viewModel.shopImageUri.value = uri
                }
            }
        }

    private val menuActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val resultData = result.data
                resultData?.data.let { uri ->
                    val bitmap = uri?.let { getBitmapFromUri(it) }
                    binding.menuPhoto.foreground = ((BitmapDrawable(resources, bitmap)))
                    viewModel.menuImageUri.value = uri
                }
            }
        }

    private val autocompleteActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val resultData = result.data
                resultData?.let { intent ->
                    val place = Autocomplete.getPlaceFromIntent(intent)
                    binding.addressEdt.setText(place.address)

                    place.phoneNumber?.let { phoneNumber ->
                        "0${phoneNumber.substring(4)}".also { binding.telEdt.setText(it) }
                        viewModel.tel = "0${phoneNumber.substring(4)}"
                    }

                    if (place.openingHours == null) {
                        viewModel.setTimeListWhenAutoCompleteFail()
                    } else {
                        viewModel.setTimeListByAutoComplete(place.openingHours!!.periods)
                    }

                    place.latLng?.let { latLng ->
                        viewModel.lat = latLng.latitude
                        viewModel.lon = latLng.longitude
                    }
                }
            } else {
                Logger.d("autoComplete error")
            }
        }

    private fun getBitmapFromUri(uri: Uri) =
        ShakeItApplication.instance.contentResolver.openFileDescriptor(uri, "r")?.use {
            BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
        }

    private fun MaterialButton.setOnClickChoosePhoto() {
        setOnClickListener {
            ImagePicker.with(fragment = this@AddShopFragment)
                .galleryOnly()
                .crop(16f, 9f)
                .compress(1024)
                .createIntent { intent ->
                    shopActivityLauncher.launch(intent)
                }
        }
    }

    private fun MaterialButton.menuSetOnClickChoosePhoto() {
        setOnClickListener {
            ImagePicker.with(fragment = this@AddShopFragment)
                .galleryOnly()
                .crop()
                .compress(1024)
                .createIntent { intent ->
                    menuActivityLauncher.launch(intent)
                }
        }
    }

    private fun startAutoCompleteIntent() {

        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        val fields =
            listOf(
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG,
                Place.Field.PHONE_NUMBER,
                Place.Field.OPENING_HOURS,
            )

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(ShakeItApplication.instance)

        autocompleteActivityLauncher.launch(intent)
    }
}
