package com.tsai.shakeit.data.source

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.android.libraries.maps.model.LatLng
import com.tsai.shakeit.data.*
import com.tsai.shakeit.data.directionPlaceModel.Direction

interface ShakeItDataSource {

    suspend fun deleteFavorite(shopId: String): Result<Boolean>

    suspend fun deleteOrder(orderId: String): Result<Boolean>

    suspend fun deleteOrderProduct(
        orderProductId: String,
        shodId: String,
        otherUserId: String
    ): Result<Boolean>

    suspend fun updateOrderTotalPrice(
        totalPrice: Int,
        shopId: String,
        otherUserId: String
    ): Result<Boolean>

    suspend fun updateFilteredShop(shopList: FilterShop): Result<Boolean>

    suspend fun postFavorite(favorite: Favorite): Result<Boolean>

    suspend fun postOrderToFireBase(
        order: Order,
        orderProduct: OrderProduct,
        otherUserId: String,
        orderSize: Int
    ): Result<Boolean>

    suspend fun postProduct(product: Product): Result<Boolean>

    suspend fun postComment(shopId: String, comment: Comment): Result<Boolean>

    suspend fun postShopInfo(shop: Shop): Result<Boolean>

    suspend fun postImage(image: Uri): Result<String>

    suspend fun postUserInfo(user: User): Result<Boolean>

    suspend fun getShopInfo(shopId: String): Result<Shop>

    suspend fun getAllShop(center: LatLng, distance: Double): Result<List<Shop>>

    suspend fun getProduct(shopId: String): Result<List<Product>>

    suspend fun getComment(shopId: String): Result<List<Comment>>

    suspend fun postHistoryOrder(order: Order, orderProduct: List<OrderProduct>): Result<Boolean>

    suspend fun getOrderProduct(orderId: String): Result<List<OrderProduct>>

    suspend fun getOrderHistory(userId: String): Result<List<Order>>

    suspend fun getHistoryOrderProduct(orderId: String): Result<List<OrderProduct>>

    suspend fun getDirection(url:String): Result<Direction>

    fun getFilteredShopList(userId: String): MutableLiveData<List<String>>

    fun getFireBaseOrder(userId: String): MutableLiveData<List<Order>>

    fun getShopOrder(shopId: String): MutableLiveData<List<Order>>

    fun getFireBaseOrderProduct(orderId: String): MutableLiveData<List<OrderProduct>>

    fun getFavorite(userId: String): MutableLiveData<List<Favorite>>

}