package com.tsai.shakeit.data.source.remote

import android.annotation.TargetApi
import android.util.Log
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

    override suspend fun postOrderToFireBase() {
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
                        Log.d(TAG, "Current data: ${document.data}")
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
                        Log.d(TAG, "Current data: ${document.data}")
                        val order = document.toObject(OrderProduct::class.java)
                        list.add(order)
                    }
                }
                liveData.value = list
            }
        return liveData
    }

    override suspend fun getFavorite(): Result<List<Shop>> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(FAVORITE)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = mutableListOf<Shop>()
                    for (document in task.result!!) {
                        Log.d(TAG, document.id + " => " + document.data)

                        val favorite = document.toObject(Shop::class.java)
                        list.add(favorite)
                    }
                    continuation.resume(Result.Success(list))
                } else {
                    task.exception?.let {

                        Log.w(TAG, "[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail("getFavorite Failed"))
                }
            }
    }


}