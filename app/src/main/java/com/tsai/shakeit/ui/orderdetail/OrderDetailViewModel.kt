package com.tsai.shakeit.ui.orderdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tsai.shakeit.data.Order
import com.tsai.shakeit.data.OrderProduct
import com.tsai.shakeit.data.source.ShakeItRepository

class OrderDetailViewModel(order: Order?, private val repository: ShakeItRepository) : ViewModel() {

    private val _order = MutableLiveData<List<OrderProduct>>().apply {
        value = order?.orderProduct
    }
    val order: LiveData<List<OrderProduct>>
        get() = _order

}