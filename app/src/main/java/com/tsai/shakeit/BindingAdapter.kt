package com.tsai.shakeit.util

import android.os.Looper
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.marginTop
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.Timestamp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.tsai.shakeit.MainViewModel
import com.tsai.shakeit.R
import com.tsai.shakeit.ShakeItApplication
import com.tsai.shakeit.data.OrderProduct
import com.tsai.shakeit.ext.toTimeFromTimeStamp
import com.tsai.shakeit.ui.addshop.AddShopViewModel
import com.tsai.shakeit.ui.menu.addmenuitem.AddMenuItemViewModel
import com.tsai.shakeit.ui.menu.detail.DrinksDetailViewModel
import com.tsai.shakeit.ui.setting.SettingViewModel
import kotlinx.coroutines.delay
import java.util.logging.Handler

@BindingAdapter("shopName", "branch")
fun TextView.bindShopName(name: String?, branch: String?) {
    name?.let { text = "$name $branch" }
}

@BindingAdapter("totalPrice")
fun TextView.bindTotalPrice(totalPrice: Int) {
    text = "訂單小計 $ $totalPrice"
}

@BindingAdapter("priceText")
fun TextView.bindPrice(price: Int) {
    text = "NT$ $price"
}

@BindingAdapter("priceForCapacity")
fun TextView.bindCapacityPrice(price: HashMap<String, Int>) {
    if (price["大"] != null) {
        text = "NT$ ${price["大"]}"
    } else {
        text = "NT$ ${price["中"]}"
    }
}

@BindingAdapter("price", "content")
fun TextView.bindAddPrice(price: Int, content: String) {
    text = if (content.length == 1) {
        "$ $price"
    } else {
        "+$ $price"
    }

}

@BindingAdapter("timeToDisplayFormat")
fun bindDisplayFormatTime(textView: TextView, time: Timestamp?) {
    textView.text = time?.toTimeFromTimeStamp()
}

@BindingAdapter("editorControllerStatus")
fun bindEditorControllerStatus(imageButton: AppCompatImageButton, qty: Int) {
    imageButton.apply {
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
    if (orderProduct.others.isEmpty()) {
        text =
            "${orderProduct.capacity},${orderProduct.sugar},${orderProduct.ice}"
    } else {
        text =
            "${orderProduct.capacity},${orderProduct.sugar},${orderProduct.ice},${orderProduct.others}"
    }


}

@BindingAdapter("qtyText")
fun TextView.bindQty(qty: Int) {
    text = "x$qty"
}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {

    if (!imgUrl.isNullOrEmpty()) {

        Glide.with(imgView.context)
            .load(imgUrl)
            .placeholder(R.drawable.placedrink)
            .error(R.drawable.placedrink)
            .into(imgView)


    }
}

@BindingAdapter("circleimageUrl")
fun bindCircleImage(imgView: ImageView, imgUrl: String?) {

    if (!imgUrl.isNullOrEmpty()) {
        Glide.with(imgView.context)
            .load(imgUrl)
            .placeholder(R.drawable.personicon)
            .error(R.drawable.personicon)
            .circleCrop()
            .into(imgView)
    }
}


@BindingAdapter("mainViewModel", "shopName", "viewModel")
fun SwitchMaterial.bindSwitch(
    mainViewModel: MainViewModel,
    shopName: String,
    viewModel: SettingViewModel
) {
    isChecked = mainViewModel.dbFilterShopList.value?.contains(shopName) != true
}

@BindingAdapter("fabAnimation")
fun ExtendedFloatingActionButton.bindAnimate(start: Boolean) {
    elevation = 10f
    iconSize = 90
    textSize = 18f
    scaleX = 0.65f
    scaleY = 0.65f
    isExtended = false
    val handler = android.os.Handler(Looper.getMainLooper())
    handler.postDelayed({
        extend()
        handler.postDelayed({
            shrink()
        }, 1000)
    }, 1500)
}

@BindingAdapter("fabAnimationBig")
fun ExtendedFloatingActionButton.bindAnimateBig(start: Boolean) {
    isExtended = false
    elevation = 10f
    textSize = 18f
    scaleX = 0.8f
    scaleY = 0.8f
    val handler = android.os.Handler(Looper.getMainLooper())
    handler.postDelayed({
        extend()
        handler.postDelayed({
            shrink()
        }, 1000)
    }, 1500)
}

@BindingAdapter("getCurrentPosition","viewModel")
fun EditText.bindPosition(position: Int, viewModel: AddMenuItemViewModel) {

    setOnFocusChangeListener { view, b ->
        if (b == true) {
            viewModel.recordCurrentSelectedPostion(position)
        }
    }

}








