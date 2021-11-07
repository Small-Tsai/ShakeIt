package com.tsai.shakeit.ui.orderdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsai.shakeit.data.Order
import com.tsai.shakeit.data.OrderProduct
import com.tsai.shakeit.data.Result
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.util.Logger
import kotlinx.coroutines.launch


class OrderDetailViewModel(
    val mOrder: Order?,
    private val repository: ShakeItRepository,
    private val shopImg: String?
) :
    ViewModel() {

    private var _order = MutableLiveData<List<OrderProduct>>()
    val order: LiveData<List<OrderProduct>>
        get() = _order

    private val _totalPrice = MutableLiveData<Int>()
    val totalPrice: LiveData<Int>
        get() = _totalPrice

    private val _shop = MutableLiveData<Shop>()
    val shop: LiveData<Shop>
        get() = _shop

    init {
        getOrderProduct()
        getShopData()
    }

    private fun getOrderProduct() {
        mOrder?.let {
            viewModelScope.launch {
                _order = repository.getFireBaseOrderProduct(it.order_Id)
            }
        }
    }

    private fun getShopData() {
        viewModelScope.launch {
            mOrder?.let {
                when (val result = repository.getShopInfo(it.shop_Id)) {
                    is Result.Success -> {
                        _shop.value = result.data!!
                    }
                }
            }
        }
    }

    private val _navToMenu = MutableLiveData<Shop?>()
    val navToMenu: LiveData<Shop?>
        get() = _navToMenu

    fun navToMenu() {
        mOrder?.let { order ->
            shop.value?.let { shop ->
                _navToMenu.value = shopImg?.let { shopImg ->
                    Shop(
                        name = order.shop_Name,
                        shop_Id = order.shop_Id,
                        shop_Img = shop.shop_Img,
                        branch = order.branch
                    )
                }
            }
            _navToMenu.value = null
        }
    }

    fun removeOrderProduct(orderProductId: String) {
        viewModelScope.launch {
            mOrder?.let {
                repository.deleteOrderProduct(orderProductId, it.shop_Id, it.user_Id)
            }
        }
    }

    fun updateTotalPrice(totalPrice: Int) {
        mOrder?.let {
            viewModelScope.launch {
                repository.updateOrderTotalPrice(totalPrice, it.shop_Id, it.user_Id)
            }
        }
    }

    fun notifyOrderChange() {
        _order.value = _order.value
    }

}