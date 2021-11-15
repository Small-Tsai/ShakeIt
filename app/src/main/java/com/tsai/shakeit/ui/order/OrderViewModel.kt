package com.tsai.shakeit.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsai.shakeit.data.Order
import com.tsai.shakeit.data.OrderProduct
import com.tsai.shakeit.data.Result
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.util.Logger
import com.tsai.shakeit.util.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderViewModel(private val repository: ShakeItRepository) : ViewModel() {

    private var _userOrderList = MutableLiveData<List<Order>>()
    val userOrderList: LiveData<List<Order>>
        get() = _userOrderList

    private val _navToOrderDetail = MutableLiveData<Order?>()
    val navToOrderDetail: LiveData<Order?>
        get() = _navToOrderDetail

    private val _navToOrderHistory = MutableLiveData<Boolean?>()
    val navToOrderHistory: LiveData<Boolean?>
        get() = _navToOrderHistory

    private val _shopId = MutableLiveData<String?>()
    val shopId: LiveData<String?>
        get() = _shopId

    val hasOrder = MutableLiveData<Boolean>().apply { value = false }

    init {
        getOrderData()
    }

    private fun getOrderData() {
        _userOrderList = repository.getFireBaseOrder(UserInfo.userId)
    }

    fun deleteOrder(orderId: String) {
        Logger.d("deleteOrder")
        viewModelScope.launch {
           withContext(Dispatchers.IO){
               repository.deleteOrder(orderId)
           }
        }
    }

    fun doNavToOrderDetail(order: Order) {
        _navToOrderDetail.value = order
        _navToOrderDetail.value = null
    }

    private var _orderProduct = MutableLiveData<List<OrderProduct>>()
    val orderProduct: LiveData<List<OrderProduct>>
        get() = _orderProduct

    fun navToSendComment(order: Order) {

        viewModelScope.launch {

            when(val result = repository.getOrderProduct(order.order_Id)){
                is Result.Success->{
                    _orderProduct.value = result.data!!
                }
            }

            _orderProduct.value?.let {
                deleteOrder(order.order_Id)
                Logger.d("${order.order_Id}")
                when (val result = repository.postHistoryOrder(order, it)) {
                    is Result.Success -> {
                        _shopId.value = order.shop_Id
                        _shopId.value = null
                    }
                }
            }
        }
    }

    fun navToOrderHistory() {
        _navToOrderHistory.value = true
        _navToOrderHistory.value = null
    }
}

