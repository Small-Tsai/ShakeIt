package com.tsai.shakeit.ui.order.orderhistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsai.shakeit.data.Order
import com.tsai.shakeit.data.Result
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.util.Logger
import com.tsai.shakeit.util.UserInfo
import kotlinx.coroutines.launch

class OrderHistoryViewModel(private val repository: ShakeItRepository) : ViewModel() {

    private val _navToOrder = MutableLiveData<Boolean?>()
    val navToOrder: LiveData<Boolean?>
        get() = _navToOrder

    private val _orderHistory = MutableLiveData<List<Order>>()
    val orderHistory: LiveData<List<Order>>
        get() = _orderHistory

    private val _navToOrderDetail = MutableLiveData<Order?>()
    val navToOrderDetail: LiveData<Order?>
        get() = _navToOrderDetail

    fun navToOrder() {
        _navToOrder.value = true
        _navToOrder.value = null
    }

    init {
        getOrderHistory()
    }

    private fun getOrderHistory() {
        viewModelScope.launch {
            when (val result = repository.getOrderHistory(UserInfo.userId)){
                is Result.Success->{
                    _orderHistory.value = result.data!!
                }
            }
        }
    }

    fun doNavToOrderDetail(order: Order) {
        _navToOrderDetail.value = order
        _navToOrderDetail.value = null
    }

}