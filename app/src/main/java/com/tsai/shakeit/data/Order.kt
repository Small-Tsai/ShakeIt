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
    val user_Id: String = "",
    var order_Id: String = "",
    val invitation: ArrayList<String>? = arrayListOf(),
    val date: Timestamp? = null,
    val orderProduct: List<OrderProduct> = listOf(),
    val order_Price: Int = 0,
    val branch: String = "",
    val shop_Id: String = "",
) : Parcelable

@Parcelize
data class OrderProduct(
    val name: String = "",
    val ice: String = "",
    val capacity: String = "",
    val qty: Int = 0,
    val sugar: String = "",
    val others: String = "",
    val product_Img: String = "",
    val price: Int = 0,
    val user: User = User(),
    var orderProduct_Id: String = ""

) : Parcelable
