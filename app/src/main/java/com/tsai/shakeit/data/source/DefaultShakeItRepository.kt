package com.tsai.shakeit.data.source

import androidx.lifecycle.MutableLiveData
import com.tsai.shakeit.data.Order
import com.tsai.shakeit.data.OrderProduct
import com.tsai.shakeit.data.Result
import com.tsai.shakeit.data.Shop

class DefaultShakeItRepository(
    private val shakeItDataSource: ShakeItDataSource
) : ShakeItRepository {

    override fun getFireBaseOrder(): MutableLiveData<List<Order>> {
        return shakeItDataSource.getFireBaseOrder()
    }

    override fun getFireBaseOrderProduct(orderId: String): MutableLiveData<List<OrderProduct>> {
        return shakeItDataSource.getFireBaseOrderProduct(orderId)
    }

    override fun getFavorite(): MutableLiveData<List<Shop>> {
        return shakeItDataSource.getFavorite()
    }

    override suspend fun postFavorite(shop: Shop): Result<Boolean> {
        return shakeItDataSource.postFavorite(shop)
    }

    override suspend fun postOrderToFireBase(order: Order, orderProduct: OrderProduct): Result<Boolean> {
        return shakeItDataSource.postOrderToFireBase(order, orderProduct)
    }

    override suspend fun deleteFavorite(shopId: String): Result<Boolean> {
        return shakeItDataSource.deleteFavorite(shopId)
    }

    override suspend fun deleteOrder(orderId: String): Result<Boolean> {
        return shakeItDataSource.deleteOrder(orderId)
    }

    override suspend fun getShopInfo(shopId: String): Result<Shop> {
        return shakeItDataSource.getShopInfo(shopId)
    }

    override suspend fun getOrderDataForMenu(orderId: String): Result<List<OrderProduct>> {
        return shakeItDataSource.getOrderDataForMenu(orderId)
    }
}