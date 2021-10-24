package com.tsai.shakeit.data.source

import androidx.lifecycle.MutableLiveData
import com.tsai.shakeit.data.Order
import com.tsai.shakeit.data.OrderProduct
import com.tsai.shakeit.data.Result
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.ui.favorite.Favorite

interface ShakeItDataSource {

    suspend fun postFavorite(shop: Shop): Result<Boolean>

    suspend fun postOrderToFireBase()

    suspend fun deleteFavorite(shopId: String): Result<Boolean>

    fun getFireBaseOrder(): MutableLiveData<List<Order>>

    fun getFireBaseOrderProduct(): MutableLiveData<List<OrderProduct>>

    fun getFavorite(): MutableLiveData<List<Shop>>

}