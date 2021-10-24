package com.tsai.shakeit.data

import android.os.Parcelable
import com.google.api.ResourceReference
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.local.ReferenceSet
import kotlinx.parcelize.Parcelize
import java.lang.ref.Reference

@Parcelize
data class Order(
    val address: String? = null,
    val order_Name: String? = null,
    val shop_Name: String? = null,
    val user_Id: Long? = null,
    val order_Id: String? = null,
    val invitation: ArrayList<Long>? = null,
    val date: Timestamp? = null,
    val orderProduct: List<OrderProduct> = listOf(),
    val order_Price: Int? = null
) : Parcelable

@Parcelize
data class OrderProduct(
    val name: String? = null,
    val ice: String? = null,
    val capacity: String? = null,
    val qty: Int? = null,
    val sugar: String? = null,
    val others: String? = null,
    val user_Name: String? = null,
    val product_Img: String? = null,
    val price: Int = 0
) : Parcelable
