package com.tsai.shakeit.data.source.remote

import android.annotation.TargetApi
import android.icu.util.Calendar
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.ktx.*
import com.tsai.shakeit.ShakeItApplication
import com.tsai.shakeit.data.Order
import com.tsai.shakeit.data.OrderProduct
import com.tsai.shakeit.data.Result
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.data.source.ShakeItDataSource
import com.tsai.shakeit.ui.favorite.Favorite
import com.tsai.shakeit.ui.home.TAG
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val ORDERS = "orders"
private const val KEY_CREATED_TIME = "date"
private const val FAVORITE = "favorite"

object ShakeItRemoteDataSource : ShakeItDataSource {

    override suspend fun postFavorite(shop: Shop): Result<Boolean> =
        suspendCoroutine { continuation ->

            val favorite = FirebaseFirestore.getInstance().collection(FAVORITE)
            val document = favorite.document(shop.shop_Id)

            document
                .set(shop)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.i(TAG, "Publish: $shop")

                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {
                            Log.w(
                                TAG,
                                "[${this::class.simpleName}] Error getting documents. ${it.message}"
                            )
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("postFavorite Failed"))
                    }
                }
        }

    override suspend fun postOrderToFireBase() {
    }

    override suspend fun deleteFavorite(shopId: String): Result<Boolean> =
        suspendCoroutine { continuation ->

            val favorite = FirebaseFirestore.getInstance().collection(FAVORITE)
            val document = favorite.document(shopId)

            document
                .delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(ShakeItApplication.instance, "已移除此收藏", Toast.LENGTH_SHORT)
                            .show()

                    } else {
                        task.exception?.let {
                            Log.w(
                                TAG,
                                "[${this::class.simpleName}] Error delete documents. ${it.message}"
                            )
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("deleteFavorite Failed"))
                    }
                }
        }

    override fun getFireBaseOrder(): MutableLiveData<List<Order>> {

        val liveData = MutableLiveData<List<Order>>()

        FirebaseFirestore.getInstance()
            .collection(ORDERS)
            .orderBy(KEY_CREATED_TIME, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->

                val list = mutableListOf<Order>()
                if (snapshot != null) {
                    for (document in snapshot) {
//                        Log.d(TAG, "Current data: ${document.data}")
                        val order = document.toObject(Order::class.java)
                        list.add(order)
                    }
                }
                liveData.value = list
            }
        return liveData
    }

    override fun getFireBaseOrderProduct(): MutableLiveData<List<OrderProduct>> {

        val liveData = MutableLiveData<List<OrderProduct>>()

        FirebaseFirestore.getInstance()
            .collection(ORDERS)
            .document("9dUeOu8aRMsg4q7dDYF5")
            .collection("orderProduct")
            .addSnapshotListener { snapshot, e ->

                val list = mutableListOf<OrderProduct>()
                if (snapshot != null) {
                    for (document in snapshot) {
//                        Log.d(TAG, "Current data: ${document.data}")
                        val order = document.toObject(OrderProduct::class.java)
                        list.add(order)
                    }
                }
                liveData.value = list
            }
        return liveData
    }

    override fun getFavorite(): MutableLiveData<List<Shop>> {

        val liveData = MutableLiveData<List<Shop>>()

        FirebaseFirestore.getInstance()
            .collection(FAVORITE)
            .addSnapshotListener { snapshot, e ->

                val list = mutableListOf<Shop>()

                if (snapshot != null) {
                    for (document in snapshot) {
//                        Log.d(TAG, "Current data: ${document.data}")
                        val shop = document.toObject(Shop::class.java)
                        list.add(shop)
                    }
                }
                liveData.value = list
            }
        return liveData
    }
}