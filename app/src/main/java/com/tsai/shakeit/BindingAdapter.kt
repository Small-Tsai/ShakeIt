package com.tsai.shakeit

import android.os.Looper
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.Timestamp
import com.tsai.shakeit.app.DRIVING
import com.tsai.shakeit.app.WALKING
import com.tsai.shakeit.data.OrderProduct
import com.tsai.shakeit.ext.toTimeFromTimeStamp
import com.tsai.shakeit.ext.visibility
import com.tsai.shakeit.network.LoadApiStatus
import com.tsai.shakeit.ui.home.HomeViewModel
import com.tsai.shakeit.ui.menu.addmenuitem.AddMenuItemViewModel
import com.tsai.shakeit.ui.menu.detail.DrinksDetailViewModel
import com.tsai.shakeit.util.*

@BindingAdapter("shopNameArray")
fun TextView.bindShopNameArray(name: ArrayList<String>?) {
    val shopName = "--${name?.last()}"
    name?.let { text = shopName }
}

@BindingAdapter("shopName", "branch")
fun TextView.bindShopName(name: String?, branch: String?) {
    val shopFullName = "$name\t$branch"
    name?.let { text = shopFullName }
}

@BindingAdapter("totalPrice")
fun TextView.bindTotalPrice(totalPrice: Int) {
    val totalPriceText = "訂單小計\t$\t$totalPrice"
    text = totalPriceText
}

@BindingAdapter("priceText")
fun TextView.bindPrice(price: Int) {
    val priceText = "NT$\t$price"
    text = priceText
}

@BindingAdapter("priceForCapacity")
fun TextView.bindCapacityPrice(price: HashMap<String, Int>) {
    text = if (price["大"] != null) {
        "NT$ ${price["大"]}"
    } else {
        "NT$ ${price["中"]}"
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
        "${orderProduct.capacity},${orderProduct.sugar},${orderProduct.ice}"
            .also { text = it }
    } else {
        "${orderProduct.capacity},${orderProduct.sugar},${orderProduct.ice},${orderProduct.others}"
            .also { text = it }
    }
}

@BindingAdapter("qtyText")
fun TextView.bindQty(qty: Int) {
    "x$qty".also { text = it }
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


@BindingAdapter("mainViewModel", "shopName")
fun SwitchMaterial.bindSwitch(
    mainViewModel: MainViewModel,
    shopName: String,
) {
    isChecked = mainViewModel.firebaseFilteredShopList.value?.contains(shopName) != true
}

@BindingAdapter("fabAnimation")
fun ExtendedFloatingActionButton.bindAnimate(start: Boolean) {
    if (start) {
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
}

@BindingAdapter("fabAnimationBig")
fun ExtendedFloatingActionButton.bindAnimateBig(start: Boolean) {
    if (start) {
        isExtended = false
        elevation = 10f
        textSize = 18f
        scaleX = 0.9f
        scaleY = 0.9f
        val handler = android.os.Handler(Looper.getMainLooper())
        handler.postDelayed({
            extend()
            handler.postDelayed({
                shrink()
            }, 1000)
        }, 1500)
    }
}

@BindingAdapter("getCurrentType", "viewModel", "gerCurrentPosition")
fun EditText.bindPosition(type: Int, viewModel: AddMenuItemViewModel, position: Int) {
    setOnFocusChangeListener { _, b ->
        if (b) {
            viewModel.recordCurrentSelectedType(type)
            viewModel.recordCurrentSelectedPosition(position)
        }
    }
}

@BindingAdapter("mode")
fun ImageView.bindTrafficIcon(mode: String) {
    when (mode) {
        WALKING -> setImageResource(R.drawable.ic_baseline_directions_walk_24)
        DRIVING -> setImageResource(R.drawable.ic_baseline_drive_eta_24)
    }
}

@BindingAdapter("btsMainViewMode", "btsViewModel", "lifecycleOwner")
fun ConstraintLayout.bindBottomSheetBehavior(
    mainViewModel: MainViewModel,
    viewModel: HomeViewModel,
    viewLifecycleOwner: LifecycleOwner
) {

    val bottomSheetBehavior = BottomSheetBehavior.from(this)
    val toolbarConstraint = rootView.findViewById<ConstraintLayout>(R.id.toolbar_constraint)

    //set toolbar visibility
    fun toolbarVisible() {
        toolbarConstraint.startAnimation(MyAnimation.fromTop)
        toolbarConstraint.visibility(1)
    }

    //set toolbar visibility
    fun toolbarGone() {
        toolbarConstraint.startAnimation(MyAnimation.toTopGone)
        toolbarConstraint.visibility(0)
    }
    //observe current fragment type
    viewModel.currentFragmentType.observe(viewLifecycleOwner, {
        when (it) {
            CurrentFragmentType.HOME ->
                if (viewModel.status.value != LoadApiStatus.LOADING &&
                    toolbarConstraint.visibility != View.VISIBLE
                ) {
                    toolbarVisible()
                }
            CurrentFragmentType.HOME_NAV -> toolbarGone()
            else -> {
            }
        }
    })

    if (mainViewModel.currentFragmentType.value == CurrentFragmentType.HOME_DIALOG ||
        mainViewModel.currentFragmentType.value == CurrentFragmentType.ORDER_DETAIL
    ) {
        bottomSheetBehavior.halfExpandedRatio = 0.0001f
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
    }

    //bottomSheet CallBack
    var x = 0
    bottomSheetBehavior.addBottomSheetCallback(object :
        BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(bottomSheet: View, newState: Int) {

            when (newState) {
                BottomSheetBehavior.STATE_HIDDEN -> {
                    if (mainViewModel.currentFragmentType.value != CurrentFragmentType.ORDER_DETAIL) {
                        mainViewModel.currentFragmentType.value = CurrentFragmentType.HOME
                        viewModel.currentFragmentType.value = CurrentFragmentType.HOME
                    }
                }
                else -> {

                }
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {

            if (slideOffset > 0.4f && x == 0) {
                x = 1; toolbarGone()
            }

            if (x == 1 && slideOffset < 0.4f &&
                viewModel.currentFragmentType.value != CurrentFragmentType.HOME_NAV
            ) {
                x = 0; toolbarVisible()
            }
        }
    })
}

@BindingAdapter("navBtsMainViewMode", "navBtsViewModel", "lifecycleOwner")
fun ConstraintLayout.bindNavBottomSheetBehavior(
    mainViewModel: MainViewModel,
    viewModel: HomeViewModel,
    viewLifecycleOwner: LifecycleOwner
) {
    val bottomSheetNavBehavior = BottomSheetBehavior.from(this)

    viewModel.options.observe(viewLifecycleOwner, {
        if (mainViewModel.currentFragmentType.value == CurrentFragmentType.HOME_DIALOG ||
            mainViewModel.currentFragmentType.value == CurrentFragmentType.ORDER_DETAIL
        ) {
            it?.let {
                mainViewModel.currentFragmentType.value = CurrentFragmentType.HOME_NAV
                viewModel.currentFragmentType.value = CurrentFragmentType.HOME_NAV
                bottomSheetNavBehavior.isDraggable = false
                bottomSheetNavBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    })

}










