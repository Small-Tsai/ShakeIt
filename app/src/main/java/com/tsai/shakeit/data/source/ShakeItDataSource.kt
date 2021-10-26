package com.tsai.shakeit.data.source

import androidx.lifecycle.MutableLiveData
import com.tsai.shakeit.data.Order
import com.tsai.shakeit.data.OrderProduct
import com.tsai.shakeit.data.Result
import com.tsai.shakeit.data.Shop

interface ShakeItDataSource {

    suspend fun postFavorite(shop: Shop): Result<Boolean>

    suspend fun postOrderToFireBase(order: Order ,  orderProduct: OrderProduct):Result<Boolean>

    suspend fun deleteFavorite(shopId: String): Result<Boolean>

    suspend fun deleteOrder(orderId: String): Result<Boolean>

    suspend fun getShopInfo(shopId: String): Result<Shop>

    suspend fun getOrderDataForMenu(orderId: String): Result<List<OrderProduct>>

    fun getFireBaseOrder(): MutableLiveData<List<Order>>

    fun getFireBaseOrderProduct(orderId: String): MutableLiveData<List<OrderProduct>>

    fun getFavorite(): MutableLiveData<List<Shop>>

}