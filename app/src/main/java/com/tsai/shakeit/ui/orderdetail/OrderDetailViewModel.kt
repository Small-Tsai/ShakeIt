package com.tsai.shakeit.ui.orderdetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tsai.shakeit.data.Order
import com.tsai.shakeit.data.OrderProduct
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.ui.home.TAG

class OrderDetailViewModel(private val mOrder: Order?, private val repository: ShakeItRepository) :
    ViewModel() {

    private var _order = MutableLiveData<List<OrderProduct>>()
    val order: LiveData<List<OrderProduct>>
        get() = _order

    private val _totalPrice = MutableLiveData<Int>()
    val totalPrice: LiveData<Int>
        get() = _totalPrice

    init {
        getOrderProduct()
    }

    private fun getOrderProduct() {
        mOrder?.let {
            _order = repository.getFireBaseOrderProduct(mOrder.order_Id)
        }
    }
}