package com.tsai.shakeit.ui.order

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.tsai.shakeit.ShakeItApplication
import com.tsai.shakeit.data.Order
import com.tsai.shakeit.data.OrderProduct
import com.tsai.shakeit.data.Result
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.data.succeeded
import com.tsai.shakeit.ui.home.TAG
import kotlinx.coroutines.launch
import java.util.function.LongFunction

class OrderViewModel(private val repository: ShakeItRepository) : ViewModel() {

    private var _userOrderList = MutableLiveData<List<Order>>()
    val userOrderList: LiveData<List<Order>>
        get() = _userOrderList

    private val _navToOrderDetail = MutableLiveData<Order?>()
    val navToOrderDetail: LiveData<Order?>
        get() = _navToOrderDetail

    init {
        getOrderData()
    }

    fun doNavToOrderDetail(order: Order) {
        _navToOrderDetail.value = order
        _navToOrderDetail.value = null
    }

    private fun getOrderData() {
     _userOrderList = repository.getFireBaseOrder()

    }
}

