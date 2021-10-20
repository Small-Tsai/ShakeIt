package com.tsai.shakeit.util

import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.Shape
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.databinding.BindingAdapter
import com.google.android.material.color.MaterialColors.getColor
import com.tsai.shakeit.R
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