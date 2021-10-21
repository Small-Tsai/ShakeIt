package com.tsai.shakeit.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tsai.shakeit.data.Order
import com.tsai.shakeit.data.OrderProduct

class OrderViewModel : ViewModel() {

    private val _userOrderList = MutableLiveData<List<Order>>()
    val userOrderList: LiveData<List<Order>>
        get() = _userOrderList

    private val _navToOrderDetail = MutableLiveData<Order?>()
    val navToOrderDetail: LiveData<Order?>
        get() = _navToOrderDetail

    private val orderMockData = listOf<Order>(
        Order(
            user_Id = 1,
            order_Id = 1,
            invitation = arrayListOf(),
            date = "2021.09.10 09:12",
            orderName = "我的訂單",
            shopName = "可不可熟成紅茶(小蔡店)",
            orderProduct = listOf(
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai" , ice = "正常冰"),
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "Tsai" , ice = "正常冰"),
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "sai" , ice = "正常冰"),
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tai" , ice = "正常冰"),
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai" , ice = "正常冰"),
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai" , ice = "正常冰"),
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai" , ice = "正常冰"),
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai" , ice = "正常冰"),
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai" , ice = "正常冰"),
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai" , ice = "正常冰"),
            ),
            shopAddress = "台北市信義區忠孝東路五段17之2號",
            orderPrice = 235
    ),
        Order(
            user_Id = 1,
            order_Id = 1,
            invitation = arrayListOf(),
            date = "2021.09.10 09:12",
            orderName = "我的訂單",
            shopName = "可不可熟成紅茶(小蔡店)",
            orderProduct = listOf(
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai" , ice = "正常冰"),
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai" , ice = "正常冰"),
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai" , ice = "正常冰"),
            ),
            shopAddress = "台北市信義區忠孝東路五段17之2號",
            orderPrice = 235
        ),
        Order(
            user_Id = 1,
            order_Id = 1,
            invitation = arrayListOf(),
            date = "2021.09.10 09:12",
            orderName = "我的訂單",
            shopName = "可不可熟成紅茶(小蔡店)",
            orderProduct = listOf(
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai" , ice = "正常冰"),
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai" , ice = "正常冰"),
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai" , ice = "正常冰"),
            ),
            shopAddress = "台北市信義區忠孝東路五段17之2號",
            orderPrice = 235
        ),
        Order(
            user_Id = 1,
            order_Id = 1,
            invitation = arrayListOf(),
            date = "2021.09.10 09:12",
            orderName = "我的訂單",
            shopName = "可不可熟成紅茶(小蔡店)",
            orderProduct = listOf(
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai" , ice = "正常冰"),
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai" , ice = "正常冰"),
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai" , ice = "正常冰"),
            ),
            shopAddress = "台北市信義區忠孝東路五段17之2號",
            orderPrice = 235
        ),
        Order(
            user_Id = 1,
            order_Id = 1,
            invitation = arrayListOf(),
            date = "2021.09.10 09:12",
            orderName = "我的訂單",
            shopName = "可不可熟成紅茶(小蔡店)",
            orderProduct = listOf(
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai" , ice = "正常冰"),
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai" , ice = "正常冰"),
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai" , ice = "正常冰"),
            ),
            shopAddress = "台北市信義區忠孝東路五段17之2號",
            orderPrice = 235
        ),
    )

    init {
        _userOrderList.value = orderMockData
    }

    fun doNavToOrderDetail (order: Order){
        _navToOrderDetail.value = order
        _navToOrderDetail.value = null
    }

}