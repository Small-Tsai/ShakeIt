package com.tsai.shakeit.util

import android.util.Log
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Timestamp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.tsai.shakeit.data.OrderProduct
import com.tsai.shakeit.ext.toDisplayFormat
import com.tsai.shakeit.ext.toTimeFromTimeStamp
import com.tsai.shakeit.ui.detail.DrinksDetailViewModel
import com.tsai.shakeit.ui.favorite.FavoriteImageAdapter
import com.tsai.shakeit.ui.favorite.FavoriteViewModel
import com.tsai.shakeit.ui.home.TAG
import java.util.function.LongFunction

@BindingAdapter("shopName","branch")
fun TextView.bindShopName(name: String, branch: String) {
    text = "$name $branch"
}

@BindingAdapter("totalPrice")
fun TextView.bindTotalPrice(totalPrice: Int) {
    text = "訂單小計 $ $totalPrice"
}

@BindingAdapter("priceText")
fun TextView.bindPrice(price: Int) {
    text = "NT$ $price"
}

@BindingAdapter("timeToDisplayFormat")
fun bindDisplayFormatTime(textView: TextView, time: Timestamp?) {
    textView.text = time?.toTimeFromTimeStamp()
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

@BindingAdapter("othersText")
fun TextView.bindOthers(orderProduct: OrderProduct) {
    text =
        "${orderProduct.capacity},${orderProduct.sugar},${orderProduct.ice},${orderProduct.others}"
}

@BindingAdapter("qtyText")
fun TextView.bindQty(qty: Int) {
    text = "x$qty"
}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let { Log.d(TAG, imgUrl) }
    val gsReference = imgUrl?.let { Firebase.storage.reference.child("$it.jpeg") }
    gsReference?.downloadUrl?.addOnSuccessListener { uri ->
        Log.d(TAG, "success Image")
        Glide.with(imgView.context)
            .load(uri)
            .into(imgView)
    }
        ?.addOnFailureListener {
            Log.d(TAG, it.toString())
        }
}


