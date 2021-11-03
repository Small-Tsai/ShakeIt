package com.tsai.shakeit.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsai.shakeit.data.Order
import com.tsai.shakeit.data.Result
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.util.UserInfo
import kotlinx.coroutines.launch

class OrderViewModel(private val repository: ShakeItRepository) : ViewModel() {

    private var _userOrderList = MutableLiveData<List<Order>>()
    val userOrderList: LiveData<List<Order>>
        get() = _userOrderList

    private val _navToOrderDetail = MutableLiveData<Order?>()
    val navToOrderDetail: LiveData<Order?>
        get() = _navToOrderDetail

    private val _shopImg = MutableLiveData<String>()
    val shopImg: LiveData<String>
        get() = _shopImg

    private val _shopId = MutableLiveData<String>()
    val shopId: LiveData<String>
        get() = _shopId

    init {
        getOrderData()
    }


    fun getShopImage(shopId: String) {
        viewModelScope.launch {
            when (val result = repository.getShopInfo(shopId)) {
                is Result.Success -> {
                    _shopImg.value = result.data.shop_Img
                }
            }
        }
    }

    private fun getOrderData() {
        _userOrderList = repository.getFireBaseOrder(UserInfo.userId)
    }

    fun deleteOrder(orderId: String) {
        viewModelScope.launch {
            repository.deleteOrder(orderId)
        }
    }

    fun doNavToOrderDetail(order: Order) {
        _navToOrderDetail.value = order
        _navToOrderDetail.value = null
    }

    // orderId = shopId
    fun navToSendComment(orderId: String) {
        _shopId.value = orderId
    }
}

