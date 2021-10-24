package com.tsai.shakeit.data.source

import androidx.lifecycle.MutableLiveData
import com.tsai.shakeit.data.Order
import com.tsai.shakeit.data.OrderProduct
import com.tsai.shakeit.data.Result
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.ui.favorite.Favorite

interface ShakeItRepository {

    suspend fun postOrderToFireBase()

    fun getFireBaseOrder(): MutableLiveData<List<Order>>

    fun getFireBaseOrderProduct(): MutableLiveData<List<OrderProduct>>

    suspend fun getFavorite(): Result<List<Shop>>

}