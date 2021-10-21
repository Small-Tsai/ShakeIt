package com.tsai.shakeit.ui.orderdetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tsai.shakeit.data.Order
import com.tsai.shakeit.data.OrderProduct
import com.tsai.shakeit.ui.home.TAG

class OrderDetailViewModel(order: Order?) : ViewModel() {

    private val _order = MutableLiveData<List<OrderProduct>>().apply {
        value = order?.orderProduct
    }
    val order: LiveData<List<OrderProduct>>
        get() = _order

}