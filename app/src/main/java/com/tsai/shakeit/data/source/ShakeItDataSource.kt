package com.tsai.shakeit.data.source

import androidx.lifecycle.MutableLiveData
import com.tsai.shakeit.data.Order
import com.tsai.shakeit.data.OrderProduct
import com.tsai.shakeit.data.Result

interface ShakeItDataSource {

    suspend fun postOrderToFireBase()

    fun getFireBaseOrder(): MutableLiveData<List<Order>>

    fun getFireBaseOrderProduct(): MutableLiveData<List<OrderProduct>>

}