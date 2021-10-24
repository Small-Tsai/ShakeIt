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

    override fun getFireBaseOrderProduct(): MutableLiveData<List<OrderProduct>> {
        return shakeItDataSource.getFireBaseOrderProduct()
    }

    override fun getFavorite(): MutableLiveData<List<Shop>> {
        return shakeItDataSource.getFavorite()
    }

    override suspend fun postFavorite(shop: Shop): Result<Boolean> {
        return shakeItDataSource.postFavorite(shop)
    }

    override suspend fun postOrderToFireBase() {
        return shakeItDataSource.postOrderToFireBase()
    }

    override suspend fun deleteFavorite(shopId: String): Result<Boolean> {
        return shakeItDataSource.deleteFavorite(shopId)
    }
}