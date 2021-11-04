package com.tsai.shakeit.ui.orderdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsai.shakeit.data.Order
import com.tsai.shakeit.data.OrderProduct
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

    init {
        getOrderProduct()
    }

    private fun getOrderProduct() {
        mOrder?.let {
            viewModelScope.launch {
                _order = repository.getFireBaseOrderProduct(it.order_Id)
            }
        }
    }

    private val _navToMenu = MutableLiveData<Shop?>()
    val navToMenu: LiveData<Shop?>
        get() = _navToMenu

    fun navToMenu() {
        mOrder?.let {
            _navToMenu.value = shopImg?.let { shopImg ->
                Shop(
                    name = mOrder.shop_Name,
                    shop_Id = mOrder.shop_Id,
                    shop_Img = shopImg,
                    branch = mOrder.branch
                )
            }
            _navToMenu.value = null
        }
    }

    fun removeOrderProduct(orderProductId: String) {
        viewModelScope.launch {
            mOrder?.let {
                repository.deleteOrderProduct(orderProductId, it.shop_Id , it.user_Id)
            }
        }
    }

}