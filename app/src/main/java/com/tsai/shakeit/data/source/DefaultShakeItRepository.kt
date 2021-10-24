package com.tsai.shakeit.data.source

import androidx.lifecycle.MutableLiveData
import com.tsai.shakeit.data.Order
import com.tsai.shakeit.data.OrderProduct
import com.tsai.shakeit.data.Result
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.ui.favorite.Favorite

class DefaultShakeItRepository (
    private val FireBaseDataSource: ShakeItDataSource
) : ShakeItRepository {

    override fun getFireBaseOrder() : MutableLiveData<List<Order>> {
       return FireBaseDataSource.getFireBaseOrder()
    }

    override fun getFireBaseOrderProduct(): MutableLiveData<List<OrderProduct>> {
        return FireBaseDataSource.getFireBaseOrderProduct()
    }

    override suspend fun getFavorite(): Result<List<Shop>> {
        return FireBaseDataSource.getFavorite()
    }

    override suspend fun postOrderToFireBase() {
        return FireBaseDataSource.postOrderToFireBase()
    }
}