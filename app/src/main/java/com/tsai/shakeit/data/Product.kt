package com.tsai.shakeit.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val name: String = "",
    val content: String = "",
    val capacity: ArrayList<String> = arrayListOf(),
    val sugar: ArrayList<String> = arrayListOf(),
    val ice: ArrayList<String> = arrayListOf(),
    val price: Int = 0,
    val others: ArrayList<String> = arrayListOf(),
    val shopId: String = "",
    val type: String = "",
    val id: String = "",
    val shop_Name: String = "",
    val shopAddress: String = "",
    val branch: String = ""
) : Parcelable



