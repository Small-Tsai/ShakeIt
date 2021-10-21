package com.tsai.shakeit.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val shopAddress: String? = null,
    val orderName: String? = null,
    val shopName: String? = null,
    val user_Id: Long? = null,
    val order_Id: Long? = null,
    val invitation: ArrayList<Long>? = null,
    val date: String? = null,
    val orderProduct: List<OrderProduct>? = null,
    val orderPrice: Int? = null
) : Parcelable

@Parcelize
data class OrderProduct(
    val name: String? = null,
    val ice: String? = null,
    val capacity: String? = null,
    val qty: Int? = null,
    val sugar: String? = null,
    val others: String? = null,
    val userName: String? = null
) : Parcelable
