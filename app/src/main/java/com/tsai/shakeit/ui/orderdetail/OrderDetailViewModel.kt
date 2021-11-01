package com.tsai.shakeit.ui.orderdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsai.shakeit.data.Order
import com.tsai.shakeit.data.OrderProduct
import com.tsai.shakeit.data.source.ShakeItRepository
import kotlinx.coroutines.launch

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
            viewModelScope.launch {
                _order = repository.getFireBaseOrderProduct(mOrder.order_Id)
            }
        }
    }
}