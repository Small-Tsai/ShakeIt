package com.tsai.shakeit.ui.orderdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsai.shakeit.data.Order
import com.tsai.shakeit.data.OrderProduct
import com.tsai.shakeit.data.Result
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.data.notification.NotificationData
import com.tsai.shakeit.data.notification.PushNotification
import com.tsai.shakeit.data.source.ShakeItRepository
import com.tsai.shakeit.ext.myToast
import com.tsai.shakeit.network.ShakeItApi
import com.tsai.shakeit.ui.order.OrderType
import com.tsai.shakeit.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderDetailViewModel(
    val mOrder: Order?,
    private val repository: ShakeItRepository,
    val type: String?,
) : ViewModel() {

    private var _orderProduct = MutableLiveData<List<OrderProduct>>()
    val orderProduct: LiveData<List<OrderProduct>>
        get() = _orderProduct

    private val _totalPrice = MutableLiveData<Int>()
    val totalPrice: LiveData<Int>
        get() = _totalPrice

    private val _shop = MutableLiveData<Shop>()
    val shop: LiveData<Shop>
        get() = _shop

    private var _navToHome = MutableLiveData<Shop>()
    val navToHome: LiveData<Shop>
        get() = _navToHome

    private val _navToMenu = MutableLiveData<Shop?>()
    val navToMenu: LiveData<Shop?>
        get() = _navToMenu

    val isNotifyBtnVisible = MutableLiveData<Boolean>().apply { value = true }

    init {
        if (type == OrderType.HISTORY.type) {
            isNotifyBtnVisible.value = false
            getHistoryProduct()
        } else {
            getOrderProduct()
        }
        getShopData()
    }

    fun navToHome(shop: Shop) {
        _navToHome.value = shop
    }

    private fun getHistoryProduct() {
        mOrder?.let {
            viewModelScope.launch {
                when (val result = repository.getHistoryOrderProduct(it.order_Id)) {
                    is Result.Success -> {
                        _orderProduct.value = result.data
                    }
                    else -> {}
                }
            }
        }
    }

    private fun getOrderProduct() {
        mOrder?.let {
            viewModelScope.launch {
                _orderProduct = repository.getOrderProductBySnapShot(it.order_Id)
            }
        }
    }

    private fun getShopData() {
        viewModelScope.launch {
            mOrder?.let {
                when (val result = repository.getShopInfo(it.shop_Id)) {
                    is Result.Success -> {
                        _shop.value = result.data
                    }
                    else -> {}
                }
            }
        }
    }

    fun navToMenu() {
        mOrder?.let { order ->
            shop.value?.let { shop ->
                _navToMenu.value =
                    Shop(
                        name = order.shop_Name,
                        shop_Id = order.shop_Id,
                        shop_Img = shop.shop_Img,
                        branch = order.branch
                    )
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
        _orderProduct.value = _orderProduct.value
    }

    fun sendNotification() = viewModelScope.launch {

        withContext(Dispatchers.IO) {
            _orderProduct.value?.let { orderProduct ->

                val allToken =
                    orderProduct
                        .map { it.user.user_Token }
//                        .filter { it != MyFirebaseService.token }
                        .distinct()

                for (token in allToken) {
                    val userProduct = _orderProduct.value?.filter { it.user.user_Token == token }
                    val userDrinks = userProduct?.map { it.name + it.price + " x${it.qty}" }
                    val totalPrice = userProduct?.sumOf { it.price * it.qty }
                    var notifyContent = "\n"

                    userDrinks?.forEach {
                        notifyContent += "$it\n"
                    }

                    val notification = PushNotification(
                        to = token,
                        data = NotificationData(
                            "飲料到囉 ！",
                            "你的飲料有" + "\n" +
                                "-----------------------------" +
                                notifyContent +
                                "-----------------------------" + "\n" +
                                "總共 $totalPrice 元"
                        )
                    )

                    withContext(Dispatchers.Main) {
                        myToast("已發送商品抵達推播給所有成員")
                    }

                    try {
                        val response = ShakeItApi.firebaseService.postNotification(notification)
                        if (response.isSuccessful) {
                            Logger.d(response.message())
                        } else {
                            Logger.e(response.errorBody().toString())
                        }
                    } catch (e: Exception) {
                        Logger.e(e.toString())
                    }
                }
            }
        }
    }
}
