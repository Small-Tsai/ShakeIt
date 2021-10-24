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
    val address: String = "",
    val order_Name: String = "",
    val shop_Name: String = "",
    val user_Id: Long = 0,
    var order_Id: String = "",
    val invitation: ArrayList<Long>? = arrayListOf(),
    val date: Timestamp? = null,
    val orderProduct: List<OrderProduct> = listOf(),
    val order_Price: Int = 0,
    val branch: String = ""
) : Parcelable

@Parcelize
data class OrderProduct(
    val name: String = "",
    val ice: String = "",
    val capacity: String = "",
    val qty: Int = 0,
    val sugar: String = "",
    val others: String = "",
    val user_Name: String = "",
    val product_Img: String = "",
    val price: Int = 0
) : Parcelable
