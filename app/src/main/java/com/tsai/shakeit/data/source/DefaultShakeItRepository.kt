package com.tsai.shakeit.data.source

import androidx.lifecycle.MutableLiveData
import com.tsai.shakeit.data.*
import com.tsai.shakeit.data.Comment

class DefaultShakeItRepository(
    private val shakeItDataSource: ShakeItDataSource
) : ShakeItRepository {

    override fun getFireBaseOrder(): MutableLiveData<List<Order>> {
        return shakeItDataSource.getFireBaseOrder()
    }

    override fun getShopOrder(shopId: String): MutableLiveData<List<Order>> {
        return shakeItDataSource.getShopOrder(shopId)
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

    override suspend fun postOrderToFireBase(
        order: Order,
        orderProduct: OrderProduct
    ): Result<Boolean> {
        return shakeItDataSource.postOrderToFireBase(order, orderProduct)
    }

    override suspend fun postProduct(product: Product): Result<Boolean> {
        return shakeItDataSource.postProduct(product)
    }

    override suspend fun postComment(shopId: String, comment: Comment) : Result<Boolean>{
        return shakeItDataSource.postComment(shopId,comment)
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

    override suspend fun getAllShop(): Result<List<Shop>> {
        return shakeItDataSource.getAllShop()
    }

    override suspend fun getProduct(shopId: String): Result<List<Product>> {
        return shakeItDataSource.getProduct(shopId)
    }

    override suspend fun updateOrderTotalPrice(totalPrice: Int, shopId: String): Result<Boolean> {
        return shakeItDataSource.updateOrderTotalPrice(totalPrice, shopId)
    }

    override suspend fun getComment(shopId: String): Result<List<Comment>> {
        return shakeItDataSource.getComment(shopId)
    }

    override suspend fun updateFilteredShop(shopList: FilterShop): Result<Boolean> {
        return shakeItDataSource.updateFilteredShop(shopList)
    }

    override fun getFilteredShopList(userId: String): MutableLiveData<List<String>> {
        return shakeItDataSource.getFilteredShopList(userId)
    }

}