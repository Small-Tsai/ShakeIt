package com.tsai.shakeit.data.source

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.android.libraries.maps.model.LatLng
import com.tsai.shakeit.data.*
import com.tsai.shakeit.data.directionPlaceModel.Direction
import kotlinx.coroutines.flow.Flow

interface ShakeItRepository {

    suspend fun deleteFavorite(shopId: String): Flow<Result<Boolean>>

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

    suspend fun postFavorite(favorite: Favorite): Flow<Result<Boolean>>

    suspend fun postOrderToFireBase(
        order: Order,
        orderProduct: OrderProduct,
        otherUserId: String,
        hasOrder: Boolean
    ): Flow<Result<Boolean>>

    suspend fun postProduct(product: Product): Flow<Result<Boolean>>

    suspend fun postComment(shopId: String, comment: Comment): Result<Boolean>

    suspend fun postShopInfo(shop: Shop): Result<Boolean>

    suspend fun postImage(imageUri: Uri):  Flow<Result<String>>

    suspend fun postUserInfo(user: User): Result<Boolean>

    suspend fun postHistoryOrder(order: Order, orderProduct: List<OrderProduct>): Result<Boolean>

    suspend fun crateNewOrderForShare(order: Order): Result<Boolean>

    suspend fun getShopInfo(shopId: String): Result<Shop>

    suspend fun getAllShop(center: LatLng, distance: Double): Flow<Result<List<Shop>>>

    suspend fun getProduct(shop: Shop): Flow<Result<List<Product>>>

    suspend fun getComment(shopId: String): Result<List<Comment>>

    suspend fun getOrderProduct(orderId: String): Result<List<OrderProduct>>

    suspend fun getOrderHistory(userId: String): Result<List<Order>>

    suspend fun getHistoryOrderProduct(orderId: String): Result<List<OrderProduct>>

    suspend fun getDirection(url: String): Flow<Result<Direction>>

    suspend fun joinToOrder(orderId: String): Result<Boolean>

    suspend fun getAllProduct(): Flow<Result<List<Product>>>

    fun getFilteredShopList(userId: String): MutableLiveData<List<String>>

    fun getFireBaseOrder(userId: String): MutableLiveData<List<Order>>

    fun getShopOrder(orderId: String): MutableLiveData<List<Order>>

    fun getFireBaseOrderProduct(orderId: String): MutableLiveData<List<OrderProduct>>

    fun getFavorite(userId: String): Flow<Result<List<Favorite>>>

    fun updateUserTokenOnFireBase(newToken: String)

}