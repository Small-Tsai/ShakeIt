package com.tsai.shakeit.util

import android.util.Log
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.databinding.BindingAdapter
import com.tsai.shakeit.ui.detail.DrinksDetailViewModel
import com.tsai.shakeit.ui.home.TAG

@BindingAdapter("priceText")
fun TextView.bindPrice(price: Int) {
    text = "NT$ $price"
}

@BindingAdapter("editorControllerStatus")
fun bindEditorControllerStatus(imageButton: AppCompatImageButton, qty: Int) {
    imageButton.apply {
        Log.d(TAG, qty.toString())
        isClickable = true
    }
}


@BindingAdapter("mRadioButton", "viewModel", "position")
fun RadioButton.bindRadioBtn(content: String, viewModel: DrinksDetailViewModel, position: Int) {
    isChecked = when (viewModel.selectedPositionList.distinct().contains(position)) {
        true -> true
        false -> false
    }
    text = content
}