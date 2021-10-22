package com.tsai.shakeit.data.source

import androidx.lifecycle.MutableLiveData
import com.tsai.shakeit.data.Order
import com.tsai.shakeit.data.OrderProduct

class DefaultShakeItRepository (
    private val FireBaseDataSource: ShakeItDataSource
) : ShakeItRepository {

    override fun getFireBaseOrder() : MutableLiveData<List<Order>> {
       return FireBaseDataSource.getFireBaseOrder()
    }

    override fun getFireBaseOrderProduct(): MutableLiveData<List<OrderProduct>> {
        return FireBaseDataSource.getFireBaseOrderProduct()
    }

    override suspend fun postOrderToFireBase() {
        return FireBaseDataSource.postOrderToFireBase()
    }
}