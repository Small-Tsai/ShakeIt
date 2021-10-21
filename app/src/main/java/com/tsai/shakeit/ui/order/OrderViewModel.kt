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

    private val orderMockData = listOf<Order>(
        Order(
            user_Id = 1,
            order_Id = 1,
            invitation = arrayListOf(),
            date = "2021.09.10 09:12",
            orderName = "我的訂單",
            shopName = "可不可熟成紅茶(小蔡店)",
            orderProduct = listOf(
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai"),
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai"),
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai")
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
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai"),
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai"),
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai")
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
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai"),
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai"),
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai")
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
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai"),
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai"),
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai")
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
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai"),
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai"),
                OrderProduct(name = "熟成紅茶", capacity = "大", qty = 1, sugar = "全糖", others = "加珍珠", userName = "tsai")
            ),
            shopAddress = "台北市信義區忠孝東路五段17之2號",
            orderPrice = 235
        ),
    )

    init {
        _userOrderList.value = orderMockData
    }

}