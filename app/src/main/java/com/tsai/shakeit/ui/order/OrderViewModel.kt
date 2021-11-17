package com.tsai.shakeit.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsai.shakeit.R
import com.tsai.shakeit.data.Order
import com.tsai.shakeit.data.OrderProduct
import com.tsai.shakeit.data.Result
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.ext.mToast
import com.tsai.shakeit.network.LoadApiStatus
import com.tsai.shakeit.util.Logger
import com.tsai.shakeit.util.UserInfo
import com.tsai.shakeit.util.Util
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

    private val _status = MutableLiveData<LoadApiStatus?>()
    val status: LiveData<LoadApiStatus?>
        get() = _status

    val hasOrder = MutableLiveData<Boolean>().apply { value = false }

    init {
        getOrderData()
    }

    private fun getOrderData() {
        _userOrderList = repository.getFireBaseOrder(UserInfo.userId)
    }

    fun deleteOrder(orderId: String, isComepleteOrder: Boolean) {
        _status.value = LoadApiStatus.LOADING
        viewModelScope.launch {
            when (val result = withContext(Dispatchers.IO) { repository.deleteOrder(orderId) }) {
                is Result.Success -> {
                    if (!isComepleteOrder) {
                        _status.value = LoadApiStatus.DONE
                    }
                }
                is Result.Fail -> {
                    Logger.e(result.error)
                }
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

        if (!Util.isInternetConnected()) {
            _status.value = LoadApiStatus.ERROR
            mToast(Util.getString(R.string.internet_not_connected))
        } else {
            viewModelScope.launch {
                _status.value = LoadApiStatus.LOADING
                when (val result = withContext(Dispatchers.IO) {
                    repository.getOrderProduct(order.order_Id)
                }) {
                    is Result.Success -> {
                        _orderProduct.value = result.data!!
                    }
                }

                if (!_orderProduct.value.isNullOrEmpty()) {
                    _orderProduct.value?.let {
                        deleteOrder(order.order_Id, true)
                        Logger.d("orderId = ${order.order_Id}")
                        when (val result = withContext(Dispatchers.IO) {
                            repository.postHistoryOrder(order, it)
                        }) {
                            is Result.Success -> {
                                _status.value = LoadApiStatus.DONE
                                _shopId.value = order.shop_Id
                                _shopId.value = null
                            }
                        }
                    }
                } else {
                    _status.value = LoadApiStatus.DONE
                    mToast("無任何商品無法完成訂單，如要移除請點選移除按鈕")
                }
            }
        }

    }

    fun navToOrderHistory() {
        _navToOrderHistory.value = true
        _navToOrderHistory.value = null
    }
}

