package com.tsai.shakeit.util

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("priceText")
fun TextView.bindPrice(price: Int) {
    text = "NT$ $price"
}