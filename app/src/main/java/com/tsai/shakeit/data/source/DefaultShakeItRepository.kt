package com.tsai.shakeit.data.source

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.android.libraries.maps.model.LatLng
import com.tsai.shakeit.data.*
import com.tsai.shakeit.data.directionPlaceModel.Direction

class DefaultShakeItRepository(
    private val shakeItDataSource: ShakeItDataSource
) : ShakeItRepository {

    override suspend fun deleteFavorite(shopId: String): Result<Boolean> {
        return shakeItDataSource.deleteFavorite(shopId)
    }

    override suspend fun deleteOrder(orderId: String): Result<Boolean> {
        return shakeItDataSource.deleteOrder(orderId)
    }

    override suspend fun deleteOrderProduct(
        orderProductId: String,
        shodId: String,
        otherUserId: String
    ): Result<Boolean> {
        return shakeItDataSource.deleteOrderProduct(orderProductId, shodId, otherUserId)
    }

    override suspend fun updateOrderTotalPrice(
        totalPrice: Int,
        shopId: String,
        otherUserId: String,
    ): Result<Boolean> {
        return shakeItDataSource.updateOrderTotalPrice(totalPrice, shopId, otherUserId)
    }

    override suspend fun updateFilteredShop(shopList: FilterShop): Result<Boolean> {
        return shakeItDataSource.updateFilteredShop(shopList)
    }

    override suspend fun postFavorite(favorite: Favorite): Result<Boolean> {
        return shakeItDataSource.postFavorite(favorite)
    }

    override suspend fun postOrderToFireBase(
        order: Order,
        orderProduct: OrderProduct,
        otherUserId: String,
        hasOrder: Boolean,
    ): Result<Boolean> {
        return shakeItDataSource.postOrderToFireBase(order, orderProduct, otherUserId, hasOrder)
    }

    override suspend fun postProduct(product: Product): Result<Boolean> {
        return shakeItDataSource.postProduct(product)
    }

    override suspend fun postComment(shopId: String, comment: Comment): Result<Boolean> {
        return shakeItDataSource.postComment(shopId, comment)
    }

    override suspend fun postShopInfo(shop: Shop): Result<Boolean> {
        return shakeItDataSource.postShopInfo(shop)
    }

    override suspend fun postImage(image: Uri): Result<String> {
        return shakeItDataSource.postImage(image)
    }

    override suspend fun postUserInfo(user: User): Result<Boolean> {
        return shakeItDataSource.postUserInfo(user)
    }

    override suspend fun postHistoryOrder(
        order: Order,
        orderProduct: List<OrderProduct>
    ): Result<Boolean> {
        return shakeItDataSource.postHistoryOrder(order, orderProduct)
    }

    override suspend fun crateNewOrderForShare(order: Order): Result<Boolean> {
        return shakeItDataSource.createNewOrderForShare(order)
    }

    override fun getFilteredShopList(userId: String): MutableLiveData<List<String>> {
        return shakeItDataSource.getFilteredShopList(userId)
    }

    override fun getFireBaseOrder(userId: String): MutableLiveData<List<Order>> {
        return shakeItDataSource.getFireBaseOrder(userId)
    }

    override fun getShopOrder(orderId: String): MutableLiveData<List<Order>> {
        return shakeItDataSource.getShopOrder(orderId)
    }

    override fun getFireBaseOrderProduct(orderId: String): MutableLiveData<List<OrderProduct>> {
        return shakeItDataSource.getFireBaseOrderProduct(orderId)
    }

    override fun getFavorite(userId: String): MutableLiveData<List<Favorite>> {
        return shakeItDataSource.getFavorite(userId)
    }

    override suspend fun getShopInfo(shopId: String): Result<Shop> {
        return shakeItDataSource.getShopInfo(shopId)
    }

    override suspend fun getAllShop(center: LatLng, distance: Double): Result<List<Shop>> {
        return shakeItDataSource.getAllShop(center, distance)
    }

    override suspend fun getProduct(shopId: String): Result<List<Product>> {
        return shakeItDataSource.getProduct(shopId)
    }

    override suspend fun getComment(shopId: String): Result<List<Comment>> {
        return shakeItDataSource.getComment(shopId)
    }

    override suspend fun getOrderProduct(orderId: String): Result<List<OrderProduct>> {
        return shakeItDataSource.getOrderProduct(orderId)
    }

    override suspend fun getOrderHistory(userId: String): Result<List<Order>> {
        return shakeItDataSource.getOrderHistory(userId)
    }

    override suspend fun getHistoryOrderProduct(orderId: String): Result<List<OrderProduct>> {
        return shakeItDataSource.getHistoryOrderProduct(orderId)
    }

    override suspend fun getDirection(url: String): Result<Direction> {
        return shakeItDataSource.getDirection(url)
    }

    override suspend fun joinToOrder(orderId: String): Result<Boolean> {
        return shakeItDataSource.joinToOrder(orderId)
    }

}